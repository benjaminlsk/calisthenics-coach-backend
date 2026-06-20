package com.coach.dto;

import com.coach.model.User;
import jakarta.validation.constraints.*;

import java.util.List;

// ========== ONBOARDING ==========

public class UserDTOs {

    public record CreateUserRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String name,

        @NotNull(message = "Le niveau est obligatoire")
        User.Level level,

        @NotEmpty(message = "Au moins un objectif est requis")
        List<String> goals,

        @Min(value = 2, message = "Minimum 2 jours par semaine")
        @Max(value = 7, message = "Maximum 7 jours par semaine")
        int weeklyFrequency,

        @NotNull(message = "Le matériel disponible est obligatoire")
        User.Equipment equipment,

        String injuries  // optionnel
    ) {}

    public record UserResponse(
        Long id,
        String name,
        User.Level level,
        List<String> goals,
        int weeklyFrequency,
        User.Equipment equipment,
        String injuries
    ) {
        public static UserResponse from(User user) {
            return new UserResponse(
                user.getId(),
                user.getName(),
                user.getLevel(),
                user.getGoals(),
                user.getWeeklyFrequency(),
                user.getEquipment(),
                user.getInjuries()
            );
        }
    }
}
