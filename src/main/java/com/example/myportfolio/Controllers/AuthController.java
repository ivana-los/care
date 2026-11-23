package com.example.myportfolio.Controllers;

import com.example.myportfolio.dto.AuthResponse;
import com.example.myportfolio.dto.LoginRequest;
import com.example.myportfolio.dto.PairRequest;
import com.example.myportfolio.dto.RegisterRequest;
import com.example.myportfolio.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/pair")
    public AuthResponse pair(@Valid @RequestBody PairRequest req) {
        return authService.pairPatient(req);
    }
}
