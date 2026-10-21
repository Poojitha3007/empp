
package com.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    private final EmployeeAccessService service =
            new EmployeeAccessService();

    private Employee createEmployee(
            int age, String department, boolean active,
            int clearance, boolean idValid) {

        return new Employee(
                "EMP101",
                "Ravi",
                age,
                department,
                "Permanent",
                active,
                clearance,
                idValid
        );
    }

    @Test
    void eligibleEmployeeTest() {
        Employee employee =
                createEmployee(25, "IT", true, 3, true);

        EligibilityResult result =
                service.checkEligibility(employee, 2);

        assertEquals("Eligible", result.getClassification());
        assertTrue(result.getReasons().isEmpty());
    }

    @Test
    void ageBoundaryTest() {
        // Exactly 21 years is allowed.
        Employee employee =
                createEmployee(21, "HR", true, 2, true);

        EligibilityResult result =
                service.checkEligibility(employee, 2);

        assertEquals("Eligible", result.getClassification());
    }

    @Test
    void belowAgeBoundaryTest() {
        // Age 20 must be rejected.
        Employee employee =
                createEmployee(20, "IT", true, 3, true);

        EligibilityResult result =
                service.checkEligibility(employee, 1);

        assertEquals("Not Eligible", result.getClassification());
        assertEquals(1, result.getReasons().size());
        assertTrue(result.getReasons().get(0).contains("21"));
    }

    @Test
    void conditionalClearanceTest() {
        // Employee clearance is 1, but access requires level 3.
        Employee employee =
                createEmployee(30, "Finance", true, 1, true);

        EligibilityResult result =
                service.checkEligibility(employee, 3);

        assertEquals("Conditionally Eligible",
                result.getClassification());

        assertEquals(1, result.getReasons().size());
        assertTrue(result.getReasons().get(0)
                .contains("Insufficient security clearance"));
    }

    @Test
    void multipleFailureTest() {
        // Age, department, employment, and ID all fail.
        Employee employee =
                createEmployee(19, "Sales", false, 3, false);

        EligibilityResult result =
                service.checkEligibility(employee, 1);

        assertEquals("Not Eligible", result.getClassification());

        // All four reasons must be reported.
        assertEquals(4, result.getReasons().size());
    }

    @Test
    void invalidAgeInputTest() {
        assertThrows(IllegalArgumentException.class, () ->
                createEmployee(-1, "IT", true, 2, true));
    }

    @Test
    void invalidClearanceInputTest() {
        assertThrows(IllegalArgumentException.class, () ->
                createEmployee(25, "IT", true, 4, true));
    }

    @Test
    void invalidRequestedAccessLevelTest() {
        Employee employee =
                createEmployee(25, "IT", true, 2, true);

        assertThrows(IllegalArgumentException.class, () ->
                service.checkEligibility(employee, 5));
    }

    @Test
    void nullEmployeeTest() {
        assertThrows(IllegalArgumentException.class, () ->
                service.checkEligibility(null, 1));
    }
}
