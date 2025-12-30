package com.luis.textlift_backend.features.auth.api;

import com.luis.textlift_backend.features.auth.api.dto.LoginUserDto;
import com.luis.textlift_backend.features.auth.api.dto.LoginUserResponseDto;
import com.luis.textlift_backend.features.auth.api.dto.RegisterUserDto;
import com.luis.textlift_backend.features.auth.api.dto.RegisterUserResponseDto;
import com.luis.textlift_backend.features.auth.domain.User;
import com.luis.textlift_backend.features.auth.service.AuthenticationService;
import com.luis.textlift_backend.features.auth.service.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/auth")
@RestController
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;
    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginUserResponseDto> authenticate(@Valid @RequestBody LoginUserDto loginUserRequestDto){
        //First, attempt to authenticate the user, and if so, return the authenticated user
        User authenticatedUser = authenticationService.authenticate(loginUserRequestDto);

        //Once authenticated, generate a token for them
        String jwtToken = jwtService.generateToken(authenticatedUser);

        //Now, just build the response
        LoginUserResponseDto loginResponse = new LoginUserResponseDto(jwtToken, jwtService.getExpirationTime());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<RegisterUserResponseDto> register(@Valid @RequestBody RegisterUserDto registerUserRequestDto){
        //Register the user
        User u = authenticationService.signup(registerUserRequestDto);
        return ResponseEntity.ok(new RegisterUserResponseDto(u.getId(), u.getUsername(), u.getFullName()));
    }
}
