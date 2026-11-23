package com.example.myportfolio.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePatientRequest(@NotBlank String fullName) {}
