package com.frenadol.goalify.models;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.util.Objects;

@Getter
@Setter
@Embeddable
public class UsuarioDesafioId implements java.io.Serializable {
    private static final long serialVersionUID = -4386152302095612100L;
    @NotNull
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @NotNull
    @Column(name = "id_desafio", nullable = false)
    private Integer idDesafio;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UsuarioDesafioId entity = (UsuarioDesafioId) o;
        return Objects.equals(this.idUsuario, entity.idUsuario) &&
                Objects.equals(this.idDesafio, entity.idDesafio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, idDesafio);
    }

    public UsuarioDesafioId(Integer idUsuario, Integer idDesafio) {
        this.idUsuario = idUsuario;
        this.idDesafio = idDesafio;
    }
    public UsuarioDesafioId() {
    }
}