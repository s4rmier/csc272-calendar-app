package com.appointmentcalendar;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UIManager {
    private AppointmentManager appointmentManager;
    private Scanner scanner;
    private DateTimeFormatter dateTimeFormatter;
    private static final Map<String, String> CATEGORIES = Map.of(
            "W", "Work",
            "P", "Personal",
            "M", "Medical",
            "O", "Other"
    );

    public UIManager(AppointmentManager appointmentManager) {
        this.appointmentManager = appointmentManager;
        this.scanner = new Scanner(System.in);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }

    public void start() {
        boolean running = true;
        while (running) {
            clearConsole();
            displayUpcomingAppointments();
            displayMainMenu();
            int choice = getUserChoice(1, 6);
            switch (choice) {
                case 1:
                    viewAllAppointments();
                    break;
                case 2:
                    viewAppointmentsByCategory();
                    break;
                case 3:
                    viewAppointmentsForDay();
                    break;
                case 4:
                    addNewAppointment();
                    break;
                case 5:
                    deleteAppointment();
                    break;
                case 6:
                    running = false;
                    System.out.println("Exiting the application. Goodbye!");
                    break;
            }
        }
        scanner.close();
    }

    private void displayUpcomingAppointments() {
        LocalDateTime today = LocalDateTime.now();
        List<Appointment> todaysAppointments = appointmentManager.getAppointmentsForDay(today);

        System.out.println("--- Upcoming Appointments ---");
        if (!todaysAppointments.isEmpty()) {
            System.out.println("Today's appointments:");
            for (Appointment app : todaysAppointments) {
                System.out.println(formatAppointment(app));
            }
        } else {
            Appointment nextAppointment = appointmentManager.getNextUpcomingAppointment();
            if (nextAppointment != null) {
                System.out.println("Next upcoming appointment:");
                System.out.println(formatAppointment(nextAppointment));
            } else {
                System.out.println("No upcoming appointments.");
            }
        }
        System.out.println("-----------------------------");
    }

    private String formatAppointment(Appointment app) {
        return String.format("%s - %s to %s: %s (%s)",
                app.getCode(),
                app.getStartTime().format(dateTimeFormatter),
                app.getEndTime().format(dateTimeFormatter),
                app.getDescription(),
                app.getCategory());
    }

    private void displayMainMenu() {
        System.out.println("\n--- Appointment Calendar ---");
        System.out.println("1. View all appointments");
        System.out.println("2. View appointments by category");
        System.out.println("3. View appointments for a specific day");
        System.out.println("4. Add new appointment");
        System.out.println("5. Delete appointment");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private int getUserChoice(int min, int max) {
        while (true) {
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.print("Invalid choice. Please enter a number between " + min + " and " + max + ": ");
                }
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private void viewAllAppointments() {
        List<Appointment> appointments = appointmentManager.getAllAppointments();
        displayAppointmentsPaginated(appointments, false);
    }

    private void displayAppointmentsPaginated(List<Appointment> appointments, boolean isDeleteMode) {
        int pageSize = 10;
        int totalPages = (int) Math.ceil((double) appointments.size() / pageSize);
        int currentPage = 1;

        while (true) {
            clearConsole();
            displayAppointmentsPage(appointments, currentPage, totalPages);
            displayViewMenu();

            String choice = scanner.nextLine().toUpperCase();
            switch (choice) {
                case "P":
                    if (currentPage > 1) currentPage--;
                    break;
                case "N":
                    if (currentPage < totalPages) currentPage++;
                    break;
                case "D":
                    performDelete();
                    appointments = appointmentManager.getAllAppointments(); // Refresh the list
                    totalPages = (int) Math.ceil((double) appointments.size() / pageSize);
                    currentPage = Math.min(currentPage, totalPages);
                    if (appointments.isEmpty()) {
                        System.out.println("No more appointments to delete.");
                        System.out.print("Press Enter to return to main menu...");
                        scanner.nextLine();
                        return;
                    }
                    break;
                case "Q":
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    System.out.print("Press Enter to continue...");
                    scanner.nextLine();
            }
        }
    }

    private void displayAppointmentsPage(List<Appointment> appointments, int currentPage, int totalPages) {
        System.out.println("--- All Appointments (Page " + currentPage + " of " + totalPages + ") ---");
        int pageSize = 10;
        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, appointments.size());

        for (int i = start; i < end; i++) {
            Appointment app = appointments.get(i);
            System.out.printf("%d. [%s] %s - %s: %s (%s)%n",
                    i + 1, app.getCode(), app.getStartTime().format(dateTimeFormatter),
                    app.getEndTime().format(dateTimeFormatter), app.getDescription(), app.getCategory());
        }
        System.out.println();
    }

    private void displayViewMenu() {
        System.out.println("Menu Options:");
        System.out.println("P - Previous page");
        System.out.println("N - Next page");
        System.out.println("D - Delete an appointment");
        System.out.println("Q - Quit to main menu");
        System.out.print("Enter your choice: ");
    }

    private void viewAppointmentsByCategory() {
        clearConsole();
        System.out.println("--- View Appointments by Category ---");
        String categoryCode = getCategoryInput();
        if (categoryCode == null) return;  // User quit to main menu

        String category = CATEGORIES.get(categoryCode);
        List<Appointment> appointments = appointmentManager.getAllAppointments();
        List<Appointment> filteredAppointments = appointments.stream()
                .filter(app -> app.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        if (filteredAppointments.isEmpty()) {
            System.out.println("No appointments found for category: " + category);
            System.out.print("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        displayAppointmentsPaginated(filteredAppointments, false);
    }

    private void viewAppointmentsForDay() {
        clearConsole();
        System.out.println("--- Appointments for a Specific Day ---");
        LocalDateTime date = getDateInput("Enter date");
        List<Appointment> appointments = appointmentManager.getAppointmentsForDay(date);
        displayAppointmentsPaginated(appointments, false);
    }

    private LocalDateTime getDateInput(String prompt) {
        while (true) {
            System.out.print(prompt + " (yyyy-MM-dd): ");
            String input = scanner.nextLine();
            try {
                return LocalDate.parse(input, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
    }

    private void addNewAppointment() {
        clearConsole();
        System.out.println("--- Add New Appointment ---");
        System.out.println("(Enter 'cancel' at any prompt to abort)");

        LocalDate date = getDateInputWithCancel("Enter date");
        if (date == null) return;

        LocalTime startTime = getTimeInputWithCancel("Enter start time");
        if (startTime == null) return;

        LocalTime endTime = getTimeInputWithCancel("Enter end time");
        if (endTime == null) return;

        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        if (description.equalsIgnoreCase("cancel")) return;

        String categoryCode = getCategoryInput();
        if (categoryCode == null) return;  // User quit to main menu

        String category = CATEGORIES.get(categoryCode);
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

        String code = appointmentManager.generateAppointmentCode(categoryCode);
        Appointment appointment = new Appointment(code, startDateTime, endDateTime, description, category);
        appointmentManager.addAppointment(appointment);

        System.out.println("Appointment added successfully. Code: " + code);
        System.out.print("Press Enter to return to main menu...");
        scanner.nextLine();
    }

    private LocalDate getDateInputWithCancel(String prompt) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            System.out.print(prompt + " (yyyy-MM-dd): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("cancel")) {
                return null;
            }
            try {
                return LocalDate.parse(input, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
    }

    private LocalTime getTimeInputWithCancel(String prompt) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        while (true) {
            System.out.print(prompt + " (HH:mm): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("cancel")) {
                return null;
            }
            try {
                return LocalTime.parse(input, timeFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Please use HH:mm.");
            }
        }
    }

    private String getCategoryInput() {
        while (true) {
            System.out.println("Select a category:");
            for (Map.Entry<String, String> entry : CATEGORIES.entrySet()) {
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }
            System.out.print("Enter category code (or 'Q' to Quit to main menu): ");
            String input = scanner.nextLine().toUpperCase();

            if (input.equals("Q")) {
                return null;  // User wants to return to main menu
            }

            if (CATEGORIES.containsKey(input)) {
                return input;
            } else {
                System.out.println("Invalid category code. Please try again.");
            }
        }
    }

    private void deleteAppointment() {
        List<Appointment> appointments = appointmentManager.getAllAppointments();
        if (appointments.isEmpty()) {
            System.out.println("There are no appointments to delete.");
            System.out.print("Press Enter to continue...");
            scanner.nextLine();
            return;
        }

        displayAppointmentsPaginated(appointments, true);
    }

    private void performDelete() {
        System.out.print("Enter appointment code to delete: ");
        String code = scanner.nextLine();
        boolean deleted = appointmentManager.deleteAppointment(code);
        if (deleted) {
            System.out.println("Appointment deleted successfully.");
        } else {
            System.out.println("Appointment not found.");
        }
        System.out.print("Press Enter to continue...");
        scanner.nextLine();
    }
}