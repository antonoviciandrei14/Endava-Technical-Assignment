package com.example.carins.web.dto;

import java.time.LocalDate;

public record CarDto(Long id, String vin, String make, String model, int year, LocalDate purchaseDate, Long ownerId, String ownerName, String ownerEmail) {}
