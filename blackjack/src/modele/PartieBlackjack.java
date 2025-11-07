package modele;

import java.util.ArrayList;
import java.util.List;

import cartes.modele.Paquet;

public class PartieBlackjack {
    private Paquet sabot;
    private Croupier croupier;
    private List<Joueur> joueurs;
    private int indiceJoueurActif;

    public PartieBlackjack(Paquet sabot, Croupier croupier, List<Joueur> joueurs, int indiceJoueurActif) {
        this.sabot = sabot;
        this.croupier = croupier;
        this.joueurs = joueurs;
        this.indiceJoueurActif = indiceJoueurActif;
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

    public void demarrerNouvellePartie() {

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
}