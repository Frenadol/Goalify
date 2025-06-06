package com.frenadol.goalify.dto;

public class RandomAssignmentResponseDTO {
    private boolean success;
    private String message;
    private String assignedUserName;
    private String assignedChallengeName;

    // Constructor para éxito
    public RandomAssignmentResponseDTO(boolean success, String message, String assignedUserName, String assignedChallengeName) {
        this.success = success;
        this.message = message;
        this.assignedUserName = assignedUserName;
        this.assignedChallengeName = assignedChallengeName;
    }

    // Constructor para fallo o casos sin asignación
    public RandomAssignmentResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters y Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAssignedUserName() {
        return assignedUserName;
    }

    public void setAssignedUserName(String assignedUserName) {
        this.assignedUserName = assignedUserName;
    }

    public String getAssignedChallengeName() {
        return assignedChallengeName;
    }

    public void setAssignedChallengeName(String assignedChallengeName) {
        this.assignedChallengeName = assignedChallengeName;
    }
}