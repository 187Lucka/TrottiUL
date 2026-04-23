# Trotti-UL - Gestion de trottinettes électriques sur le campus ULaval

Plateforme complète pour la gestion des déplacements, abonnements, stations, trottinettes et opérations de maintenance

# 📌 Description du projet

Trotti-UL est une application académique simulant un système complet de gestion de trottinettes électriques sur le campus de l’Université Laval.
Ce projet a été fait pour le cours d’Architecture logicielle (GLO-4003).
Elle inclut :

- Un système d’inscription et authentification sécurisé
- L’achat de passes par session universitaire
- L’utilisation réelle de trottinettes : déverrouillage, voyages, limites, facturation
- La gestion des stations, énergie, recharge
- Un mode maintenance pour les techniciens
- Un mode « carte blanche » pour les employés ULaval

Le projet est découpé en Aventures (use cases complets) et Récits utilisateurs, avec des critères d’acceptation précis.
Ce README documente l’installation, le fonctionnement, les fonctionnalités, et les choix spéciaux du domaine.

# 🛠️ Installation

Vous pouvez installer et exécuter le logiciel de deux façons.

> Prérequis : avant toute exécution (scripts, Dockerfile ou docker compose), configurez vos variables dans `src/main/resources/application.properties` ou via des variables d’environnement équivalentes (ex. SMTP_*, JWT_SECRET). Sans ces valeurs, l’application ne démarrera pas correctement.

## Option 1 : Utiliser Docker

1. Construire l’image Docker

```bash
docker build -t trotti-ul .
```

2. Exécuter le conteneur

```bash
docker run --rm -p 8080:8080 --name trotti-ul-dev trotti-ul
```

### Option 1bis : Docker Compose (application + Mailhog)

```bash
docker compose up --build
```

- API : http://localhost:8080
- Mailhog (interface web) : http://localhost:8025
- SMTP utilisé par l’app : host `mailhog`, port `1025` (définis dans `docker-compose.yml` via variables d’environnement, surchargeables si besoin).
- JWT : définissez `JWT_SECRET` (env ou `src/main/resources/application.properties`). Utilise le même secret pour signer/vérifier (HS256).

## Option 2 : Utiliser les scripts de démarrage

### Unix/macOS

```bash
./start.sh
```

### Windows

```bash
.\start.bat
```

# 📚 Fonctionnalités principales

Cette section réunit toutes les fonctionnalités du projet, organisées par domaines, pour un aperçu clair du système.

## 👤 1. Gestion des utilisateurs

### Inscription

Un utilisateur peut créer un compte contenant :
- Nom
- Date de naissance
- Genre
- IDUL (sans validation/regex)
- Adresse courriel
- Mot de passe (≥10 caractères, 1 maj, 1 chiffre, 1 spécial)

### Authentification
- Connexion par courriel + mot de passe
- Session expire après 60 minutes
- Notification courriel lors de l’activation du compte

## 🎫 2. Abonnements & passes

### Achat d’une passe

Lors d’une commande, l’utilisateur peut :

- Choisir la session universitaire (ex : A2025, H2026, E2026…)
- Choisir la durée maximale de déplacement quotidien (par bonds de 10 minutes)
- Choisir la méthode de facturation : après chaque voyage / mensuel
- Payer par carte de crédit
- Obtenir automatiquement une facture PDF/JSON
- Stocker les informations de paiement (pas de route pour les modifier plus tard)

### Prix

- 45$ pour la session; comprend 30 min/jour
   - 2$ / mois par tranche de 10 minutes supplémentaire

### Validité

- Une passe couvre la session universitaire complète
- Valide de la date de début de la session choisie jusqu’au début de la prochaine session

## 🛴 3. Utilisation des trottinettes

### Activation

- Après achat, l’utilisateur est autorisé à voyager
- Il peut demander un code unique pour déverrouiller une trottinette
   - Code envoyé par courriel
   - Entre 4 et 6 chiffres
   - Expire après 60 secondes
   - Nécessite un nouveau code pour chaque voyage

### Conditions d’un voyage

- La session active doit correspondre à la session de la passe
- La trottinette doit avoir > 15% d’énergie
- La trottinette doit être disponible dans une station (ex : Station X - emplacement #1)

### Énergie & recharge

- Perte : 5% / 10 minutes d’utilisation
- Recharge : 2% / 10 minutes
- Énergie ne descend pas en dessous de 0%
- Recharge automatique dans les stations (hors maintenance)

### Historique

Un voyage inclut :

- Station de départ
- Station d’arrivée
- Moment de départ
- Durée de déplacement

#### Frais supplémentaires

Si la durée illimitée journalière est dépassée → 5 $ / jour

## 🏢 4. Stations

- Chaque station débute avec 80% de sa capacité remplie
- Chaque station contient un ensemble d’emplacements numérotés
- Les trottinettes doivent y être stationnées pour être utilisables

## 🔧 5. Maintenance

### Gestion des problèmes

- Les demandes de service sont envoyées par un usager ou un responsable
- Reçues par courriel à tous les techniciens

### Mode maintenance

Lorsqu’une station est mise en maintenance :

- Aucun voyage n'est possible depuis cette station
- Aucune trottinette ne peut y être déposée
- La recharge s’arrête
- La remise en service doit être effectuée manuellement par un technicien.

### Déplacement de trottinettes

Le technicien peut :

- Sortir un ensemble de trottinettes (#emplacements)
- Les placer dans son camion (opération intermédiaire "In Transfer")
- Les déplacer vers une autre station (si de la place)
   - Elles ne se déchargent pas durant le transfert

Il peut :

- Sortir 10 trottinettes
- En déposer 5 dans une station
- 5 dans une autre
- Puis terminer la maintenance

### Carte blanche employé

Les employés ULaval ayant un IDUL :

- Ont une passe gratuite
- Peuvent voyager illimité
- N'ont pas besoin d’acheter de passe

## ⚙️ Considérations techniques

### 1. Modèle énergétique

- Décharge linéaire : 5%/10 min
- Recharge linéaire : 2%/10 min

### 2. Codes uniques

- Stockés temporairement
- Expiration automatique au bout de 60 secondes

### 3. Sessions universitaires

Les identifiants suivent le format :
- A2025 = Automne 2025
- H2026 = Hiver 2026
- E2026 = Été 2026

## 📬 Notifications courriel

L’application envoie des courriels pour :

- Activation du compte (lors de l'achat d'un pass)
- Envoi d’un code unique
- Demandes de maintenance

## 🧾 Facturation

Chaque transaction génère une facture contenant :

- Informations du client
- Type de passe
- Session
- Méthode de facturation
- Montants supplémentaires
- Total payé

## 📜 Licence

Projet académique – Licence libre selon les besoins

## 🎉 Conclusion

Ce projet offre une simulation complète et réaliste d’un système de trottinettes électriques sur un campus universitaire, avec :

- Expérience utilisateur complète
- Gestion administrative
- Système énergétique simulé
- Maintenance poussée
- Logique métier riche

Prêt à être exécuté, testé et amélioré.
