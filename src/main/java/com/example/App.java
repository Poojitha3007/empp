
package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Employee {
    private final String employeeId;
    private final String name;
    private final int age;
    private final String department;
    private final String employmentType;
    private final boolean active;
    private final int securityClearance;
    private final boolean idValid;

    public Employee(String employeeId, String name, int age,
                   String department, String employmentType,
                   boolean active, int securityClearance,
                   boolean idValid) {

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Employee ID cannot be empty");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }

        if (age < 0 || age > 120) {
            throw new IllegalArgumentException("Age must be between 0 and 120");
        }

        if (securityClearance < 0 || securityClearance > 3) {
            throw new IllegalArgumentException(
                    "Security clearance must be between 0 and 3");
        }

        this.employeeId = employeeId.trim();
        this.name = name.trim();
        this.age = age;
        this.department = department;
        this.employmentType = employmentType;
        this.active = active;
        this.securityClearance = securityClearance;
        this.idValid = idValid;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDepartment() {
        return department;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public boolean isActive() {
        return active;
    }

    public int getSecurityClearance() {
        return securityClearance;
    }

    public boolean isIdValid() {
        return idValid;
    }
}

class EligibilityResult {
    private final String classification;
    private final List<String> reasons;

    public EligibilityResult(String classification, List<String> reasons) {
        this.classification = classification;
        this.reasons = new ArrayList<>(reasons);
    }

    public String getClassification() {
        return classification;
    }

    public List<String> getReasons() {
        return new ArrayList<>(reasons);
    }
}

class EmployeeAccessService {

    private static final List<String> AUTHORIZED_DEPARTMENTS =
            List.of("IT", "HR", "FINANCE", "ADMINISTRATION");

    public EligibilityResult checkEligibility(
            Employee employee, int requestedAccessLevel) {

        if (employee == null) {
            throw new IllegalArgumentException("Employee cannot be null");
        }

        if (requestedAccessLevel < 0 || requestedAccessLevel > 3) {
            throw new IllegalArgumentException(
                    "Requested access level must be between 0 and 3");
        }

        List<String> reasons = new ArrayList<>();

        // Check every basic eligibility condition.
        if (employee.getAge() < 21) {
            reasons.add("Employee must be at least 21 years old.");
        }

        String department = employee.getDepartment();

        if (department == null ||
                !AUTHORIZED_DEPARTMENTS.contains(
                        department.trim().toUpperCase())) {
            reasons.add("Employee belongs to an unauthorized department.");
        }

        if (!employee.isActive()) {
            reasons.add("Employee employment status is not active.");
        }

        if (!employee.isIdValid()) {
            reasons.add("Employee ID is invalid or expired.");
        }

        // Any basic failure makes the employee Not Eligible.
        if (!reasons.isEmpty()) {
            return new EligibilityResult("Not Eligible", reasons);
        }

        // Clearance is checked separately.
        if (employee.getSecurityClearance() < requestedAccessLevel) {
            reasons.add("Insufficient security clearance. Required level: "
                    + requestedAccessLevel
                    + ", employee level: "
                    + employee.getSecurityClearance());

            return new EligibilityResult(
                    "Conditionally Eligible", reasons);
        }

        return new EligibilityResult("Eligible", reasons);
    }
}

public class App {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        EmployeeAccessService service = new EmployeeAccessService();

        System.out.println("EMPLOYEE ACCESS ELIGIBILITY SYSTEM");

        int employeeCount = readInt(
                "Enter number of employees: ", 1, 100);

        for (int i = 1; i <= employeeCount; i++) {

            System.out.println("\nEnter details for Employee " + i);

            try {
                String id = readText("Employee ID: ");
                String name = readText("Name: ");
                int age = readInt("Age: ", 0, 120);
                String department = readText("Department: ");
                String employmentType = readText(
                        "Employment type (Permanent/Contract/Temporary): ");

                boolean active = readBoolean(
                        "Is employment active? (yes/no): ");

                int clearance = readInt(
                        "Security clearance (0-3): ", 0, 3);

                boolean idValid = readBoolean(
                        "Is employee ID valid? (yes/no): ");

                int requestedLevel = readInt(
                        "Requested access level (0-3): ", 0, 3);

                Employee employee = new Employee(
                        id, name, age, department, employmentType,
                        active, clearance, idValid);

                EligibilityResult result =
                        service.checkEligibility(employee, requestedLevel);

                System.out.println("\n----- ACCESS RESULT -----");
                System.out.println("Employee ID: " + id);
                System.out.println("Employee Name: " + name);
                System.out.println("Classification: "
                        + result.getClassification());

                if (!result.getReasons().isEmpty()) {
                    System.out.println("Reasons:");

                    for (String reason : result.getReasons()) {
                        System.out.println("- " + reason);
                    }
                } else {
                    System.out.println("All eligibility requirements satisfied.");
                }

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid employee details: " + e.getMessage());
                System.out.println("Please enter this employee's details again.");

                // Repeat the current employee, not the entire application.
                i--;
            }
        }

        scanner.close();
        System.out.println("\nEligibility checking completed.");
    }

    private static String readText(String message) {
        System.out.print(message);
        String value = scanner.nextLine().trim();

        if (value.isEmpty()) {
            throw new IllegalArgumentException("Input cannot be empty.");
        }

        return value;
    }

    private static int readInt(String message, int min, int max) {
        System.out.print(message);
        String input = scanner.nextLine().trim();

        try {
            int value = Integer.parseInt(input);

            if (value < min || value > max) {
                throw new IllegalArgumentException(
                        "Value must be between " + min + " and " + max);
            }

            return value;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Enter a valid integer.");
        }
    }

    private static boolean readBoolean(String message) {
        System.out.print(message);
        String input = scanner.nextLine().trim().toLowerCase();

        if (input.equals("yes") || input.equals("y")) {
            return true;
        }

        if (input.equals("no") || input.equals("n")) {
            return false;
        }

        throw new IllegalArgumentException(
                "Enter yes or no.");
    }
}
