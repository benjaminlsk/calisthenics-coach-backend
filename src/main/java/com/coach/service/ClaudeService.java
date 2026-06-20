package com.coach.service;

import com.coach.model.Message;
import com.coach.model.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClaudeService {

    private final WebClient anthropicWebClient;

    @Value("${anthropic.api.model}")
    private String model;

    @Value("${anthropic.api.max-tokens}")
    private int maxTokens;

    private static final String SYSTEM_PROMPT_TEMPLATE = """
        Tu es StreetCoach, un agent IA expert en calisthénie et en entraînement au poids du corps.
        Tu as coaché des centaines d'athlètes, du débutant absolu au compétiteur mondial.

        ═══════════════════════════════════════
        PROFIL DE TON ATHLÈTE
        ═══════════════════════════════════════
        Nom         : %s
        Niveau      : %s
        Objectifs   : %s
        Fréquence   : %d jours par semaine
        Matériel    : %s
        Restrictions: %s
        ═══════════════════════════════════════

        TES RÈGLES ABSOLUES :
        1. Adapte CHAQUE réponse au profil ci-dessus. Un débutant ne reçoit jamais d'exercices avancés.
        2. Respecte la progression : pull-ups stricts avant les muscle-ups, pike push-ups avant le handstand.
        3. Pour tout programme, structure tes réponses ainsi :
           🔥 ÉCHAUFFEMENT (5-10 min)
           💪 TRAVAIL PRINCIPAL (exercices + séries + reps)
           🧘 GAINAGE / MOBILITÉ
           ❄️ RÉCUPÉRATION
        4. Ton ton : exigeant et direct, mais toujours bienveillant. Tu crois en ton athlète.
        5. Refuse systématiquement les conseils médicaux. Redirige vers un médecin du sport.
        6. Si l'utilisateur mentionne une douleur, priorise sa sécurité avant la performance.
        7. Utilise des emojis avec parcimonie pour structurer (pas pour décorer).
        8. Réponds toujours en français sauf demande explicite.
        """;

    /**
     * Envoie un message à Claude avec le contexte complet de l'utilisateur.
     *
     * @param user          profil de l'athlète (injecté dans le system prompt)
     * @param history       historique des échanges précédents (contexte de la conversation)
     * @param userMessage   nouveau message de l'utilisateur
     * @return              réponse de Claude
     */
    public Mono<String> chat(User user, List<Message> history, String userMessage) {
        String systemPrompt = buildSystemPrompt(user);

        // Construction de l'historique au format Anthropic
        List<Map<String, String>> messages = history.stream()
                .map(m -> Map.of("role", m.getRole(), "content", m.getContent()))
                .collect(Collectors.toList());

        // Ajout du message courant
        messages.add(Map.of("role", "user", "content", userMessage));

        Map<String, Object> requestBody = Map.of(
                "model", model,
                "max_tokens", maxTokens,
                "system", systemPrompt,
                "messages", messages
        );

        log.debug("Appel Claude pour l'utilisateur [{}] — {} messages dans l'historique",
                user.getName(), history.size());

        return anthropicWebClient.post()
                .uri("/v1/messages")
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response ->
                    response.bodyToMono(String.class)
                        .doOnNext(body -> log.error("Anthropic API error {}: {}", response.statusCode(), body))
                        .flatMap(body -> Mono.error(new RuntimeException("Anthropic " + response.statusCode() + ": " + body)))
                )
                .bodyToMono(ClaudeApiResponse.class)
                .map(response -> response.content().get(0).text())
                .doOnError(e -> log.error("Erreur lors de l'appel à l'API Anthropic : {}", e.getMessage()));
    }

    private String buildSystemPrompt(User user) {
        return SYSTEM_PROMPT_TEMPLATE.formatted(
                user.getName(),
                user.getLevel().toFrench(),
                String.join(", ", user.getGoals()),
                user.getWeeklyFrequency(),
                user.getEquipment().toFrench(),
                user.getInjuries() != null && !user.getInjuries().isBlank()
                        ? user.getInjuries()
                        : "Aucune restriction connue"
        );
    }

    // ── Mapping des réponses Anthropic ──────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ClaudeApiResponse(
            List<ContentBlock> content
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record ContentBlock(
            String type,
            String text
    ) {}
}
