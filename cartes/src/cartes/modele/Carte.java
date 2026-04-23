package cartes.modele;

public class Carte {

    private Couleur couleur;
    private Hauteur hauteur;

    public Carte(Couleur couleur, Hauteur hauteur) {
        this.couleur = couleur;
        this.hauteur = hauteur;
    }

    public Couleur getCouleur() {
        return couleur;
    }

    public Hauteur getHauteur() {
        return hauteur;
    }

    @Override
    public String toString() {
        return "Carte " + this.hauteur + " de " + this.couleur;
    }

}
