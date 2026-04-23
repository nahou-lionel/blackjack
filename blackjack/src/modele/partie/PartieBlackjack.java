package modele.partie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cartes.modele.Paquet;
import modele.joueur.Croupier;
import modele.joueur.Joueur;
import modele.paiement.Paiement;
import modele.paiement.ServicePaiement;
import modele.paiement.TypeResultat;

/**
 * Orchestrateur principal du jeu de Blackjack
 * Gère le cycle de vie complet d'une manche : distribution, tours, résultats et
 * paiements
 */
public class PartieBlackjack {
    private Paquet sabot;
    private Croupier croupier;
    private List<Joueur> joueurs;
    private int indiceJoueurActif;
    private EtatPartie etat;

    public PartieBlackjack(Paquet sabot, Croupier croupier, List<Joueur> joueurs, int indiceJoueurActif) {
        this.sabot = sabot;
        this.croupier = croupier;
        this.joueurs = joueurs;
        this.indiceJoueurActif = indiceJoueurActif;
        this.etat = EtatPartie.ATTENTE_MISE;
    }

    public Paquet getSabot() {
        return this.sabot;
    }

    public Croupier getCroupier() {
        return this.croupier;
    }

    public List<Joueur> getJoueurs() {
        return this.joueurs;
    }

    public int getJoueurActif() {
        return this.indiceJoueurActif;
    }

    public EtatPartie getEtat() {
        return this.etat;
    }

    /**
     * Démarre une nouvelle manche de Blackjack
     * Vérifie et reshuffle le sabot si nécessaire, réinitialise les mains
     */
    public void demarrerNouvellePartie() {
        // Vérifier si le sabot a besoin d'être reshufflé
        if (sabotNecessiteReshuffle()) {
            reshufflerSabot(2); // 2 par défaut
        }

        // Réinitialiser l'état
        this.etat = EtatPartie.DISTRIBUTION;

        // Vider les mains et réinitialiser les joueurs (important pour le split)
        croupier.getMain().vider();
        for (Joueur joueur : joueurs) {
            // Réinitialiser tous les joueurs (humains et robots) pour gérer le split
            joueur.reinitialiser();
        }

        // Réinitialiser la carte cachée du croupier
        croupier.revelerCartes(); // Reset au cas où
    }

    public void distribuerCartesInitiales() {
        this.croupier.recevoirCarte(this.sabot.retirerPremiereCarte());
        this.croupier.recevoirCarte(this.sabot.retirerPremiereCarte());

        for (Joueur joueur : this.joueurs) {
            joueur.recevoirCarte(this.sabot.retirerPremiereCarte());
            joueur.recevoirCarte(this.sabot.retirerPremiereCarte());
        }
    }

    public void joueurTire(int joueurActif) {
        System.out.println(this.sabot.getCarte(0));
        this.joueurs.get(joueurActif).recevoirCarte(this.sabot.retirerPremiereCarte());
    }

    /**
     * Fait tirer une carte à une main spécifique d'un joueur
     *
     * @param joueurActif Index du joueur
     * @param indexMain   Index de la main du joueur
     */
    public void joueurTire(int joueurActif, int indexMain) {
        System.out.println(this.sabot.getCarte(0));
        this.joueurs.get(joueurActif).recevoirCarte(this.sabot.retirerPremiereCarte(), indexMain);
    }

    /**
     * Effectue un split pour un joueur
     * Sépare sa main en deux mains distinctes et distribue une carte à chaque
     *
     * @param joueurActif Index du joueur qui split
     * @return true si le split a réussi, false sinon
     */
    public boolean effectuerSplit(int joueurActif) {
        Joueur joueur = joueurs.get(joueurActif);

        // Tenter le split
        boolean succes = joueur.separer();

        if (succes) {
            // Distribuer une carte à chaque nouvelle main
            joueur.recevoirCarte(sabot.retirerPremiereCarte(), 0);
            joueur.recevoirCarte(sabot.retirerPremiereCarte(), 1);
            this.etat = EtatPartie.TOUR_JOUEUR_SPLIT;
        }

        return succes;
    }

    public void jouerTourCroupier() {
        this.croupier.revelerCartes();

        while (this.croupier.getStrategieCroupier().decider(this.croupier.getScore()).equals(Action.TIRER)) {
            this.croupier.recevoirCarte(this.sabot.retirerPremiereCarte());
        }

        if (CalculateurScore.aDepasse(this.croupier.getMain())) {
            System.out.println("Le croupier a dépassé avec un score de : "
                    + CalculateurScore.calculerScore(this.croupier.getMain()));
        }
    }

    public List<Joueur> determinerGagnants() {
        ArrayList<Joueur> gagnants = new ArrayList<>();
        int scoreCroupier = this.croupier.getScore();
        boolean croupierBlackjack = CalculateurScore.estBlackjack(this.croupier.getMain());
        boolean croupierBust = CalculateurScore.aDepasse(this.croupier.getMain());

        for (Joueur joueur : this.joueurs) {
            boolean joueurBlackjack = CalculateurScore.estBlackjack(joueur.getMain());
            boolean joueurBust = CalculateurScore.aDepasse(joueur.getMain());
            int scoreJoueur = joueur.getScore();

            if (joueurBust) {
                continue; // Joueur a perdu
            }

            if (croupierBust) {
                gagnants.add(joueur); // Joueur gagne
            } else if (joueurBlackjack && !croupierBlackjack) {
                gagnants.add(joueur); // Joueur gagne avec blackjack
            } else if (croupierBlackjack && !joueurBlackjack) {
                continue; // Croupier gagne avec blackjack
            } else if (scoreJoueur > scoreCroupier) {
                gagnants.add(joueur); // Joueur gagne avec score supérieur
            } else if (scoreJoueur == scoreCroupier && joueurBlackjack && croupierBlackjack) {
                gagnants.add(joueur); // Égalité avec blackjack des deux côtés
            }
            // Dans le cas d'égalité sans blackjack, aucun gagnant n'est ajouté (push)
        }

        return gagnants;
    }

    /**
     * Vérifie s'il y a des blackjacks naturels après la distribution initiale
     *
     * @return ResultatBlackjack si au moins un blackjack détecté, null sinon
     */
    public ResultatBlackjack verifierBlackjacksNaturels() {
        boolean joueurBJ = false;
        boolean croupierBJ = CalculateurScore.estBlackjack(croupier.getMain());

        // Pour simplifier, on vérifie seulement le premier joueur
        // (extension possible pour multi-joueurs)
        if (!joueurs.isEmpty()) {
            joueurBJ = CalculateurScore.estBlackjack(joueurs.get(0).getMain());
        }

        // Si au moins un blackjack détecté
        if (joueurBJ || croupierBJ) {
            this.etat = EtatPartie.BLACKJACK_NATUREL;
            return new ResultatBlackjack(joueurBJ, croupierBJ);
        }

        // Pas de blackjack, transition vers le tour du joueur
        this.etat = EtatPartie.TOUR_JOUEUR;
        return null;
    }

    /**
     * Termine la manche et calcule tous les résultats et paiements
     *
     * @return ResultatManche complet avec gagnants et paiements
     */
    public ResultatManche terminerManche() {
        // Jouer le tour du croupier
        this.etat = EtatPartie.TOUR_CROUPIER;
        jouerTourCroupier();

        // Déterminer les gagnants
        List<Joueur> gagnants = determinerGagnants();

        // Calculer les paiements
        Map<Joueur, List<Paiement>> paiements = ServicePaiement.calculerPaiements(
                joueurs, gagnants, croupier);

        // Appliquer les paiements
        ServicePaiement.appliquerPaiements(paiements);

        // Marquer la manche comme terminée
        this.etat = EtatPartie.TERMINE;

        return new ResultatManche(gagnants, paiements, etat, false);
    }

    /**
     * Termine une manche qui s'est achevée sur un blackjack naturel
     *
     * @param resultatBlackjack Le résultat de la vérification des blackjacks
     * @return ResultatManche avec les paiements appropriés
     */
    public ResultatManche terminerMancheBlackjack(ResultatBlackjack resultatBlackjack) {
        // Révéler les cartes du croupier
        croupier.revelerCartes();

        // Calculer le paiement pour le joueur principal
        Map<Joueur, List<Paiement>> paiements = new HashMap<>();
        List<Joueur> gagnants = new ArrayList<>();

        for (Joueur joueur : joueurs) {
            List<Paiement> paiementsJoueur = ServicePaiement.calculerPaiementBlackjack(
                    joueur, resultatBlackjack);
            paiements.put(joueur, paiementsJoueur);

            // Si le joueur gagne ou fait push, l'ajouter aux "gagnants"
            // On vérifie le premier (et seul) paiement car pas de split au blackjack
            // naturel
            if (!paiementsJoueur.isEmpty() &&
                    paiementsJoueur.get(0).getTypeResultat() != TypeResultat.PERTE) {
                gagnants.add(joueur);
            }
        }

        // Appliquer les paiements
        ServicePaiement.appliquerPaiements(paiements);

        // Marquer comme terminé
        this.etat = EtatPartie.TERMINE;

        return new ResultatManche(gagnants, paiements, etat, true);
    }

    /**
     * Vérifie si le sabot a besoin d'être reshufflé
     *
     * @return true si le sabot contient moins de 10 cartes
     */
    public boolean sabotNecessiteReshuffle() {
        return sabot.getTaille() < 10;
    }

    /**
     * Reshuffle le sabot avec un nombre donné de jeux de cartes
     *
     * @param nombreJeux Nombre de jeux de 52 cartes à utiliser
     */
    public void reshufflerSabot(int nombreJeux) {
        sabot.vider();

        // Créer un nouveau paquet temporaire
        Paquet nouveauPaquet = Paquet.creerPaquetMultiple(nombreJeux);

        // Transférer toutes les cartes vers le sabot existant
        // (pour maintenir les observateurs/listeners)
        while (!nouveauPaquet.estVide()) {
            sabot.ajouterCarte(nouveauPaquet.retirerPremiereCarte());
        }

        sabot.melanger();
    }
}