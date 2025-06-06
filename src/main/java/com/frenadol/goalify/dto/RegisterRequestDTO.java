package com.frenadol.goalify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
// Si usas Lombok, puedes añadir estas anotaciones para getters, setters, etc.
// import lombok.Getter;
// import lombok.Setter;
// import lombok.NoArgsConstructor;
// import lombok.AllArgsConstructor;

// @Getter
// @Setter
// @NoArgsConstructor
// @AllArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres.")
    private String nombre;

    @NotBlank(message = "El email no puede estar vacío.")
    @Email(message = "Debe ser una dirección de email válida.")
    @Size(max = 255, message = "El email no puede exceder los 255 caracteres.")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía.")
    @Size(min = 6, max = 100, message = "La contraseña debe tener entre 6 y 100 caracteres.")
    private String contrasena; // En el frontend se llama 'password', asegúrate de que el mapeo sea correcto
    // o considera cambiar el nombre aquí a 'password' para consistencia.

    // Opcional: Si permites subir la foto de perfil durante el registro
    // y la envías como una cadena (ej. Data URL Base64)
    private String fotoPerfil;

    // --- Constructores, Getters y Setters manuales si no usas Lombok ---

    public RegisterRequestDTO() {
    }

    public RegisterRequestDTO(String nombre, String email, String contrasena, String fotoPerfil) {
        this.nombre = nombre;
        this.email = email;
        this.contrasena = contrasena;
        this.fotoPerfil = fotoPerfil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }
}   