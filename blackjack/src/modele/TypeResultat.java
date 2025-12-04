package modele;

/**
 * Représente le type de résultat d'une manche pour un joueur
 */
public enum TypeResultat {
    /** Victoire normale avec un score supérieur (paiement 1:1) */
    VICTOIRE,

    /** Victoire avec un blackjack naturel (paiement 3:2) */
    BLACKJACK,

    /** Égalité avec le croupier (remboursement de la mise) */
    PUSH,

    /** Défaite (perte de la mise) */
    PERTE
}
