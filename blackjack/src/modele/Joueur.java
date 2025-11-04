package modele;

import cartes.modele.Carte;
import cartes.modele.Paquet;

public class Joueur {
    private String nom;
    private Paquet main;
    // private int jetons;
    private int miseActuelle;

    // Constructeur
    public Joueur(String nom) {
        this.nom = nom;
        this.miseActuelle = 0;
        this.main = new Paquet();
    }

    public String getNom() {
        return this.nom;
    }

    public int getMiseActuelle() {
        return this.miseActuelle;
    }

    // Cette méthode place la mise du joueur 
    public void miser(int montant) {
        this.miseActuelle = montant;
    }

    // Cette méthode ajoute une carte à la main du joueur
    public void recevoirCarte(Carte c) {
        this.main.ajouterCarte(c);
    }

    // Cette méthode calcule le score du joueur
    public int getScore() {
        return CalculateurScore.calculerScore(main);
    }

    // Cette méthode vérifie si le score dépasse le seuil 21 ou non
    public boolean aDepasse() {
        return CalculateurScore.aDepasse(main);
    }

    // Cette méthode réinitialise le jeu
    public void reinitialiser() {
        main.vider();
        miseActuelle = 0;
    }

    // Cette méthode permet au joueur de rester
    public Action rester(){
        return Action.RESTER;
    }

    // Cette méthode permet au joueur de tirer
    public Action tirer(){
        return Action.TIRER;
    }

    // Cette méthode permet au joueur de doubler
    public Action doubler(){
        return Action.DOUBLER;
    }
}