package vue;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import modele.PartieBlackjack;
import modele.Paiement;
import modele.ResultatBlackjack;
import modele.ResultatManche;

public class VuePartie extends JPanel {

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

    public VuePartie() {

        setBackground(new Color(10, 106, 51));
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

        // Zone centrale avec bandeau de message
        JLayeredPane conteneurCentre = new JLayeredPane();
        conteneurCentre.setLayout(null);

        JPanel zoneCentre = creerZoneCentre();
        conteneurCentre.add(zoneCentre, JLayeredPane.DEFAULT_LAYER);

        // Créer le bandeau de message
        labelMessage = new JLabel("Placez votre mise pour commencer", SwingConstants.CENTER);
        labelMessage.setFont(new Font("SansSerif", Font.BOLD, 18));
        labelMessage.setForeground(new Color(255, 215, 255));
        labelMessage.setOpaque(true);
        labelMessage.setBackground(new Color(0, 0, 0, 100));
        labelMessage.setBorder(BorderFactory.createEmptyBorder(15, 40, 15, 40));
        labelMessage.setVisible(true);
        conteneurCentre.add(labelMessage, JLayeredPane.PALETTE_LAYER);

        // Gérer le redimensionnement
        conteneurCentre.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                zoneCentre.setBounds(0, 0, conteneurCentre.getWidth(), conteneurCentre.getHeight());
                positionnerBandeau();
            }
        });

        add(conteneurCentre, BorderLayout.CENTER);

        // Panel actions et mises
        JPanel panelActions = new JPanel(new BorderLayout());
        panelActions.setOpaque(false);
        JPanel zoneActions = creerZoneActions();
        JPanel zoneMises = creerZoneMises();
        panelActions.add(zoneActions, BorderLayout.NORTH);
        panelActions.add(zoneMises, BorderLayout.SOUTH);
        add(panelActions, BorderLayout.SOUTH);

        // Démarrer la partie
        rafraichirAffichage();
        basculerEtatBoutonsInitial();
    }

    private JPanel creerBarreHaut() {
        JPanel panelHaut = new JPanel(new BorderLayout());
        panelHaut.setOpaque(true);
        panelHaut.setBackground(new Color(14, 14, 14));
        panelHaut.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        labelTitre = new JLabel("BLACKJACK", SwingConstants.CENTER);
        labelTitre.setFont(new Font("SansSerif", Font.BOLD, 24));
        labelTitre.setForeground(Color.WHITE);

        // Panel pour contenir le solde et la mise actuel
        JPanel panelInfo = new JPanel();
        panelInfo.setOpaque(false);
        panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.X_AXIS));
        labelSolde = new JLabel();
        labelSolde.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labelSolde.setForeground(Color.WHITE);
        labelMise = new JLabel();
        labelMise.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labelMise.setForeground(Color.WHITE);
        panelInfo.add(labelSolde);
        panelInfo.add(Box.createHorizontalStrut(20));
        panelInfo.add(labelMise);

        panelHaut.add(labelTitre, BorderLayout.CENTER);
        panelHaut.add(panelInfo, BorderLayout.EAST);
        return panelHaut;
    }

    private JPanel creerZoneCentre() {
        JPanel centre = new JPanel(new BorderLayout());
        centre.setOpaque(false);

        JPanel panelPioche = new JPanel();
        panelPioche.setOpaque(false);
        panelPioche.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        panelPioche.add(vuePioche);
        centre.add(panelPioche, BorderLayout.WEST);

        // Zone des cartes
        JPanel plateau = new JPanel();
        plateau.setOpaque(false);
        plateau.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 10, 4, 10);

        // panelCroupier
        JPanel blocCroupier = new JPanel();
        blocCroupier.setOpaque(false);
        blocCroupier.setLayout(new BorderLayout());
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
        JPanel centreCroupier = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centreCroupier.setOpaque(false);
        centreCroupier.add(coucheCroupier);
        blocCroupier.add(centreCroupier, BorderLayout.CENTER);
        JLabel titreCroupier = new JLabel("CROUPIER", SwingConstants.CENTER);
        titreCroupier.setFont(new Font("SansSerif", Font.BOLD, 16));
        titreCroupier.setForeground(Color.WHITE);
        labelScoreCroupier = new JLabel("", SwingConstants.CENTER);
        labelScoreCroupier.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labelScoreCroupier.setForeground(Color.WHITE);
        JPanel footerCroupier = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        footerCroupier.setOpaque(false);
        badgeCroupier = creerBadgeGagnant();
        footerCroupier.add(titreCroupier);
        footerCroupier.add(labelScoreCroupier);
        footerCroupier.add(badgeCroupier);
        footerCroupier.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        blocCroupier.add(footerCroupier, BorderLayout.SOUTH);

        gbc.gridy = 0;
        gbc.weighty = 0.4;
        gbc.anchor = GridBagConstraints.PAGE_START;
        plateau.add(blocCroupier, gbc);

        // Panel joueur
        JPanel blocJoueur = new JPanel();
        blocJoueur.setOpaque(false);
        blocJoueur.setLayout(new BorderLayout());
        JPanel contJoueur = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        contJoueur.setOpaque(false);
        contJoueur.add(vueJoueur);
        blocJoueur.add(contJoueur, BorderLayout.CENTER);
        JLabel titreJoueur = new JLabel("JOUEUR", SwingConstants.CENTER);
        titreJoueur.setFont(new Font("SansSerif", Font.BOLD, 16));
        titreJoueur.setForeground(Color.WHITE);
        labelScoreJoueur = new JLabel("", SwingConstants.CENTER);
        labelScoreJoueur.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labelScoreJoueur.setForeground(Color.WHITE);
        JPanel footerJoueur = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        footerJoueur.setOpaque(false);
        badgeJoueur = creerBadgeGagnant();
        footerJoueur.add(titreJoueur);
        footerJoueur.add(labelScoreJoueur);
        footerJoueur.add(badgeJoueur);
        footerJoueur.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        blocJoueur.add(footerJoueur, BorderLayout.SOUTH);

        gbc.gridy = 1;
        gbc.weighty = 0.6;
        gbc.anchor = GridBagConstraints.PAGE_END;
        plateau.add(blocJoueur, gbc);

        centre.add(plateau, BorderLayout.CENTER);
        return centre;
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
        // Separer laissé inactif (selon règle, nécessite logique de paires)
        boutonSeparer.setEnabled(false);

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

        ligneMiseActions.add(boutonResetMise);

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
        afficherMessage("À votre tour - Cliquez sur la pioche pour tirer ou Rester");
        rafraichirAffichage();
    }

    /**
     * Gère l'action Hit : le joueur tire une carte supplémentaire
     */
    private void gererActionHit() {
        if (!pioche.estVide()) {
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

    /**
     * Gère l'action Stand : le joueur reste, le croupier joue et la manche se
     * termine
     */
    private void gererActionStand() {
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
        labelMise.setText("Mise: $" + mise);
        labelScoreJoueur.setText(scoreTexte(joueurPrincipal.getMain()));
        // Afficher le score du croupier basé sur ses cartes visibles si la 1re est
        // cachée
        labelScoreCroupier.setText(scoreTexteCroupier());
        mettreEnPlaceCoucheCroupier();
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
        basculerEtatBoutonsInitial();
        carteCroupierCachee = true;
        mise = 0;
        afficherMessage("Placez votre mise pour commencer");
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
        vueCroupier.setBounds(0, 0, d.width, d.height);
        if (overlayCroupier != null) {
            overlayCroupier.setBounds(20, 20, 70, 100);
            overlayCroupier.setVisible(carteCroupierCachee && mainCroupier.getCartes().size() >= 1);
        }
    }

    /**
     * Affiche un message dynamique pour guider l'utilisateur
     */
    private void afficherMessage(String message) {
        if (labelMessage != null) {
            labelMessage.setText(message);
            labelMessage.setVisible(true);
            positionnerBandeau();

            // Masquer le bandeau après 3 secondes pour tous les messages
            Timer timer = new Timer(3000, e -> labelMessage.setVisible(false));
            timer.setRepeats(false);
            timer.start();
        }
    }

    /**
     * Positionne le bandeau au centre de la fenêtre
     */
    private void positionnerBandeau() {
        if (labelMessage != null && labelMessage.getParent() != null) {
            Dimension tailleBandeau = labelMessage.getPreferredSize();
            int largeur = labelMessage.getParent().getWidth();
            int hauteur = labelMessage.getParent().getHeight();

            int x = 0;
            int y = (hauteur - tailleBandeau.height) / 2;

            labelMessage.setBounds(x, y, largeur, tailleBandeau.height);
        }
    }
}
