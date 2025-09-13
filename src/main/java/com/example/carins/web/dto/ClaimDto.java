package com.example.carins.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ClaimDto(Long id, @NotNull Long car_id, @NotNull String description, @NotNull LocalDate claimDate, @NotNull double amount) {}
