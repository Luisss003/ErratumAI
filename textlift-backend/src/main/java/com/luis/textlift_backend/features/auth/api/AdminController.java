package com.luis.textlift_backend.features.auth.api;

import com.luis.textlift_backend.features.auth.api.dto.RegisterUserDto;
import com.luis.textlift_backend.features.auth.api.dto.RegisterUserResponseDto;
import com.luis.textlift_backend.features.auth.domain.User;
import com.luis.textlift_backend.features.auth.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {
    private final AuthenticationService authenticationService;
    public AdminController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value="")
    public ResponseEntity<RegisterUserResponseDto> createAdmin(
            @Valid @RequestBody RegisterUserDto registerUserRequestDto){
        User createdAdmin = authenticationService.createAdmin(registerUserRequestDto);
        return ResponseEntity.ok(new RegisterUserResponseDto(createdAdmin.getId(),
                createdAdmin.getUsername(),
                createdAdmin.getFullName()));
    }
}
