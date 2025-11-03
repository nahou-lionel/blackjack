# 🎰 Règles du Blackjack

## 🎯 Objectif du jeu

**But** : Avoir une main dont la valeur est **plus proche de 21** que celle du croupier, **sans dépasser 21**.

- Si vous dépassez 21 → Vous perdez immédiatement (on dit "bust" ou "sauté")
- Si le croupier dépasse 21 → Tous les joueurs restants gagnent
- Si vous avez 21 avec 2 cartes (As + figure/10) → **Blackjack** ! Vous gagnez 1.5× votre mise

---

## 🃏 Valeur des cartes

| Carte            | Valeur                                                        |
| ---------------- | ------------------------------------------------------------- |
| 2 à 10           | Valeur nominale (2 = 2 points, 10 = 10 points)                |
| Valet, Dame, Roi | 10 points                                                     |
| **As**           | **1 OU 11 points** (au choix, selon ce qui arrange le joueur) |

### Exemple de comptage avec As :

```
Main 1 : As + 7 = 18  (As vaut 11)
         ou = 8   (As vaut 1)
         → On choisit 18 (c'est mieux)

Main 2 : As + 7 + 5 = 13  (As vaut 1, sinon 23 = bust!)
```

**Terminologie** :

- **As "souple" (soft)** : L'As compte pour 11 sans faire dépasser 21
- **As "dur" (hard)** : L'As compte pour 1

---

## 👥 Participants

- **Le croupier** (dealer) : Représente la maison (le casino)
- **Les joueurs** : De 1 à 7 joueurs généralement (dans votre cas, 1 humain + plusieurs IA)

**Important** : Les joueurs jouent **contre le croupier**, pas entre eux !

---

## 🎲 Déroulement d'une partie

### Phase 1️⃣ : Mise (Bet)

Chaque joueur place sa mise sur la table.

```
Joueur 1 mise : 50 jetons
Joueur 2 mise : 100 jetons
Joueur 3 mise : 25 jetons
```

---

### Phase 2️⃣ : Distribution initiale

Le croupier distribue **2 cartes** à chaque joueur et à lui-même :

- **Joueurs** : 2 cartes **faces visibles** ✅
- **Croupier** : 1 carte **face visible** ✅ + 1 carte **face cachée** ❌

**Exemple** :

```
Croupier :   [7♠]  [???]
             visible  cachée

Joueur 1 :   [10♥] [6♣]  = 16 points
Joueur 2 :   [A♠]  [K♦]  = 21 points → BLACKJACK !
Joueur 3 :   [5♦]  [7♥]  = 12 points
```

**Cas spécial : Blackjack naturel**

- Si un joueur a un **Blackjack** (As + figure/10 en 2 cartes), il gagne immédiatement 1.5× sa mise
- Sauf si le croupier a aussi un Blackjack → Égalité (push), le joueur récupère sa mise

---

### Phase 3️⃣ : Tour des joueurs

Chaque joueur joue **à tour de rôle**, de gauche à droite. Pour chaque joueur, plusieurs actions possibles :

#### 🔹 **TIRER** (Hit)

Demander une carte supplémentaire.

```
Joueur 1 :   [10♥] [6♣]  = 16 points
             ↓ TIRE
             [10♥] [6♣] [3♠]  = 19 points
```

On peut tirer autant de cartes qu'on veut, tant qu'on ne dépasse pas 21.

#### 🔹 **RESTER** (Stand)

Garder sa main actuelle et terminer son tour.

```
Joueur 1 :   [10♥] [6♣] [3♠]  = 19 points
             → RESTE avec 19
```

#### 🔹 **DOUBLER** (Double Down) - Optionnel dans votre projet

- Seulement avec les **2 premières cartes**
- Double la mise initiale
- Reçoit **une seule carte supplémentaire** puis doit rester

```
Joueur 3 :   [5♦] [7♥]  = 12 points, mise : 25 jetons
             ↓ DOUBLE (mise devient 50 jetons)
             [5♦] [7♥] [9♣]  = 21 points
             → Termine automatiquement
```

**Quand doubler ?** Généralement sur un total de 10 ou 11 (bonne chance de faire 20 ou 21).

#### 🔹 **SÉPARER** (Split) - Optionnel dans votre projet

- Seulement si les **2 premières cartes ont la même valeur**
- Sépare la main en 2 mains indépendantes
- Place une nouvelle mise égale pour la 2ème main
- Reçoit une nouvelle carte pour chaque main

```
Joueur 2 :   [8♠] [8♥]  = 16 points
             ↓ SPLIT
Main A :     [8♠] [?]
Main B :     [8♥] [?]

             Après distribution :
Main A :     [8♠] [10♦]  = 18 points
Main B :     [8♥] [3♣]   = 11 points
             → Chaque main joue indépendamment
```

#### 🔹 **ABANDONNER** (Surrender) - Rare, optionnel

- Abandonner la main
- Récupère la moitié de sa mise

---

### Phase 4️⃣ : Tour du croupier

Une fois que **tous les joueurs** ont terminé, le croupier :

1. **Retourne sa carte cachée**
2. **Suit des règles strictes** (pas de choix) :
   - **Tire** (Hit) si son total est **< 17**
   - **Reste** (Stand) si son total est **≥ 17**

**Exemple** :

```
Croupier :   [7♠] [10♣]  = 17 points
             → RESTE automatiquement (≥ 17)

Croupier :   [7♠] [6♣]  = 13 points
             → TIRE automatiquement (< 17)
             [7♠] [6♣] [9♦]  = 22 points
             → BUST ! Tous les joueurs restants gagnent
```

**Variante importante** :

- Certains casinos : le croupier **reste sur 17 souple** (Soft 17 = As + 6)
- D'autres casinos : le croupier **tire sur 17 souple**

**Pour simplifier** : Le croupier reste sur tout 17 (dur ou souple).

---

### Phase 5️⃣ : Résolution et paiements

On compare la main de chaque joueur avec celle du croupier :

#### Cas 1 : Joueur a dépassé 21 (bust)

```
Joueur perdu ❌ → Perd sa mise
```

#### Cas 2 : Croupier a dépassé 21 (bust)

```
Tous les joueurs restants gagnent ✅ → Reçoivent 1× leur mise
```

#### Cas 3 : Personne n'a bust

On compare les scores :

| Situation                  | Résultat                                      |
| -------------------------- | --------------------------------------------- |
| Joueur > Croupier          | Joueur **gagne** ✅ → Reçoit 1× sa mise       |
| Joueur < Croupier          | Joueur **perd** ❌ → Perd sa mise             |
| Joueur = Croupier          | **Égalité** (Push) 🤝 → Récupère sa mise      |
| Joueur a Blackjack naturel | Joueur **gagne** 🎉 → Reçoit **1.5×** sa mise |

**Exemple complet** :

```
Croupier :   [7♠] [10♣]  = 17 points

Joueur 1 :   [10♥] [6♣] [3♠]  = 19 points
             → 19 > 17 → GAGNE 1× sa mise (50 jetons)

Joueur 2 :   [A♠]  [K♦]  = 21 points (Blackjack)
             → GAGNE 1.5× sa mise (100 × 1.5 = 150 jetons)

Joueur 3 :   [5♦] [7♥] [9♣]  = 21 points
             → 21 > 17 → GAGNE 1× sa mise (25 jetons)
             (Pas 1.5× car ce n'est pas un Blackjack naturel)

Joueur 4 :   [10♠] [5♣] [9♥]  = 24 points
             → BUST → PERD sa mise (déjà perdue avant le tour du croupier)
```

---

## 🎮 Exemple de partie complète

### Situation initiale

```
Joueur (Vous) : 100 jetons
IA Robot 1    : 100 jetons
IA Robot 2    : 100 jetons
Croupier      : ∞ jetons (la maison)
```

### 1. Mises

```
Vous      mise 10 jetons  → Reste : 90 jetons
Robot 1   mise 20 jetons  → Reste : 80 jetons
Robot 2   mise 15 jetons  → Reste : 85 jetons
```

### 2. Distribution

```
Croupier :     [9♦]  [???]

Vous :         [10♠] [7♣]  = 17 points
Robot 1 :      [5♥]  [6♠]  = 11 points
Robot 2 :      [K♦]  [A♠]  = 21 points → BLACKJACK !
```

### 3. Tours des joueurs

**Tour de Robot 2** :

```
Robot 2 a un Blackjack → Gagne automatiquement 1.5× sa mise = 22.5 jetons
(arrondi à 23 jetons en pratique)
Nouveau solde : 85 + 15 + 23 = 123 jetons
```

**Tour de Vous** :

```
Vous avez : [10♠] [7♣] = 17 points
Carte visible du croupier : 9

Options :
- TIRER : Risqué (17 est moyen, mais le croupier a 9)
- RESTER : Plutôt safe

→ Vous décidez : RESTER
```

**Tour de Robot 1** :

```
Robot 1 a : [5♥] [6♠] = 11 points
→ Robot 1 : TIRE
→ Reçoit [7♦] : [5♥] [6♠] [7♦] = 18 points
→ Robot 1 : RESTE
```

### 4. Tour du croupier

```
Croupier retourne sa carte cachée :
Croupier : [9♦] [8♣] = 17 points
→ Le croupier a 17 → RESTE (≥ 17)
```

### 5. Résolution

```
Croupier : 17 points

Vous :     17 points → 17 = 17 → ÉGALITÉ → Récupère 10 jetons
           Solde : 90 + 10 = 100 jetons (inchangé)

Robot 1 :  18 points → 18 > 17 → GAGNE → Reçoit 20 jetons
           Solde : 80 + 20 + 20 = 120 jetons

Robot 2 :  Blackjack → (déjà payé avant)
           Solde : 123 jetons
```

---

## 📊 Stratégies de base (pour vos IA)

### Stratégie conservative (robot prudent)

```
- Si score ≥ 17 → RESTER
- Si score < 17 → TIRER
```

### Stratégie agressive (robot risqué)

```
- Si score ≥ 19 → RESTER
- Si score < 19 → TIRER
- Doubler sur 10 ou 11
```

### Stratégie basique (optimale mathématiquement)

Basée sur un tableau de décision :

**En fonction de votre main ET de la carte visible du croupier** :

```
Votre main : 16
Carte croupier : 7, 8, 9, 10, As → TIRER
Carte croupier : 2, 3, 4, 5, 6   → RESTER

Votre main : 11
→ Toujours DOUBLER

Votre main : Paire de 8 ou As
→ Toujours SPLIT
```

_(Cette stratégie est complexe, vous pouvez l'implémenter progressivement)_

---

## ⚙️ Règles simplifiées pour votre projet

Pour commencer, je recommande :

### Version minimale ✅

- Mises fixes (ex : 10 jetons par partie)
- Actions : TIRER et RESTER uniquement
- Croupier reste sur 17
- Pas de split, pas de double, pas d'assurance

### Version complète (optionnel) ⭐

- Mises variables
- Actions : TIRER, RESTER, DOUBLER
- Split sur paires
- Assurance contre le Blackjack du croupier

---

## 🎯 Résumé du flow pour votre code

```
1. INITIALISATION
   - Créer le sabot (6 jeux de 52 cartes mélangés)
   - Donner des jetons aux joueurs

2. BOUCLE DE PARTIE
   ┌─────────────────────────────────────┐
   │                                     │
   │ A. Phase de MISE                    │
   │    - Chaque joueur mise             │
   │                                     │
   │ B. Phase de DISTRIBUTION            │
   │    - 2 cartes à chaque joueur       │
   │    - 2 cartes au croupier (1 cachée)│
   │    - Vérifier Blackjack             │
   │                                     │
   │ C. Phase TOURS DES JOUEURS          │
   │    Pour chaque joueur :             │
   │    - Tant que joueur n'a pas terminé│
   │      * Afficher options             │
   │      * Exécuter action choisie      │
   │      * Vérifier si bust             │
   │                                     │
   │ D. Phase TOUR DU CROUPIER           │
   │    - Révéler carte cachée           │
   │    - Tirer jusqu'à ≥ 17             │
   │                                     │
   │ E. Phase RÉSOLUTION                 │
   │    - Comparer scores                │
   │    - Distribuer gains/pertes        │
   │    - Afficher résultats             │
   │                                     │
   │ F. VÉRIFIER FIN DE JEU              │
   │    - Joueurs sans jetons → éliminés│
   │    - Demander rejouer ?             │
   │                                     │
   └─────────────────────────────────────┘
         ↓
    Retour à 2.A ou FIN
```
