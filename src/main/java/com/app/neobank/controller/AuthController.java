package com.app.neobank.controller;

import com.app.neobank.security.JwtService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    // DTO interno para las credenciales
    public record LoginRequest(String email, String password) {}

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        // "hardcodeado" de un usuario admin
        if ("franco@neobank.com".equals(request.email()) && "admin123".equals(request.password())) {

            // Credenciales válidas (Token)
            String token = jwtService.generarToken(request.email());
            return Map.of("token", token);

        } else {
            throw new RuntimeException("Credenciales inválidas. Acceso denegado.");
        }
    }
}