package modele.paiement;

/**
 * Représente un paiement effectué à un joueur à la fin d'une manche
 */
public class Paiement {
    private final int miseOriginale;
    private final int montantPaye;
    private final TypeResultat typeResultat;

    /**
     * Crée un nouveau paiement
     *
     * @param miseOriginale La mise initiale du joueur
     * @param montantPaye   Le montant total rendu au joueur (0 si perte)
     * @param typeResultat  Le type de résultat (VICTOIRE, BLACKJACK, PUSH, PERTE)
     */
    public Paiement(int miseOriginale, int montantPaye, TypeResultat typeResultat) {
        this.miseOriginale = miseOriginale;
        this.montantPaye = montantPaye;
        this.typeResultat = typeResultat;
    }

    /**
     * @return La mise originale du joueur
     */
    public int getMiseOriginale() {
        return miseOriginale;
    }

    /**
     * @return Le montant total payé au joueur (incluant la mise si victoire)
     */
    public int getMontantPaye() {
        return montantPaye;
    }

    /**
     * @return Le type de résultat de la manche
     */
    public TypeResultat getTypeResultat() {
        return typeResultat;
    }

    /**
     * Calcule le profit net du joueur (montant payé - mise originale)
     *
     * @return Le profit (positif si gain, négatif si perte, 0 si push)
     */
    public int getProfit() {
        return montantPaye - miseOriginale;
    }

    @Override
    public String toString() {
        return String.format("Paiement{mise=%d, payé=%d, profit=%d, type=%s}",
                miseOriginale, montantPaye, getProfit(), typeResultat);
    }
}
