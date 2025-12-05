package vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import cartes.modele.Carte;
import cartes.modele.Hauteur;
import cartes.modele.Paquet;
import cartes.vue.VuePaquetCache;
import cartes.vue.VuePaquetVisible;
import modele.CalculateurScore;
import modele.Croupier;
import modele.Joueur;
import modele.MainJoueur;
import modele.PartieBlackjack;
import modele.Paiement;
import modele.ResultatBlackjack;
import modele.ResultatManche;

public class VuePartie extends JPanel {

    private static final Color VERT_TABLE = new Color(8, 102, 60);
    private static final Color VERT_TABLE_SOMBRE = new Color(4, 70, 42);
    private static final Color ACCENT_AMBRE = new Color(255, 196, 92);
    private static final Color ACCENT_TURQUOISE = new Color(0, 170, 150);
    private static final Color PANNEAU_NUIT = new Color(24, 28, 38, 210);

    // Les Paquets
    private Paquet pioche;
    private Paquet mainCroupier;
    private Paquet mainJoueur;

    // Ajouter les champs (après ligne 40)
    private PartieBlackjack partie;
    private Joueur joueurPrincipal;
    private Croupier croupier;

    // Les vues pour dessiner les cartes
    private final VuePaquetCache vuePioche;
    private final VuePaquetVisible vueCroupier;
    private final VuePaquetVisible vueJoueur;

    // Elements propre à la partie
    private JLabel labelTitre;
    private JLabel labelSolde;
    private JLabel labelMise;
    private JLabel labelScoreCroupier;
    private JLabel labelScoreJoueur;
    private JLabel labelMessage;
    private JLabel badgeCroupier;
    private JLabel badgeJoueur;
    private boolean carteCroupierCachee = true;
    private boolean peutTirer = false;
    private JLayeredPane coucheCroupier;
    private JPanel overlayCroupier;

    private JButton boutonMiser;
    private JButton boutonRester;
    private JButton boutonDouble;
    private JButton boutonSeparer;
    private JButton boutonResetMise;

    private final List<JButton> jetons = new ArrayList<>();

    private int mise = 0;
    private int misePrecedente = 0;

    // Gestion du split
    private int mainActiveIndex = 0; // Index de la main actuellement jouée (0 ou 1 après split)
    private boolean enModeSplit = false;

    public VuePartie() {

        setBackground(VERT_TABLE);
        setPreferredSize(new Dimension(1080, 720));
        setLayout(new BorderLayout());

        // creer des mains
        pioche = Paquet.creerPaquetMultiple(2);
        pioche.melanger();

        croupier = new Croupier();
        joueurPrincipal = new Joueur("Joueur");
        List<Joueur> joueurs = new ArrayList<>();
        joueurs.add(joueurPrincipal);
        partie = new PartieBlackjack(pioche, croupier, joueurs, 0);

        mainCroupier = croupier.getMain();
        mainJoueur = joueurPrincipal.getMain();

        // création des vues des mains avec couleur de fond verte
        Color fondVert = new Color(10, 106, 51);
        vuePioche = new VuePaquetCache(pioche, fondVert);
        vueCroupier = new VuePaquetVisible(mainCroupier, fondVert);
        vueJoueur = new VuePaquetVisible(mainJoueur, fondVert);

        // Ajouter un contrôleur pour cliquer sur la pioche
        vuePioche.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (peutTirer && !pioche.estVide()) {
                    gererActionHit();
                }
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (peutTirer) {
                    vuePioche.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                vuePioche.setCursor(Cursor.getDefaultCursor());
            }
        });

        // Barre haut
        JPanel barreHaut = creerBarreHaut();
        add(barreHaut, BorderLayout.NORTH);

        // Zone centrale
        JPanel zoneCentre = creerZoneCentre();
        add(zoneCentre, BorderLayout.CENTER);

        // Démarrer la partie
        rafraichirAffichage();
        basculerEtatBoutonsInitial();
    }

    private JPanel creerBarreHaut() {
        JPanel panelHaut = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                java.awt.GradientPaint gp = new java.awt.GradientPaint(0, 0, VERT_TABLE_SOMBRE, 0, getHeight(),
                        VERT_TABLE);
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 0, 0);
                g2.dispose();
            }
        };
        panelHaut.setOpaque(false);
        panelHaut.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));

        JPanel panelCentre = new JPanel();
        panelCentre.setOpaque(false);
        panelCentre.setLayout(new BoxLayout(panelCentre, BoxLayout.Y_AXIS));

        labelTitre = new JLabel("BLACKJACK", SwingConstants.CENTER);
        labelTitre.setFont(new Font("SansSerif", Font.BOLD, 26));
        labelTitre.setForeground(ACCENT_AMBRE);
        labelTitre.setAlignmentX(CENTER_ALIGNMENT);

        labelMessage = new JLabel("Placez votre mise pour commencer", SwingConstants.CENTER);
        labelMessage.setFont(new Font("SansSerif", Font.BOLD, 14));
        labelMessage.setForeground(new Color(50, 40, 0));
        labelMessage.setOpaque(true);
        labelMessage.setBackground(new Color(255, 242, 210));
        labelMessage.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 222, 140)),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)));
        labelMessage.setAlignmentX(CENTER_ALIGNMENT);

        panelCentre.add(labelTitre);
        panelCentre.add(Box.createVerticalStrut(8));
        panelCentre.add(labelMessage);

        panelHaut.add(panelCentre, BorderLayout.CENTER);
        return panelHaut;
    }

        private JPanel creerZoneCentre() {
        JPanel centre = new JPanel(new BorderLayout());
        centre.setOpaque(false);

        // Bloc solde et mise
        JPanel panelInfo = new JPanel();
        panelInfo.setOpaque(true);
        panelInfo.setBackground(PANNEAU_NUIT);
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
        panelInfo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_AMBRE, 1, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));

        labelSolde = new JLabel();
        labelSolde.setFont(new Font("SansSerif", Font.BOLD, 16));
        labelSolde.setForeground(Color.WHITE);
        labelSolde.setAlignmentX(CENTER_ALIGNMENT);

        labelMise = new JLabel();
        labelMise.setFont(new Font("SansSerif", Font.BOLD, 16));
        labelMise.setForeground(ACCENT_TURQUOISE);
        labelMise.setAlignmentX(CENTER_ALIGNMENT);

        panelInfo.add(labelSolde);
        panelInfo.add(Box.createVerticalStrut(5));
        panelInfo.add(labelMise);

        // Bandeau haut: pioche + actions + mises/info
        JPanel panelHautCentre = new JPanel(new BorderLayout());
        panelHautCentre.setOpaque(false);
        panelHautCentre.setBorder(BorderFactory.createEmptyBorder(8, 12, 6, 12));

        JPanel panelPioche = new JPanel();
        panelPioche.setOpaque(false);
        panelPioche.add(vuePioche);

        JPanel blocActions = creerZoneActions();
        blocActions.setOpaque(false);

        JPanel blocDroite = new JPanel();
        blocDroite.setOpaque(false);
        blocDroite.setLayout(new BoxLayout(blocDroite, BoxLayout.Y_AXIS));
        blocDroite.add(panelInfo);
        blocDroite.add(Box.createVerticalStrut(6));
        blocDroite.add(creerZoneMises());

        panelHautCentre.add(panelPioche, BorderLayout.WEST);
        panelHautCentre.add(blocActions, BorderLayout.CENTER);
        panelHautCentre.add(blocDroite, BorderLayout.EAST);

        centre.add(panelHautCentre, BorderLayout.NORTH);

        // Zone des cartes
        JPanel plateau = new JPanel(new GridBagLayout());
        plateau.setOpaque(false);
        plateau.setBorder(BorderFactory.createEmptyBorder(10, 12, 12, 12));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.BOTH;

        // Panel Croupier
        JPanel blocCroupier = new JPanel(new BorderLayout());
        blocCroupier.setOpaque(false);
        blocCroupier.setBorder(BorderFactory.createEmptyBorder(6, 0, 12, 0));
        blocCroupier.setAlignmentX(CENTER_ALIGNMENT);

        // Couche: vue + overlay dos de carte
        coucheCroupier = new JLayeredPane();
        coucheCroupier.setOpaque(false);
        overlayCroupier = new JPanel() {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                if (!carteCroupierCachee)
                    return;
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                int w = 70, h = 100, r = 10;
                g2.setColor(new Color(25, 45, 85));
                g2.fillRoundRect(0, 0, w, h, r, r);
                g2.setColor(Color.WHITE);
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawRoundRect(0, 0, w, h, r, r);
                g2.setColor(new Color(40, 70, 120));
                g2.fillRoundRect(10, 15, w - 20, h - 30, r - 2, r - 2);
                g2.setColor(new Color(100, 140, 200));
                int cx = w / 2, cy = h / 2;
                java.awt.Polygon p = new java.awt.Polygon();
                p.addPoint(cx, cy - 15);
                p.addPoint(cx + 12, cy);
                p.addPoint(cx, cy + 15);
                p.addPoint(cx - 12, cy);
                g2.fill(p);
            }
        };
        overlayCroupier.setOpaque(false);
        coucheCroupier.add(vueCroupier, JLayeredPane.DEFAULT_LAYER);
        coucheCroupier.add(overlayCroupier, JLayeredPane.PALETTE_LAYER);
        mettreEnPlaceCoucheCroupier();
        coucheCroupier.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                mettreEnPlaceCoucheCroupier();
            }

            @Override
            public void componentShown(ComponentEvent e) {
                mettreEnPlaceCoucheCroupier();
            }
        });

        JPanel centreCroupier = creerBandeauCartes(coucheCroupier);

        JLabel titreCroupier = new JLabel("CROUPIER", SwingConstants.CENTER);
        titreCroupier.setFont(new Font("SansSerif", Font.BOLD, 16));
        titreCroupier.setForeground(Color.WHITE);
        labelScoreCroupier = new JLabel("0", SwingConstants.CENTER);
        labelScoreCroupier.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labelScoreCroupier.setForeground(ACCENT_AMBRE);

        JPanel footerCroupier = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        footerCroupier.setOpaque(false);
        badgeCroupier = creerBadgeGagnant();
        footerCroupier.add(titreCroupier);
        footerCroupier.add(labelScoreCroupier);
        footerCroupier.add(badgeCroupier);

        blocCroupier.add(centreCroupier, BorderLayout.CENTER);
        blocCroupier.add(footerCroupier, BorderLayout.SOUTH);

        // Panel Joueur
        JPanel blocJoueur = new JPanel(new BorderLayout());
        blocJoueur.setOpaque(false);
        blocJoueur.setBorder(BorderFactory.createEmptyBorder(12, 0, 6, 0));
        blocJoueur.setAlignmentX(CENTER_ALIGNMENT);

        JPanel contJoueur = creerBandeauCartes(vueJoueur);

        JLabel titreJoueur = new JLabel("JOUEUR", SwingConstants.CENTER);
        titreJoueur.setFont(new Font("SansSerif", Font.BOLD, 16));
        titreJoueur.setForeground(Color.WHITE);
        labelScoreJoueur = new JLabel("0", SwingConstants.CENTER);
        labelScoreJoueur.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labelScoreJoueur.setForeground(ACCENT_AMBRE);

        JPanel footerJoueur = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        footerJoueur.setOpaque(false);
        badgeJoueur = creerBadgeGagnant();
        footerJoueur.add(titreJoueur);
        footerJoueur.add(labelScoreJoueur);
        footerJoueur.add(badgeJoueur);

        blocJoueur.add(contJoueur, BorderLayout.CENTER);
        blocJoueur.add(footerJoueur, BorderLayout.SOUTH);

        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.insets = new java.awt.Insets(0, 0, 6, 0);
        plateau.add(blocCroupier, gbc);

        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.insets = new java.awt.Insets(0, 0, 0, 0);
        plateau.add(blocJoueur, gbc);

        centre.add(plateau, BorderLayout.CENTER);
        return centre;
    }

    private JPanel creerBandeauCartes(JComponent contenu) {
        JPanel supportCentre = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 12));
        supportCentre.setOpaque(false);
        supportCentre.add(contenu);

        Dimension contenuPref = contenu.getPreferredSize();
        int bandeauHeight = contenuPref.height + 40;

        JPanel bandeau = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                super.paintComponent(g);
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                int arc = 24;
                g2.setPaint(new java.awt.GradientPaint(0, 0, new Color(0, 0, 0, 90), 0, getHeight(),
                        new Color(255, 255, 255, 30)));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.setColor(new Color(255, 255, 255, 50));
                g2.setStroke(new java.awt.BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, arc, arc);
                g2.dispose();
            }
        };
        bandeau.setOpaque(false);
        bandeau.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        bandeau.setMinimumSize(new Dimension(200, bandeauHeight));
        bandeau.add(supportCentre, BorderLayout.CENTER);
        return bandeau;
    }

    private JPanel creerZoneActions() {
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        actions.setOpaque(false);
        actions.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        boutonMiser = creerBoutonAction("Miser", new Color(0, 114, 187));
        boutonRester = creerBoutonAction("Rester", new Color(187, 134, 0));
        boutonDouble = creerBoutonAction("Double", new Color(153, 0, 0));
        boutonSeparer = creerBoutonAction("Separer", new Color(102, 0, 153));

        actions.add(boutonMiser);
        actions.add(boutonRester);
        actions.add(boutonDouble);
        actions.add(boutonSeparer);

        boutonMiser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                demarrerManche();
            }
        });

        boutonRester.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gererActionStand();
            }
        });
        boutonDouble.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gererActionDouble();
            }
        });
        boutonSeparer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gererActionSeparer();
            }
        });

        return actions;
    }

    private JPanel creerZoneMises() {
        JPanel misesPanel = new JPanel();
        misesPanel.setOpaque(false);
        misesPanel.setLayout(new BoxLayout(misesPanel, BoxLayout.Y_AXIS));
        misesPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));

        // Ligne jetons
        JPanel ligneJetons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        ligneJetons.setOpaque(false);
        creerJeton(ligneJetons, 10, new Color(230, 230, 230), Color.BLACK);
        creerJeton(ligneJetons, 25, new Color(204, 0, 0), Color.WHITE);
        creerJeton(ligneJetons, 50, new Color(0, 153, 0), Color.WHITE);
        creerJeton(ligneJetons, 100, new Color(0, 102, 204), Color.WHITE);

        // Ligne actions de mise
        JPanel ligneMiseActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        ligneMiseActions.setOpaque(false);

        boutonResetMise = creerBoutonAction("Reset Bet", new Color(80, 80, 80));
        boutonResetMise.addActionListener(e -> {
            mise = 0;
            rafraichirAffichage();
            basculerEtatBoutonsInitial();
        });

        JButton boutonToutMiser = creerBoutonAction("Tout Miser", new Color(204, 0, 0));
        boutonToutMiser.addActionListener(e -> {
            mise = joueurPrincipal.getBanque();
            rafraichirAffichage();
            boutonMiser.setEnabled(mise > 0);
        });

        ligneMiseActions.add(boutonResetMise);
        ligneMiseActions.add(boutonToutMiser);

        misesPanel.add(ligneJetons);
        misesPanel.add(ligneMiseActions);
        return misesPanel;
    }

    private JButton creerBoutonAction(String texte, Color fond) {
        JButton b = new JButton(texte) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                // Dessiner le fond arrondi - gris si désactivé, couleur normale sinon
                if (isEnabled()) {
                    g2.setColor(getBackground());
                } else {
                    g2.setColor(new Color(100, 100, 100));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setBackground(fond);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }

    private void creerJeton(JPanel parent, int valeur, Color fond, Color texte) {
        JButton jeton = new JButton("" + valeur) {
            @Override
            protected void paintComponent(java.awt.Graphics g) {
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g.create();
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                // Dessiner un jeton circulaire - ajuster pour être bien visible
                int size = Math.min(getWidth(), getHeight()) - 4; // Réduire un peu pour la bordure
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;

                // Fond du jeton
                if (isEnabled()) {
                    g2.setColor(getBackground());
                } else {
                    g2.setColor(new Color(100, 100, 100));
                }
                g2.fillOval(x, y, size, size);

                // Bordure
                g2.setColor(texte.darker());
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawOval(x, y, size, size);

                g2.dispose();
                super.paintComponent(g);
            }

            @Override
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                int size = Math.max(d.width, d.height);
                return new Dimension(size, size);
            }
        };
        jeton.setFont(new Font("SansSerif", Font.BOLD, 16));
        jeton.setBackground(fond);
        jeton.setForeground(texte);
        jeton.setFocusPainted(false);
        jeton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jeton.setContentAreaFilled(false);
        jeton.setOpaque(false);
        jeton.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        jeton.addActionListener(e -> {
            if (valeur <= joueurPrincipal.getBanque() - mise) {
                mise += valeur;
                rafraichirAffichage();
                boutonMiser.setEnabled(mise > 0);
            }
        });
        jetons.add(jeton);
        parent.add(jeton);
    }

    // Les méthodes qui aident à controler le jeu
    private void demarrerManche() {
        if (mise <= 0)
            return;

        // Sauvegarder la mise pour le prochain tour
        misePrecedente = mise;

        afficherMessage("Distribution des cartes...");

        // Démarrer une nouvelle manche via l'orchestrateur
        partie.demarrerNouvellePartie();
        joueurPrincipal.miser(mise);
        partie.distribuerCartesInitiales();
        rafraichirAffichage();

        // Vérifier les blackjacks naturels via l'orchestrateur
        ResultatBlackjack resultatBJ = partie.verifierBlackjacksNaturels();

        if (resultatBJ != null) {
            // Blackjack détecté - terminer immédiatement
            afficherMainCompleteCroupier();
            desactiverActionsPendantAnnonce();

            ResultatManche resultat = partie.terminerMancheBlackjack(resultatBJ);
            afficherResultat(resultat);
            rafraichirAffichage();
            return;
        }

        // Jeu normal - activer les boutons d'action
        activerJetons(false);
        boutonMiser.setEnabled(false);
        peutTirer = true;
        boutonRester.setEnabled(true);
        boutonDouble.setEnabled(true);

        // Activer le bouton Séparer si les conditions sont remplies
        boolean peutSeparer = joueurPrincipal.getMainAIndex(0).peutEtreSplittee()
                && joueurPrincipal.getBanque() >= mise;
        boutonSeparer.setEnabled(peutSeparer);

        afficherMessage("À votre tour - Cliquez sur la pioche pour tirer ou Rester");
        rafraichirAffichage();
    }

    /**
     * Gère l'action Hit : le joueur tire une carte supplémentaire
     */
    private void gererActionHit() {
        if (!pioche.estVide()) {
            if (enModeSplit) {
                // Tirer sur la main active
                partie.joueurTire(0, mainActiveIndex);
                rafraichirAffichage();

                // Vérifier si cette main a dépassé
                if (joueurPrincipal.aDepasse(mainActiveIndex)) {
                    afficherMessage("Main " + (mainActiveIndex + 1) + " a dépassé 21");
                    passerMainSuivante();
                } else {
                    afficherMessage("Main " + (mainActiveIndex + 1) + " - Tirez ou Restez");
                }
            } else {
                // Mode normal (une seule main)
                partie.joueurTire(0);
                rafraichirAffichage();

                // on termine immédiatement la manche si le joueur dépasse 21
                if (CalculateurScore.aDepasse(joueurPrincipal.getMain())) {
                    peutTirer = false;
                    desactiverActionsPendantAnnonce();
                    afficherMainCompleteCroupier();
                    afficherMessage("Vous avez dépassé 21...");
                    afficherGagnantEtReset(false);
                } else {
                    afficherMessage("À votre tour - Cliquez sur la pioche pour tirer ou Rester");
                }
            }
        }
    }

    /**
     * Gère l'action Stand : le joueur reste, le croupier joue et la manche se
     * termine
     */
    private void gererActionStand() {
        if (enModeSplit) {
            // En mode split, passer à la main suivante
            passerMainSuivante();
        } else {
            // Mode normal
            peutTirer = false;
            afficherMainCompleteCroupier();
            desactiverActionsPendantAnnonce();
            afficherMessage("Le croupier joue...");

            // L'orchestrateur gère le tour du croupier, calcule les résultats et applique
            // les paiements
            ResultatManche resultat = partie.terminerManche();

            afficherResultat(resultat);
            rafraichirAffichage();
        }
    }

    /**
     * Passe à la main suivante lors d'un split, ou termine la manche si toutes les mains sont jouées
     */
    private void passerMainSuivante() {
        mainActiveIndex++;

        if (mainActiveIndex < joueurPrincipal.getNombreMains()) {
            // Il reste des mains à jouer
            afficherMessage("Jouez maintenant la main " + (mainActiveIndex + 1));
            rafraichirAffichage();

            // Si la main suivante est une main d'As splittés, elle ne peut tirer qu'une carte
            // et a déjà reçu cette carte, donc on passe automatiquement à la suivante
            if (!joueurPrincipal.getMainAIndex(mainActiveIndex).peutEncoreTirer()) {
                passerMainSuivante();
            }
        } else {
            // Toutes les mains ont été jouées, passer au tour du croupier
            peutTirer = false;
            afficherMainCompleteCroupier();
            desactiverActionsPendantAnnonce();
            afficherMessage("Le croupier joue...");

            ResultatManche resultat = partie.terminerManche();
            afficherResultatSplit(resultat);
            rafraichirAffichage();
        }
    }

    /**
     * Gère l'action Double Down : double la mise, tire une carte et passe au
     * croupier
     */
    private void gererActionDouble() {
        // Vérifier que le joueur a assez d'argent pour doubler
        if (mise > 0 && joueurPrincipal.getBanque() >= mise) {
            peutTirer = false;
            afficherMessage("Double mise - Une carte tirée");

            // Déduire le montant supplémentaire
            joueurPrincipal.setBanque(joueurPrincipal.getBanque() - mise);
            mise *= 2;

            // Tirer exactement une carte
            if (!pioche.estVide()) {
                partie.joueurTire(0);
            }

            // Désactiver les actions et passer au tour du croupier
            boutonDouble.setEnabled(false);
            boutonRester.setEnabled(false);

            rafraichirAffichage();

            // Vérifier si bust sinon jouer le croupier
            if (CalculateurScore.aDepasse(joueurPrincipal.getMain())) {
                desactiverActionsPendantAnnonce();
                afficherMainCompleteCroupier();
                afficherMessage("Vous avez dépassé 21...");
                ResultatManche resultat = partie.terminerManche();
                afficherResultat(resultat);
                rafraichirAffichage();
            } else {
                // Forcer le tour du croupier
                gererActionStand();
            }
        }
    }

    /**
     * Gère l'action Séparer : sépare la main en deux mains distinctes
     */
    private void gererActionSeparer() {
        // Vérifier que le joueur peut séparer
        if (!joueurPrincipal.getMainAIndex(0).peutEtreSplittee()) {
            return;
        }

        // Vérifier que le joueur a assez d'argent
        if (joueurPrincipal.getBanque() < mise) {
            afficherMessage("Solde insuffisant pour séparer");
            return;
        }

        // Effectuer le split via l'orchestrateur
        boolean succes = partie.effectuerSplit(0);

        if (succes) {
            enModeSplit = true;
            mainActiveIndex = 0;

            // Désactiver le bouton séparer et double
            boutonSeparer.setEnabled(false);
            boutonDouble.setEnabled(false);

            // Rafraîchir l'affichage pour montrer les deux mains
            rafraichirAffichage();
            afficherMessage("Main séparée - Jouez la main 1");
        }
    }

    private void activerJetons(boolean actif) {
        for (JButton j : jetons)
            j.setEnabled(actif);
        boutonResetMise.setEnabled(actif);
    }

    private void basculerEtatBoutonsInitial() {
        boutonMiser.setEnabled(mise > 0);
        peutTirer = false;
        boutonRester.setEnabled(false);
        boutonDouble.setEnabled(false);
        boutonSeparer.setEnabled(false);
    }

    /**
     * Rafraîchit l'affichage de tous les éléments de l'interface
     * (solde, mise, scores, cartes)
     */
    private void rafraichirAffichage() {
        labelSolde.setText("Solde: $" + joueurPrincipal.getBanque());

        // Afficher la mise totale (toutes les mains)
        if (enModeSplit) {
            int miseTotal = 0;
            for (MainJoueur main : joueurPrincipal.getMains()) {
                miseTotal += main.getMise();
            }
            labelMise.setText("Mise totale: $" + miseTotal);
        } else {
            labelMise.setText("Mise: $" + mise);
        }

        // Afficher le score du joueur (avec split si nécessaire)
        if (enModeSplit) {
            StringBuilder scores = new StringBuilder();
            for (int i = 0; i < joueurPrincipal.getNombreMains(); i++) {
                if (i == mainActiveIndex) {
                    scores.append("[");
                }
                scores.append("M").append(i + 1).append(": ");
                scores.append(joueurPrincipal.getScore(i));
                if (i == mainActiveIndex) {
                    scores.append("]");
                }
                if (i < joueurPrincipal.getNombreMains() - 1) {
                    scores.append(" | ");
                }
            }
            labelScoreJoueur.setText(scores.toString());

            // Afficher toutes les cartes de toutes les mains dans le paquet visuel
            mainJoueur.vider();
            for (MainJoueur main : joueurPrincipal.getMains()) {
                for (Carte carte : main.getMain().getCartes()) {
                    mainJoueur.ajouterCarte(carte);
                }
            }
        } else {
            labelScoreJoueur.setText(scoreTexte(joueurPrincipal.getMain()));
        }

        // Afficher le score du croupier basé sur ses cartes visibles si la 1re est
        // cachée
        labelScoreCroupier.setText(scoreTexteCroupier());
        vueCroupier.setMasquerPremiereCarte(carteCroupierCachee);
        mettreEnPlaceCoucheCroupier();
        verrouillerSiSoldeVide();
        revalidate();
        repaint();
    }

    private String scoreTexte(Paquet paquet) {
        if (paquet.getCartes().isEmpty())
            return "0";
        return String.valueOf(CalculateurScore.calculerScore(paquet));
    }

    private String scoreTexteCroupier() {
        if (mainCroupier.getCartes().isEmpty())
            return "0";
        if (carteCroupierCachee && mainCroupier.getCartes().size() >= 1) {
            // Afficher seulement les cartes visibles
            Paquet visible = Paquet.creerPaquetVide();
            for (int i = 1; i < mainCroupier.getCartes().size(); i++) {
                visible.ajouterCarte(mainCroupier.getCartes().get(i));
            }
            return String.valueOf(CalculateurScore.calculerScore(visible));
        }
        return String.valueOf(CalculateurScore.calculerScore(mainCroupier));
    }

    /**
     * Affiche le résultat d'une manche (délègue l'affichage visuel selon le type de
     * résultat)
     * Toute la logique métier (calculs, paiements) a déjà été faite par
     * l'orchestrateur
     *
     * @param resultat Le résultat complet de la manche
     */
    private void afficherResultat(ResultatManche resultat) {
        Paiement paiement = resultat.getPaiementPour(joueurPrincipal);

        if (paiement == null) {
            // Ne devrait pas arriver, mais sécurité
            afficherGagnantEtReset(false);
            return;
        }

        // Afficher visuellement selon le type de résultat
        switch (paiement.getTypeResultat()) {
            case VICTOIRE:
                int profit = paiement.getProfit();
                afficherMessage("Vous avez gagné +" + profit + "$!");
                afficherGagnantEtReset(true);
                break;
            case BLACKJACK:
                int profitBJ = paiement.getProfit();
                afficherMessage("BLACKJACK! +" + profitBJ + "$!");
                afficherGagnantEtReset(true);
                break;
            case PUSH:
                afficherMessage("Égalité - Mise remboursée");
                afficherBadgeAucunPuisReset();
                break;
            case PERTE:
                afficherMessage("Vous avez perdu...");
                afficherGagnantEtReset(false);
                break;
        }
    }

    /**
     * Affiche le résultat d'une manche avec split (plusieurs mains)
     *
     * @param resultat Le résultat complet de la manche
     */
    private void afficherResultatSplit(ResultatManche resultat) {
        List<Paiement> paiements = resultat.getPaiementsPour(joueurPrincipal);

        if (paiements == null || paiements.isEmpty()) {
            afficherGagnantEtReset(false);
            return;
        }

        // Calculer le profit total
        int profitTotal = resultat.getProfitTotal(joueurPrincipal);

        // Déterminer si le joueur a globalement gagné
        boolean aGagne = profitTotal > 0;

        // Message détaillé
        StringBuilder message = new StringBuilder();
        for (int i = 0; i < paiements.size(); i++) {
            Paiement p = paiements.get(i);
            message.append("Main ").append(i + 1).append(": ");
            switch (p.getTypeResultat()) {
                case VICTOIRE:
                    message.append("Victoire (+" + p.getProfit() + "$)");
                    break;
                case BLACKJACK:
                    message.append("21! (+" + p.getProfit() + "$)");
                    break;
                case PUSH:
                    message.append("Push");
                    break;
                case PERTE:
                    message.append("Perdu");
                    break;
            }
            if (i < paiements.size() - 1) {
                message.append(" | ");
            }
        }

        if (profitTotal > 0) {
            message.append(" - Total: +").append(profitTotal).append("$");
        } else if (profitTotal < 0) {
            message.append(" - Total: ").append(profitTotal).append("$");
        }

        afficherMessage(message.toString());
        afficherGagnantEtReset(aGagne || profitTotal == 0);
    }

    private void desactiverActionsPendantAnnonce() {
        peutTirer = false;
        boutonRester.setEnabled(false);
        boutonDouble.setEnabled(false);
        boutonSeparer.setEnabled(false);
        boutonMiser.setEnabled(false);
        activerJetons(false);
    }

    private void afficherBadgeAucunPuisReset() {
        Timer t = new Timer(2000, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                reinitialiserPlateau();
            }
        });
        t.setRepeats(false);
        t.start();
    }

    private void afficherGagnantEtReset(boolean joueurGagnant) {
        if (badgeJoueur != null && badgeCroupier != null) {
            badgeJoueur.setVisible(joueurGagnant);
            badgeCroupier.setVisible(!joueurGagnant);
        }
        revalidate();
        repaint();

        Timer t = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (badgeJoueur != null)
                    badgeJoueur.setVisible(false);
                if (badgeCroupier != null)
                    badgeCroupier.setVisible(false);
                reinitialiserPlateau();
            }
        });
        t.setRepeats(false);
        t.start();
    }

    /**
     * Réinitialise le plateau pour préparer une nouvelle manche
     */
    private void reinitialiserPlateau() {
        mainCroupier.vider();
        mainJoueur.vider();
        activerJetons(true);

        // Réinitialiser les variables de split
        enModeSplit = false;
        mainActiveIndex = 0;

        // Remettre la mise précédente (ou 0 si le joueur n'a plus assez d'argent)
        if (misePrecedente > joueurPrincipal.getBanque()) {
            mise = 0;
        } else {
            mise = misePrecedente;
        }

        basculerEtatBoutonsInitial();
        carteCroupierCachee = true;
        if (joueurPrincipal.getBanque() <= 0) {
            afficherMessage("Solde à zéro : vous ne pouvez plus jouer");
            activerJetons(false);
            boutonMiser.setEnabled(false);
        } else {
            afficherMessage("Placez votre mise pour commencer");
        }
        rafraichirAffichage();
        if (vueCroupier != null) {
            vueCroupier.revalidate();
        }
        if (vueJoueur != null) {
            vueJoueur.revalidate();
        }
        this.revalidate();
    }

    /**
     * Affiche la main complète du croupier (révèle sa carte cachée)
     */
    private void afficherMainCompleteCroupier() {
        if (carteCroupierCachee) {
            carteCroupierCachee = false;
            rafraichirAffichage();
        }
    }

    private JLabel creerBadgeGagnant() {
        JLabel badge = new JLabel("GAGNE !", SwingConstants.CENTER);
        badge.setFont(new Font("SansSerif", Font.BOLD, 14));
        badge.setForeground(Color.WHITE);
        badge.setOpaque(true);
        badge.setBackground(new Color(0, 150, 136));
        badge.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        badge.setVisible(false);
        return badge;
    }

    private void mettreEnPlaceCoucheCroupier() {
        if (coucheCroupier == null || vueCroupier == null)
            return;
        Dimension d = vueCroupier.getPreferredSize();
        if (coucheCroupier.getParent() != null && coucheCroupier.getParent().getWidth() > 0) {
            d = new Dimension(Math.max(d.width, coucheCroupier.getParent().getWidth()), d.height);
        }
        coucheCroupier.setPreferredSize(d);
        coucheCroupier.setMinimumSize(d);
        vueCroupier.setBounds(0, 0, d.width, d.height);
        if (overlayCroupier != null) {
            overlayCroupier.setVisible(false);
        }
    }

    private void positionnerOverlayCroupier() {
        int largeurVue = vueCroupier.getWidth();
        if (largeurVue <= 0) {
            largeurVue = vueCroupier.getPreferredSize().width;
        }
        int startX = Math.max(20, (largeurVue - 70) / 2);
        overlayCroupier.setBounds(startX, 20, 70, 100);
        overlayCroupier.setVisible(carteCroupierCachee && mainCroupier.getCartes().size() >= 1);
    }

    private void verrouillerSiSoldeVide() {
        if (joueurPrincipal != null && joueurPrincipal.getBanque() <= 0) {
            mise = 0;
            misePrecedente = 0;
            activerJetons(false);
            boutonMiser.setEnabled(false);
        }
    }

    /**
     * Affiche un message dynamique pour guider l'utilisateur
     */
    private void afficherMessage(String message) {
        if (labelMessage != null) {
            labelMessage.setText(message);
            labelMessage.setVisible(true);

        }
    }
}

