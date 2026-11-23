package com.example.myportfolio.config;


import com.example.myportfolio.Role;

public record AuthUser(Long userId, Long familyId, Role role) {}
