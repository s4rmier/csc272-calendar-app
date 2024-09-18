package com.appointmentcalendar;

import java.util.List;

public class AppointmentCalendar {
    public static void main(String[] args) {
        System.out.println("Welcome to the Appointment Calendar Application!");

        AppointmentManager appointmentManager = new AppointmentManager();
        List<Appointment> loadedAppointments = FileManager.loadAppointments();
        appointmentManager.setAppointments(loadedAppointments);

        UIManager uiManager = new UIManager(appointmentManager);
        uiManager.start();

        FileManager.saveAppointments(appointmentManager.getAllAppointments());
    }
}