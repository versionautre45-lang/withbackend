# API Backend - Système de Gestion de Stages

Backend REST API développé avec Spring Boot pour le système de gestion de stages.

## Technologies Utilisées

- Java 17
- Spring Boot 3.2.0
- Spring Security avec JWT
- Spring Data JPA
- MySQL 8.0
- Lombok
- Maven

## Prérequis

- JDK 17 ou supérieur
- Maven 3.6+
- MySQL 8.0 ou supérieur
- Un IDE (IntelliJ IDEA, Eclipse, VS Code avec extensions Java)

## Installation et Configuration

### 1. Cloner le projet

```bash
cd spring-boot-backend
```

### 2. Configurer MySQL

Démarrez MySQL et créez la base de données :

```sql
CREATE DATABASE internship_db;
```

Ou exécutez le script complet :

```bash
mysql -u root -p < database/schema.sql
```

### 3. Configurer l'application

Modifiez `src/main/resources/application.properties` selon votre configuration MySQL :

```properties
spring.datasource.username=root
spring.datasource.password=votre_mot_de_passe
```

### 4. Compiler le projet

```bash
mvn clean install
```

### 5. Lancer l'application

```bash
mvn spring-boot:run
```

L'API sera accessible sur `http://localhost:8080/api`

## Flux d'Authentification Personnalisé

### Processus d'inscription pour les stagiaires

1. **L'encadreur crée un stagiaire** via `POST /api/interns`
   - Les informations sont stockées dans la base de données
   - Le compte est en statut `PENDING` (sans mot de passe)

2. **Le stagiaire accède à la page de connexion**
   - Il saisit son email via `POST /api/auth/check-email`
   - Le système vérifie si l'email existe

3. **Si l'email existe et n'a pas de mot de passe**
   - Le système retourne `{ "exists": true, "hasPassword": false }`
   - Le stagiaire est redirigé vers la page de création de mot de passe

4. **Le stagiaire crée son mot de passe** via `POST /api/auth/create-password`
   - Le mot de passe est hashé et stocké
   - Le compte passe en statut `ACTIVE`
   - Un token JWT est retourné
   - Le stagiaire est connecté automatiquement

5. **Connexions futures** via `POST /api/auth/login`
   - Connexion classique avec email et mot de passe

## Endpoints API

### Authentification

#### Vérifier un email
```http
POST /api/auth/check-email
Content-Type: application/json

{
  "email": "stagiaire@example.com"
}
```

Réponse :
```json
{
  "exists": true,
  "hasPassword": false,
  "message": "Email trouvé. Créez votre mot de passe."
}
```

#### Créer un mot de passe
```http
POST /api/auth/create-password
Content-Type: application/json

{
  "email": "stagiaire@example.com",
  "password": "MonMotDePasse123!"
}
```

Réponse :
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "user": {
    "id": 3,
    "email": "stagiaire@example.com",
    "firstName": "Marie",
    "lastName": "Martin",
    "role": "STAGIAIRE",
    "accountStatus": "ACTIVE"
  }
}
```

#### Se connecter
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "stagiaire@example.com",
  "password": "MonMotDePasse123!"
}
```

Réponse : même format que create-password

### Gestion des Stagiaires

#### Créer un stagiaire (réservé aux encadreurs)
```http
POST /api/interns
Authorization: Bearer {token}
Content-Type: application/json

{
  "email": "nouveau@example.com",
  "firstName": "Pierre",
  "lastName": "Dubois",
  "phone": "0612345678",
  "school": "Université Paris",
  "department": "Informatique",
  "startDate": "2025-01-15",
  "endDate": "2025-06-15",
  "encadreurId": 1
}
```

#### Récupérer tous les stagiaires
```http
GET /api/interns
Authorization: Bearer {token}
```

#### Récupérer un stagiaire par ID
```http
GET /api/interns/{id}
Authorization: Bearer {token}
```

#### Supprimer un stagiaire
```http
DELETE /api/interns/{id}
Authorization: Bearer {token}
```

## Structure de la Base de Données

### Table `users`
- Stocke tous les utilisateurs (Admin, Encadreur, Stagiaire)
- Champs : id, email, firstName, lastName, password, role, accountStatus, phone, avatar

### Table `encadreurs`
- Informations spécifiques aux encadreurs
- Relation OneToOne avec users

### Table `interns`
- Informations spécifiques aux stagiaires
- Relation OneToOne avec users
- Relation ManyToOne avec encadreurs et projects

### Table `projects`
- Projets de stage
- Relation ManyToOne avec encadreurs

### Table `tasks`
- Tâches associées aux projets
- Relation ManyToOne avec projects et users

## Sécurité

- Authentification JWT avec expiration de 24 heures
- Mots de passe hashés avec BCrypt
- Protection CSRF désactivée (API REST)
- CORS configuré pour accepter toutes les origines (à modifier en production)
- Endpoints publics : `/api/auth/**`
- Endpoints protégés : nécessitent un token JWT valide

## Données de Test

Le script `database/schema.sql` crée automatiquement :

1. **Admin**
   - Email : `admin@example.com`
   - Mot de passe : `password123`
   - Rôle : ADMIN

2. **Encadreur**
   - Email : `encadreur@example.com`
   - Mot de passe : `password123`
   - Rôle : ENCADREUR

3. **Stagiaire**
   - Email : `stagiaire@example.com`
   - Pas de mot de passe (compte PENDING)
   - Rôle : STAGIAIRE

## Tests avec Postman ou cURL

### Exemple complet de flux

1. Créer un stagiaire (avec le token de l'encadreur) :
```bash
curl -X POST http://localhost:8080/api/interns \
  -H "Authorization: Bearer {token_encadreur}" \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "firstName": "Test",
    "lastName": "User",
    "phone": "0612345678",
    "school": "Test School",
    "department": "IT",
    "startDate": "2025-01-15",
    "endDate": "2025-06-15",
    "encadreurId": 1
  }'
```

2. Vérifier l'email :
```bash
curl -X POST http://localhost:8080/api/auth/check-email \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com"}'
```

3. Créer le mot de passe :
```bash
curl -X POST http://localhost:8080/api/auth/create-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "MyPassword123!"
  }'
```

## Déploiement

Pour créer un fichier JAR exécutable :

```bash
mvn clean package
java -jar target/management-system-1.0.0.jar
```

## Variables d'Environnement en Production

Modifiez ces valeurs pour la production :

```properties
# JWT Secret (256 bits minimum)
jwt.secret=votre-clé-secrète-très-longue-et-sécurisée

# Base de données
spring.datasource.url=jdbc:mysql://votre-serveur:3306/internship_db
spring.datasource.username=votre_utilisateur
spring.datasource.password=votre_mot_de_passe

# CORS (limiter aux domaines autorisés)
# Modifier dans SecurityConfig.java
```

## Support

Pour toute question ou problème, contactez l'équipe de développement.
