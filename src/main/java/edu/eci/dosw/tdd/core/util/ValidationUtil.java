package edu.eci.dosw.tdd.core.util;

public class ValidationUtil {

    private ValidationUtil() {
        // Utility class
    }

    public static void validateNotNull(Object obj, String fieldName) {
        if (obj == null) {
            throw new IllegalArgumentException(fieldName + " no puede ser nulo.");
        }
    }

    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " no puede estar vacío.");
        }
    }
}