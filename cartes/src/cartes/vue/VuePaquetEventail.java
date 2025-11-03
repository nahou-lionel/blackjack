package cartes.vue;

import java.awt.*;
import java.awt.geom.*;

import cartes.modele.Carte;
import cartes.modele.Paquet;

/**
 * Vue d'un paquet en éventail
 */
public class VuePaquetEventail extends VuePaquet implements VuePaquetInteractive {

    private static final int ESPACEMENT_HORIZONTAL = 25; // pixels entre cartes
    private static final double ANGLE_MAX = 30.0; // degrés max de rotation
    private int carteEnSurbrillance = -1;

    public VuePaquetEventail(Paquet paquet) {
        super(paquet);
        calculerDimensions();
    }

    /**
     * Calcule les dimensions du panel en fonction du nombre de cartes
     */
    private void calculerDimensions() {
        int nbCartes = paquet.getTaille();
        if (nbCartes == 0) {
            setPreferredSize(new Dimension(LARGEUR_CARTE + 20, HAUTEUR_CARTE + 60));
            return;
        }

        // Largeur nécessaire pour toutes les cartes avec espacement
        int largeur = LARGEUR_CARTE + (nbCartes - 1) * ESPACEMENT_HORIZONTAL + 40;

        // Hauteur tenant compte de la rotation
        // La rotation fait dépasser la carte en hauteur
        int hauteurExtra = (int) (LARGEUR_CARTE * Math.sin(Math.toRadians(ANGLE_MAX)));
        int hauteur = HAUTEUR_CARTE + hauteurExtra + 40;

        setPreferredSize(new Dimension(largeur, hauteur));
    }

    protected void dessinerPaquet(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int nbCartes = paquet.getTaille();
        if (nbCartes == 0) {
            // Afficher un message si le paquet est vide
            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.ITALIC, 14));
            g2d.drawString("Paquet vide", 10, 30);
            return;
        }

        // Calculer le point pivot (centre bas de l'éventail)
        int pivotX = getWidth() / 2;
        int pivotY = getHeight() - 20;

        // Calculer l'angle de départ et l'incrément
        double angleDebut = -ANGLE_MAX;
        double angleIncrement = (nbCartes > 1) ? (2 * ANGLE_MAX) / (nbCartes - 1) : 0;

        // Dessiner chaque carte
        for (int i = 0; i < nbCartes; i++) {
            Carte carte = paquet.getCarte(i);
            double angle = angleDebut + i * angleIncrement;

            // Sauvegarder la transformation actuelle
            AffineTransform transformOriginale = g2d.getTransform();

            // Calculer la position de base de la carte
            int decalageX = (i - nbCartes / 2) * ESPACEMENT_HORIZONTAL;

            // Déplacer l'origine au point de rotation
            g2d.translate(pivotX + decalageX, pivotY - HAUTEUR_CARTE / 2);

            // Rotation autour du centre bas de la carte
            g2d.rotate(Math.toRadians(angle),
                    LARGEUR_CARTE / 2,
                    HAUTEUR_CARTE / 2);

            // Effet de surbrillance si la souris survole
            if (i == carteEnSurbrillance) {
                // Décaler légèrement vers le haut
                g2d.translate(0, -15);

                // Cadre vert
                g2d.setColor(new Color(0, 255, 0));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRoundRect(-2, -2, LARGEUR_CARTE + 4,
                        HAUTEUR_CARTE + 4, RAYON_COIN, RAYON_COIN);
            }

            // Dessiner la carte
            dessinerCarte(g2d, carte, 0, 0);

            // Restaurer la transformation
            g2d.setTransform(transformOriginale);
        }
    }

    /**
     * Retourne l'index de la carte située aux coordonnées données
     * 
     * @param x coordonnée X
     * @param y coordonnée Y
     * @return index de la carte ou -1 si aucune carte
     */
    @Override
    public int getIndiceCarte(int x, int y) {
        int nbCartes = paquet.getTaille();
        if (nbCartes == 0)
            return -1;

        int pivotX = getWidth() / 2;
        int pivotY = getHeight() - 20;

        double angleDebut = -ANGLE_MAX;
        double angleIncrement = (nbCartes > 1) ? (2 * ANGLE_MAX) / (nbCartes - 1) : 0;

        // Vérifier en ordre inverse pour prioriser les cartes du dessus
        for (int i = nbCartes - 1; i >= 0; i--) {
            double angle = angleDebut + i * angleIncrement;
            int decalageX = (i - nbCartes / 2) * ESPACEMENT_HORIZONTAL;

            // Position du centre de la carte
            int centreCarteX = pivotX + decalageX + LARGEUR_CARTE / 2;
            int centreCarteY = pivotY - HAUTEUR_CARTE / 2 + HAUTEUR_CARTE / 2;

            // Transformer les coordonnées du clic dans le repère de la carte
            double dx = x - centreCarteX;
            double dy = y - centreCarteY;

            // Rotation inverse
            double angleRad = Math.toRadians(-angle);
            double xRotate = dx * Math.cos(angleRad) - dy * Math.sin(angleRad);
            double yRotate = dx * Math.sin(angleRad) + dy * Math.cos(angleRad);

            // Vérifier si le point est dans le rectangle de la carte
            if (Math.abs(xRotate) <= LARGEUR_CARTE / 2 &&
                    Math.abs(yRotate) <= HAUTEUR_CARTE / 2) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Définit la carte à mettre en surbrillance
     * 
     * @param index index de la carte ou -1 pour aucune
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