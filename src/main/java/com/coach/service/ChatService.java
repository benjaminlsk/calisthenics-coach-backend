package com.coach.service;

import com.coach.dto.ChatDTOs.ChatResponse;
import com.coach.dto.ChatDTOs.MessageResponse;
import com.coach.model.Message;
import com.coach.model.User;
import com.coach.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ClaudeService claudeService;
    private final UserService userService;
    private final MessageRepository messageRepository;

    // Fenêtre de contexte : on envoie les 20 derniers messages à Claude
    private static final int CONTEXT_WINDOW = 20;

    @Transactional
    public ChatResponse sendMessage(Long userId, String userMessage) {
        User user = userService.findById(userId);

        // Récupération de l'historique récent pour le contexte
        List<Message> history = messageRepository.findLastNByUserId(userId, CONTEXT_WINDOW);
        Collections.reverse(history); // remettre dans l'ordre chronologique

        // Appel à Claude (bloquant volontairement pour simplicité — voir note ci-dessous)
        String assistantResponse = claudeService
                .chat(user, history, userMessage)
                .block();

        // Persistance des deux messages
        messageRepository.save(new Message(user, "user", userMessage));
        messageRepository.save(new Message(user, "assistant", assistantResponse));

        log.debug("Message traité pour {} — réponse de {} caractères",
                user.getName(), assistantResponse.length());

        return new ChatResponse(assistantResponse, LocalDateTime.now());

        /*
         * NOTE : Le .block() est volontaire ici car le controller est en Spring MVC
         * (synchrone). Si tu passes en Spring WebFlux (Mono/Flux dans le controller),
         * tu peux retourner le Mono<ChatResponse> directement pour un vrai streaming.
         */
    }

    @Transactional(readOnly = true)
    public List<MessageResponse> getHistory(Long userId) {
        // Vérifie que l'utilisateur existe
        userService.findById(userId);
        List<Message> messages = messageRepository.findByUserIdOrderByCreatedAtAsc(userId);
        return MessageResponse.fromList(messages);
    }
}
