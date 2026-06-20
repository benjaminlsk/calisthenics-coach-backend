package com.coach.repository;

import com.coach.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByUserIdOrderByCreatedAtAsc(Long userId);

    /**
     * Récupère les N derniers messages pour limiter la taille du contexte envoyé à Claude.
     * On garde les 20 derniers échanges pour éviter de dépasser la fenêtre de contexte.
     */
    @Query("""
        SELECT m FROM Message m
        WHERE m.user.id = :userId
        ORDER BY m.createdAt DESC
        LIMIT :limit
        """)
    List<Message> findLastNByUserId(Long userId, int limit);
}
