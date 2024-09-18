package com.appointmentcalendar;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

public class AppointmentManager {
    private List<Appointment> appointments;
    private Map<String, Integer> categoryCounters;

    public AppointmentManager() {
        this.appointments = new ArrayList<>();
        this.categoryCounters = new HashMap<>();
        categoryCounters.put("W", 0);
        categoryCounters.put("P", 0);
        categoryCounters.put("M", 0);
        categoryCounters.put("O", 0);
    }

    public void addAppointment(Appointment appointment) {
        appointments.add(appointment);
    }

    public List<Appointment> getAllAppointments() {
        return new ArrayList<>(appointments);
    }

    public List<Appointment> getAppointmentsForDay(LocalDateTime date) {
        return appointments.stream()
                .filter(app -> app.getStartTime().toLocalDate().equals(date.toLocalDate()))
                .collect(Collectors.toList());
    }

    public Appointment getNextUpcomingAppointment() {
        LocalDateTime now = LocalDateTime.now();
        return appointments.stream()
                .filter(app -> app.getStartTime().isAfter(now))
                .min((a1, a2) -> a1.getStartTime().compareTo(a2.getStartTime()))
                .orElse(null);
    }

    public boolean deleteAppointment(String code) {
        return appointments.removeIf(app -> app.getCode().equals(code));
    }

    public void setAppointments(List<Appointment> appointments) {
        this.appointments = new ArrayList<>(appointments);
        resetCategoryCounters();
    }

    private void resetCategoryCounters() {
        for (String key : categoryCounters.keySet()) {
            categoryCounters.put(key, 0);
        }
        for (Appointment app : appointments) {
            String categoryCode = app.getCode().substring(0, 1);
            categoryCounters.put(categoryCode, categoryCounters.get(categoryCode) + 1);
        }
    }

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

    private boolean isCodeTaken(String code) {
        return appointments.stream().anyMatch(app -> app.getCode().equals(code));
    }
}