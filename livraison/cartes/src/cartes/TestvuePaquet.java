package cartes;

import javax.swing.*;

import cartes.modele.Carte;
import cartes.modele.Couleur;
import cartes.modele.Hauteur;
import cartes.modele.Paquet;
import cartes.vue.VuePaquet;
import cartes.vue.VuePaquetInteractive;
import cartes.vue.VuePaquetVisible;

import java.awt.*;
import java.awt.event.*;

/**
 * Programme de test pour la VuePaquetEventail
 */
public class TestvuePaquet {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            creerEtAfficherGUI();
        });
    }

    private static void creerEtAfficherGUI() {
        // Créer la fenêtre principale
        JFrame frame = new JFrame("Test Vue Paquet en Éventail");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Créer un paquet avec quelques cartes
        Paquet mainJoueur = Paquet.creerPaquetVide();
        mainJoueur.ajouterCarte(new Carte(Couleur.COEUR, Hauteur.AS));
        mainJoueur.ajouterCarte(new Carte(Couleur.PIQUE, Hauteur.ROI));
        mainJoueur.ajouterCarte(new Carte(Couleur.CARREAU, Hauteur.DAME));
        mainJoueur.ajouterCarte(new Carte(Couleur.TREFLE, Hauteur.VALET));
        mainJoueur.ajouterCarte(new Carte(Couleur.COEUR, Hauteur.DIX));

        // Créer la vue en éventail
        VuePaquet vuePaquet = new VuePaquetVisible(mainJoueur);

        vuePaquet.setBackground(new Color(34, 139, 34)); // Fond vert table de jeu

        // Ajouter l'effet de surbrillance au survol
        // Ajouter l'effet de surbrillance au survol
        if (vuePaquet instanceof VuePaquetInteractive) {
            VuePaquetInteractive vueInteractive = (VuePaquetInteractive) vuePaquet;

            vuePaquet.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    int index = vueInteractive.getIndiceCarte(e.getX(), e.getY());
                    vueInteractive.setCarteEnSurbrillance(index);
                    if (index >= 0) {
                        vuePaquet.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    } else {
                        vuePaquet.setCursor(Cursor.getDefaultCursor());
                    }
                }
            });

            vuePaquet.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    vueInteractive.setCarteEnSurbrillance(-1);
                    vuePaquet.setCursor(Cursor.getDefaultCursor());
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    int index = vueInteractive.getIndiceCarte(e.getX(), e.getY());
                    if (index >= 0) {
                        Carte carte = mainJoueur.getCarte(index);
                        JOptionPane.showMessageDialog(frame,
                                "Vous avez cliqué sur : " + carte,
                                "Carte sélectionnée",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
        }

        // Créer un panneau de contrôle avec des boutons
        JPanel panneauControle = new JPanel();
        panneauControle.setLayout(new FlowLayout());

        // Bouton pour ajouter une carte aléatoire
        JButton btnAjouter = new JButton("Ajouter une carte");
        btnAjouter.addActionListener(e -> {
            // Créer une carte aléatoire
            Couleur[] couleurs = Couleur.values();
            Hauteur[] hauteurs = Hauteur.values();
            Couleur couleurAleatoire = couleurs[(int) (Math.random() * couleurs.length)];
            Hauteur hauteurAleatoire = hauteurs[(int) (Math.random() * hauteurs.length)];

            mainJoueur.ajouterCarte(new Carte(couleurAleatoire, hauteurAleatoire));
            // La vue se met à jour automatiquement via le pattern Observer
        });

        // Bouton pour retirer la dernière carte
        JButton btnRetirer = new JButton("Retirer une carte");
        btnRetirer.addActionListener(e -> {
            if (!mainJoueur.estVide()) {
                mainJoueur.retirerCarte(mainJoueur.getTaille() - 1);
                // La vue se met à jour automatiquement
            } else {
                JOptionPane.showMessageDialog(frame,
                        "Le paquet est vide !",
                        "Erreur",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Bouton pour vider le paquet
        JButton btnVider = new JButton("Vider le paquet");
        btnVider.addActionListener(e -> {
            mainJoueur.vider();
            // La vue se met à jour automatiquement
        });

        // Bouton pour distribuer 5 cartes
        JButton btnDistribuer5 = new JButton("Distribuer 5 cartes");
        btnDistribuer5.addActionListener(e -> {
            mainJoueur.vider();
            Paquet jeu = Paquet.creerPaquet52();
            jeu.melanger();
            for (int i = 0; i < 5; i++) {
                mainJoueur.ajouterCarte(jeu.retirerPremiereCarte());
            }
        });

        // Label pour afficher le nombre de cartes
        JLabel lblNbCartes = new JLabel("Nombre de cartes : " + mainJoueur.getTaille());

        // Mettre à jour le label quand le paquet change
        // (Dans une vraie implémentation, cela se ferait via le pattern Observer)
        Timer timer = new Timer(100, e -> {
            lblNbCartes.setText("Nombre de cartes : " + mainJoueur.getTaille());
        });
        timer.start();

        panneauControle.add(lblNbCartes);
        panneauControle.add(btnDistribuer5);
        panneauControle.add(btnAjouter);
        panneauControle.add(btnRetirer);
        panneauControle.add(btnVider);

        // Ajouter un panneau d'instructions
        JPanel panneauInstructions = new JPanel();
        panneauInstructions.setLayout(new BoxLayout(panneauInstructions, BoxLayout.Y_AXIS));
        panneauInstructions.setBorder(BorderFactory.createTitledBorder("Instructions"));
        panneauInstructions.add(new JLabel("• Survolez les cartes pour voir l'effet de surbrillance"));
        panneauInstructions.add(new JLabel("• Cliquez sur une carte pour voir ses détails"));
        panneauInstructions.add(new JLabel("• Utilisez les boutons pour modifier le paquet"));

        // Assembler l'interface
        frame.add(panneauInstructions, BorderLayout.NORTH);
        frame.add(vuePaquet, BorderLayout.CENTER);
        frame.add(panneauControle, BorderLayout.SOUTH);

        // Afficher la fenêtre
        frame.pack();
        frame.setLocationRelativeTo(null); // Centrer la fenêtre
        frame.setVisible(true);
    }
}
