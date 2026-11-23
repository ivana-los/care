package com.example.myportfolio.service;

import com.example.myportfolio.Family;
import com.example.myportfolio.Role;
import com.example.myportfolio.User;
import com.example.myportfolio.config.JwtService;
import com.example.myportfolio.dto.AuthResponse;
import com.example.myportfolio.dto.LoginRequest;
import com.example.myportfolio.dto.PairRequest;
import com.example.myportfolio.dto.RegisterRequest;
import com.example.myportfolio.repository.FamilyRepository;
import com.example.myportfolio.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private final UserRepository userRepo;
    private final FamilyRepository familyRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.email().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email vekje postoi");
        }

        Family family;
        if (req.inviteCode() != null && !req.inviteCode().isBlank()) {
            family = familyRepo.findByInviteCode(req.inviteCode().trim().toUpperCase())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Nevaliden invite kod"));
        } else {
            if (req.familyName() == null || req.familyName().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Vnesi ime na familija ili invite kod");
            }
            family = new Family();
            family.setName(req.familyName());
            family.setInviteCode(generateUniqueInviteCode());
            familyRepo.save(family);
        }

        User user = new User();
        user.setFullName(req.fullName());
        user.setEmail(req.email().toLowerCase());
        user.setPasswordHash(encoder.encode(req.password()));
        user.setRole(Role.FAMILY);
        user.setFamily(family);
        userRepo.save(user);

        return toResponse(user);
    }
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        User user = userRepo.findByEmail(req.email().toLowerCase())
                .filter(u -> u.getPasswordHash() != null
                        && encoder.matches(req.password(), u.getPasswordHash()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Pogreshen email ili lozinka"));
        return toResponse(user);
    }


    @Transactional
    public AuthResponse pairPatient(PairRequest req) {
        User patient = userRepo.findByPairingCode(req.code().trim())
                .filter(u -> u.getRole() == Role.PATIENT)
                .filter(u -> u.getPairingExpiresAt() != null
                        && u.getPairingExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Kodot e nevaliden ili istechen"));


        patient.setPairingCode(null);
        patient.setPairingExpiresAt(null);
        return toResponse(patient);
    }

    private AuthResponse toResponse(User u) {
        String invite = u.getRole() == Role.FAMILY ? u.getFamily().getInviteCode() : null;
        return new AuthResponse(jwt.generate(u), u.getRole(), u.getId(),
                u.getFamily().getId(), u.getFullName(), invite);
    }

    private String generateUniqueInviteCode() {
        String code;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) sb.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
            code = sb.toString();
        } while (familyRepo.existsByInviteCode(code));
        return code;
    }
}
