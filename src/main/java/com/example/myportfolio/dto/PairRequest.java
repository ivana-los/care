package com.example.myportfolio.dto;

import jakarta.validation.constraints.NotBlank;

public record PairRequest(@NotBlank String code) {}