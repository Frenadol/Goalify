package com.frenadol.goalify.repositories;

import com.frenadol.goalify.models.Desafio;
import com.frenadol.goalify.models.Usuario;
import com.frenadol.goalify.models.UsuarioDesafio;
import com.frenadol.goalify.models.UsuarioDesafioId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserChallengeRepository extends JpaRepository<UsuarioDesafio, UsuarioDesafioId> {

    List<UsuarioDesafio> findById_IdUsuario(Integer idUsuario);

    @Modifying
    @Query("DELETE FROM UsuarioDesafio ud WHERE ud.id.idDesafio = :idDesafio")
    void deleteById_IdDesafio(@Param("idDesafio") Integer idDesafio);




    // Método para verificar si un usuario ya está inscrito en un desafío específico
    // Se busca por los objetos Usuario y Desafio directamente
    // Spring Data JPA debería poder generar la consulta basada en los campos de la entidad UsuarioDesafio
    // que son idUsuario (tipo Usuario) e idDesafio (tipo Desafio)
    boolean existsByIdUsuarioAndIdDesafio(Usuario idUsuario, Desafio idDesafio);

    // Alternativa más explícita usando los IDs si la anterior no funciona como se espera
    // o si prefieres ser más directo con los IDs.
    // @Query("SELECT CASE WHEN COUNT(ud) > 0 THEN TRUE ELSE FALSE END " +
    //        "FROM UsuarioDesafio ud " +
    //        "WHERE ud.id.idUsuario = :idUsuario AND ud.id.idDesafio = :idDesafio")
    // boolean existsByUsuarioIdAndDesafioId(@Param("idUsuario") Integer idUsuario, @Param("idDesafio") Integer idDesafio);

    // Para usar la alternativa anterior en AdminService, necesitarías pasar los IDs:
    // boolean alreadyEnrolled = userChallengeRepository.existsByUsuarioIdAndDesafioId(randomUser.getId(), randomChallenge.getId());

    // También podrías buscar por la instancia completa del ID compuesto si lo construyes
    Optional<UsuarioDesafio> findById(UsuarioDesafioId id);

    // >>> CAMBIO AQUÍ <<<
    // Añade JOIN FETCH uc.idDesafio para cargar la entidad Desafio asociada
    @Query("SELECT uc FROM UsuarioDesafio uc JOIN FETCH uc.idDesafio WHERE uc.idUsuario = :usuario AND uc.notificadoAlUsuario = false")
    List<UsuarioDesafio> findPendingNotificationsForUser(@Param("usuario") Usuario usuario);
}