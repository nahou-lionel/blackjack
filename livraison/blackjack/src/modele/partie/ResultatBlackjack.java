package modele.partie;

/**
 * Représente le résultat de la vérification des blackjacks naturels
 * après la distribution initiale
 */
public class ResultatBlackjack {
    private final boolean joueurBlackjack;
    private final boolean croupierBlackjack;

    /**
     * Crée un résultat de vérification de blackjack
     *
     * @param joueurBlackjack   true si le joueur a un blackjack naturel
     * @param croupierBlackjack true si le croupier a un blackjack naturel
     */
    public ResultatBlackjack(boolean joueurBlackjack, boolean croupierBlackjack) {
        this.joueurBlackjack = joueurBlackjack;
        this.croupierBlackjack = croupierBlackjack;
    }

    /**
     * @return true si le joueur a un blackjack naturel
     */
    public boolean isJoueurBlackjack() {
        return joueurBlackjack;
    }

    /**
     * @return true si le croupier a un blackjack naturel
     */
    public boolean isCroupierBlackjack() {
        return croupierBlackjack;
    }

    /**
     * Vérifie si c'est une égalité (push) - les deux ont un blackjack
     *
     * @return true si les deux ont un blackjack
     */
    public boolean estPush() {
        return joueurBlackjack && croupierBlackjack;
    }

    /**
     * Vérifie si le joueur gagne avec son blackjack
     *
     * @return true si le joueur a un blackjack et pas le croupier
     */
    public boolean joueurGagne() {
        return joueurBlackjack && !croupierBlackjack;
    }

    /**
     * Vérifie si le croupier gagne avec son blackjack
     *
     * @return true si le croupier a un blackjack et pas le joueur
     */
    public boolean croupierGagne() {
        return croupierBlackjack && !joueurBlackjack;
    }

    /**
     * Vérifie si au moins un des deux a un blackjack
     *
     * @return true si blackjack détecté
     */
    public boolean blackjackDetecte() {
        return joueurBlackjack || croupierBlackjack;
    }

    @Override
    public String toString() {
        return String.format("ResultatBlackjack{joueur=%b, croupier=%b}",
                joueurBlackjack, croupierBlackjack);
    }
}
