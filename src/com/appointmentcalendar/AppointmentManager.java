/**
 * Manages the collection of appointments and provides methods for
 * manipulating and querying appointment data.
 */
package com.appointmentcalendar;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class AppointmentManager {
    private List<Appointment> appointments;
    private Map<String, Integer> categoryCounters;

    /**
     * Initializes a new AppointmentManager with an empty list of appointments
     * and initializes category counters.
     */
    public AppointmentManager() {
        this.appointments = new ArrayList<>();
        this.categoryCounters = new HashMap<>();
        categoryCounters.put("W", 0);
        categoryCounters.put("P", 0);
        categoryCounters.put("M", 0);
        categoryCounters.put("O", 0);
    }

    /**
     * Adds a new appointment to the collection.
     */
    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    /**
     * Returns a new list containing all appointments.
     */
    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
    }

    /**
     * Returns a list of appointments for a specific day.
     */
    public List<Appointment> getAppointmentsForDay(LocalDateTime date) {
        return appointments.stream()
                .filter(app -> app.getStartTime().toLocalDate().equals(date.toLocalDate()))
                .collect(Collectors.toList());
    }

    /**
     * Returns the next upcoming appointment after the current time.
     */
    public Appointment getNextUpcomingAppointment() {
        LocalDateTime now = LocalDateTime.now();
        return appointments.stream()
                .filter(app -> app.getStartTime().isAfter(now))
                .min((a1, a2) -> a1.getStartTime().compareTo(a2.getStartTime()))
                .orElse(null);
    }

    /**
     * Returns a new list of all appointments sorted chronologically by start time.
     * This method does not modify the original list of appointments.
     *
     * @return A new ArrayList containing all appointments sorted by start time.
     */
    public List<Appointment> getSortedAppointments() {
        List<Appointment> sortedList = new ArrayList<>(appointments);
        sortedList.sort(Comparator.comparing(Appointment::getStartTime));
        return sortedList;
    }

    /**
     * Deletes an appointment with the given code.
     * Returns true if an appointment was deleted, false otherwise.
     */
    public boolean deleteAppointment(String code) {
        return appointments.removeIf(app -> app.getCode().equalsIgnoreCase(code));
    }

    /**
     * Sets the list of appointments and resets category counters.
     */
    public void setAppointments(List<Appointment> appointments) {
        this.appointments = new ArrayList<>(appointments);
        resetCategoryCounters();
    }

    /**
     * Resets the category counters based on the current appointments.
     */
    private void resetCategoryCounters() {
        for (String key : categoryCounters.keySet()) {
            categoryCounters.put(key, 0);
        }
        for (Appointment app : appointments) {
            String categoryCode = app.getCode().substring(0, 1);
            categoryCounters.put(categoryCode, categoryCounters.get(categoryCode) + 1);
        }
    }

    /**
     * Generates a unique appointment code for a given category.
     */
    public String generateAppointmentCode(String category) {
        String categoryCode = category.substring(0, 1).toUpperCase();
        int counter = categoryCounters.getOrDefault(categoryCode, 0) + 1;
        categoryCounters.put(categoryCode, counter);

        String code;
        do {
            code = String.format("%s%03d", categoryCode, counter);
            counter++;
        } while (isCodeTaken(code));

        return code;
    }

    /**
     * Checks if a given appointment code is already in use.
     */
    private boolean isCodeTaken(String code) {
        return appointments.stream().anyMatch(app -> app.getCode().equals(code));
    }
}