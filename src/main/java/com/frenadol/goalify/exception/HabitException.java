package com.frenadol.goalify.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class HabitException extends RuntimeException {
  private final String campo;
  private final Object valor;

  @ResponseStatus(HttpStatus.NOT_FOUND)
  public static class HabitNotFoundException extends HabitException {
    // MODIFICACIÓN IMPORTANTE: Hacer el constructor más flexible o específico para Integer
    public HabitNotFoundException(Object id) { // Acepta Object para manejar Integer o Long
      super("Hábito no encontrado", "id", id);
    }
    // O, si prefieres ser específico y tenías uno para Long:
    // public HabitNotFoundException(Long id) { super("Hábito no encontrado", "id", id); }
    // public HabitNotFoundException(Integer id) { super("Hábito no encontrado", "id", id); }
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public static class InvalidHabitDataException extends HabitException {
    public InvalidHabitDataException(String campo, String mensajeEspecifico) {
      super(mensajeEspecifico, campo, null);
    }
    public InvalidHabitDataException(String mensajeGeneral) {
      super(mensajeGeneral, null, null);
    }
  }

  @ResponseStatus(HttpStatus.FORBIDDEN)
  public static class HabitAccessException extends HabitException {
    public HabitAccessException(String mensaje) {
      super(mensaje, "permiso", null);
    }
    public HabitAccessException() {
      super("Acceso denegado al hábito", "permiso", null);
    }
  }

  protected HabitException(String mensaje, String campo, Object valor) {
    super(mensaje);
    this.campo = campo;
    this.valor = valor;
  }

  public String getCampo() {
    return campo;
  }

  public Object getValor() {
    return valor;
  }
}