# Application de gestion de Taxi-Brousse

Ce document décrit les **fonctionnalités** et les **données à gérer** pour une application de gestion interne destinée aux entreprises de taxi-brousse (transport provincial).

---

## 1. Gestion des véhicules

### Données à gérer

- Identifiant du véhicule
- Immatriculation
- Marque
- Modèle
- Année de mise en service
- Capacité (nombre de places)
- État (actif, en panne, en maintenance)
- Kilométrage actuel
- Consommation moyenne estimée
- Date d’expiration de l’assurance
- Date de visite technique

### Fonctionnalités

- Création / modification / suppression d’un véhicule
- Consultation de l’historique du véhicule
- Suivi des pannes et réparations
- Alertes pour assurance et visite technique expirées

---

## 2. Gestion du personnel

### Chauffeurs

#### Données

- Identifiant
- Nom et prénom
- Numéro de téléphone
- Numéro de permis
- Catégorie de permis
- Date d’embauche
- Statut (actif, suspendu, absent)

#### Fonctionnalités

- Gestion des chauffeurs
- Affectation aux véhicules
- Historique des voyages effectués

### Aides / receveurs

#### Données

- Identifiant
- Nom
- Téléphone
- Véhicule ou voyage affecté

#### Fonctionnalités

- Gestion des aides
- Affectation aux voyages

---

## 3. Gestion des lignes (trajets)

### Données à gérer

- Identifiant de la ligne
- Ville de départ
- Ville d’arrivée
- Escales intermédiaires
- Distance estimée (km)
- Durée moyenne
- Tarif par passager

### Fonctionnalités

- Création et modification des lignes
- Consultation des statistiques par ligne
- Activation / désactivation d’une ligne

---

## 4. Gestion des voyages

### Données à gérer

- Identifiant du voyage
- Date de départ
- Heure de départ
- Ligne associée
- Véhicule utilisé
- Chauffeur
- Aide / receveur
- Nombre de passagers
- Recette totale
- Quantité de carburant consommée
- Incidents signalés

### Fonctionnalités

- Planification des voyages
- Enregistrement d’un voyage effectué
- Calcul automatique des recettes
- Historique et suivi des voyages

---

## 5. Gestion financière

### Recettes

#### Données

- Voyage associé
- Montant
- Date

#### Fonctionnalités

- Suivi des recettes par voyage
- Calcul du chiffre d’affaires

### Dépenses

#### Données

- Type de dépense (carburant, réparation, salaire, autres)
- Montant
- Date
- Véhicule ou voyage concerné
- Description

#### Fonctionnalités

- Enregistrement des dépenses
- Consultation du bilan financier
- Calcul des bénéfices

---

## 6. Gestion du carburant

### Données à gérer

- Véhicule
- Date d’achat
- Quantité (litres)
- Prix unitaire
- Kilométrage avant / après

### Fonctionnalités

- Suivi de la consommation réelle
- Comparaison consommation prévue / réelle
- Détection de surconsommation

---

## 7. Gestion des places et passagers

### Gestion des places

#### Données à gérer

- Identifiant de la place
- Numéro de place
- Véhicule associé
- Statut (libre, réservé, occupé)

#### Fonctionnalités

- Numérotation des places par véhicule
- Visualisation du plan de places
- Mise à jour automatique du statut des places

---

### Gestion des passagers

#### Données à gérer

- Identifiant du passager
- Nom et prénom
- Contact (téléphone)
- Voyage associé
- Place occupée
- Type de transaction (réservation ou achat direct)

#### Fonctionnalités

- Enregistrement d’un passager avec sa place
- Association passager ↔ place ↔ voyage
- Suivi des passagers par voyage

---

### Réservations

#### Données à gérer

- Identifiant de la réservation
- Passager
- Voyage
- Place réservée
- Date de réservation
- Statut (en attente, confirmée, annulée)

#### Fonctionnalités

- Réservation d’une place à l’avance
- Blocage temporaire des places réservées
- Confirmation ou annulation de réservation

---

### Achat direct

#### Données à gérer

- Identifiant de l’achat
- Passager
- Voyage
- Place attribuée
- Montant payé
- Date d’achat

#### Fonctionnalités

- Attribution immédiate d’une place
- Mise à jour instantanée du statut de la place
- Enregistrement du paiement

---

## 8. Gestion des utilisateurs

### Données à gérer

- Identifiant utilisateur
- Nom d’utilisateur
- Mot de passe (hashé)
- Rôle (administrateur, gestionnaire, agent)

### Fonctionnalités

- Authentification
- Gestion des rôles et permissions
- Journal des actions (audit)

---

## 8. Rapports et statistiques

### Fonctionnalités

- Rapport journalier
- Rapport mensuel
- Rentabilité par véhicule
- Rentabilité par ligne
- Taux de remplissage des voyages

---

## 9. Fonctionnalités complémentaires

- Export des données (PDF / Excel)
- Mode hors-ligne avec synchronisation
- Interface adaptée mobile
- Historique non modifiable pour traçabilité

---

## 10. Périmètre MVP (recommandé pour projet étudiant)

- Véhicules
- Chauffeurs
- Lignes
- Voyages
- Recettes et dépenses
- Rapport mensuel
