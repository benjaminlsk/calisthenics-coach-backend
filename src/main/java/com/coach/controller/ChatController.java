package com.coach.controller;

import com.coach.dto.ChatDTOs.ChatRequest;
import com.coach.dto.ChatDTOs.ChatResponse;
import com.coach.dto.ChatDTOs.MessageResponse;
import com.coach.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * POST /api/chat/{userId}
     * Envoie un message à StreetCoach et reçoit sa réponse
     */
    @PostMapping("/{userId}")
    public ResponseEntity<ChatResponse> sendMessage(
            @PathVariable Long userId,
            @Valid @RequestBody ChatRequest request) {
        ChatResponse response = chatService.sendMessage(userId, request.message());
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/chat/{userId}/history
     * Récupère tout l'historique de conversation (chargement initial du chat)
     */
    @GetMapping("/{userId}/history")
    public ResponseEntity<List<MessageResponse>> getHistory(@PathVariable Long userId) {
        return ResponseEntity.ok(chatService.getHistory(userId));
    }
}
