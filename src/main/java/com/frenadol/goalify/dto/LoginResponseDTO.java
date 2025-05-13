package com.frenadol.goalify.dto;

public class LoginResponseDTO {
    private String token;
    private UsuarioDTO user;

    public LoginResponseDTO(String token, UsuarioDTO user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UsuarioDTO getUser() {
        return user;
    }

    public void setUser(UsuarioDTO user) {
        this.user = user;
    }
}