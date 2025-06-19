# 🕒 Projet Algorithmes Répartis – Simulation des Horloges Scalaire, Vectorielle et Matricielle

Ce projet universitaire en Java simule les horloges logiques utilisées dans les systèmes répartis : **scalaire**, **vectorielle** et **matricielle**, à travers une interface graphique interactive.

---

## 📌 Objectifs du projet

- Implémenter trois types d'horloges logiques :
  - 🧮 **Horloge scalaire** (Lamport)
  - 🧭 **Horloge vectorielle**
  - 🧠 **Horloge matricielle**
- Permettre à l'utilisateur de :
  - Lancer entre **1 et 4 processus**
  - Simuler des **événements locaux**
  - **Envoyer des messages** entre processus
  - Observer les **mises à jour des horloges** en temps réel

---

## 🖼️ Interface Graphique

- Interface Java Swing simple et intuitive
- Chaque processus est représenté avec :
  - Son horloge actuelle
  - Un bouton pour générer un événement local
  - Un bouton pour envoyer un message
- La zone centrale affiche l’évolution des horloges après chaque action

---

## 🧠 Fonctionnement

### 🔹 Horloge Scalaire
- Chaque événement local incrémente l’horloge de 1.
- En réception, on fait `max(local, reçu) + 1`.

### 🔹 Horloge Vectorielle
- Un vecteur de taille *N* (nombre de processus).
- Incrément de la propre position lors d’un événement local.
- Envoi/réception met à jour avec `max(local[i], reçu[i])` pour chaque *i*.

### 🔹 Horloge Matricielle
- Matrice *N x N* contenant l’état des vecteurs de tous les processus.
- Mise à jour plus complexe, conserve l’historique des communications.

---

## ⚙️ Technologies utilisées

- 🧩 **Java**
- 🎨 **Java Swing** (pour l’interface)
- 🧪 **Multithreading léger** (pour simuler la concurrence)
- 🗃️ **Design orienté objet** pour la modularité

---

## 🚀 Lancer le projet

### 1. Compilation

```bash
javac *.java
```
### Lancement de la simulation
java MatrixClockSimulator
