package cartes.modele;

import java.util.*;
import util.observer.*;

/**
 * Classe représentant un paquet de cartes
 */
public class Paquet extends AbstractListenableModel {
    private List<Carte> cartes;

    // Hauteurs pour un jeu de 32 cartes (7 à As)
    private static final List<Hauteur> HAUTEURS_32 = List.of(
            Hauteur.SEPT, Hauteur.HUIT, Hauteur.NEUF, Hauteur.DIX,
            Hauteur.VALET, Hauteur.DAME, Hauteur.ROI, Hauteur.AS);

    // Toutes les hauteurs pour un jeu de 52 cartes (2 à As)
    private static final List<Hauteur> HAUTEURS_52 = List.of(Hauteur.values());

    /**
     * Constructeur créant un paquet vide
     */
    public Paquet() {
        this.cartes = new ArrayList<>();
    }

    /**
     * Constructeur créant un paquet avec une liste de cartes
     */
    public Paquet(List<Carte> cartes) {
        this.cartes = new ArrayList<>(cartes);
    }

    /**
     * Retourne la liste des cartes (attention à l'encapsulation!)
     */
    public List<Carte> getCartes() {
        return cartes;
    }

    /**
     * Factory: Crée un paquet de 52 cartes standard
     */
    public static Paquet creerPaquet52() {
        Paquet paquet = new Paquet();
        for (Couleur couleur : Couleur.values()) {
            for (Hauteur hauteur : HAUTEURS_52) {
                paquet.cartes.add(new Carte(couleur, hauteur));
            }
        }
        return paquet;
    }

    /**
     * Factory: Crée un paquet de 32 cartes (du 7 à l'As)
     */
    public static Paquet creerPaquet32() {
        Paquet paquet = new Paquet();
        for (Couleur couleur : Couleur.values()) {
            for (Hauteur hauteur : HAUTEURS_32) {
                paquet.cartes.add(new Carte(couleur, hauteur));
            }
        }
        return paquet;
    }

    /**
     * Factory: Crée un paquet vide
     */
    public static Paquet creerPaquetVide() {
        return new Paquet();
    }

    /**
     * Factory: Crée un paquet avec plusieurs jeux de 52 cartes mélangés
     * Utile pour le Blackjack qui utilise généralement 6 jeux
     */
    public static Paquet creerPaquetMultiple(int nombreJeux) {
        Paquet paquet = new Paquet();
        for (int i = 0; i < nombreJeux; i++) {
            for (Couleur couleur : Couleur.values()) {
                for (Hauteur hauteur : HAUTEURS_52) {
                    paquet.cartes.add(new Carte(couleur, hauteur));
                }
            }
        }
        paquet.melanger();
        return paquet;
    }

    /**
     * Ajoute une carte au paquet
     */
    public void ajouterCarte(Carte carte) {
        cartes.add(carte);
        fireChange();
    }

    /**
     * Retire et retourne la première carte du paquet
     */
    public Carte retirerPremiereCarte() {
        if (cartes.isEmpty()) {
            throw new IllegalStateException("Le paquet est vide");
        }
        fireChange();
        return cartes.remove(0);
    }

    /**
     * Retire et retourne la carte à l'index spécifié
     */
    public Carte retirerCarte(int index) {
        if (index < 0 || index >= cartes.size()) {
            throw new IndexOutOfBoundsException("Index invalide: " + index);
        }
        fireChange();
        return cartes.remove(index);
    }

    /**
     * Retourne la carte à l'index spécifié sans la retirer
     */
    public Carte getCarte(int index) {
        return cartes.get(index);
    }

    /**
     * Mélange le paquet
     */
    public void melanger() {
        fireChange();
        Collections.shuffle(cartes);
    }

    /**
     * Coupe le paquet à un endroit aléatoire (hors première et dernière carte)
     * Retourne un nouveau paquet contenant les cartes de la position de coupe
     * jusqu'à la fin
     * Le paquet actuel conserve les cartes de 0 à la position de coupe (exclu)
     * 
     * @return nouveau paquet avec la partie coupée
     */
    public Paquet couper() {
        if (cartes.size() <= 2) {
            return Paquet.creerPaquetVide(); // Pas assez de cartes pour couper
        }

        Random random = new Random();
        // Coupe entre la 2ème carte et l'avant-dernière
        int positionCoupe = random.nextInt(cartes.size() - 2) + 1;

        // Crée un nouveau paquet avec les cartes de positionCoupe à la fin
        List<Carte> cartesCoupees = new ArrayList<>(cartes.subList(positionCoupe, cartes.size()));
        Paquet paquetCoupe = new Paquet(cartesCoupees);

        // Conserve uniquement les cartes de 0 à positionCoupe dans le paquet actuel
        cartes = new ArrayList<>(cartes.subList(0, positionCoupe));

        fireChange();
        return paquetCoupe;
    }

    /**
     * Vide le paquet
     */
    public void vider() {
        fireChange();
        cartes.clear();
    }

    /**
     * Retourne le nombre de cartes dans le paquet
     */
    public int getTaille() {
        return cartes.size();
    }

    /**
     * Vérifie si le paquet est vide
     */
    public boolean estVide() {
        return cartes.isEmpty();
    }

    /**
     * Vérifie si le paquet contient une carte donnée
     */
    public boolean contient(Carte carte) {
        return cartes.contains(carte);
    }

    /**
     * Trie les cartes par hauteur (de 2 à As)
     */
    public void trierParHauteur() {
        Collections.sort(cartes, Comparator.comparing(Carte::getHauteur));
        fireChange();
    }

    /**
     * Trie les cartes par couleur puis par hauteur
     */
    public void trierParCouleur() {
        Collections.sort(cartes, Comparator
                .comparing(Carte::getCouleur)
                .thenComparing(Carte::getHauteur));
        fireChange();
    }

    @Override
    public String toString() {
        return "Paquet{" + cartes.size() + " cartes}";
    }
}