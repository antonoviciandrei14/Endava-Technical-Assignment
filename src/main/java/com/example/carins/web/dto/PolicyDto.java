package com.example.carins.web.dto;

import java.time.LocalDate;

public record PolicyDto(Long id, Long car_id, String provider, LocalDate startDate, LocalDate endDate) {
}
