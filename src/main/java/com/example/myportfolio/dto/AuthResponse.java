package com.example.myportfolio.dto;

import com.example.myportfolio.Role;

public record AuthResponse(String token, Role role, Long userId, Long familyId,
                           String fullName, String inviteCode) {}