package cartes.vue;

import java.awt.*;

import cartes.modele.Carte;
import cartes.modele.Paquet;

/**
 * Vue d'un paquet avec cartes visibles alignées horizontalement
 */
public class VuePaquetVisible extends VuePaquet implements VuePaquetInteractive {

    private static final int ESPACEMENT_CARTE = 90; // Espacement entre cartes
    private static final int ESPACEMENT_MIN = 50;   // éviter que les cartes se chevauchent
    private static final Dimension TAILLE_VIDE_FIXE = new Dimension(LARGEUR_CARTE + 60, HAUTEUR_CARTE + 60);
    private int carteEnSurbrillance = -1;
    private boolean masquerPremiereCarte = false;

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
        Dimension d = calculerDimensionOptimisee();
        setPreferredSize(d);
        setMinimumSize(TAILLE_VIDE_FIXE);
    }

    @Override
    protected void dessinerPaquet(Graphics g) {
        int nbCartes = paquet.getTaille();

        if (nbCartes == 0) {
            setPreferredSize(TAILLE_VIDE_FIXE);
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

        int largeurDispo = getWidth() > 0 ? getWidth()
                : (getParent() != null ? getParent().getWidth() : calculerDimensionOptimisee().width);
        int spacing = calculerEspacement(nbCartes, largeurDispo);
        int totalWidth = LARGEUR_CARTE + (nbCartes - 1) * spacing;
        int startX = Math.max(10, (largeurDispo - totalWidth) / 2);

        // Dessiner chaque carte
        for (int i = 0; i < nbCartes; i++) {
            Carte carte = paquet.getCarte(i);
            int x = startX + i * spacing;
            int y = 20;

            if (masquerPremiereCarte && i == 0) {
                dessinerDosCarte(g, x, y);
                continue;
            }

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

    private int calculerEspacement(int nbCartes, int largeurDisponible) {
        if (nbCartes <= 1) {
            return ESPACEMENT_CARTE;
        }

        int spacing = ESPACEMENT_CARTE;
        if (nbCartes > 1 && largeurDisponible > 0) {
            int spacingCalcule = (largeurDisponible - LARGEUR_CARTE - 40) / Math.max(1, nbCartes - 1);
            spacing = Math.min(ESPACEMENT_CARTE, Math.max(ESPACEMENT_MIN, spacingCalcule));
        }
        return spacing;
    }

    private Dimension calculerDimensionOptimisee() {
        int nbCartes = paquet.getTaille();
        if (nbCartes <= 0) {
            return TAILLE_VIDE_FIXE;
        }

        int largeurDispo = (getParent() != null && getParent().getWidth() > 0)
                ? getParent().getWidth()
                : nbCartes * ESPACEMENT_CARTE + 60;
        int spacing = calculerEspacement(nbCartes, largeurDispo);
        int largeurCible = Math.max(LARGEUR_CARTE + 60,
                LARGEUR_CARTE + (nbCartes - 1) * spacing + 60);

        if (getParent() != null && getParent().getWidth() > 0) {
            largeurCible = Math.max(largeurCible, getParent().getWidth());
        }

        return new Dimension(largeurCible, HAUTEUR_CARTE + 60);
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

    @Override
    public Dimension getPreferredSize() {
        return calculerDimensionOptimisee();
    }

    public void setMasquerPremiereCarte(boolean masquer) {
        if (this.masquerPremiereCarte != masquer) {
            this.masquerPremiereCarte = masquer;
            calculerDimensions();
            revalidate();
            repaint();
        }
    }
}
