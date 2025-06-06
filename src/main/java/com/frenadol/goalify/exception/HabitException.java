package com.frenadol.goalify.exception;

public class HabitException {

  public static class HabitNotFoundException extends RuntimeException {
    public HabitNotFoundException(Integer id) {
      super("Hábito no encontrado con ID: " + id);
    }
    public HabitNotFoundException(String message) {
      super(message);
    }
  }

  public static class HabitAccessException extends RuntimeException {
    public HabitAccessException(String message) {
      super(message);
    }
  }

  public static class InvalidHabitDataException extends RuntimeException {
    public InvalidHabitDataException(String message) {
      super(message);
    }
  }

  public static class HabitAlreadyCompletedException extends RuntimeException { // NUEVA EXCEPCIÓN
    public HabitAlreadyCompletedException(String message) {
      super(message);
    }
  }
}