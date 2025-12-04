package modele;

/**
 * Représente les différents états d'une manche de Blackjack
 */
public enum EtatPartie {
    /** En attente de la mise du joueur */
    ATTENTE_MISE,

    /** Distribution des cartes initiales en cours */
    DISTRIBUTION,

    /** Tour du joueur (Hit/Stand/Double) */
    TOUR_JOUEUR,

    /** Tour du joueur après un split (gère plusieurs mains) */
    TOUR_JOUEUR_SPLIT,

    /** Tour du croupier (tire jusqu'à 17+) */
    TOUR_CROUPIER,

    /** Manche terminée, résultats calculés */
    TERMINE,

    /** Blackjack naturel détecté dès la distribution */
    BLACKJACK_NATUREL
}
