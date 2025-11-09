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

public class VuePartie extends JPanel {

    // Les Paquets
    private Paquet pioche;          
    private Paquet mainCroupier; 
    private Paquet mainJoueur;  

    // Les vues pour dessiner les cartes
    private final VuePaquetCache vuePioche;
    private final VuePaquetVisible vueCroupier;
    private final VuePaquetVisible vueJoueur;

    //Elements propre à la partie
    private JLabel labelTitre;
    private JLabel labelSolde;
    private JLabel labelMise;
    private JLabel labelScoreCroupier;
    private JLabel labelScoreJoueur;
    private JLabel badgeCroupier;
    private JLabel badgeJoueur;
    private boolean carteCroupierCachee = true;
    private JLayeredPane coucheCroupier;
    private JPanel overlayCroupier;

    private JButton boutonMiser;
    private JButton boutonTirer;
    private JButton boutonRester;
    private JButton boutonDouble;
    private JButton boutonSeparer;
    private JButton boutonResetMise;

    private final List<JButton> jetons = new ArrayList<>();

    // A changer
    private int solde = 1000;
    private int mise = 0;

    public VuePartie() {
        
        setBackground(new Color(10, 106, 51));
        setPreferredSize(new Dimension(1080, 720));
        setLayout(new BorderLayout());

        //creer des mains 
        pioche = Paquet.creerPaquetMultiple(2);
        pioche.melanger();
        mainCroupier = Paquet.creerPaquetVide();
        mainJoueur = Paquet.creerPaquetVide();

        //création des vues des mains
        vuePioche = new VuePaquetCache(pioche);
        vueCroupier = new VuePaquetVisible(mainCroupier);
        vueJoueur = new VuePaquetVisible(mainJoueur);

        // Barre haut 
        JPanel barreHaut = creerBarreHaut();
        add(barreHaut, BorderLayout.NORTH);

        // Zone centrale
        JPanel zoneCentre = creerZoneCentre();
        add(zoneCentre, BorderLayout.CENTER);

        // Panel actions et mises 
        JPanel panelActions = new JPanel(new BorderLayout());
        panelActions.setOpaque(false);
        JPanel zoneActions = creerZoneActions();
        JPanel zoneMises = creerZoneMises();
        panelActions.add(zoneActions, BorderLayout.NORTH);
        panelActions.add(zoneMises, BorderLayout.SOUTH);
        add(panelActions, BorderLayout.SOUTH);

        //Démarrer la partie
        mettreAJourInfos();
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

        JPanel panelGauche = new JPanel();
        panelGauche.setOpaque(false);

        //Panel pour contenir le solde et la mise actuel
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

        panelHaut.add(panelGauche, BorderLayout.WEST);
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

        //Zone des cartes 
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
        overlayCroupier = new JPanel(){
            @Override protected void paintComponent(java.awt.Graphics g){
                super.paintComponent(g);
                if(!carteCroupierCachee) return;
                java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                int w=70,h=100,r=10;
                g2.setColor(new Color(25,45,85));
                g2.fillRoundRect(0,0,w,h,r,r);
                g2.setColor(Color.WHITE);
                g2.setStroke(new java.awt.BasicStroke(2));
                g2.drawRoundRect(0,0,w,h,r,r);
                g2.setColor(new Color(40,70,120));
                g2.fillRoundRect(10,15,w-20,h-30,r-2,r-2);
                g2.setColor(new Color(100,140,200));
                int cx=w/2, cy=h/2;
                java.awt.Polygon p = new java.awt.Polygon();
                p.addPoint(cx,cy-15); p.addPoint(cx+12,cy); p.addPoint(cx,cy+15); p.addPoint(cx-12,cy);
                g2.fill(p);
            }
        };
        overlayCroupier.setOpaque(false);
        coucheCroupier.add(vueCroupier, JLayeredPane.DEFAULT_LAYER);
        coucheCroupier.add(overlayCroupier, JLayeredPane.PALETTE_LAYER);
        mettreEnPlaceCoucheCroupier();
        coucheCroupier.addComponentListener(new ComponentAdapter(){
            @Override public void componentResized(ComponentEvent e)
            { 
                mettreEnPlaceCoucheCroupier(); 
            }
            @Override public void componentShown(ComponentEvent e){
                mettreEnPlaceCoucheCroupier(); 
            }
        });
        JPanel centreCroupier = new JPanel(new FlowLayout(FlowLayout.CENTER,0,0));
        centreCroupier.setOpaque(false);
        centreCroupier.add(coucheCroupier);
        blocCroupier.add(centreCroupier, BorderLayout.CENTER);
        JLabel titreCroupier = new JLabel("CROUPIER", SwingConstants.CENTER);
        titreCroupier.setFont(new Font("SansSerif", Font.BOLD, 16));
        titreCroupier.setForeground(Color.WHITE);
        labelScoreCroupier = new JLabel("", SwingConstants.CENTER);
        labelScoreCroupier.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labelScoreCroupier.setForeground(Color.WHITE);
        JPanel footerCroupier = new JPanel(new FlowLayout(FlowLayout.CENTER,8,0));
        footerCroupier.setOpaque(false);
        badgeCroupier = creerBadgeGagnant();
        footerCroupier.add(titreCroupier);
        footerCroupier.add(labelScoreCroupier);
        footerCroupier.add(badgeCroupier);
        footerCroupier.setBorder(BorderFactory.createEmptyBorder(2,0,0,0));
        blocCroupier.add(footerCroupier, BorderLayout.SOUTH);

        gbc.gridy = 0;
        gbc.weighty = 0.4;
        gbc.anchor = GridBagConstraints.PAGE_START;
        plateau.add(blocCroupier, gbc);

        //Panel joueur
        JPanel blocJoueur = new JPanel();
        blocJoueur.setOpaque(false);
        blocJoueur.setLayout(new BorderLayout());
        JPanel contJoueur = new JPanel();
        contJoueur.setOpaque(false);
        contJoueur.add(vueJoueur);
        blocJoueur.add(contJoueur, BorderLayout.CENTER);
        JLabel titreJoueur = new JLabel("JOUEUR", SwingConstants.CENTER);
        titreJoueur.setFont(new Font("SansSerif", Font.BOLD, 16));
        titreJoueur.setForeground(Color.WHITE);
        labelScoreJoueur = new JLabel("", SwingConstants.CENTER);
        labelScoreJoueur.setFont(new Font("SansSerif", Font.PLAIN, 14));
        labelScoreJoueur.setForeground(Color.WHITE);
        JPanel footerJoueur = new JPanel(new FlowLayout(FlowLayout.CENTER,8,0));
        footerJoueur.setOpaque(false);
        badgeJoueur = creerBadgeGagnant();
        footerJoueur.add(titreJoueur);
        footerJoueur.add(labelScoreJoueur);
        footerJoueur.add(badgeJoueur);
        footerJoueur.setBorder(BorderFactory.createEmptyBorder(2,0,0,0));
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
        boutonTirer = creerBoutonAction("Tirer", new Color(0, 153, 68));
        boutonRester = creerBoutonAction("Rester", new Color(187, 134, 0));
        boutonDouble = creerBoutonAction("Double", new Color(153, 0, 0));
        boutonSeparer = creerBoutonAction("Separer", new Color(102, 0, 153));

        actions.add(boutonMiser);
        actions.add(boutonTirer);
        actions.add(boutonRester);
        actions.add(boutonDouble);
        actions.add(boutonSeparer);

        
        boutonMiser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                demarrerManche();
            }
        });

         boutonTirer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                tirerPourJoueur();
            }
        });
         boutonRester.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                tirerPourCroupier();;
            }
        });
         boutonDouble.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                doublerMise();;
            }
        });
        // boutonTirer.addActionListener(e -> tirerPourJoueur());
        // boutonRester.addActionListener(e -> tirerPourCroupier());
        // boutonDouble.addActionListener(e -> doublerMise());
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
            mettreAJourInfos();
            basculerEtatBoutonsInitial();
        });
        
        ligneMiseActions.add(boutonResetMise);

        misesPanel.add(ligneJetons);
        misesPanel.add(ligneMiseActions);
        return misesPanel;
    }

    private JButton creerBoutonAction(String texte, Color fond) {
        JButton b = new JButton(texte);
        b.setFont(new Font("SansSerif", Font.BOLD, 16));
        b.setBackground(fond);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        return b;
    }

    private void creerJeton(JPanel parent, int valeur, Color fond, Color texte) {
        JButton jeton = new JButton("" + valeur);
        jeton.setFont(new Font("SansSerif", Font.BOLD, 16));
        jeton.setBackground(fond);
        jeton.setForeground(texte);
        jeton.setFocusPainted(false);
        jeton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        jeton.setOpaque(true);
        jeton.setBorder(BorderFactory.createEmptyBorder(10, 14, 10, 14));
        jeton.addActionListener(e -> {
            if (valeur <= solde - mise) {
                mise += valeur;
                mettreAJourInfos();
                boutonMiser.setEnabled(mise > 0);
            }
        });
        jetons.add(jeton);
        parent.add(jeton);
    }

    //Les méthodes qui aident à controler le jeu 
    private void demarrerManche() {
        if (mise <= 0) return;
        // Nettoyer les mains
        mainCroupier.vider();
        mainJoueur.vider();

        // Tirer 2 cartes chacun
        for (int i = 0; i < 2; i++) {
            if (!pioche.estVide()) mainJoueur.ajouterCarte(pioche.retirerPremiereCarte());
            if (!pioche.estVide()) mainCroupier.ajouterCarte(pioche.retirerPremiereCarte());
        }

        // Empecher ensuite les mises pendant la manche, seul tirer et rester sont actifs
        activerJetons(false);
        boutonMiser.setEnabled(false);
        boutonTirer.setEnabled(true);
        boutonRester.setEnabled(true);
        boutonDouble.setEnabled(true);
        mettreAJourInfos();
    }

    private void tirerPourJoueur() {
        if (!pioche.estVide()) {
            mainJoueur.ajouterCarte(pioche.retirerPremiereCarte());
            mettreAJourInfos();
            // on termine  immédiatement la manchesi le joueur dépasse 21
            if (estBust(mainJoueur.getCartes())) {
                desactiverActionsPendantAnnonce();
                revelerCarteCroupier();
                afficherGagnantEtReset(false);
            }
        }
    }

    // Tire de carte pour le croupier simpliste
    private void tirerPourCroupier() {
        if (!pioche.estVide()) {
            mainCroupier.ajouterCarte(pioche.retirerPremiereCarte());
        }
        revelerCarteCroupier();
        desactiverActionsPendantAnnonce();
        determinerEtNotifierGagnant();
    }

    //sorte d'implémentation pour  doubler la mise
    private void doublerMise() {
        if (mise > 0 && solde - mise >= mise) {
            mise *= 2;
            if (!pioche.estVide()) mainJoueur.ajouterCarte(pioche.retirerPremiereCarte());
            boutonTirer.setEnabled(false);
            boutonDouble.setEnabled(false);
            boutonRester.setEnabled(true);
            mettreAJourInfos();
        }
    }

    private void activerJetons(boolean actif) {
        for (JButton j : jetons) j.setEnabled(actif);
        boutonResetMise.setEnabled(actif);
    }

    private void basculerEtatBoutonsInitial() {
        boutonMiser.setEnabled(mise > 0);
        boutonTirer.setEnabled(false);
        boutonRester.setEnabled(false);
        boutonDouble.setEnabled(false);
        boutonSeparer.setEnabled(false);
    }

    private void mettreAJourInfos() {
        labelSolde.setText("Solde: $" + solde);
        labelMise.setText("Mise: $" + mise);
        labelScoreJoueur.setText(scoreTexte(mainJoueur.getCartes()));
        // Afficher le score du croupier basé sur ses cartes visibles si la 1re est cachée
        labelScoreCroupier.setText(scoreTexteCroupier());
        mettreEnPlaceCoucheCroupier();
        revalidate();
        repaint();
    }

    private String scoreTexteCroupier() {
        List<Carte> cartes = mainCroupier.getCartes();
        if (cartes == null || cartes.isEmpty()) return "";
        if (carteCroupierCachee && cartes.size() >= 1) {
            List<Carte> visibles = cartes.subList(1, cartes.size());
            return scoreTexte(visibles);
        }
        return scoreTexte(cartes);
    }

    private String scoreTexte(List<Carte> cartes) {
        if (cartes == null || cartes.isEmpty()) return "";
        int[] score = calculerScoreBlackjack(cartes);
        int best = (score[1] <= 21) ? score[1] : score[0];
        return String.valueOf(best);
    }

    // Calcul de score blackjack simple (As = 1 ou 11)
    private int[] calculerScoreBlackjack(List<Carte> cartes) {
        int total = 0;
        int asCount = 0;
        for (Carte c : cartes) {
            Hauteur h = c.getHauteur();
            int v;
            switch (h) {
                case DEUX: v = 2; break;
                case TROIS: v = 3; break;
                case QUATRE: v = 4; break;
                case CINQ: v = 5; break;
                case SIX: v = 6; break;
                case SEPT: v = 7; break;
                case HUIT: v = 8; break;
                case NEUF: v = 9; break;
                case DIX: case VALET: case DAME: case ROI: v = 10; break;
                case AS: default: v = 1; asCount++; break;
            }
            total += v;
        }
        int alternative = total;
        // Compter certains As comme 11 si possible
        for (int i = 0; i < asCount && alternative + 10 <= 21; i++) {
            alternative += 10;
        }
        return new int[] { total, alternative };
    }

    // Les méthodes pour annoncer le gagnant
    private boolean estBust(List<Carte> cartes) {
        int[] score = calculerScoreBlackjack(cartes);
        return score[0] > 21;
    }

    private int meilleurScoreValide(int[] score) {
        return (score[1] <= 21) ? score[1] : score[0];
    }

    private void determinerEtNotifierGagnant() {
        boolean joueurBust = estBust(mainJoueur.getCartes());
        boolean croupierBust = estBust(mainCroupier.getCartes());
        boolean joueurGagne;
        if (joueurBust && croupierBust) {
            afficherBadgeAucunPuisReset();
            return;
        } else if (joueurBust) {
            joueurGagne = false;
        } else if (croupierBust) {
            joueurGagne = true;
        } else {
            int scoreJoueur = meilleurScoreValide(calculerScoreBlackjack(mainJoueur.getCartes()));
            int scoreCroupier = meilleurScoreValide(calculerScoreBlackjack(mainCroupier.getCartes()));
            if (scoreJoueur == scoreCroupier) {
                afficherBadgeAucunPuisReset();
                return;
            }
            joueurGagne = scoreJoueur > scoreCroupier;
        }
        afficherGagnantEtReset(joueurGagne);
    }

    
    private void desactiverActionsPendantAnnonce() {
        boutonTirer.setEnabled(false);
        boutonRester.setEnabled(false);
        boutonDouble.setEnabled(false);
        boutonSeparer.setEnabled(false);
        boutonMiser.setEnabled(false);
        activerJetons(false);
    }

    private void afficherBadgeAucunPuisReset() {
        Timer t = new Timer(2000, new java.awt.event.ActionListener() {
            @Override public void actionPerformed(java.awt.event.ActionEvent e) {
                resetPlateau();
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
            @Override public void actionPerformed( ActionEvent e) {
                if (badgeJoueur != null) badgeJoueur.setVisible(false);
                if (badgeCroupier != null) badgeCroupier.setVisible(false);
                resetPlateau();
            }
        });
        t.setRepeats(false);
        t.start();
    }

    private void resetPlateau() {
        mainCroupier.vider();
        mainJoueur.vider();
        activerJetons(true);
        basculerEtatBoutonsInitial();
        carteCroupierCachee = true;
        mettreAJourInfos();
        if (vueCroupier != null) { vueCroupier.revalidate(); }
        if (vueJoueur != null) { vueJoueur.revalidate(); }
        this.revalidate();
    }

    private void revelerCarteCroupier() {
        if (carteCroupierCachee) {
            carteCroupierCachee = false;
            mettreAJourInfos();
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
        if (coucheCroupier == null || vueCroupier == null) return;
        Dimension d = vueCroupier.getPreferredSize();
        coucheCroupier.setPreferredSize(d);
        vueCroupier.setBounds(0, 0, d.width, d.height);
        if (overlayCroupier != null) {
            overlayCroupier.setBounds(20, 20, 70, 100);
            overlayCroupier.setVisible(carteCroupierCachee && mainCroupier.getCartes().size() >= 1);
        }
    }
}
