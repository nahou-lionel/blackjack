package modele;

import cartes.modele.Carte;
import cartes.modele.Paquet;

/**
 * Représente une main de cartes pour un joueur avec sa mise associée
 * Utilisé notamment lors du split où un joueur peut avoir plusieurs mains
 */
public class MainJoueur {
    private Paquet main;
    private int mise;
    private boolean estSplittee;
    private boolean peutEncoreTirerApresSplit; // Pour les As splittés (1 seule carte)

    /**
     * Crée une nouvelle main vide avec une mise
     *
     * @param mise La mise placée sur cette main
     */
    public MainJoueur(int mise) {
        this.main = new Paquet();
        this.mise = mise;
        this.estSplittee = false;
        this.peutEncoreTirerApresSplit = true;
    }

    /**
     * Crée une main avec une carte initiale et une mise
     *
     * @param carteInitiale La première carte de la main
     * @param mise          La mise placée sur cette main
     */
    public MainJoueur(Carte carteInitiale, int mise) {
        this.main = new Paquet();
        this.main.ajouterCarte(carteInitiale);
        this.mise = mise;
        this.estSplittee = false;
        this.peutEncoreTirerApresSplit = true;
    }

    public Paquet getMain() {
        return main;
    }

    public int getMise() {
        return mise;
    }

    public void setMise(int mise) {
        this.mise = mise;
    }

    public boolean estSplittee() {
        return estSplittee;
    }

    public void marquerCommeSplittee() {
        this.estSplittee = true;
    }

    public boolean peutEncoreTirer() {
        return peutEncoreTirerApresSplit;
    }

    public void interdireTirage() {
        this.peutEncoreTirerApresSplit = false;
    }

    /**
     * Ajoute une carte à cette main
     *
     * @param carte La carte à ajouter
     */
    public void recevoirCarte(Carte carte) {
        main.ajouterCarte(carte);
    }

    /**
     * Calcule le score de cette main
     *
     * @return Le score calculé
     */
    public int getScore() {
        return CalculateurScore.calculerScore(main);
    }

    /**
     * Vérifie si cette main a dépassé 21
     *
     * @return true si le score > 21
     */
    public boolean aDepasse() {
        return CalculateurScore.aDepasse(main);
    }

    /**
     * Vérifie si cette main est un blackjack naturel
     *
     * @return true si blackjack naturel (seulement si pas splittée)
     */
    public boolean estBlackjack() {
        // Un blackjack après split ne compte pas comme blackjack naturel
        if (estSplittee) {
            return false;
        }
        return CalculateurScore.estBlackjack(main);
    }

    /**
     * Vérifie si cette main peut être splittée (2 cartes de même hauteur)
     *
     * @return true si la main contient exactement 2 cartes de même hauteur
     */
    public boolean peutEtreSplittee() {
        if (main.getTaille() != 2) {
            return false;
        }
        Carte carte1 = main.getCarte(0);
        Carte carte2 = main.getCarte(1);
        return carte1.getHauteur() == carte2.getHauteur();
    }

    /**
     * Vide la main
     */
    public void vider() {
        main.vider();
        mise = 0;
        estSplittee = false;
        peutEncoreTirerApresSplit = true;
    }

    @Override
    public String toString() {
        return String.format("MainJoueur{cartes=%d, score=%d, mise=%d, splittee=%b}",
                main.getTaille(), getScore(), mise, estSplittee);
    }
}
