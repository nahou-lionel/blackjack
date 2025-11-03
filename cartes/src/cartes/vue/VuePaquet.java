package cartes.vue;

import util.observer.*;
import java.awt.*;
import java.awt.geom.*;

import javax.swing.*;

import cartes.modele.Carte;
import cartes.modele.Paquet;

/**
 * Classe abstraite VuePaquet contenant les méthodes utilitaires de dessin
 */
public abstract class VuePaquet extends JPanel implements ModelListener {

    protected Paquet paquet;
    protected static final int LARGEUR_CARTE = 70;
    protected static final int HAUTEUR_CARTE = 100;
    protected static final int RAYON_COIN = 10;

    public VuePaquet(Paquet paquet) {
        super();
        this.paquet = paquet;
        this.paquet.addModelListener(this);
    }

    public Paquet getPaquet() {
        return paquet;
    }

    /**
     * Méthode abstraite pour dessiner le paquet
     * Doit être implémentée par les sous-classes
     * 
     * @param g le contexte graphique
     */
    protected abstract void dessinerPaquet(Graphics g);

    /**
     * Redéfinition de paintComponent pour dessiner le paquet
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        dessinerPaquet(g);
    }

    /**
     * Méthode appelée quand le paquet observé change
     * Redessine automatiquement la vue
     */
    @Override
    public void somethingHasChanged(Object source) {
        repaint();
    }

    /**
     * Dessine le dos d'une carte à la position spécifiée
     * 
     * @param g le contexte graphique
     * @param x position X
     * @param y position Y
     */
    protected void dessinerDosCarte(Graphics g, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Rectangle arrondi pour la carte
        RoundRectangle2D carte = new RoundRectangle2D.Double(
                x, y, LARGEUR_CARTE, HAUTEUR_CARTE, RAYON_COIN, RAYON_COIN);

        // Fond bleu marine
        g2d.setColor(new Color(25, 45, 85));
        g2d.fill(carte);

        // Bordure blanche
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(carte);

        // Motif décoratif au centre
        // Rectangle intérieur
        g2d.setColor(new Color(40, 70, 120));
        g2d.fillRoundRect(x + 10, y + 15, LARGEUR_CARTE - 20,
                HAUTEUR_CARTE - 30, RAYON_COIN - 2, RAYON_COIN - 2);

        // Losanges décoratifs
        g2d.setColor(new Color(100, 140, 200));
        int centreX = x + LARGEUR_CARTE / 2;
        int centreY = y + HAUTEUR_CARTE / 2;

        // Losange central
        Polygon losange = new Polygon();
        losange.addPoint(centreX, centreY - 15);
        losange.addPoint(centreX + 12, centreY);
        losange.addPoint(centreX, centreY + 15);
        losange.addPoint(centreX - 12, centreY);
        g2d.fill(losange);

        // // Petits losanges aux coins
        // int[][] positions = { { 15, 25 }, { LARGEUR_CARTE - 15, 25 },
        // { 15, HAUTEUR_CARTE - 25 },
        // { LARGEUR_CARTE - 15, HAUTEUR_CARTE - 25 } };

        // for (int[] pos : positions) {
        // Polygon petit = new Polygon();
        // petit.addPoint(x + pos[0], y + pos[1] - 5);
        // petit.addPoint(x + pos[0] + 4, y + pos[1]);
        // petit.addPoint(x + pos[0], y + pos[1] + 5);
        // petit.addPoint(x + pos[0] - 4, y + pos[1]);
        // g2d.fill(petit);
        // }
    }

    /**
     * Dessine une carte face visible à la position spécifiée
     * 
     * @param g     le contexte graphique
     * @param carte la carte à dessiner
     * @param x     position X
     * @param y     position Y
     */
    protected void dessinerCarte(Graphics g, Carte carte, int x, int y) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // Rectangle arrondi pour la carte
        RoundRectangle2D carteRect = new RoundRectangle2D.Double(
                x, y, LARGEUR_CARTE, HAUTEUR_CARTE, RAYON_COIN, RAYON_COIN);

        // Fond blanc
        g2d.setColor(Color.WHITE);
        g2d.fill(carteRect);

        // Bordure noire
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(carteRect);

        // Couleur du texte selon la couleur de la carte
        Color couleurCarte = carte.getCouleur().estRouge() ? new Color(220, 20, 20) : Color.BLACK;
        g2d.setColor(couleurCarte);

        // Hauteur en haut à gauche
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        String hauteur = carte.getHauteur().getSymbole();
        g2d.drawString(hauteur, x + 8, y + 20);

        // Symbole de couleur sous la hauteur
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        String symbole = carte.getCouleur().getSymbole();
        g2d.drawString(symbole, x + 8, y + 40);

        // Hauteur en bas à droite (inversé)
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        AffineTransform old = g2d.getTransform();
        g2d.translate(x + LARGEUR_CARTE - 8, y + HAUTEUR_CARTE - 14);
        g2d.rotate(Math.PI);
        g2d.drawString(hauteur, 0, 0);
        g2d.setTransform(old);

        // Symbole en bas à droite (inversé)
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));
        old = g2d.getTransform();
        g2d.translate(x + LARGEUR_CARTE - 8, y + HAUTEUR_CARTE - 30);
        g2d.rotate(Math.PI);
        g2d.drawString(symbole, 0, 0);
        g2d.setTransform(old);

        // Symbole central agrandi
        g2d.setFont(new Font("Arial", Font.BOLD, 36));
        FontMetrics fm = g2d.getFontMetrics();
        int largeurSymbole = fm.stringWidth(symbole);
        int centreX = x + (LARGEUR_CARTE - largeurSymbole) / 2;
        int centreY = y + (HAUTEUR_CARTE + fm.getAscent()) / 2 - 5;
        g2d.drawString(symbole, centreX, centreY);
    }

}
