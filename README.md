# INF1163 — Borne de prêt (Swing) – Bilingue FR/EN + Reçu TXT

**Date du pack** : 2025-11-13

Ce projet illustre un mini-système de borne de prêt en libre-service
(style bibliothèque universitaire) avec interface graphique Swing.

Cette version ajoute :
- Interface bilingue Français / Anglais
- Récapitulatif des emprunts dans la langue choisie
- Reçu d'emprunt formaté style bibliothèque, aussi bilingue
- Option pour enregistrer le reçu dans un fichier texte (`.txt`)

## Structure
- `src/ca/uqo/inf1163/borne/Main.java` : point d'entrée.
- `src/ca/uqo/inf1163/borne/ui/*.java` : interface graphique (connexion + emprunt, bilingue).
- `src/ca/uqo/inf1163/borne/domain/*.java` : couche d'affaires (usagers, livres, exemplaires, catalogue, session, langue).
- `src/ca/uqo/inf1163/borne/service/*.java` : services d'authentification, d'emprunt et de reçu.
- `resources/catalogue.csv` : catalogue d'exemple.

## Exécution (Eclipse ou IntelliJ)
1. Crée un projet Java (JDK 17 ou plus).
2. Copie le contenu du dossier `src` dans le `src` du projet.
3. Ajoute le dossier `resources` à la racine du projet (au même niveau que `src`).
4. Exécute la classe `ca.uqo.inf1163.borne.Main`.

   - Comptes de test : `jean123/1234`, `jeanne456/4567`.
   - RFID : utilise un des codes de `resources/catalogue.csv` (6 chiffres).
   - Langue : choisir Français ou English dans la combo de l'écran de connexion.

Lorsqu'un emprunt est terminé et que l'option "Imprimer un reçu" est cochée :
- le reçu est affiché dans une fenêtre (JTextArea monospacée)
- l'application propose d'enregistrer ce reçu dans un fichier texte (`.txt`).
# Borne_de_pret
