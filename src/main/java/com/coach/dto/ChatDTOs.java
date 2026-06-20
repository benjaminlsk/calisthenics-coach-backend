package com.coach.dto;

import com.coach.model.Message;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public class ChatDTOs {

    public record ChatRequest(
        @NotBlank(message = "Le message ne peut pas être vide")
        String message
    ) {}

    public record ChatResponse(
        String message,
        LocalDateTime timestamp
    ) {}

    public record MessageResponse(
        Long id,
        String role,
        String content,
        LocalDateTime createdAt
    ) {
        public static MessageResponse from(Message message) {
            return new MessageResponse(
                message.getId(),
                message.getRole(),
                message.getContent(),
                message.getCreatedAt()
            );
        }

        public static List<MessageResponse> fromList(List<Message> messages) {
            return messages.stream().map(MessageResponse::from).toList();
        }
    }
}
