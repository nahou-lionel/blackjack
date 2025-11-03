package cartes;

import java.awt.*;
import javax.swing.*;

import cartes.controleur.ControleurChoixCarteVersPaquet;
import cartes.controleur.ControleurPiocheVersPaquet;
import cartes.modele.Paquet;
import cartes.vue.VuePaquet;
import cartes.vue.VuePaquetCache;
import cartes.vue.VuePaquetVisible;

public class TestGUI {
    private static final int LARGEUR_PANNEAU = 900; // Largeur par défaut des panneaux

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

        // Couleur de fond
        Color couleurFond = new Color(34, 139, 34); // Vert table de jeu

        // Créer la pioche
        Paquet pioche = Paquet.creerPaquet52();
        VuePaquet vuePioche = new VuePaquetCache(pioche);

        // Créer la défausse
        Paquet defausse = Paquet.creerPaquetVide();
        VuePaquetVisible vueDefausse = new VuePaquetVisible(defausse);

        // Créer la main du joueur
        Paquet mainJoueur = Paquet.creerPaquetVide();
        VuePaquetVisible vueMainJoueur = new VuePaquetVisible(mainJoueur);

        // Controleur
        ControleurPiocheVersPaquet controleurPiocheVersDefausse = new ControleurPiocheVersPaquet(vuePioche, mainJoueur);
        vuePioche.addMouseListener(controleurPiocheVersDefausse);

        ControleurChoixCarteVersPaquet controleurMainVersDefausse = new ControleurChoixCarteVersPaquet(vueMainJoueur,
                defausse);
        vueMainJoueur.addMouseListener(controleurMainVersDefausse);
        vueMainJoueur.addMouseMotionListener(controleurMainVersDefausse);

        ControleurChoixCarteVersPaquet controleurDefausseVersPioche = new ControleurChoixCarteVersPaquet(vueDefausse,
                pioche);
        vueDefausse.addMouseListener(controleurDefausseVersPioche);
        vueDefausse.addMouseMotionListener(controleurDefausseVersPioche);

        // Créer les panneaux avec labels et boutons
        JPanel panneauPioche = creerPanneauAvecLabel("Pioche", vuePioche, couleurFond, false, false);
        JPanel panneauMain = creerPanneauAvecLabel("Main", vueMainJoueur, couleurFond, true, true);
        JPanel panneauDefausse = creerPanneauAvecLabel("Défausse", vueDefausse, couleurFond, true, true);

        // Ajouter les contrôleurs de tri
        ajouterControleursTri(panneauMain, mainJoueur);
        ajouterControleursTri(panneauDefausse, defausse);

        // Ajouter les vues au panneau principal
        JPanel panneauPrincipal = new JPanel();
        panneauPrincipal.setLayout(new GridLayout(3, 1, 10, 10));
        panneauPrincipal.add(panneauPioche);
        panneauPrincipal.add(panneauMain);
        panneauPrincipal.add(panneauDefausse);
        panneauPrincipal.setBackground(couleurFond);

        frame.add(panneauPrincipal, BorderLayout.CENTER);

        // Configurer et afficher la fenêtre
        frame.setSize(LARGEUR_PANNEAU + 20, 700);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Créer la fenêtre pour visualiser la pioche
        creerFenetrePioche(pioche, couleurFond);
    }

    private static JPanel creerPanneauAvecLabel(String texte, VuePaquet vue, Color couleurFond,
            boolean avecBoutons, boolean avecScroll) {
        JPanel panneau = new JPanel(new BorderLayout(5, 5));
        panneau.setBackground(couleurFond);
        panneau.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Panneau du haut avec label et boutons
        JPanel panneauHaut = new JPanel(new BorderLayout());
        panneauHaut.setBackground(couleurFond);

        // Label
        JLabel label = new JLabel(texte);
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        panneauHaut.add(label, BorderLayout.WEST);

        // Boutons de tri (si demandé)
        if (avecBoutons) {
            JPanel panneauBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
            panneauBoutons.setBackground(couleurFond);

            JButton btnTriHauteur = new JButton("Tri par hauteur");
            JButton btnTriCouleur = new JButton("Tri par couleur");

            panneauBoutons.add(btnTriHauteur);
            panneauBoutons.add(btnTriCouleur);
            panneauHaut.add(panneauBoutons, BorderLayout.EAST);
        }

        panneau.add(panneauHaut, BorderLayout.NORTH);

        // Vue des cartes avec ou sans scroll
        vue.setBackground(couleurFond);

        if (avecScroll) {
            JScrollPane scrollPane = new JScrollPane(vue);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
            scrollPane.getViewport().setBackground(couleurFond);
            scrollPane.setBorder(null);
            panneau.add(scrollPane, BorderLayout.CENTER);
        } else {
            panneau.add(vue, BorderLayout.CENTER);
        }

        return panneau;
    }

    private static void ajouterControleursTri(JPanel panneau, Paquet paquet) {
        Component[] components = ((JPanel) panneau.getComponent(0)).getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel panneauBoutons = (JPanel) comp;
                Component[] boutons = panneauBoutons.getComponents();
                for (Component btn : boutons) {
                    if (btn instanceof JButton) {
                        JButton button = (JButton) btn;
                        if (button.getText().contains("hauteur")) {
                            button.addActionListener(e -> paquet.trierParHauteur());
                        } else if (button.getText().contains("couleur")) {
                            button.addActionListener(e -> paquet.trierParCouleur());
                        }
                    }
                }
            }
        }
    }

    private static void creerFenetrePioche(Paquet pioche, Color couleurFond) {
        JFrame framePioche = new JFrame("Contenu de la Pioche");
        framePioche.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        framePioche.setLayout(new BorderLayout());

        // Créer une vue visible de la pioche
        VuePaquetVisible vuePiocheVisible = new VuePaquetVisible(pioche);

        // Créer le panneau avec label et boutons
        JPanel panneauPiocheVisible = creerPanneauAvecLabel("Pioche (visible)", vuePiocheVisible, couleurFond, true,
                true);

        // Ajouter les contrôleurs de tri
        ajouterControleursTri(panneauPiocheVisible, pioche);

        framePioche.add(panneauPiocheVisible, BorderLayout.CENTER);
        framePioche.setSize(LARGEUR_PANNEAU + 20, 300);
        framePioche.setLocationRelativeTo(null);
        framePioche.setVisible(true);
    }
}
