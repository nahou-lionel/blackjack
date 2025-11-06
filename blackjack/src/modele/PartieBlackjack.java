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
        this.indiceJoueurActif = 0;
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
        this.joueurs.get(joueurActif).recevoirCarte(this.sabot.retirerPremiereCarte());
        ;
    }

    public int joueurReste(int joueurActif) {
        return joueurs.get(joueurActif).getScore();
    }

    // public void joueurDouble(int joueurActif) {
    // this.joueurs.get(joueurActif).miser(this.joueurs.get(joueurActif).getMiseActuelle()
    // * 2);
    // }

    public void jouerTourCroupier() {
        Action actionCroupier = this.croupier.getStrategieCroupier().decider(this.croupier.getScore());

        while (!actionCroupier.equals(Action.RESTER)) {
            actionCroupier = this.croupier.getStrategieCroupier().decider(this.croupier.getScore());

        }

        if (CalculateurScore.aDepasse(this.croupier.getMain())) {
            System.out.println("Le croupier a dépassé avec un score de : "
                    + CalculateurScore.calculerScore(this.croupier.getMain()));
        }
    }

    public List<Joueur> determinerGagnants() {
        int scoreMax = 0;
        ArrayList<Joueur> gagnantsSansBlackJack = new ArrayList<>();
        ArrayList<Joueur> joueursRestants = new ArrayList<>();
        ArrayList<Joueur> gagnantsAvecBlackJack = new ArrayList<>();

        // 1. Filtrer les joueurs qui n'ont pas bust
        for (Joueur joueur : this.joueurs) {
            if (joueur.getScore() <= 21) {
                joueursRestants.add(joueur);
            }
        }

        // 2. Si le croupier a bust, tous les joueurs non-bust gagnent
        if (CalculateurScore.aDepasse(this.croupier.getMain())) {
            System.out.println("Le croupier a bust ! Tous les joueurs restants gagnent.");
            return joueursRestants;
        }

        // 3. Trouver le score max parmi les joueurs non-bust
        for (Joueur joueur : joueursRestants) {
            if (joueur.getScore() > scoreMax) {
                scoreMax = joueur.getScore();
            }
        }

        // 4. Déterminer les gagnants avec blackjack
        for (Joueur joueur : this.joueurs) {
            if (CalculateurScore.estBlackjack(joueur.getMain())) {
                gagnantsAvecBlackJack.add(joueur);
            }
        }

        // 5. Si le croupier a un blackjack
        if (CalculateurScore.estBlackjack(this.croupier.getMain())) {
            if (gagnantsAvecBlackJack.isEmpty()) {
                System.out.println("Le croupier gagne avec un Blackjack !");
                return new ArrayList<>(); // Personne ne gagne
            } else {
                System.out.println(
                        "Égalité ! Le croupier et " + gagnantsAvecBlackJack.size() + " joueur(s) ont un Blackjack.");
                return gagnantsAvecBlackJack; // Push (égalité)
            }
        }

        // 6. Si des joueurs ont un blackjack (mais pas le croupier)
        if (!gagnantsAvecBlackJack.isEmpty()) {
            System.out.println("Blackjack ! Les joueurs gagnent avec un Blackjack naturel.");
            return gagnantsAvecBlackJack;
        }

        // 7. Comparer les scores (ni joueur ni croupier n'a de blackjack)
        int scoreCroupier = this.croupier.getScore();

        for (Joueur joueur : joueursRestants) {
            if (joueur.getScore() > scoreCroupier) {
                gagnantsSansBlackJack.add(joueur);
            } else if (joueur.getScore() == scoreCroupier) {
                System.out.println(joueur.getNom() + " fait égalité avec le croupier (Push).");
                // Tu peux gérer le push ici (rendre la mise)
            }
            // Si joueur.getScore() < scoreCroupier, le joueur perd (rien à faire)
        }

        return gagnantsSansBlackJack;
    }
}
