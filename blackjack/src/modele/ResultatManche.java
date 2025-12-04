package modele;

import java.util.List;
import java.util.Map;

/**
 * Représente le résultat complet d'une manche de Blackjack
 * incluant les gagnants et tous les paiements effectués
 */
public class ResultatManche {
    private final List<Joueur> gagnants;
    private final Map<Joueur, Paiement> paiements;
    private final EtatPartie etatFinal;
    private final boolean blackjackNaturel;

    /**
     * Crée un résultat de manche
     *
     * @param gagnants        Liste des joueurs gagnants
     * @param paiements       Map des paiements pour chaque joueur
     * @param etatFinal       État final de la partie
     * @param blackjackNaturel true si la manche s'est terminée par un blackjack naturel
     */
    public ResultatManche(List<Joueur> gagnants, Map<Joueur, Paiement> paiements,
                          EtatPartie etatFinal, boolean blackjackNaturel) {
        this.gagnants = gagnants;
        this.paiements = paiements;
        this.etatFinal = etatFinal;
        this.blackjackNaturel = blackjackNaturel;
    }

    /**
     * @return La liste des joueurs gagnants
     */
    public List<Joueur> getGagnants() {
        return gagnants;
    }

    /**
     * @return La map des paiements pour chaque joueur
     */
    public Map<Joueur, Paiement> getPaiements() {
        return paiements;
    }

    /**
     * Récupère le paiement pour un joueur donné
     *
     * @param joueur Le joueur
     * @return Le paiement du joueur, ou null si non trouvé
     */
    public Paiement getPaiementPour(Joueur joueur) {
        return paiements.get(joueur);
    }

    /**
     * @return L'état final de la partie
     */
    public EtatPartie getEtatFinal() {
        return etatFinal;
    }

    /**
     * @return true si la manche s'est terminée par un blackjack naturel
     */
    public boolean isBlackjackNaturel() {
        return blackjackNaturel;
    }

    /**
     * Vérifie si un joueur a gagné
     *
     * @param joueur Le joueur à vérifier
     * @return true si le joueur est dans la liste des gagnants
     */
    public boolean aGagne(Joueur joueur) {
        return gagnants.contains(joueur);
    }

    @Override
    public String toString() {
        return String.format("ResultatManche{gagnants=%d, etat=%s, blackjackNaturel=%b}",
                gagnants.size(), etatFinal, blackjackNaturel);
    }
}
