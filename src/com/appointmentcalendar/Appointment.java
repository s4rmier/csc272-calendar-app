package com.appointmentcalendar;

import java.time.LocalDateTime;

public class Appointment {
    private String code;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private String category;

    public Appointment(String code, LocalDateTime startTime, LocalDateTime endTime, String description, String category) {
        this.code = code;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryCode() {
        return code.substring(0, 1);
    }

    @Override
    public String toString() {
        return String.format("Appointment [%s] %s - %s: %s (%s)",
                code, startTime, endTime, description, category);
    }
}