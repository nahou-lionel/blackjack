package modele.strategie;

import cartes.modele.Carte;
import modele.partie.Action;

/**
 * Interface définissant la stratégie de décision d'un joueur de Blackjack
 */
public interface StrategieJoueur {

    /**
     * Décide de l'action à effectuer pour le joueur
     *
     * @param scoreJoueur          Le score actuel du joueur
     * @param carteVisibleCroupier La carte visible du croupier
     * @return L'action à effectuer (TIRER, RESTER, DOUBLER)
     */
    Action decider(int scoreJoueur, Carte carteVisibleCroupier);

    /**
     * Détermine le montant de la mise pour ce tour
     *
     * @param banque Le solde disponible du joueur
     * @return Le montant à miser
     */
    int determinerMise(int banque);

    /**
     * Décide si le joueur doit séparer sa paire
     *
     * @param cartePaire           Une des cartes de la paire (les deux ont la même
     *                             hauteur)
     * @param carteVisibleCroupier La carte visible du croupier
     * @return true si le joueur doit splitter, false sinon
     */
    boolean doitSplitter(Carte cartePaire, Carte carteVisibleCroupier);
}
