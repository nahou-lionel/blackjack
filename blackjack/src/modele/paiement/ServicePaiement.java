package modele.paiement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import modele.joueur.Croupier;
import modele.joueur.Joueur;
import modele.joueur.MainJoueur;
import modele.partie.CalculateurScore;
import modele.partie.ResultatBlackjack;

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
     * Supporte plusieurs mains par joueur (cas du split)
     *
     * @param joueurs  Liste de tous les joueurs
     * @param gagnants Liste des joueurs gagnants
     * @param croupier Le croupier
     * @return Map associant chaque joueur à ses paiements (un par main)
     */
    public static Map<Joueur, List<Paiement>> calculerPaiements(List<Joueur> joueurs,
            List<Joueur> gagnants,
            Croupier croupier) {
        Map<Joueur, List<Paiement>> paiements = new HashMap<>();

        for (Joueur joueur : joueurs) {
            List<Paiement> paiementsJoueur = calculerPaiementsJoueur(joueur, gagnants, croupier);
            paiements.put(joueur, paiementsJoueur);
        }

        return paiements;
    }

    /**
     * Calcule les paiements pour toutes les mains d'un joueur
     *
     * @param joueur   Le joueur
     * @param gagnants Liste des gagnants
     * @param croupier Le croupier
     * @return Liste des paiements (un par main)
     */
    private static List<Paiement> calculerPaiementsJoueur(Joueur joueur,
            List<Joueur> gagnants,
            Croupier croupier) {
        List<Paiement> paiementsJoueur = new ArrayList<>();
        List<MainJoueur> mains = joueur.getMains();

        for (MainJoueur main : mains) {
            Paiement paiement = calculerPaiementMain(joueur, main, gagnants, croupier);
            paiementsJoueur.add(paiement);
        }

        return paiementsJoueur;
    }

    /**
     * Calcule le paiement pour une main spécifique d'un joueur
     *
     * @param joueur   Le joueur
     * @param main     La main à évaluer
     * @param gagnants Liste des gagnants
     * @param croupier Le croupier
     * @return Le paiement calculé pour cette main
     */
    private static Paiement calculerPaiementMain(Joueur joueur,
            MainJoueur main,
            List<Joueur> gagnants,
            Croupier croupier) {
        int mise = main.getMise();
        int montantPaye = 0;
        TypeResultat typeResultat;
        int scoreCroupier = croupier.getScore();
        int scoreMain = main.getScore();

        // Cas 1 : Main a bust (dépassé 21)
        if (main.aDepasse()) {
            typeResultat = TypeResultat.PERTE;
            montantPaye = 0; // Perd sa mise (déjà déduite)
        }
        // Cas 2 : Croupier a bust - joueur gagne
        else if (CalculateurScore.aDepasse(croupier.getMain())) {
            if (main.estBlackjack()) {
                // Blackjack naturel : paiement 3:2
                typeResultat = TypeResultat.BLACKJACK;
                montantPaye = (int) (mise + (RATIO_BLACKJACK * mise));
            } else {
                // Victoire normale : paiement 1:1
                typeResultat = TypeResultat.VICTOIRE;
                montantPaye = mise + (RATIO_VICTOIRE * mise);
            }
        }
        // Cas 3 : Main a un blackjack naturel et pas le croupier
        else if (main.estBlackjack() && !CalculateurScore.estBlackjack(croupier.getMain())) {
            typeResultat = TypeResultat.BLACKJACK;
            montantPaye = (int) (mise + (RATIO_BLACKJACK * mise));
        }
        // Cas 4 : Croupier a un blackjack naturel et pas la main
        else if (CalculateurScore.estBlackjack(croupier.getMain()) && !main.estBlackjack()) {
            typeResultat = TypeResultat.PERTE;
            montantPaye = 0;
        }
        // Cas 5 : Score de la main > score croupier
        else if (scoreMain > scoreCroupier) {
            typeResultat = TypeResultat.VICTOIRE;
            montantPaye = mise + (RATIO_VICTOIRE * mise);
        }
        // Cas 6 : Égalité (push)
        else if (scoreMain == scoreCroupier) {
            typeResultat = TypeResultat.PUSH;
            montantPaye = mise; // Remboursement de la mise
        }
        // Cas 7 : Défaite (croupier a mieux)
        else {
            typeResultat = TypeResultat.PERTE;
            montantPaye = 0; // Perd sa mise
        }

        return new Paiement(mise, montantPaye, typeResultat);
    }

    /**
     * Applique tous les paiements aux banques des joueurs
     * Supporte plusieurs paiements par joueur (cas du split)
     *
     * @param paiements Map des paiements à appliquer
     */
    public static void appliquerPaiements(Map<Joueur, List<Paiement>> paiements) {
        for (Map.Entry<Joueur, List<Paiement>> entry : paiements.entrySet()) {
            Joueur joueur = entry.getKey();
            List<Paiement> listePaiements = entry.getValue();

            // Appliquer tous les paiements du joueur
            for (Paiement paiement : listePaiements) {
                appliquerPaiement(joueur, paiement);
            }
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
     * Retourne une liste pour compatibilité avec le système multi-mains
     *
     * @param joueur            Le joueur
     * @param resultatBlackjack Le résultat de la vérification des blackjacks
     * @return Liste contenant le paiement calculé (une seule main au blackjack
     *         naturel)
     */
    public static List<Paiement> calculerPaiementBlackjack(Joueur joueur,
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

        List<Paiement> paiements = new ArrayList<>();
        paiements.add(new Paiement(mise, montantPaye, typeResultat));
        return paiements;
    }
}
