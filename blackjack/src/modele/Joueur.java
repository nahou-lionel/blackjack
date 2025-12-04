package modele;

import java.util.ArrayList;
import java.util.List;

import cartes.modele.Carte;
import cartes.modele.Hauteur;
import cartes.modele.Paquet;

public class Joueur {
    private String nom;
    private List<MainJoueur> mains;
    private int banque;

    // Constructeur Joueur
    public Joueur(String nom) {
        this.nom = nom;
        this.banque = 500;
        this.mains = new ArrayList<>();
        // Créer une main initiale vide
        this.mains.add(new MainJoueur(0));
    }

    public Joueur() {
        this.mains = new ArrayList<>();
        this.mains.add(new MainJoueur(0));
    }

    public String getNom() {
        return this.nom;
    }

    public int getBanque(){
        return this.banque;
    }

    public void setBanque(int banque){
        this.banque = banque;
    }

    /**
     * Retourne la main principale (première main)
     * Pour compatibilité avec le code existant
     *
     * @return Le paquet de la première main
     */
    public Paquet getMain(){
        return this.mains.get(0).getMain();
    }

    /**
     * Retourne toutes les mains du joueur
     *
     * @return Liste des mains
     */
    public List<MainJoueur> getMains() {
        return mains;
    }

    /**
     * Retourne une main spécifique
     *
     * @param index Index de la main
     * @return La main à l'index donné
     */
    public MainJoueur getMainAIndex(int index) {
        return mains.get(index);
    }

    /**
     * Retourne le nombre de mains actives
     *
     * @return Nombre de mains
     */
    public int getNombreMains() {
        return mains.size();
    }

    /**
     * Retourne la mise de la main principale
     * Pour compatibilité avec le code existant
     *
     * @return La mise de la première main
     */
    public int getMiseActuelle() {
        return this.mains.get(0).getMise();
    }

    /**
     * Vérifie si le joueur a des mains splittées
     *
     * @return true si le joueur a plus d'une main
     */
    public boolean aSplitte() {
        return mains.size() > 1;
    }

    // Cette méthode place la mise du joueur
    public void miser(int montant) {
        this.mains.get(0).setMise(montant);
        this.banque = this.banque - montant;
    }

    // Cette méthode ajoute une carte à la main du joueur (première main par défaut)
    public void recevoirCarte(Carte c) {
        this.mains.get(0).recevoirCarte(c);
    }

    /**
     * Ajoute une carte à une main spécifique
     *
     * @param c     La carte à ajouter
     * @param index L'index de la main
     */
    public void recevoirCarte(Carte c, int index) {
        this.mains.get(index).recevoirCarte(c);
    }

    // Cette méthode calcule le score du joueur (première main par défaut)
    public int getScore() {
        return mains.get(0).getScore();
    }

    /**
     * Calcule le score d'une main spécifique
     *
     * @param index Index de la main
     * @return Le score de la main
     */
    public int getScore(int index) {
        return mains.get(index).getScore();
    }

    // Cette méthode vérifie si le score dépasse le seuil 21 ou non (première main)
    public boolean aDepasse() {
        return mains.get(0).aDepasse();
    }

    /**
     * Vérifie si une main spécifique a dépassé 21
     *
     * @param index Index de la main
     * @return true si la main a dépassé 21
     */
    public boolean aDepasse(int index) {
        return mains.get(index).aDepasse();
    }

    /**
     * Sépare la main principale en deux mains (split)
     * Nécessite que la main ait exactement 2 cartes de même hauteur
     * et que le joueur ait assez d'argent pour doubler la mise
     *
     * @return true si le split a réussi, false sinon
     */
    public boolean separer() {
        MainJoueur mainPrincipale = mains.get(0);

        // Vérifier les conditions du split
        if (!mainPrincipale.peutEtreSplittee()) {
            return false;
        }

        int mise = mainPrincipale.getMise();

        // Vérifier que le joueur a assez d'argent
        if (banque < mise) {
            return false;
        }

        // Déduire la mise supplémentaire
        banque -= mise;

        // Récupérer les deux cartes
        Paquet paquetOriginal = mainPrincipale.getMain();
        Carte carte1 = paquetOriginal.getCarte(0);
        Carte carte2 = paquetOriginal.getCarte(1);

        // Créer deux nouvelles mains
        MainJoueur main1 = new MainJoueur(carte1, mise);
        MainJoueur main2 = new MainJoueur(carte2, mise);

        // Marquer comme splittées
        main1.marquerCommeSplittee();
        main2.marquerCommeSplittee();

        // Si on split des As, interdire de tirer plus d'une carte
        if (carte1.getHauteur() == Hauteur.AS) {
            main1.interdireTirage();
            main2.interdireTirage();
        }

        // Remplacer la liste des mains
        mains.clear();
        mains.add(main1);
        mains.add(main2);

        return true;
    }

    // Cette méthode réinitialise le jeu
    public void reinitialiser() {
        mains.clear();
        mains.add(new MainJoueur(0));
    }

    // // Cette méthode permet au joueur de rester
    // public Action rester(){
    //     return Action.RESTER;
    // }

    // // Cette méthode permet au joueur de tirer
    // public Action tirer(){
    //     return Action.TIRER;
    // }

    // // Cette méthode permet au joueur de doubler
    // public Action doubler(){
    //     return Action.DOUBLER;
    // }
}