package com.example.myportfolio.Controllers;

import com.example.myportfolio.config.AuthUser;
import com.example.myportfolio.dto.CreatePatientRequest;
import com.example.myportfolio.dto.MemberDto;
import com.example.myportfolio.dto.PairingResponse;
import com.example.myportfolio.service.FamilyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/family")
@RequiredArgsConstructor
public class FamilyController {
    private final FamilyService familyService;

    @PostMapping("/patient")
    public PairingResponse createPatient(@AuthenticationPrincipal AuthUser me,
                                         @Valid @RequestBody CreatePatientRequest req) {
        return familyService.createPatient(me, req);
    }

    @PostMapping("/patient/{id}/pairing-code")
    public PairingResponse newCode(@AuthenticationPrincipal AuthUser me, @PathVariable Long id) {
        return familyService.newPairingCode(me, id);
    }

    @GetMapping("/members")
    public List<MemberDto> members(@AuthenticationPrincipal AuthUser me) {
        return familyService.members(me);
    }
}