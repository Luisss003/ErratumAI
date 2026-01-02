package com.luis.textlift_backend.features.auth.api;

import com.luis.textlift_backend.features.auth.api.dto.LoginUserDto;
import com.luis.textlift_backend.features.auth.api.dto.RegisterUserDto;
import com.luis.textlift_backend.features.auth.api.dto.RegisterUserResponseDto;
import com.luis.textlift_backend.features.auth.domain.User;
import com.luis.textlift_backend.features.auth.service.AuthenticationService;
import com.luis.textlift_backend.features.auth.service.JwtService;
import com.luis.textlift_backend.features.auth.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthenticationController {
    private static final String ACCESS_TOKEN_COOKIE = "ACCESS_TOKEN";
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    private final TokenService tokenService;
    private final boolean secureCookies;

    public AuthenticationController(
            JwtService jwtService,
            AuthenticationService authenticationService,
            TokenService tokenService,
            @Value("${auth.cookie.secure:true}") boolean secureCookies
    ) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
        this.tokenService = tokenService;
        this.secureCookies = secureCookies;
    }

    @PostMapping("/login")
    public ResponseEntity<Void> authenticate(
            @Valid @RequestBody LoginUserDto loginUserRequestDto,
            HttpServletRequest request
    ){
        //First, attempt to authenticate the user, and if so, return the authenticated user
        User authenticatedUser = authenticationService.authenticate(loginUserRequestDto);

        //Once authenticated, generate a token for them
        String jwtToken = jwtService.generateToken(authenticatedUser);

        //Now, just build the response
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, buildAccessTokenCookie(jwtToken, request))
                .build();
    }

    @PostMapping("/signup")
    public ResponseEntity<RegisterUserResponseDto> register(@Valid @RequestBody RegisterUserDto registerUserRequestDto){
        //Register the user
        User u = authenticationService.signup(registerUserRequestDto);
        return ResponseEntity.ok(new RegisterUserResponseDto(u.getId(), u.getUsername(), u.getFullName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        String token = resolveToken(request);
        if (token != null) {
            tokenService.revokeToken(token);
        }

        return ResponseEntity
                .noContent()
                .header(HttpHeaders.SET_COOKIE, buildExpiredAccessTokenCookie(request))
                .build();
    }

    private String resolveToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    private String buildAccessTokenCookie(String token, HttpServletRequest request) {
        return ResponseCookie
                .from(ACCESS_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(secureCookies)
                .path("/")
                .maxAge(Duration.ofMillis(jwtService.getExpirationTime()))
                .sameSite("Strict")
                .build()
                .toString();
    }

    private String buildExpiredAccessTokenCookie(HttpServletRequest request) {
        return ResponseCookie
                .from(ACCESS_TOKEN_COOKIE, "")
                .httpOnly(true)
                .secure(secureCookies)
                .path("/")
                .maxAge(Duration.ZERO)
                .sameSite("Strict")
                .build()
                .toString();
    }
}
