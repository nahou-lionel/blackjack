package cartes.vue;

import java.awt.*;

import cartes.modele.Paquet;

/**
 * Vue d'un paquet avec cartes cachées (empilées)
 * Utilisée pour la pioche ou le sabot
 */
public class VuePaquetCache extends VuePaquet {

    private static final int DECALAGE_CARTE = 2; // Décalage en pixels pour l'effet d'empilement
    private static final int MAX_CARTES_VISIBLES = 10; // Nombre max de cartes à dessiner

    /**
     * Constructeur avec couleur de fond par défaut
     * @param paquet Le paquet à afficher
     */
    public VuePaquetCache(Paquet paquet) {
        super(paquet);
        calculerDimensions();
    }

    /**
     * Constructeur avec couleur de fond personnalisée
     * @param paquet Le paquet à afficher
     * @param backgroundColor Couleur de fond (null pour défaut, new Color(0,0,0,0) pour transparent)
     */
    public VuePaquetCache(Paquet paquet, Color backgroundColor) {
        super(paquet, backgroundColor);
        calculerDimensions();
    }

    /**
     * Calcule les dimensions du panel en fonction du nombre de cartes
     */
    private void calculerDimensions() {
        int nbCartes = Math.min(paquet.getTaille(), MAX_CARTES_VISIBLES);
        int epaisseur = nbCartes * DECALAGE_CARTE;

        setPreferredSize(new Dimension(
                LARGEUR_CARTE + epaisseur + 20,
                HAUTEUR_CARTE + epaisseur + 20));
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
            g2d.drawRoundRect(10, 10, LARGEUR_CARTE, HAUTEUR_CARTE, RAYON_COIN, RAYON_COIN);

            g2d.setColor(Color.GRAY);
            g2d.setFont(new Font("Arial", Font.ITALIC, 12));
            g2d.drawString("Vide", 30, HAUTEUR_CARTE / 2 + 15);
            return;
        }

        // Dessiner l'empilement (max MAX_CARTES_VISIBLES cartes)
        int nbCartesADessiner = Math.min(nbCartes, MAX_CARTES_VISIBLES);

        for (int i = 0; i < nbCartesADessiner; i++) {
            int decalage = i * DECALAGE_CARTE;
            dessinerDosCarte(g, 10 + decalage, 10 + decalage);
        }

        // Afficher le nombre de cartes
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));

        String texte = String.valueOf(nbCartes);
        FontMetrics fm = g2d.getFontMetrics();
        int largeurTexte = fm.stringWidth(texte);

        // Dessiner un fond sombre pour le texte
        int x = LARGEUR_CARTE / 2 - largeurTexte / 2 + 10;
        int y = HAUTEUR_CARTE - 15;
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(x - 5, y - fm.getAscent(),
                largeurTexte + 10, fm.getHeight(), 5, 5);

        // Dessiner le texte
        g2d.setColor(Color.WHITE);
        g2d.drawString(texte, x, y);
    }

    @Override
    public void somethingHasChanged(Object source) {
        calculerDimensions();
        revalidate();
        repaint();
    }
}
