package modele;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Représente le résultat complet d'une manche de Blackjack
 * incluant les gagnants et tous les paiements effectués
 * Supporte plusieurs mains par joueur (cas du split)
 */
public class ResultatManche {
    private final List<Joueur> gagnants;
    private final Map<Joueur, List<Paiement>> paiements;
    private final EtatPartie etatFinal;
    private final boolean blackjackNaturel;

    /**
     * Crée un résultat de manche
     *
     * @param gagnants        Liste des joueurs gagnants
     * @param paiements       Map des paiements pour chaque joueur (liste de paiements pour split)
     * @param etatFinal       État final de la partie
     * @param blackjackNaturel true si la manche s'est terminée par un blackjack naturel
     */
    public ResultatManche(List<Joueur> gagnants, Map<Joueur, List<Paiement>> paiements,
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
    public Map<Joueur, List<Paiement>> getPaiements() {
        return paiements;
    }

    /**
     * Récupère les paiements pour un joueur donné
     *
     * @param joueur Le joueur
     * @return Liste des paiements du joueur (un par main), ou null si non trouvé
     */
    public List<Paiement> getPaiementsPour(Joueur joueur) {
        return paiements.get(joueur);
    }

    /**
     * Récupère le premier paiement pour un joueur donné
     * Méthode de compatibilité pour le cas sans split
     *
     * @param joueur Le joueur
     * @return Le premier paiement du joueur, ou null si non trouvé
     */
    public Paiement getPaiementPour(Joueur joueur) {
        List<Paiement> listePaiements = paiements.get(joueur);
        if (listePaiements == null || listePaiements.isEmpty()) {
            return null;
        }
        return listePaiements.get(0);
    }

    /**
     * Calcule le profit total d'un joueur (somme de tous ses paiements)
     *
     * @param joueur Le joueur
     * @return Le profit total
     */
    public int getProfitTotal(Joueur joueur) {
        List<Paiement> listePaiements = paiements.get(joueur);
        if (listePaiements == null) {
            return 0;
        }
        return listePaiements.stream()
                .mapToInt(Paiement::getProfit)
                .sum();
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
     * Vérifie si un joueur a gagné au moins une main
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
