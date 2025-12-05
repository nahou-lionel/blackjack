package modele.strategie;

import cartes.modele.Carte;
import cartes.modele.Hauteur;
import modele.partie.Action;

/**
 * Stratégie optimale basée sur la "Basic Strategy" du Blackjack
 * Cette stratégie suit les règles mathématiquement optimales pour maximiser les
 * chances de gain
 *
 * Référence: Basic Strategy standard utilisée dans les casinos
 * Source: www.casinous.com/online-blackjack/basic-strategy/
 */
public class StrategieOptimale implements StrategieJoueur {

    private static final int MISE_MINIMUM = 10;
    private static final double POURCENTAGE_MISE = 0.15; // 15% de la banque

    @Override
    public Action decider(int scoreJoueur, Carte carteVisibleCroupier) {
        int valeurCroupier = obtenirValeurCarte(carteVisibleCroupier);

        // Note: Cette implémentation utilise uniquement le score
        // Une version complète nécessiterait l'accès au paquet pour détecter les soft
        // hands
        // Pour l'instant, on applique la stratégie des hard totals
        return appliquerStrategieHard(scoreJoueur, valeurCroupier);
    }

    /**
     * Applique la Basic Strategy pour les "hard totals" (mains dures)
     *
     * Règles de la Basic Strategy:
     * - 17+: Toujours STAND
     * - 13-16: STAND contre 2-6, HIT contre 7-A
     * - 12: STAND contre 4-6, HIT contre 2-3,7-A
     * - 11: DOUBLE (ou HIT si impossible)
     * - 10: DOUBLE contre 2-9, HIT contre 10-A
     * - 9: DOUBLE contre 3-6, HIT sinon
     * - 5-8: Toujours HIT
     */
    private Action appliquerStrategieHard(int score, int valeurCroupier) {
        // 17 ou plus: Toujours STAND
        if (score >= 17) {
            return Action.RESTER;
        }

        // 13-16: STAND contre 2-6 (croupier faible), HIT contre 7-A (croupier fort)
        if (score >= 13 && score <= 16) {
            if (valeurCroupier >= 2 && valeurCroupier <= 6) {
                return Action.RESTER; // Croupier risque de bust
            }
            return Action.TIRER;
        }

        // 12: STAND contre 4-6 seulement
        if (score == 12) {
            if (valeurCroupier >= 4 && valeurCroupier <= 6) {
                return Action.RESTER;
            }
            return Action.TIRER;
        }

        // 11: Toujours DOUBLE (meilleure chance d'obtenir 21)
        if (score == 11) {
            return Action.DOUBLER;
        }

        // 10: DOUBLE contre 2-9, HIT contre 10-A
        if (score == 10) {
            if (valeurCroupier >= 2 && valeurCroupier <= 9) {
                return Action.DOUBLER;
            }
            return Action.TIRER;
        }

        // 9: DOUBLE contre 3-6, HIT sinon
        if (score == 9) {
            if (valeurCroupier >= 3 && valeurCroupier <= 6) {
                return Action.DOUBLER;
            }
            return Action.TIRER;
        }

        // 5-8: Toujours HIT (impossible de bust)
        return Action.TIRER;
    }

    /**
     * Obtient la valeur d'une carte pour les calculs de stratégie
     * - As = 11
     * - Figures (V, D, R) = 10
     * - Autres cartes = valeur nominale
     */
    private int obtenirValeurCarte(Carte carte) {
        Hauteur hauteur = carte.getHauteur();

        switch (hauteur) {
            case AS:
                return 11;
            case VALET:
            case DAME:
            case ROI:
                return 10;
            default:
                return hauteur.getValeur();
        }
    }

    @Override
    public int determinerMise(int banque) {
        // Stratégie de mise progressive: 15% de la banque
        int mise = (int) (banque * POURCENTAGE_MISE);

        // Assurer un minimum de 10$
        if (mise < MISE_MINIMUM) {
            mise = Math.min(MISE_MINIMUM, banque);
        }

        // Limiter à 25% maximum pour éviter la ruine rapide
        int miseMax = (int) (banque * 0.25);
        if (mise > miseMax) {
            mise = miseMax;
        }

        return mise;
    }

    @Override
    public boolean doitSplitter(Carte cartePaire, Carte carteVisibleCroupier) {
        Hauteur hauteurPaire = cartePaire.getHauteur();
        int valeurCroupier = obtenirValeurCarte(carteVisibleCroupier);

        // Appliquer la Basic Strategy pour les paires
        switch (hauteurPaire) {
            case AS:
                // A,A: Toujours SPLIT
                return true;

            case ROI:
            case DAME:
            case VALET:
            case DIX:
                // 10,10: Jamais SPLIT (trop bon, 20 points)
                return false;

            case NEUF:
                // 9,9: SPLIT vs 2-9 sauf 7, STAND vs 7,10,A
                if (valeurCroupier == 7 || valeurCroupier >= 10) {
                    return false; // STAND avec 18
                }
                return valeurCroupier >= 2 && valeurCroupier <= 9;

            case HUIT:
                // 8,8: Toujours SPLIT (16 est mauvais)
                return true;

            case SEPT:
                // 7,7: SPLIT vs 2-7, HIT vs 8-A
                return valeurCroupier >= 2 && valeurCroupier <= 7;

            case SIX:
                // 6,6: SPLIT vs 2-6, HIT vs 7-A
                return valeurCroupier >= 2 && valeurCroupier <= 6;

            case CINQ:
                // 5,5: Jamais SPLIT (traiter comme 10 et doubler)
                return false;

            case QUATRE:
                // 4,4: SPLIT vs 5-6, HIT sinon
                return valeurCroupier == 5 || valeurCroupier == 6;

            case TROIS:
            case DEUX:
                // 3,3 et 2,2: SPLIT vs 2-7, HIT vs 8-A
                return valeurCroupier >= 2 && valeurCroupier <= 7;

            default:
                return false;
        }
    }
}

/**
 * BASIC STRATEGY COMPLÈTE (pour référence future)
 *
 * === HARD TOTALS ===
 * 17-20: STAND
 * 13-16: STAND vs 2-6, HIT vs 7-A
 * 12: STAND vs 4-6, HIT vs 2-3,7-A
 * 11: DOUBLE (HIT si impossible)
 * 10: DOUBLE vs 2-9, HIT vs 10-A
 * 9: DOUBLE vs 3-6, HIT sinon
 * 5-8: HIT
 *
 * === SOFT TOTALS (avec As comptant comme 11) ===
 * A,9 (20): STAND
 * A,8 (19): STAND
 * A,7 (18): STAND vs 2,7,8; DOUBLE vs 3-6; HIT vs 9,10,A
 * A,6 (17): DOUBLE vs 3-6, HIT sinon
 * A,5 (16): DOUBLE vs 4-6, HIT sinon
 * A,4 (15): DOUBLE vs 4-6, HIT sinon
 * A,3 (14): DOUBLE vs 5-6, HIT sinon
 * A,2 (13): DOUBLE vs 5-6, HIT sinon
 *
 * === PAIRES (pour Split) ===
 * A,A: Toujours SPLIT
 * 10,10: Jamais SPLIT (STAND)
 * 9,9: SPLIT vs 2-9 sauf 7, STAND vs 7,10,A
 * 8,8: Toujours SPLIT
 * 7,7: SPLIT vs 2-7, HIT vs 8-A
 * 6,6: SPLIT vs 2-6, HIT vs 7-A
 * 5,5: Jamais SPLIT (traiter comme 10)
 * 4,4: SPLIT vs 5-6, HIT sinon
 * 3,3: SPLIT vs 2-7, HIT vs 8-A
 * 2,2: SPLIT vs 2-7, HIT vs 8-A
 */
