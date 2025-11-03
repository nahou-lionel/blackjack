## Architecture du package Blackjack

### Structure recommandée

```
fr.unicaen.blackjack/
├── modele/
│   ├── PartieBlackjack.java      // Orchestrateur principal
│   ├── Joueur.java                // Joueur (humain ou IA)
│   ├── Croupier.java              // Le croupier
│   ├── CalculateurScore.java     // Calcul des scores (gère les As)
│   ├── EtatPartie.java           // Enum des états
│   └── Resultat.java             // Résultat d'un joueur (gagné/perdu/égalité)
├── strategie/
│   ├── StrategieJoueur.java      // Interface
│   ├── StrategieHumain.java      // Attend input utilisateur
│   ├── StrategieConservatrice.java
│   └── StrategieBasique.java
├── vue/
│   ├── VuePartieBlackjack.java   // JFrame principale
│   ├── VueTable.java             // Panel central (table verte)
│   ├── VueMainJoueur.java        // extends VuePaquetEventail
│   ├── VueMainCroupier.java      // Vue spéciale (1 carte cachée)
│   └── VuePanneauControles.java  // Boutons Tirer/Rester/etc.
└── controleur/
    └── ControleurPartie.java     // Gère les interactions
```

---

## Implementation

#### 1. Créer `CalculateurScore.java`

```java
public class CalculateurScore {
    public static int calculerScore(Paquet main) {
        // Compter les points
        // Gérer les As (1 ou 11)
        // Retourner le meilleur score ≤ 21
    }

    public static boolean estBlackjack(Paquet main) {
        // 2 cartes + score = 21
    }

    public static boolean aDepasse(Paquet main) {
        // score > 21
    }
}
```

#### 2. Créer `Joueur.java`

```java
public class Joueur {
    private String nom;
    private Paquet main;
    private int jetons;
    private int miseActuelle;
    private StrategieJoueur strategie;

    public void miser(int montant) { ... }
    public void recevoirCarte(Carte c) { ... }
    public int getScore() {
        return CalculateurScore.calculerScore(main);
    }
    public boolean aDepasse() { ... }
    public void reinitialiser() {
        main.vider();
        miseActuelle = 0;
    }
}
```

#### 3. Créer `Croupier.java`

```java
public class Croupier {
    private Paquet main;
    private boolean cartesCachees = true;

    public boolean doitTirer() {
        return getScore() < 17;
    }

    public void revelerCartes() {
        cartesCachees = false;
    }
}
```

#### 4. Créer `PartieBlackjack.java`

```java
public class PartieBlackjack {
    private Paquet sabot;
    private Croupier croupier;
    private List<Joueur> joueurs;
    private int indiceJoueurActif;

    public void demarrerNouvellePartie() { ... }
    public void distribuerCartesInitiales() { ... }
    public void joueurTire() { ... }
    public void joueurReste() { ... }
    public void jouerTourCroupier() { ... }
    public void determinerGagnants() { ... }
}
```

---

#### 5. Créer l'interface `StrategieJoueur.java`

```java
public interface StrategieJoueur {
    Action decider(int scoreJoueur, int carteVisibleCroupier);
}

enum Action {
    TIRER, RESTER, DOUBLER
}
```

#### 6. Implémenter les stratégies

- **StrategieConservatrice** : Reste à 17+
- **StrategieBasique** : Tableau de décision simple

---

#### 7. Créer les vues

- **VueMainCroupier** : Hérite de VuePaquet, gère la carte cachée
- **VueMainJoueur** : Hérite de VuePaquetEventail, affiche score et jetons
- **VueTable** : Assemble tout

#### 8. Créer le contrôleur

- Connecte les boutons aux actions du modèle
- Met à jour les vues

---

### ⚠️ Attention

- Ne pas oublier de vider les mains entre les parties
- Bien gérer le cas où le joueur/croupier a un Blackjack dès le début
- Penser à remettre les cartes dans le sabot quand il est trop vide

---

Voulez-vous que je commence par vous détailler une classe en particulier, ou que je vous montre un exemple de code pour `CalculateurScore` qui est crucial ? 🎴
