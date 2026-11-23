package com.example.myportfolio.service;

import com.example.myportfolio.Family;
import com.example.myportfolio.Role;
import com.example.myportfolio.User;
import com.example.myportfolio.config.AuthUser;
import com.example.myportfolio.dto.CreatePatientRequest;
import com.example.myportfolio.dto.MemberDto;
import com.example.myportfolio.dto.PairingResponse;
import com.example.myportfolio.repository.FamilyRepository;
import com.example.myportfolio.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FamilyService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int CODE_MINUTES = 15;

    private final UserRepository userRepo;
    private final FamilyRepository familyRepo;

    @Transactional
    public PairingResponse createPatient(AuthUser me, CreatePatientRequest req) {
        Family family = familyRepo.getReferenceById(me.familyId());

        User patient = new User();
        patient.setFullName(req.fullName());
        patient.setRole(Role.PATIENT);
        patient.setFamily(family);
        assignPairingCode(patient);
        userRepo.save(patient);

        return new PairingResponse(patient.getId(), patient.getPairingCode(), CODE_MINUTES);
    }

    @Transactional
    public PairingResponse newPairingCode(AuthUser me, Long patientId) {
        User patient = userRepo.findById(patientId)
                .filter(u -> u.getRole() == Role.PATIENT)
                .filter(u -> u.getFamily().getId().equals(me.familyId())) // samo svoja familija!
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        assignPairingCode(patient);
        return new PairingResponse(patient.getId(), patient.getPairingCode(), CODE_MINUTES);
    }

    public List<MemberDto> members(AuthUser me) {
        return userRepo.findByFamilyId(me.familyId()).stream()
                .map(u -> new MemberDto(u.getId(), u.getFullName(), u.getRole()))
                .toList();
    }

    private void assignPairingCode(User patient) {
        String code;
        do {
            code = String.format("%06d", RANDOM.nextInt(1_000_000));
        } while (userRepo.findByPairingCode(code).isPresent());
        patient.setPairingCode(code);
        patient.setPairingExpiresAt(Instant.now().plus(CODE_MINUTES, ChronoUnit.MINUTES));
    }
}
