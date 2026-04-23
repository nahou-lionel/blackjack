package vue;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import cartes.modele.*;
import cartes.vue.*;
import modele.joueur.Croupier;
import modele.joueur.Joueur;
import modele.joueur.JoueurRobot;
import modele.joueur.MainJoueur;
import modele.paiement.Paiement;
import modele.paiement.TypeResultat;
import modele.partie.Action;
import modele.partie.CalculateurScore;
import modele.partie.PartieBlackjack;
import modele.partie.ResultatBlackjack;
import modele.partie.ResultatManche;
import modele.strategie.StrategieJoueur;
import modele.strategie.StrategieOptimale;
import modele.strategie.StrategieSimple;

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
    private Paquet mainRobot;

    // Ajouter les champs (après ligne 40)
    private PartieBlackjack partie;
    private Joueur joueurPrincipal;
    private JoueurRobot robot;
    private Croupier croupier;

    // Les vues pour dessiner les cartes
    private final VuePaquetCache vuePioche;
    private final VuePaquetVisible vueCroupier;
    private final VuePaquetVisible vueJoueur;
    private VuePaquetVisible vueRobot;

    // Conteneur pour les mains du joueur (pour le split)
    private JPanel conteneursMainsJoueur;

    // Conteneur pour les mains du robot (pour le split)
    private JPanel conteneursMainsRobot;

    // Elements propre à la partie
    private JLabel labelTitre;
    private JLabel labelSolde;
    private JLabel labelMise;
    private JLabel labelSoldeRobot;
    private JLabel labelMiseRobot;
    private JLabel labelScoreCroupier;
    private JLabel labelScoreJoueur;
    private JLabel labelScoreRobot;
    private JLabel labelMessage;
    private JLabel badgeCroupier;
    private JLabel badgeJoueur;
    private JLabel badgeRobot;
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

    /**
     * Constructeur avec paramètres pour configuration du robot
     *
     * @param avecRobot     true pour jouer avec un robot
     * @param typeStrategie "simple" ou "optimal"
     */
    public VuePartie(boolean avecRobot, String typeStrategie) {

        setBackground(VERT_TABLE);
        setPreferredSize(new Dimension(1080, 720));
        setLayout(new BorderLayout());

        // creer des mains (2 par défaut)
        pioche = Paquet.creerPaquetMultiple(2);
        pioche.melanger();

        croupier = new Croupier();
        joueurPrincipal = new Joueur("Joueur");

        List<Joueur> joueurs = new ArrayList<>();
        joueurs.add(joueurPrincipal);

        // Ajouter un joueur robot selon les paramètres
        if (avecRobot) {
            StrategieJoueur strategie;
            if (typeStrategie.equalsIgnoreCase("optimal")) {
                strategie = new StrategieOptimale();
            } else {
                strategie = new StrategieSimple();
            }

            robot = new JoueurRobot("Robot", 500, strategie);
            joueurs.add(robot);
            mainRobot = robot.getMain();
        }

        partie = new PartieBlackjack(pioche, croupier, joueurs, 0);

        mainCroupier = croupier.getMain();
        mainJoueur = joueurPrincipal.getMain();

        // création des vues des mains avec couleur de fond verte
        Color fondVert = VERT_TABLE;
        vuePioche = new VuePaquetCache(pioche, fondVert);
        vueCroupier = new VuePaquetVisible(mainCroupier, fondVert);
        vueJoueur = new VuePaquetVisible(mainJoueur, fondVert);

        // Créer la vue du robot seulement si le robot existe
        if (robot != null && mainRobot != null) {
            vueRobot = new VuePaquetVisible(mainRobot, fondVert);
        }

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
        // Les actions et mises sont gérées dans le bandeau central

        panelHaut.add(panelCentre, BorderLayout.CENTER);
        return panelHaut;
    }

    private JPanel creerZoneCentre() {
        JPanel centre = new JPanel(new BorderLayout());
        centre.setOpaque(false);

        // Bloc solde et mise du joueur
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

        // Bloc solde et mise du robot (créé seulement si le robot existe)
        JPanel panelInfoRobot = null;
        if (robot != null) {
            panelInfoRobot = new JPanel();
            panelInfoRobot.setOpaque(true);
            panelInfoRobot.setBackground(PANNEAU_NUIT);
            panelInfoRobot.setLayout(new BoxLayout(panelInfoRobot, BoxLayout.Y_AXIS));
            panelInfoRobot.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(150, 200, 255), 1, true),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)));

            labelSoldeRobot = new JLabel();
            labelSoldeRobot.setFont(new Font("SansSerif", Font.BOLD, 15));
            labelSoldeRobot.setForeground(new Color(200, 225, 255));
            labelSoldeRobot.setAlignmentX(CENTER_ALIGNMENT);

            labelMiseRobot = new JLabel();
            labelMiseRobot.setFont(new Font("SansSerif", Font.BOLD, 15));
            labelMiseRobot.setForeground(new Color(200, 225, 255));
            labelMiseRobot.setAlignmentX(CENTER_ALIGNMENT);

            panelInfoRobot.add(labelSoldeRobot);
            panelInfoRobot.add(Box.createVerticalStrut(5));
            panelInfoRobot.add(labelMiseRobot);
        }

        // Bandeau haut: pioche + actions/jetons + infos
        JPanel panelHautCentre = new JPanel(new BorderLayout());
        panelHautCentre.setOpaque(false);
        panelHautCentre.setBorder(BorderFactory.createEmptyBorder(8, 12, 6, 12));

        JPanel panelPioche = new JPanel();
        panelPioche.setOpaque(false);
        panelPioche.add(vuePioche);

        // Centre : actions + jetons + reset/tout miser alignés au centre
        JPanel centreActionsWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centreActionsWrapper.setOpaque(false);
        JPanel centreActions = new JPanel();
        centreActions.setOpaque(false);
        centreActions.setLayout(new BoxLayout(centreActions, BoxLayout.Y_AXIS));
        JPanel blocActions = creerZoneActions();
        blocActions.setAlignmentX(CENTER_ALIGNMENT);
        JPanel blocJetons = creerPanelJetons();
        blocJetons.setAlignmentX(CENTER_ALIGNMENT);
        JPanel blocReset = creerPanelResetMises();
        blocReset.setAlignmentX(CENTER_ALIGNMENT);
        centreActions.add(blocActions);
        centreActions.add(Box.createVerticalStrut(6));
        centreActions.add(blocJetons);
        centreActions.add(Box.createVerticalStrut(6));
        centreActions.add(blocReset);
        centreActionsWrapper.add(centreActions);

        // Droite : soldes joueur/robot
        JPanel blocDroite = new JPanel();
        blocDroite.setOpaque(false);
        blocDroite.setLayout(new BoxLayout(blocDroite, BoxLayout.Y_AXIS));
        panelInfo.setAlignmentX(RIGHT_ALIGNMENT);
        blocDroite.add(panelInfo);

        // Ajouter les infos du robot seulement s'il existe
        if (panelInfoRobot != null) {
            panelInfoRobot.setAlignmentX(RIGHT_ALIGNMENT);
            blocDroite.add(Box.createVerticalStrut(6));
            blocDroite.add(panelInfoRobot);
        }

        panelHautCentre.add(panelPioche, BorderLayout.WEST);
        panelHautCentre.add(centreActionsWrapper, BorderLayout.CENTER);
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

        // Panel Robot (créé seulement si le robot existe)
        JPanel blocRobot = null;
        if (robot != null) {
            blocRobot = new JPanel(new BorderLayout());
            blocRobot.setOpaque(false);
            blocRobot.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
            blocRobot.setAlignmentX(CENTER_ALIGNMENT);

            // Conteneur dynamique pour les mains du robot (1 ou 2 selon split)
            conteneursMainsRobot = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 12));
            conteneursMainsRobot.setOpaque(false);
            rafraichirAffichageMainsRobot(); // Initialiser avec une seule main

            JLabel titreRobot = new JLabel("ROBOT", SwingConstants.CENTER);
            titreRobot.setFont(new Font("SansSerif", Font.BOLD, 16));
            titreRobot.setForeground(new Color(150, 200, 255)); // Bleu clair pour différencier
            labelScoreRobot = new JLabel("0", SwingConstants.CENTER);
            labelScoreRobot.setFont(new Font("SansSerif", Font.PLAIN, 14));
            labelScoreRobot.setForeground(ACCENT_AMBRE);

            JPanel footerRobot = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
            footerRobot.setOpaque(false);
            badgeRobot = creerBadgeGagnant();
            footerRobot.add(titreRobot);
            footerRobot.add(labelScoreRobot);
            footerRobot.add(badgeRobot);

            blocRobot.add(creerBandeauCartes(conteneursMainsRobot), BorderLayout.CENTER);
            blocRobot.add(footerRobot, BorderLayout.SOUTH);
        }

        // Panel Joueur
        JPanel blocJoueur = new JPanel(new BorderLayout());
        blocJoueur.setOpaque(false);
        blocJoueur.setBorder(BorderFactory.createEmptyBorder(12, 0, 6, 0));
        blocJoueur.setAlignmentX(CENTER_ALIGNMENT);

        // Conteneur dynamique pour les mains (1 ou 2 selon split)
        conteneursMainsJoueur = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 12));
        conteneursMainsJoueur.setOpaque(false);
        rafraichirAffichageMainsJoueur(); // Initialiser avec une seule main

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

        blocJoueur.add(creerBandeauCartes(conteneursMainsJoueur), BorderLayout.CENTER);
        blocJoueur.add(footerJoueur, BorderLayout.SOUTH);

        // Ajouter les panneaux au plateau
        gbc.gridy = 0;
        gbc.weighty = 1.0;
        gbc.insets = new java.awt.Insets(0, 0, 6, 0);
        plateau.add(blocCroupier, gbc);

        // Ajouter le robot seulement s'il existe
        if (blocRobot != null) {
            gbc.gridy = 1;
            gbc.weighty = 0.8;
            gbc.insets = new java.awt.Insets(0, 0, 6, 0);
            plateau.add(blocRobot, gbc);
            gbc.gridy = 2;
        } else {
            gbc.gridy = 1;
        }

        gbc.weighty = 1.0;
        gbc.insets = new java.awt.Insets(0, 0, 0, 0);
        plateau.add(blocJoueur, gbc);

        centre.add(plateau, BorderLayout.CENTER);
        return centre;
    }

    private JPanel creerBandeauCartes(JComponent contenu) {
        JPanel supportCentre = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 8));
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
                int arc = 18;
                g2.setColor(VERT_TABLE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.setColor(new Color(255, 255, 255, 70));
                g2.setStroke(new java.awt.BasicStroke(2f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, arc, arc);
                g2.dispose();
            }
        };
        bandeau.setOpaque(true);
        bandeau.setBackground(VERT_TABLE);
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

    private JPanel creerPanelJetons() {
        JPanel ligneJetons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        ligneJetons.setOpaque(false);
        creerJeton(ligneJetons, 10, new Color(230, 230, 230), Color.BLACK);
        creerJeton(ligneJetons, 25, new Color(204, 0, 0), Color.WHITE);
        creerJeton(ligneJetons, 50, new Color(0, 153, 0), Color.WHITE);
        creerJeton(ligneJetons, 100, new Color(0, 102, 204), Color.WHITE);
        return ligneJetons;
    }

    private JPanel creerPanelResetMises() {
        JPanel ligneMiseActions = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        ligneMiseActions.setOpaque(false);

        boutonResetMise = creerBoutonAction("Mise à 0", new Color(80, 80, 80));
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
        return ligneMiseActions;
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

        // Mettre à jour les références des mains après réinitialisation
        mainJoueur = joueurPrincipal.getMain();
        mainCroupier = croupier.getMain();
        // mainRobot est déjà vide grâce à demarrerNouvellePartie()

        joueurPrincipal.miser(mise);

        // Faire miser le robot s'il existe
        for (Joueur j : partie.getJoueurs()) {
            if (j.estRobot() && j instanceof JoueurRobot) {
                JoueurRobot robot = (JoueurRobot) j;
                int miseRobot = robot.choisirMise();
                robot.miser(miseRobot);
                System.out.println("Le robot mise " + miseRobot + "$");
                break;
            }
        }

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

                // on termine la manche si le joueur dépasse 21
                if (CalculateurScore.aDepasse(joueurPrincipal.getMain())) {
                    peutTirer = false;
                    desactiverActionsPendantAnnonce();
                    afficherMessage("Vous avez dépassé 21...");

                    // Faire jouer le robot avant de terminer
                    faireJouerRobot(() -> {
                        afficherMainCompleteCroupier();
                        afficherMessage("Le croupier joue...");

                        // Terminer la manche normalement
                        ResultatManche resultat = partie.terminerManche();
                        afficherResultat(resultat);
                        rafraichirAffichage();
                    });
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
            desactiverActionsPendantAnnonce();

            // Faire jouer le robot avant le croupier
            faireJouerRobot(() -> {
                afficherMainCompleteCroupier();
                afficherMessage("Le croupier joue...");

                // L'orchestrateur gère le tour du croupier, calcule les résultats et applique
                // les paiements
                ResultatManche resultat = partie.terminerManche();

                afficherResultat(resultat);
                rafraichirAffichage();
            });
        }
    }

    /**
     * Passe à la main suivante lors d'un split, ou termine la manche si toutes les
     * mains sont jouées
     */
    private void passerMainSuivante() {
        mainActiveIndex++;

        if (mainActiveIndex < joueurPrincipal.getNombreMains()) {
            // Il reste des mains à jouer
            afficherMessage("Jouez maintenant la main " + (mainActiveIndex + 1));
            rafraichirAffichage();

            // Si la main suivante est une main d'As splittés, elle ne peut tirer qu'une
            // carte
            // et a déjà reçu cette carte, donc on passe automatiquement à la suivante
            if (!joueurPrincipal.getMainAIndex(mainActiveIndex).peutEncoreTirer()) {
                passerMainSuivante();
            }
        } else {
            // Toutes les mains ont été jouées, passer au tour du robot puis du croupier
            peutTirer = false;
            desactiverActionsPendantAnnonce();

            // Faire jouer le robot
            faireJouerRobot(() -> {
                afficherMainCompleteCroupier();
                afficherMessage("Le croupier joue...");

                ResultatManche resultat = partie.terminerManche();
                afficherResultatSplit(resultat);
                rafraichirAffichage();
            });
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
                afficherMessage("Vous avez dépassé 21...");

                // Faire jouer le robot
                faireJouerRobot(() -> {
                    afficherMainCompleteCroupier();
                    afficherMessage("Le croupier joue...");
                    ResultatManche resultat = partie.terminerManche();
                    afficherResultat(resultat);
                    rafraichirAffichage();
                });
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
        } else {
            labelScoreJoueur.setText(scoreTexte(joueurPrincipal.getMain()));
        }

        // Rafraîchir l'affichage des mains (1 ou 2 selon le mode)
        rafraichirAffichageMainsJoueur();

        // Afficher le score du robot (gérer le split)
        if (robot != null) {
            rafraichirAffichageMainsRobot();

            if (robot.getNombreMains() > 1) {
                // Le robot a splitté - afficher les scores des deux mains
                StringBuilder scoresRobot = new StringBuilder();
                for (int i = 0; i < robot.getNombreMains(); i++) {
                    scoresRobot.append("M").append(i + 1).append(": ");
                    scoresRobot.append(robot.getScore(i));
                    if (i < robot.getNombreMains() - 1) {
                        scoresRobot.append(" | ");
                    }
                }
                labelScoreRobot.setText(scoresRobot.toString());

                // Mise totale
                int miseTotaleRobot = 0;
                for (MainJoueur main : robot.getMains()) {
                    miseTotaleRobot += main.getMise();
                }
                labelMiseRobot.setText("Mise robot: $" + miseTotaleRobot);
            } else {
                // Affichage normal
                labelScoreRobot.setText(scoreTexte(robot.getMain()));
                labelMiseRobot.setText("Mise robot: $" + robot.getMiseActuelle());
            }

            labelSoldeRobot.setText("Solde robot: $" + robot.getBanque());
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
        afficherMessage(construireMessageResultats(resultat));
        afficherBadgesEtReset(resultat);
    }

    /**
     * Affiche le résultat d'une manche avec split (plusieurs mains)
     *
     * @param resultat Le résultat complet de la manche
     */
    private void afficherResultatSplit(ResultatManche resultat) {
        afficherMessage(construireMessageResultats(resultat));
        afficherBadgesEtReset(resultat);
    }

    private void desactiverActionsPendantAnnonce() {
        peutTirer = false;
        boutonRester.setEnabled(false);
        boutonDouble.setEnabled(false);
        boutonSeparer.setEnabled(false);
        boutonMiser.setEnabled(false);
        activerJetons(false);
    }

    /**
     * Réinitialise le plateau pour préparer une nouvelle manche
     */
    private void reinitialiserPlateau() {
        mainCroupier.vider();
        activerJetons(true);

        // Réinitialiser les variables de split
        enModeSplit = false;
        mainActiveIndex = 0;

        // Réinitialiser le joueur (important pour revenir à 1 seule main après split)
        joueurPrincipal.reinitialiser();

        // Mettre à jour les références des mains après réinitialisation
        mainJoueur = joueurPrincipal.getMain();

        // Pour le robot, réinitialiser pour revenir à une seule main après split
        if (robot != null) {
            robot.reinitialiser();
            mainRobot = robot.getMain();
        }

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
        coucheCroupier.setPreferredSize(d);
        coucheCroupier.setMinimumSize(d);
        vueCroupier.setBounds(0, 0, d.width, d.height);
        if (overlayCroupier != null) {
            overlayCroupier.setBounds(0, 0, 0, 0);
            overlayCroupier.setVisible(false);
        }
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

    /**
     * Rafraîchit l'affichage des mains du joueur (1 ou 2 en mode split)
     */
    private void rafraichirAffichageMainsJoueur() {
        if (conteneursMainsJoueur == null)
            return;

        conteneursMainsJoueur.removeAll();

        int nombreMains = joueurPrincipal.getNombreMains();

        for (int i = 0; i < nombreMains; i++) {
            Paquet mainActuelle = joueurPrincipal.getMainAIndex(i).getMain();

            // Créer une vue pour cette main
            Color fondVert = VERT_TABLE;
            VuePaquetVisible vueMain = new VuePaquetVisible(mainActuelle, fondVert);

            // Créer un conteneur pour cette main
            JPanel conteneurMain = new JPanel();
            conteneurMain.setLayout(new BoxLayout(conteneurMain, BoxLayout.Y_AXIS));
            conteneurMain.setOpaque(false);

            // Titre de la main
            String titreMain = nombreMains > 1 ? "Main " + (i + 1) : "";

            // Indicateur si c'est la main active
            if (nombreMains > 1 && i == mainActiveIndex && enModeSplit) {
                titreMain += " ACTIVE";
            }

            if (!titreMain.isEmpty()) {
                JLabel labelTitreMain = new JLabel(titreMain, SwingConstants.CENTER);
                labelTitreMain.setFont(new Font("SansSerif", Font.BOLD, 14));
                labelTitreMain.setForeground(i == mainActiveIndex && enModeSplit ? ACCENT_TURQUOISE : Color.WHITE);
                labelTitreMain.setAlignmentX(CENTER_ALIGNMENT);
                conteneurMain.add(labelTitreMain);
                conteneurMain.add(Box.createVerticalStrut(8));
            }

            // Ajouter la vue des cartes
            vueMain.setAlignmentX(CENTER_ALIGNMENT);
            conteneurMain.add(vueMain);

            // Score de cette main
            int scoreMain = CalculateurScore.calculerScore(mainActuelle);
            JLabel labelScoreMain = new JLabel("Score: " + scoreMain, SwingConstants.CENTER);
            labelScoreMain.setFont(new Font("SansSerif", Font.PLAIN, 13));
            labelScoreMain.setForeground(ACCENT_AMBRE);
            labelScoreMain.setAlignmentX(CENTER_ALIGNMENT);
            conteneurMain.add(Box.createVerticalStrut(6));
            conteneurMain.add(labelScoreMain);

            // Bordure si c'est la main active
            if (nombreMains > 1 && i == mainActiveIndex && enModeSplit) {
                conteneurMain.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT_TURQUOISE, 3, true),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)));
            } else {
                conteneurMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            }

            conteneursMainsJoueur.add(conteneurMain);
        }

        conteneursMainsJoueur.revalidate();
        conteneursMainsJoueur.repaint();
    }

    /**
     * Rafraîchit l'affichage des mains du robot (gère 1 ou 2 mains après un split)
     */
    private void rafraichirAffichageMainsRobot() {
        if (conteneursMainsRobot == null || robot == null)
            return;

        conteneursMainsRobot.removeAll();

        int nombreMains = robot.getNombreMains();

        for (int i = 0; i < nombreMains; i++) {
            Paquet mainActuelle = robot.getMainAIndex(i).getMain();

            // Créer une vue pour cette main
            Color fondVert = VERT_TABLE;
            VuePaquetVisible vueMain = new VuePaquetVisible(mainActuelle, fondVert);

            // Créer un conteneur pour cette main
            JPanel conteneurMain = new JPanel();
            conteneurMain.setLayout(new BoxLayout(conteneurMain, BoxLayout.Y_AXIS));
            conteneurMain.setOpaque(false);

            // Titre de la main
            String titreMain = nombreMains > 1 ? "Main " + (i + 1) : "";

            if (!titreMain.isEmpty()) {
                JLabel labelTitreMain = new JLabel(titreMain, SwingConstants.CENTER);
                labelTitreMain.setFont(new Font("SansSerif", Font.BOLD, 14));
                labelTitreMain.setForeground(new Color(150, 200, 255)); // Bleu clair comme le robot
                labelTitreMain.setAlignmentX(CENTER_ALIGNMENT);
                conteneurMain.add(labelTitreMain);
                conteneurMain.add(Box.createVerticalStrut(8));
            }

            // Ajouter la vue des cartes
            vueMain.setAlignmentX(CENTER_ALIGNMENT);
            conteneurMain.add(vueMain);

            // Score de cette main
            int scoreMain = CalculateurScore.calculerScore(mainActuelle);
            JLabel labelScoreMain = new JLabel("Score: " + scoreMain, SwingConstants.CENTER);
            labelScoreMain.setFont(new Font("SansSerif", Font.PLAIN, 13));
            labelScoreMain.setForeground(ACCENT_AMBRE);
            labelScoreMain.setAlignmentX(CENTER_ALIGNMENT);
            conteneurMain.add(Box.createVerticalStrut(6));
            conteneurMain.add(labelScoreMain);

            // Bordure simple
            conteneurMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            conteneursMainsRobot.add(conteneurMain);
        }

        conteneursMainsRobot.revalidate();
        conteneursMainsRobot.repaint();
    }

    /**
     * Fait jouer le robot automatiquement selon sa stratégie
     *
     * @param onFinish Action à exécuter quand le robot a fini
     */
    private void faireJouerRobot(Runnable onFinish) {
        // Trouver le joueur robot dans la liste des joueurs
        JoueurRobot robotJoueur = null;
        for (Joueur j : partie.getJoueurs()) {
            if (j.estRobot() && j instanceof JoueurRobot) {
                robotJoueur = (JoueurRobot) j;
                break;
            }
        }

        if (robotJoueur == null) {
            // Pas de robot, exécuter directement la suite
            if (onFinish != null) {
                onFinish.run();
            }
            return;
        }

        final JoueurRobot robotFinal = robotJoueur;
        final Carte carteVisibleCroupier = croupier.getMain().getCarte(1);

        // Vérifier si le robot veut splitter
        if (robotFinal.veutSplitter(carteVisibleCroupier)) {
            afficherMessage("Robot décide de SPLIT !");

            // Attendre un peu avant de splitter
            Timer splitTimer = new Timer(1500, evt -> {
                // Effectuer le split
                int indexRobot = partie.getJoueurs().indexOf(robotFinal);
                boolean splitReussi = partie.effectuerSplit(indexRobot);

                if (splitReussi) {
                    rafraichirAffichage();
                    afficherMessage("Robot a splitté sa paire");

                    // Jouer la première main, puis la deuxième
                    Timer delayTimer = new Timer(1500, e -> {
                        afficherMessage("Robot joue sa première main...");
                        faireJouerRobotMain(robotFinal, 0, carteVisibleCroupier, () -> {
                            // Première main terminée, jouer la deuxième
                            Timer entreMainsTimer = new Timer(1500, e2 -> {
                                afficherMessage("Robot joue sa deuxième main...");
                                faireJouerRobotMain(robotFinal, 1, carteVisibleCroupier, onFinish);
                            });
                            entreMainsTimer.setRepeats(false);
                            entreMainsTimer.start();
                        });
                    });
                    delayTimer.setRepeats(false);
                    delayTimer.start();
                } else {
                    afficherMessage("Split impossible, robot joue normalement");
                    faireJouerRobotMain(robotFinal, 0, carteVisibleCroupier, onFinish);
                }
            });
            splitTimer.setRepeats(false);
            splitTimer.start();
        } else {
            // Pas de split, jouer normalement
            int scoreRobot = robotFinal.getScore();
            afficherMessage("Robot (score: " + scoreRobot + ") à son tour");
            faireJouerRobotMain(robotFinal, 0, carteVisibleCroupier, onFinish);
        }
    }

    /**
     * Fait jouer une main spécifique du robot
     *
     * @param robotFinal           Le robot
     * @param indexMain            L'index de la main à jouer
     * @param carteVisibleCroupier La carte visible du croupier
     * @param onFinish             Action à exécuter quand la main est terminée
     */
    private void faireJouerRobotMain(JoueurRobot robotFinal, int indexMain, Carte carteVisibleCroupier,
            Runnable onFinish) {
        // Utiliser un Timer pour jouer sans bloquer l'interface
        final Timer[] timerRef = new Timer[1]; // Array pour permettre la référence récursive
        timerRef[0] = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int scoreActuel = robotFinal.getScore(indexMain);
                MainJoueur mainActuelle = robotFinal.getMainAIndex(indexMain);

                // Si le robot a dépassé ou a 21, il arrête
                if (scoreActuel >= 21) {
                    if (scoreActuel == 21) {
                        String prefix = robotFinal.getNombreMains() > 1 ? "Robot (main " + (indexMain + 1) + ") "
                                : "Robot ";
                        afficherMessage(prefix + "atteint 21 !");
                    }
                    timerRef[0].stop();
                    // Exécuter la suite après un court délai
                    Timer finishTimer = new Timer(1500, evt -> {
                        if (onFinish != null)
                            onFinish.run();
                    });
                    finishTimer.setRepeats(false);
                    finishTimer.start();
                    return;
                }

                // Vérifier si la main ne peut plus tirer (cas des As splittés)
                if (mainActuelle.estSplittee() && mainActuelle.getMain().getTaille() >= 2 &&
                        mainActuelle.getMain().getCarte(0).getHauteur() == Hauteur.AS) {
                    // Les As splittés ne peuvent tirer qu'une carte
                    String prefix = robotFinal.getNombreMains() > 1 ? "Robot (main " + (indexMain + 1) + ") "
                            : "Robot ";
                    afficherMessage(prefix + "termine (As splitté, score: " + scoreActuel + ")");
                    timerRef[0].stop();
                    Timer finishTimer = new Timer(1500, evt -> {
                        if (onFinish != null)
                            onFinish.run();
                    });
                    finishTimer.setRepeats(false);
                    finishTimer.start();
                    return;
                }

                Action action = robotFinal.choisirAction(carteVisibleCroupier, indexMain);
                String prefix = robotFinal.getNombreMains() > 1 ? "Robot (main " + (indexMain + 1) + ", score: "
                        : "Robot (score: ";

                if (action == Action.TIRER) {
                    afficherMessage(prefix + scoreActuel + ") -> TIRE");

                    Carte nouvelleCarte = pioche.retirerPremiereCarte();
                    robotFinal.recevoirCarte(nouvelleCarte, indexMain);
                    int nouveauScore = robotFinal.getScore(indexMain);

                    rafraichirAffichage();

                    String prefixResult = robotFinal.getNombreMains() > 1 ? "Robot (main " + (indexMain + 1) + ") "
                            : "Robot ";
                    afficherMessage(prefixResult + "tire " + nouvelleCarte + " (score: " + nouveauScore + ")");

                    // Vérifier si le robot a dépassé
                    if (robotFinal.aDepasse(indexMain)) {
                        afficherMessage(prefixResult + "dépasse 21 !");
                        timerRef[0].stop();
                        // Exécuter la suite après un court délai
                        Timer finishTimer = new Timer(1500, evt -> {
                            if (onFinish != null)
                                onFinish.run();
                        });
                        finishTimer.setRepeats(false);
                        finishTimer.start();
                    }
                } else if (action == Action.DOUBLER) {
                    int miseActuelle = mainActuelle.getMise();

                    // Vérifier si le robot a assez d'argent pour doubler
                    if (robotFinal.getBanque() >= miseActuelle) {
                        afficherMessage(prefix + scoreActuel + ") -> DOUBLE");

                        // Doubler la mise
                        robotFinal.miser(miseActuelle);
                        mainActuelle.setMise(miseActuelle * 2);

                        // Tirer exactement une carte
                        Carte nouvelleCarte = pioche.retirerPremiereCarte();
                        robotFinal.recevoirCarte(nouvelleCarte, indexMain);
                        int nouveauScore = robotFinal.getScore(indexMain);

                        rafraichirAffichage();

                        String prefixResult = robotFinal.getNombreMains() > 1 ? "Robot (main " + (indexMain + 1) + ") "
                                : "Robot ";
                        afficherMessage(
                                prefixResult + "double et tire " + nouvelleCarte + " (score: " + nouveauScore + ")");

                        // Arrêter après avoir tiré une seule carte
                        timerRef[0].stop();
                        Timer finishTimer = new Timer(1500, evt -> {
                            if (onFinish != null)
                                onFinish.run();
                        });
                        finishTimer.setRepeats(false);
                        finishTimer.start();
                    } else {
                        // Pas assez d'argent pour doubler, tirer normalement
                        afficherMessage(prefix + scoreActuel + ") -> TIRE (pas assez pour doubler)");

                        Carte nouvelleCarte = pioche.retirerPremiereCarte();
                        robotFinal.recevoirCarte(nouvelleCarte, indexMain);
                        int nouveauScore = robotFinal.getScore(indexMain);

                        rafraichirAffichage();

                        String prefixResult = robotFinal.getNombreMains() > 1 ? "Robot (main " + (indexMain + 1) + ") "
                                : "Robot ";
                        afficherMessage(prefixResult + "tire " + nouvelleCarte + " (score: " + nouveauScore + ")");

                        if (robotFinal.aDepasse(indexMain)) {
                            afficherMessage(prefixResult + "dépasse 21 !");
                            timerRef[0].stop();
                            Timer finishTimer = new Timer(1500, evt -> {
                                if (onFinish != null)
                                    onFinish.run();
                            });
                            finishTimer.setRepeats(false);
                            finishTimer.start();
                        }
                    }
                } else {
                    afficherMessage(prefix + scoreActuel + ") -> RESTE");
                    timerRef[0].stop();
                    // Exécuter la suite après un court délai
                    Timer finishTimer = new Timer(1500, evt -> {
                        if (onFinish != null)
                            onFinish.run();
                    });
                    finishTimer.setRepeats(false);
                    finishTimer.start();
                }
            }
        });

        timerRef[0].start();
    }

    private void afficherBadgesEtReset(ResultatManche resultat) {
        boolean joueurGagnant = resultat.aGagne(joueurPrincipal);
        boolean robotGagnant = robot != null && resultat.aGagne(robot);

        boolean toutPush = true;
        for (Joueur j : partie.getJoueurs()) {
            List<Paiement> ps = resultat.getPaiementsPour(j);
            if (ps == null || ps.isEmpty()) {
                toutPush = false;
                break;
            }
            Paiement p = ps.get(0);
            if (p.getTypeResultat() != TypeResultat.PUSH) {
                toutPush = false;
                break;
            }
        }

        if (badgeJoueur != null)
            badgeJoueur.setVisible(joueurGagnant);
        if (badgeRobot != null)
            badgeRobot.setVisible(robotGagnant);
        if (badgeCroupier != null)
            badgeCroupier.setVisible(!joueurGagnant && !robotGagnant && !toutPush);

        revalidate();
        repaint();

        Timer t = new Timer(4000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (badgeJoueur != null)
                    badgeJoueur.setVisible(false);
                if (badgeRobot != null)
                    badgeRobot.setVisible(false);
                if (badgeCroupier != null)
                    badgeCroupier.setVisible(false);
                reinitialiserPlateau();
            }
        });
        t.setRepeats(false);
        t.start();
    }

    private String construireMessageResultats(ResultatManche resultat) {
        List<String> parties = new ArrayList<>();
        for (Joueur j : partie.getJoueurs()) {
            List<Paiement> paiements = resultat.getPaiementsPour(j);
            String nom = j.getNom();
            if (paiements == null || paiements.isEmpty()) {
                parties.add(nom + " : match nul");
                continue;
            }
            Paiement p = paiements.get(0);
            String statut;
            switch (p.getTypeResultat()) {
                case VICTOIRE:
                    statut = "gagne (+" + p.getProfit() + "$)";
                    break;
                case BLACKJACK:
                    statut = "BLACKJACK (+" + p.getProfit() + "$)";
                    break;
                case PUSH:
                    statut = "match nul";
                    break;
                case PERTE:
                default:
                    statut = "perd";
                    break;
            }
            parties.add(nom + " : " + statut);
        }
        if (parties.isEmpty()) {
            return "Aucun résultat";
        }
        return String.join(" | ", parties);
    }
}
