package cartes.vue;

import java.awt.*;

import cartes.modele.Carte;
import cartes.modele.Paquet;

/**
 * Vue d'un paquet avec cartes visibles alignées horizontalement
 */
public class VuePaquetVisible extends VuePaquet implements VuePaquetInteractive {

    private static final int ESPACEMENT_CARTE = 80; // Espacement entre cartes
    private int carteEnSurbrillance = -1;

    /**
     * Constructeur avec couleur de fond par défaut
     * @param paquet Le paquet à afficher
     */
    public VuePaquetVisible(Paquet paquet) {
        super(paquet);
        calculerDimensions();
    }

    /**
     * Constructeur avec couleur de fond personnalisée
     * @param paquet Le paquet à afficher
     * @param backgroundColor Couleur de fond (null pour défaut, new Color(0,0,0,0) pour transparent)
     */
    public VuePaquetVisible(Paquet paquet, Color backgroundColor) {
        super(paquet, backgroundColor);
        calculerDimensions();
    }

    /**
     * Calcule les dimensions du panel
     */
    private void calculerDimensions() {
        int nbCartes = paquet.getTaille();
        int largeur = (nbCartes > 0) ? nbCartes * ESPACEMENT_CARTE + 20 : LARGEUR_CARTE + 40;

        setPreferredSize(new Dimension(largeur, HAUTEUR_CARTE + 40));
    }

    @Override
    protected void dessinerPaquet(Graphics g) {
        int nbCartes = paquet.getTaille();

        if (nbCartes == 0) {
            // Dessiner un cadre vide
            Graphics2D g2d = (Graphics2D) g;
            g2d.setColor(new Color(200, 200, 200));
            g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 0, new float[] { 5 }, 0));
            g2d.drawRoundRect(20, 20, LARGEUR_CARTE, HAUTEUR_CARTE, RAYON_COIN, RAYON_COIN);

            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.ITALIC, 12));
            g2d.drawString("Vide", 30, HAUTEUR_CARTE / 2 + 15);
            return;
        }

        // Dessiner chaque carte
        for (int i = 0; i < nbCartes; i++) {
            Carte carte = paquet.getCarte(i);
            int x = 20 + i * ESPACEMENT_CARTE;
            int y = 20;

            // Effet de surbrillance
            if (i == carteEnSurbrillance) {
                Graphics2D g2d = (Graphics2D) g;

                // Cadre vert
                g2d.setColor(new Color(0, 255, 0));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(x - 2, y - 2, LARGEUR_CARTE + 4,
                        HAUTEUR_CARTE + 4, RAYON_COIN, RAYON_COIN);
            }

            dessinerCarte(g, carte, x, y);
        }
    }

    /**
     * Retourne l'index de la carte aux coordonnées données
     */
    @Override
    public int getIndiceCarte(int x, int y) {
        int nbCartes = paquet.getTaille();

        for (int i = 0; i < nbCartes; i++) {
            int carteX = 20 + i * ESPACEMENT_CARTE;
            int carteY = 20;

            if (x >= carteX && x <= carteX + LARGEUR_CARTE &&
                    y >= carteY && y <= carteY + HAUTEUR_CARTE) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Définit la carte à mettre en surbrillance
     */
    @Override
    public void setCarteEnSurbrillance(int index) {
        if (this.carteEnSurbrillance != index) {
            this.carteEnSurbrillance = index;
            repaint();
        }
    }

    @Override
    public void somethingHasChanged(Object source) {
        calculerDimensions();
        revalidate();
        repaint();
    }
}