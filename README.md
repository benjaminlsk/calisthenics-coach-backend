# StreetCoach — Agent IA Coach Calisthénie

Stack : **Angular 17** (frontend) + **Spring Boot 3.3** (backend) + **PostgreSQL** + **API Anthropic**

---

## Prérequis

- Java 21+
- Node.js 20+
- PostgreSQL 15+
- Une clé API Anthropic → https://console.anthropic.com

---

## 1. Base de données

```sql
CREATE DATABASE calisthenics_coach;
CREATE USER coach WITH PASSWORD 'coach';
GRANT ALL PRIVILEGES ON DATABASE calisthenics_coach TO coach;
```

---

## 2. Backend Spring Boot

```bash
cd calisthenics-coach-backend

# Définir la clé API en variable d'environnement (ne jamais la mettre dans le code)
export ANTHROPIC_API_KEY=sk-ant-xxxxxxxxxxxx

# Lancer
./mvnw spring-boot:run
```

Le backend démarre sur **http://localhost:8080**

### Endpoints disponibles

| Méthode | URL                          | Description                    |
|---------|------------------------------|--------------------------------|
| POST    | /api/users                   | Créer un profil athlète        |
| GET     | /api/users/{id}              | Récupérer un profil            |
| PUT     | /api/users/{id}              | Mettre à jour un profil        |
| POST    | /api/chat/{userId}           | Envoyer un message au coach    |
| GET     | /api/chat/{userId}/history   | Récupérer l'historique du chat |

---

## 3. Frontend Angular

```bash
cd calisthenics-coach-ui

npm install

ng serve
```

Le frontend démarre sur **http://localhost:4200**

---

## 4. Structure du projet

```
calisthenics-coach-backend/
├── src/main/java/com/coach/
│   ├── config/
│   │   ├── AnthropicConfig.java     # WebClient configuré
│   │   └── CorsConfig.java          # CORS pour Angular
│   ├── controller/
│   │   ├── UserController.java      # Endpoints onboarding
│   │   └── ChatController.java      # Endpoints chat
│   ├── service/
│   │   ├── ClaudeService.java       # Appel API Anthropic + system prompt
│   │   ├── UserService.java         # Logique métier utilisateur
│   │   └── ChatService.java         # Orchestration du chat
│   ├── model/
│   │   ├── User.java                # Entité profil athlète
│   │   └── Message.java             # Entité historique
│   ├── repository/
│   │   ├── UserRepository.java
│   │   └── MessageRepository.java
│   └── dto/
│       ├── UserDTOs.java            # Request/Response onboarding
│       └── ChatDTOs.java            # Request/Response chat

calisthenics-coach-ui/
├── src/app/
│   ├── core/
│   │   ├── models/models.ts         # Interfaces TypeScript
│   │   └── services/
│   │       ├── api.service.ts       # Appels HTTP
│   │       └── user-state.service.ts # State utilisateur (signals)
│   ├── features/
│   │   ├── onboarding/              # Wizard 5 étapes
│   │   └── chat/                    # Interface de chat
│   └── app.routes.ts                # Routing lazy-loaded
```

---

## 5. Variables d'environnement

| Variable           | Description              | Exemple                  |
|--------------------|--------------------------|--------------------------|
| ANTHROPIC_API_KEY  | Clé API Anthropic        | sk-ant-xxxxx             |
| DB_USERNAME        | Utilisateur PostgreSQL   | coach (défaut)           |
| DB_PASSWORD        | Mot de passe PostgreSQL  | coach (défaut)           |

---

## Prochaines étapes (Phase 5)

- [ ] Entité `ProgressionEntry` pour tracker les performances
- [ ] Injection de la progression dans le system prompt Claude
- [ ] Graphiques de progression dans le frontend
- [ ] Streaming de la réponse (Server-Sent Events)
