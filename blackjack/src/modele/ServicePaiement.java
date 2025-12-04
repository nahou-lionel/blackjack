package modele;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service responsable de tous les calculs financiers du Blackjack
 * Isole la logique de paiement du reste du modèle
 */
public class ServicePaiement {

    /**
     * Ratio de paiement pour un blackjack naturel (3:2)
     * Le joueur reçoit sa mise + 2.5× sa mise
     */
    public static final double RATIO_BLACKJACK = 2.5;

    /**
     * Ratio de paiement pour une victoire normale (1:1)
     * Le joueur reçoit sa mise + 2× sa mise
     */
    public static final int RATIO_VICTOIRE = 2;

    /**
     * Calcule les paiements pour tous les joueurs d'une manche
     *
     * @param joueurs  Liste de tous les joueurs
     * @param gagnants Liste des joueurs gagnants
     * @param croupier Le croupier
     * @return Map associant chaque joueur à son paiement
     */
    public static Map<Joueur, Paiement> calculerPaiements(List<Joueur> joueurs,
                                                           List<Joueur> gagnants,
                                                           Croupier croupier) {
        Map<Joueur, Paiement> paiements = new HashMap<>();

        for (Joueur joueur : joueurs) {
            Paiement paiement = calculerPaiementJoueur(joueur, gagnants, croupier);
            paiements.put(joueur, paiement);
        }

        return paiements;
    }

    /**
     * Calcule le paiement pour un joueur spécifique
     *
     * @param joueur   Le joueur
     * @param gagnants Liste des gagnants
     * @param croupier Le croupier
     * @return Le paiement calculé
     */
    private static Paiement calculerPaiementJoueur(Joueur joueur,
                                                    List<Joueur> gagnants,
                                                    Croupier croupier) {
        int mise = joueur.getMiseActuelle();
        int montantPaye = 0;
        TypeResultat typeResultat;

        // Cas 1 : Joueur a bust (dépassé 21)
        if (CalculateurScore.aDepasse(joueur.getMain())) {
            typeResultat = TypeResultat.PERTE;
            montantPaye = 0; // Perd sa mise (déjà déduite)
        }
        // Cas 2 : Joueur a gagné
        else if (gagnants.contains(joueur)) {
            if (CalculateurScore.estBlackjack(joueur.getMain())) {
                // Blackjack naturel : paiement 3:2
                typeResultat = TypeResultat.BLACKJACK;
                montantPaye = (int) (mise + (RATIO_BLACKJACK * mise));
            } else {
                // Victoire normale : paiement 1:1
                typeResultat = TypeResultat.VICTOIRE;
                montantPaye = mise + (RATIO_VICTOIRE * mise);
            }
        }
        // Cas 3 : Égalité (push) - ni bust ni gagnant
        else if (!CalculateurScore.aDepasse(croupier.getMain()) &&
                joueur.getScore() == croupier.getScore()) {
            typeResultat = TypeResultat.PUSH;
            montantPaye = mise; // Remboursement de la mise
        }
        // Cas 4 : Défaite (croupier a mieux)
        else {
            typeResultat = TypeResultat.PERTE;
            montantPaye = 0; // Perd sa mise
        }

        return new Paiement(mise, montantPaye, typeResultat);
    }

    /**
     * Applique tous les paiements aux banques des joueurs
     *
     * @param paiements Map des paiements à appliquer
     */
    public static void appliquerPaiements(Map<Joueur, Paiement> paiements) {
        for (Map.Entry<Joueur, Paiement> entry : paiements.entrySet()) {
            Joueur joueur = entry.getKey();
            Paiement paiement = entry.getValue();
            appliquerPaiement(joueur, paiement);
        }
    }

    /**
     * Applique un paiement à la banque d'un joueur
     *
     * @param joueur   Le joueur
     * @param paiement Le paiement à appliquer
     */
    public static void appliquerPaiement(Joueur joueur, Paiement paiement) {
        int nouvelleBanque = joueur.getBanque() + paiement.getMontantPaye();
        joueur.setBanque(nouvelleBanque);
    }

    /**
     * Calcule le paiement pour un blackjack naturel spécifique
     * Utilisé quand la manche se termine dès la distribution
     *
     * @param joueur           Le joueur
     * @param resultatBlackjack Le résultat de la vérification des blackjacks
     * @return Le paiement calculé
     */
    public static Paiement calculerPaiementBlackjack(Joueur joueur,
                                                      ResultatBlackjack resultatBlackjack) {
        int mise = joueur.getMiseActuelle();
        int montantPaye;
        TypeResultat typeResultat;

        if (resultatBlackjack.estPush()) {
            // Égalité - les deux ont un blackjack
            typeResultat = TypeResultat.PUSH;
            montantPaye = mise;
        } else if (resultatBlackjack.joueurGagne()) {
            // Joueur gagne avec blackjack
            typeResultat = TypeResultat.BLACKJACK;
            montantPaye = (int) (mise + (RATIO_BLACKJACK * mise));
        } else {
            // Croupier gagne avec blackjack
            typeResultat = TypeResultat.PERTE;
            montantPaye = 0;
        }

        return new Paiement(mise, montantPaye, typeResultat);
    }
}
