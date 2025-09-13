package com.nab.account_service.controller;

import com.nab.account_service.dto.AuthenticationRequest;
import com.nab.account_service.dto.AuthenticationResponse;
import com.nab.account_service.dto.SignupRequest;
import com.nab.account_service.model.User;
import com.nab.account_service.security.JwtTokenUtil;
import com.nab.account_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationProvider authenticationProvider;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody SignupRequest request) {
        User user = userService.registerUser(request);
        String token = jwtTokenUtil.generateToken(user.getEmail());
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest request) {
        Authentication authentication = authenticationProvider.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        String token = jwtTokenUtil.generateToken(request.getEmail());
        return ResponseEntity.ok(new AuthenticationResponse(token));
    }
}
