package vue;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Fenêtre principale du jeu Blackjack
 */
public class GUI extends JFrame {

    private boolean avecRobot;
    private String typeStrategie;

    /**
     * Constructeur de l'interface graphique
     *
     * @param avecRobot true pour jouer avec un robot, false sinon
     * @param typeStrategie "simple" ou "optimal" (utilisé seulement si avecRobot = true)
     */
    public GUI(boolean avecRobot, String typeStrategie) {
        super("Blackjack");
        this.avecRobot = avecRobot;
        this.typeStrategie = typeStrategie;

        initialiser();
    }

    /**
     * Initialise l'interface graphique
     */
    private void initialiser() {
        // Appliquer le look & feel Nimbus
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Créer la vue de départ avec les paramètres
        VueDepart vueDepart = new VueDepart(avecRobot, typeStrategie);
        vueDepart.getBoutonPlay().addActionListener(e -> vueDepart.demarrerPartie(this));

        setContentPane(vueDepart);
        pack();
        setLocationRelativeTo(null);
    }

    /**
     * Point d'entrée alternatif pour lancer le GUI directement
     * (conservé pour compatibilité)
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI(false, "simple");
            gui.setVisible(true);
        });
    }
}

