package edu.eci.dosw.tdd.core.util;

public final class ValidationUtil {

    private ValidationUtil() {}

    public static void requireNonBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be null/blank");
        }
    }

    public static void requireNonNull(Object value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    public static void requirePositive(int value, String fieldName) {
        if (value <= 0) {
            throw new IllegalArgumentException(fieldName + " must be > 0");
        }
    }


    public static void validateNotEmpty(String value, String fieldName) {
        requireNonBlank(value, fieldName);
    }

    public static void validateNotNull(Object value, String fieldName) {
        requireNonNull(value, fieldName);
    }
}