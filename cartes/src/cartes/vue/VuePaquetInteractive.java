package cartes.vue;

/**
 * Interface pour les vues de paquet qui supportent l'interaction souris
 */
public interface VuePaquetInteractive {
    /**
     * Retourne l'index de la carte aux coordonnées données
     * 
     * @param x coordonnée X
     * @param y coordonnée Y
     * @return index de la carte ou -1 si aucune carte
     */
    int getIndiceCarte(int x, int y);

    /**
     * Définit la carte à mettre en surbrillance
     * 
     * @param index index de la carte ou -1 pour aucune
     */
    void setCarteEnSurbrillance(int index);
}