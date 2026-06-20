package com.coach.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_goals", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "goal")
    private List<String> goals = new ArrayList<>();

    @Column(name = "weekly_frequency", nullable = false)
    private int weeklyFrequency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Equipment equipment;

    @Column(columnDefinition = "TEXT")
    private String injuries;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<Message> messages = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Enums
    public enum Level {
        BEGINNER, INTERMEDIATE, ADVANCED;

        public String toFrench() {
            return switch (this) {
                case BEGINNER -> "Débutant";
                case INTERMEDIATE -> "Intermédiaire";
                case ADVANCED -> "Avancé";
            };
        }
    }

    public enum Equipment {
        BODYWEIGHT_ONLY,  // sol uniquement
        BAR_ONLY,         // barre de traction
        BAR_AND_RINGS,    // barre + anneaux
        FULL;             // tout le matériel

        public String toFrench() {
            return switch (this) {
                case BODYWEIGHT_ONLY -> "Poids du corps uniquement (sol)";
                case BAR_ONLY -> "Barre de traction";
                case BAR_AND_RINGS -> "Barre + anneaux de gymnastique";
                case FULL -> "Matériel complet (barre, anneaux, parallettes)";
            };
        }
    }
}
