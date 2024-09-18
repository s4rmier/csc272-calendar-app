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

    /**
     * Constructs a UIManager with the given AppointmentManager.
     * @param appointmentManager The AppointmentManager to be used for managing appointments.
     */
    public UIManager(AppointmentManager appointmentManager) {
        this.appointmentManager = appointmentManager;
        this.scanner = new Scanner(System.in);
        this.dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    }

    /**
     * Starts the main application loop, displaying the menu and processing user choices.
     */
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

    /**
     * Displays upcoming appointments for today or the next available day.
     */
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

    /**
     * Formats an appointment for display.
     * @param app The appointment to format.
     * @return A formatted string representation of the appointment.
     */
    private String formatAppointment(Appointment app) {
        return String.format("%s - %s to %s: %s (%s)",
                app.getCode(),
                app.getStartTime().format(dateTimeFormatter),
                app.getEndTime().format(dateTimeFormatter),
                app.getDescription(),
                app.getCategory());
    }

    /**
     * Displays the main menu options.
     */
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

    /**
     * Clears the console screen.
     */
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    /**
     * Gets user input as an integer within a specified range.
     * @param min The minimum valid value.
     * @param max The maximum valid value.
     * @return The user's choice as an integer.
     */
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

    /**
     * Displays all appointments in a paginated format.
     */
    private void viewAllAppointments() {
        List<Appointment> appointments = appointmentManager.getAllAppointments();
        displayAppointmentsPaginated(appointments, false);
    }

    /**
     * Displays appointments in a paginated format with options to navigate, delete, or quit.
     * @param appointments The list of appointments to display.
     * @param isDeleteMode Whether the display is in delete mode.
     */
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

    /**
     * Displays a single page of appointments.
     * @param appointments The list of appointments to display.
     * @param currentPage The current page number.
     * @param totalPages The total number of pages.
     */
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

    /**
     * Displays the menu options for viewing appointments.
     */
    private void displayViewMenu() {
        System.out.println("Menu Options:");
        System.out.println("P - Previous page");
        System.out.println("N - Next page");
        System.out.println("D - Delete an appointment");
        System.out.println("Q - Quit to main menu");
        System.out.print("Enter your choice: ");
    }

    /**
     * Allows the user to view appointments by category.
     */
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

    /**
     * Allows the user to view appointments for a specific day.
     */
    private void viewAppointmentsForDay() {
        clearConsole();
        System.out.println("--- Appointments for a Specific Day ---");
        LocalDateTime date = getDateInput("Enter date");
        List<Appointment> appointments = appointmentManager.getAppointmentsForDay(date);
        displayAppointmentsPaginated(appointments, false);
    }

    /**
     * Gets a date input from the user.
     * @param prompt The prompt to display to the user.
     * @return The entered date as a LocalDateTime.
     */
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

    /**
     * Allows the user to add a new appointment with input validation.
     */
    private void addNewAppointment() {
        clearConsole();
        System.out.println("--- Add New Appointment ---");
        System.out.println("(Enter 'cancel' at any prompt to abort)");

        LocalDate date = getValidDateInput("Enter date");
        if (date == null) return;

        LocalTime startTime = getValidTimeInput("Enter start time", date, null);
        if (startTime == null) return;

        LocalTime endTime = getValidTimeInput("Enter end time", date, startTime);
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

    /**
     * Gets a valid date input from the user, ensuring it's not in the past.
     * @param prompt The prompt to display to the user.
     * @return The entered date as a LocalDate, or null if the user cancels.
     */
    private LocalDate getValidDateInput(String prompt) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while (true) {
            System.out.print(prompt + " (yyyy-MM-dd): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("cancel")) {
                return null;
            }
            try {
                LocalDate date = LocalDate.parse(input, dateFormatter);
                if (date.isBefore(LocalDate.now())) {
                    System.out.println("Error: Date cannot be in the past. Please enter a future or today's date.");
                } else {
                    return date;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use yyyy-MM-dd.");
            }
        }
    }

    /**
     * Gets a valid time input from the user, ensuring it's after the start time if it's an end time.
     * @param prompt The prompt to display to the user.
     * @param startTime The start time to compare against, or null if this is a start time input.
     * @return The entered time as a LocalTime, or null if the user cancels.
     */
    private LocalTime getValidTimeInput(String prompt, LocalDate date, LocalTime startTime) {
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        LocalDateTime now = LocalDateTime.now();
        while (true) {
            System.out.print(prompt + " (HH:mm): ");
            String input = scanner.nextLine();
            if (input.equalsIgnoreCase("cancel")) {
                return null;
            }
            try {
                LocalTime time = LocalTime.parse(input, timeFormatter);
                LocalDateTime dateTime = LocalDateTime.of(date, time);
                if (dateTime.isBefore(now)) {
                    System.out.println("Error: Time cannot be in the past. Please enter a future time.");
                } else if (startTime != null && time.isBefore(startTime)) {
                    System.out.println("Error: End time must be after the start time.");
                } else {
                    return time;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Please use HH:mm.");
            }
        }
    }

    /**
     * Gets a category input from the user.
     * @return The selected category code, or null if the user quits to the main menu.
     */
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
    /**
     * Allows the user to delete an appointment.
     */
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

    /**
     * Performs the deletion of an appointment based on user input.
     */
    private void performDelete() {
        System.out.print("Enter appointment code to delete: ");
        String code = scanner.nextLine().trim(); // Trim any leading or trailing whitespace
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