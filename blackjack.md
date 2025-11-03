## Architecture du package Blackjack





## Implementation

#### 1. `CalculateurScore.java`

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

#### 2. `Joueur.java`

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

#### 3. `Croupier.java`

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

#### 4. `PartieBlackjack.java`

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

#### 5.  `StrategieJoueur.java`

```java
public interface StrategieJoueur {
    Action decider(int scoreJoueur, int carteVisibleCroupier);
}

enum Action {
    TIRER, RESTER, DOUBLER
}
```

#### 6. Nos stratégies

- **StrategieConservatrice** : Reste à 17+
- **StrategieBasique** : Tableau de décision simple

---

#### 7. Nos vues

- **VueMainCroupier** : Hérite de VuePaquet, gère la carte cachée
- **VueMainJoueur** : Hérite de VuePaquetEventail, affiche score et jetons
- **VueTable** : Assemble tout

#### 8. Contrôleur

- Connecte les boutons aux actions du modèle
- Met à jour les vues

---

### ⚠️ Attention

- Ne pas oublier de vider les mains entre les parties
- Bien gérer le cas où le joueur/croupier a un Blackjack dès le début
- Penser à remettre les cartes dans le sabot quand il est trop vide

---