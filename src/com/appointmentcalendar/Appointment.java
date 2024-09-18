/**
 * Represents an individual appointment in the calendar.
 * Encapsulates all the details of an appointment, including its unique code, start and end times, description, and category.
 */
package com.appointmentcalendar;

import java.time.LocalDateTime;

public class Appointment {
    private String code;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private String category;

    /**
     * Constructs a new Appointment with the given details.
     */
    public Appointment(String code, LocalDateTime startTime, LocalDateTime endTime, String description, String category) {
        this.code = code;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.category = category;
    }

    /**
     * Returns the unique code of the appointment.
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the start time of the appointment.
     */
    public LocalDateTime getStartTime() {
        return startTime;
    }

    /**
     * Returns the end time of the appointment.
     */
    public LocalDateTime getEndTime() {
        return endTime;
    }

    /**
     * Returns the description of the appointment.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the category of the appointment.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the start time of the appointment.
     */
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    /**
     * Sets the end time of the appointment.
     */
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    /**
     * Sets the description of the appointment.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the category of the appointment.
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Returns the category code (first letter of the appointment code).
     */
    public String getCategoryCode() {
        return code.substring(0, 1);
    }

    /**
     * Returns a string representation of the appointment.
     */
    @Override
    public String toString() {
        return String.format("Appointment [%s] %s - %s: %s (%s)",
                code, startTime, endTime, description, category);
    }
}