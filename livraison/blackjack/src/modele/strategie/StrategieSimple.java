package modele.strategie;

import cartes.modele.Carte;
import cartes.modele.Hauteur;
import modele.partie.Action;

/**
 * Stratégie simple pour un joueur robot
 * - Tire si score < 17
 * - Reste si score >= 17
 * - Mise 10% de la banque (minimum 10$)
 */
public class StrategieSimple implements StrategieJoueur {

    private static final int SEUIL_RESTER = 17;
    private static final int MISE_MINIMUM = 10;
    private static final double POURCENTAGE_MISE = 0.1; // 10% de la banque

    @Override
    public Action decider(int scoreJoueur, Carte carteVisibleCroupier) {
        // Stratégie très simple : tire si < 17, reste sinon
        if (scoreJoueur < SEUIL_RESTER) {
            return Action.TIRER;
        } else {
            return Action.RESTER;
        }
    }

    @Override
    public int determinerMise(int banque) {
        // Mise 10% de la banque, avec un minimum de 10$
        int mise = (int) (banque * POURCENTAGE_MISE);

        // S'assurer que la mise est au moins de 10$ et pas plus que la banque
        if (mise < MISE_MINIMUM) {
            mise = Math.min(MISE_MINIMUM, banque);
        }

        return mise;
    }

    @Override
    public boolean doitSplitter(Carte cartePaire, Carte carteVisibleCroupier) {
        // Stratégie simple : toujours splitter les As et les 8
        // C'est une règle de base au Blackjack
        Hauteur hauteur = cartePaire.getHauteur();
        return hauteur == Hauteur.AS || hauteur == Hauteur.HUIT;
    }
}
