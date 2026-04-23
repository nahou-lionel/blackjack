# Blackjack

Jeu de Blackjack en Java avec interface graphique Swing. Oppose un joueur humain au croupier, avec la possibilité d'ajouter un joueur robot.

## Fonctionnalités

- Règles complètes : Hit, Stand, Double, Split (multi-mains)
- Gestion de banque et système de paiement (blackjack naturel 3:2, victoire 1:1, push)
- Mode robot : deux stratégies d'IA sélectionnables au lancement
- Bibliothèque `cartes` indépendante et réutilisable

## Prérequis

- Java 11+
- [Apache Ant](https://ant.apache.org/)

## Lancement

La bibliothèque `cartes` doit être distribuée avant de lancer le jeu (opération unique) :

```bash
cd cartes
ant dist
```

Puis pour lancer le jeu :

```bash
cd blackjack

ant run                  # Mode solo (joueur vs croupier)
ant run-robot            # Avec robot — stratégie simple
ant run-robot-optimal    # Avec robot — stratégie optimale (Basic Strategy)

# Ou après génération du JAR :
java -jar dist/blackjack-0.1.jar
java -jar dist/blackjack-0.1.jar robot
java -jar dist/blackjack-0.1.jar robot optimal
```

## Architecture

Le projet est organisé en deux modules :

**`cartes/`** — bibliothèque générique de jeux de cartes (Carte, Paquet, vues Swing avec pattern Observer).

**`blackjack/`** — jeu s'appuyant sur la lib `cartes`, structuré en MVC :

```
src/
├── blackjack/
│   └── MainClass.java              # Point d'entrée, parsing des arguments
├── modele/
│   ├── joueur/
│   │   ├── Joueur.java             # Joueur humain (banque, mains, mises)
│   │   ├── JoueurRobot.java        # Joueur IA (délègue à une StrategieJoueur)
│   │   ├── MainJoueur.java         # Une main de jeu (cartes + mise)
│   │   └── Croupier.java           # Croupier (règle fixe : tirer sous 17)
│   ├── strategie/
│   │   ├── StrategieJoueur.java    # Interface commune (action, mise, split)
│   │   ├── StrategieSimple.java    # Tirer < 17, split As/8 uniquement
│   │   ├── StrategieOptimale.java  # Basic Strategy (hard totals + paires)
│   │   └── StrategieCroupier.java  # Règle fixe du croupier
│   ├── partie/
│   │   ├── PartieBlackjack.java    # Orchestration : distribution, actions, résolution
│   │   ├── EtatPartie.java         # États successifs d'une manche
│   │   ├── Action.java             # TIRER, RESTER, DOUBLER, SÉPARER
│   │   ├── CalculateurScore.java   # Calcul de score (As 1 ou 11)
│   │   └── ResultatManche.java     # Résultat par main (victoire, défaite, push, BJ)
│   └── paiement/
│       └── ServicePaiement.java    # Calcul et application des gains/pertes
└── vue/
    ├── GUI.java                    # Fenêtre principale
    ├── VueDepart.java              # Écran d'accueil
    └── VuePartie.java              # Table de jeu
```

## IA — Stratégies

**Stratégie Simple** : tirer si score < 17, rester sinon. Split uniquement sur As et 8. Mise à 10 % de la banque.

**Stratégie Optimale** (Basic Strategy) : décisions basées sur la carte visible du croupier.
- Hard totals : double sur 11 systématiquement, double sur 10/9 selon croupier, stand sur 17+
- Split : As/8 toujours, 10 jamais, autres paires selon carte du croupier
- Mise entre 15 % et 25 % de la banque

Les deux stratégies implémentent `StrategieJoueur`, rendant le robot extensible sans modifier `PartieBlackjack`.
