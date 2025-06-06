package com.frenadol.goalify.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Ignorar campos desconocidos es una buena práctica
public class UserProfilePreferencesDTO {

    // Preferencias generales existentes
    private String themeColor;
    private Boolean showBio; // Para la página de perfil detallado
    private Boolean showHabitStats; // Para la página de perfil detallado
    private Boolean showCurrentChallenges; // Para la página de perfil detallado
    private Boolean showCompletedChallenges; // Para la página de perfil detallado

    // Preferencias para la TARJETA DE PERFIL DEL USUARIO (navbar o similar)
    private String cardBackgroundColor; // Color de fondo de la tarjeta de perfil
    private String cardTextColor;       // Color del texto en la tarjeta de perfil
    private Boolean showEmailOnCard;
    private Boolean showJoinDateOnCard;
    private Boolean showPointsOnCard;
    private Boolean showLevelOnCard;
    // Podrías añadir más como:
    // private Boolean showBioOnCard; // Si quieres controlar la bio específicamente en la tarjeta
    // private Boolean showRangoOnCard;

    // Preferencias para TARJETAS DE DESAFÍO (ya existentes)
    private String cardColor; // Color específico para tarjetas de desafío (¿o es el fondo?)
    private Boolean showChallengeCategoryOnCard;
    private Boolean showChallengePointsOnCard;
    private Boolean showChallengeDatesOnCard;

    // --- NUEVO CAMPO PARA INDICAR EL ÍTEM A DESBLOQUEAR ---
    private String itemIdToUnlock; // Identificador del ítem que se intenta desbloquear, ej: "profile_color_gold"


    /**
     * Aplica valores por defecto a cualquier campo que sea null.
     * Este método es útil si quieres asegurar que las preferencias siempre tengan un valor
     * cuando se crea un nuevo usuario o cuando se recuperan y algún campo es null.
     */
    public void applyDefaults() {
        // Defaults generales
        if (this.themeColor == null) this.themeColor = "#3f51b5"; // Un azul material design como ejemplo
        if (this.showBio == null) this.showBio = true;
        if (this.showHabitStats == null) this.showHabitStats = true;
        if (this.showCurrentChallenges == null) this.showCurrentChallenges = true;
        if (this.showCompletedChallenges == null) this.showCompletedChallenges = true;

        // Defaults para la TARJETA DE PERFIL DEL USUARIO
        if (this.cardBackgroundColor == null) this.cardBackgroundColor = "#FFFFFF"; // Fondo blanco por defecto
        if (this.cardTextColor == null) this.cardTextColor = "#333333";       // Texto oscuro por defecto
        if (this.showEmailOnCard == null) this.showEmailOnCard = false;
        if (this.showJoinDateOnCard == null) this.showJoinDateOnCard = false;
        if (this.showPointsOnCard == null) this.showPointsOnCard = true;
        if (this.showLevelOnCard == null) this.showLevelOnCard = true;

        // Defaults para TARJETAS DE DESAFÍO
        // Considera si 'cardColor' debe tener un default diferente o si se relaciona con cardBackgroundColor/cardTextColor
        if (this.cardColor == null) this.cardColor = "#FFFFFF"; // Ejemplo: blanco por defecto, ajusta según necesidad
        if (this.showChallengeCategoryOnCard == null) this.showChallengeCategoryOnCard = true;
        if (this.showChallengePointsOnCard == null) this.showChallengePointsOnCard = true;
        if (this.showChallengeDatesOnCard == null) this.showChallengeDatesOnCard = true;

        // El campo itemIdToUnlock no necesita un valor por defecto, ya que solo se envía cuando se intenta desbloquear algo.
    }
}