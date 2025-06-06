    package com.frenadol.goalify.exception;

    public class UserException extends RuntimeException {
        private final String campo;
        private final Object valor;

        public static class UserNotFoundException extends UserException {
            public UserNotFoundException(Integer id) {
                super("Usuario no encontrado", "id", id);
            }

            public UserNotFoundException(String email) {
                super("Usuario no encontrado", "email", email);
            }
        }
        public static class UserAlreadyExistsException extends RuntimeException {
            public UserAlreadyExistsException(String message) {
                super(message);
            }
        }

        public static class DuplicateUserException extends UserException {
            public DuplicateUserException(String email) {
                super("Ya existe un usuario registrado", "email", email);
            }
        }

        public static class InvalidCredentialsException extends UserException {
            public InvalidCredentialsException() {
                super("Credenciales inválidas", "contraseña", null);
            }
        }

        public static class InvalidUserDataException extends UserException {
            public InvalidUserDataException(String campo) {
                super("Datos de usuario inválidos", campo, null);
            }
        }

        public static class UnauthorizedAccessException extends UserException {
            public UnauthorizedAccessException() {
                super("Usuario no tiene permisos de administrador", "esAdministrador", false);
            }
        }

        public static class UserRelationshipException extends UserException {
            public UserRelationshipException(String entidad) {
                super("Error en relación de usuario", entidad, null);
            }
        }
        public static class InsufficientPointsException extends UserException {
            public InsufficientPointsException(String message) {
                // No necesitamos 'campo' ni 'valor' para esta excepción específica,
                // así que podemos pasar null o valores genéricos si el constructor base los requiere.
                // O, si prefieres, puedes hacer que esta excepción herede directamente de RuntimeException
                // si no necesita los campos 'campo' y 'valor'.
                // Por ahora, la hacemos heredar de UserException para mantener la estructura.
                super(message, "puntosTotales", null); // Pasamos nulls o valores genéricos para campo y valor
            }
        }

        protected UserException(String mensaje, String campo, Object valor) {
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