package com.example.carins.web.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PolicyDto(Long id, Long car_id, String provider, LocalDate startDate,
                        @NotNull(message = "End date must be provided") LocalDate endDate) {}
