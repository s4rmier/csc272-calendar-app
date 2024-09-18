/**
 * Manages the persistence of appointment data to and from a text file.
 * Handles saving appointments to a file and loading them back into the application.
 */
package com.appointmentcalendar;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String FILE_NAME = "appointments.txt";
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Saves the list of appointments to a text file.
     */
    public static void saveAppointments(List<Appointment> appointments) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (Appointment app : appointments) {
                writer.println(String.format("%s|%s|%s|%s|%s",
                        app.getCode(),
                        app.getStartTime().format(dateFormatter),
                        app.getStartTime().format(timeFormatter),
                        app.getEndTime().format(timeFormatter),
                        app.getDescription()));
            }
        } catch (IOException e) {
            System.err.println("Error saving appointments: " + e.getMessage());
        }
    }

    /**
     * Loads appointments from the text file.
     * Returns a list of all loaded appointments.
     */
    public static List<Appointment> loadAppointments() {
        List<Appointment> appointments = new ArrayList<>();
        File file = new File(FILE_NAME);

        if (!file.exists()) {
            return appointments;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    try {
                        LocalDateTime startDateTime = LocalDateTime.parse(parts[1] + "T" + parts[2]);
                        LocalDateTime endDateTime = LocalDateTime.parse(parts[1] + "T" + parts[3]);
                        Appointment app = new Appointment(
                                parts[0],
                                startDateTime,
                                endDateTime,
                                parts[4],
                                getCategoryFromCode(parts[0]));
                        appointments.add(app);
                    } catch (DateTimeParseException e) {
                        System.err.println("Error parsing date/time: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading appointments: " + e.getMessage());
        }
        return appointments;
    }

    /**
     * Determines the category of an appointment based on its code.
     */
    private static String getCategoryFromCode(String code) {
        switch (code.charAt(0)) {
            case 'W': return "Work";
            case 'P': return "Personal";
            case 'M': return "Medical";
            case 'O': return "Other";
            default: return "Unknown";
        }
    }
}