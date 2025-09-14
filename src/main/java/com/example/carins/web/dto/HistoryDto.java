package com.example.carins.web.dto;

import java.util.Map;

public record HistoryDto(String eventType, String eventDate, Map<String, Object> details) {}
