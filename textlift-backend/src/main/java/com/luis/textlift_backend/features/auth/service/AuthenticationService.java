package com.luis.textlift_backend.features.auth.service;

import com.luis.textlift_backend.features.auth.api.dto.LoginUserDto;
import com.luis.textlift_backend.features.auth.api.dto.RegisterUserDto;
import com.luis.textlift_backend.features.auth.domain.Role;
import com.luis.textlift_backend.features.auth.domain.RoleEnum;
import com.luis.textlift_backend.features.auth.domain.User;
import com.luis.textlift_backend.features.auth.repository.RoleRepository;
import com.luis.textlift_backend.features.auth.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.springframework.http.HttpStatus.CONFLICT;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final RoleRepository roleRepository;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder, RoleRepository roleRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public User signup(RegisterUserDto input) {
        User user = new User();
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.USER);
        if(optionalRole.isEmpty()){
            throw new ResponseStatusException(CONFLICT, "Could not find user role");
        }
        user.setFullName(input.fullName());

        //Check email uniqueness
        if (userRepository.findByEmail(input.email()).isPresent()) {
            throw new ResponseStatusException(CONFLICT, "Email is already in use");
        }

        user.setEmail(input.email());
        user.setPassword(passwordEncoder.encode(input.password()));
        user.setEnabled(true);
        user.setRole(optionalRole.get());

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {

        //given the users creds, authenticate them
        //This triggers DaoAuthenticationProvider, which calls on our UserDetailsService
        //which loads user from DB, and compares their raw password to our Bcrypt hash
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.email(),
                        input.password()
                )
        );

        //Once authenticated, return the user obj via their email
        return userRepository.findByEmail(input.email())
                .orElseThrow();
    }

    public User createAdmin(RegisterUserDto input){
        Optional<Role> optionalRole = roleRepository.findByName(RoleEnum.ADMIN);
        if(optionalRole.isEmpty()){
            throw new ResponseStatusException(CONFLICT, "Could not find admin role");
        }
        User user = new User();
        user.setFullName(input.fullName());
        user.setEmail(input.email());
        user.setPassword(passwordEncoder.encode(input.password()));
        user.setEnabled(true);
        user.setRole(optionalRole.get());
        return userRepository.save(user);
    }
}
