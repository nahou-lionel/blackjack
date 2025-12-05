package blackjack;

import vue.GUI;

/**
 * Point d'entrée de l'application Blackjack
 *
 * Usage:
 * - java blackjack.MainClass                    : Lance le jeu sans robot
 * - java blackjack.MainClass robot              : Lance avec robot (stratégie simple par défaut)
 * - java blackjack.MainClass robot simple       : Lance avec robot (stratégie simple)
 * - java blackjack.MainClass robot optimal      : Lance avec robot (stratégie optimale)
 */
public class MainClass {

    public static void main(String[] args) {
        // Analyser les arguments de ligne de commande
        boolean avecRobot = false;
        String typeStrategie = "simple"; // Par défaut

        if (args.length > 0) {
            String premierArg = args[0].toLowerCase();

            if (premierArg.equals("robot")) {
                avecRobot = true;

                // Vérifier s'il y a un deuxième argument pour la stratégie
                if (args.length > 1) {
                    String strategieArg = args[1].toLowerCase();

                    if (strategieArg.equals("simple") || strategieArg.equals("optimal")) {
                        typeStrategie = strategieArg;
                    } else {
                        System.err.println("Stratégie invalide: " + args[1]);
                        System.err.println("Stratégies disponibles: simple, optimal");
                        afficherUsage();
                        System.exit(1);
                    }
                }
            } else if (premierArg.equals("help") || premierArg.equals("-h") || premierArg.equals("--help")) {
                afficherUsage();
                System.exit(0);
            } else {
                System.err.println("Argument invalide: " + args[0]);
                afficherUsage();
                System.exit(1);
            }
        }

        // Lancer l'interface graphique avec les paramètres
        lancerGUI(avecRobot, typeStrategie);
    }

    /**
     * Lance l'interface graphique avec les paramètres spécifiés
     *
     * @param avecRobot true pour jouer avec un robot
     * @param typeStrategie "simple" ou "optimal"
     */
    private static void lancerGUI(boolean avecRobot, String typeStrategie) {
        if (avecRobot) {
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║      BLACKJACK - Mode avec Robot      ║");
            System.out.println("╠════════════════════════════════════════╣");
            System.out.println("║  Stratégie: " + String.format("%-27s", capitaliser(typeStrategie)) + "║");
            System.out.println("╚════════════════════════════════════════╝");
        } else {
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║      BLACKJACK - Mode Solo             ║");
            System.out.println("╚════════════════════════════════════════╝");
        }

        // Créer et afficher l'interface graphique
        javax.swing.SwingUtilities.invokeLater(() -> {
            GUI gui = new GUI(avecRobot, typeStrategie);
            gui.setVisible(true);
        });
    }

    /**
     * Affiche les instructions d'utilisation
     */
    private static void afficherUsage() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║              BLACKJACK - Instructions d'utilisation            ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║                                                                ║");
        System.out.println("║  Usage:                                                        ║");
        System.out.println("║    java blackjack.MainClass [OPTIONS]                          ║");
        System.out.println("║                                                                ║");
        System.out.println("║  Options:                                                      ║");
        System.out.println("║    (aucune)          Lance le jeu sans robot                   ║");
        System.out.println("║    robot             Lance avec robot (stratégie simple)       ║");
        System.out.println("║    robot simple      Lance avec robot (stratégie simple)       ║");
        System.out.println("║    robot optimal     Lance avec robot (stratégie optimale)     ║");
        System.out.println("║    help              Affiche cette aide                        ║");
        System.out.println("║                                                                ║");
        System.out.println("║  Exemples:                                                     ║");
        System.out.println("║    java blackjack.MainClass                                    ║");
        System.out.println("║    java blackjack.MainClass robot                              ║");
        System.out.println("║    java blackjack.MainClass robot optimal                      ║");
        System.out.println("║                                                                ║");
        System.out.println("║  Stratégies disponibles:                                       ║");
        System.out.println("║    - simple   : Tire si score < 17, reste sinon               ║");
        System.out.println("║                 Split sur As et 8                              ║");
        System.out.println("║                                                                ║");
        System.out.println("║    - optimal  : Suit la Basic Strategy professionnelle        ║");
        System.out.println("║                 (www.casinous.com/online-blackjack/            ║");
        System.out.println("║                  basic-strategy/)                              ║");
        System.out.println("║                 Gère DOUBLE et SPLIT optimalement              ║");
        System.out.println("║                                                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
    }

    /**
     * Capitalise la première lettre d'une chaîne
     *
     * @param str La chaîne à capitaliser
     * @return La chaîne avec la première lettre en majuscule
     */
    private static String capitaliser(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
