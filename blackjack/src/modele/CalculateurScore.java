package modele;

import cartes.modele.Carte;
import cartes.modele.Paquet;

public class CalculateurScore {

    // Cette méthode calcule le score de la main
    public static int calculerScore(Paquet main) {
        // Compter les points
        // Gérer les As (1 ou 11)
        // Retourner le meilleur score ≤ 21
        int score = 0;

        for (Carte c : main.getCartes()) {
            if (c.getHauteur().getValeur() <= 10) {
                score += c.getHauteur().getValeur();
            }

            else {
                if (c.getHauteur().getValeur() < 14) {
                    score += 10;
                } else {
                    score += 11;
                }
            }
        }

        // si le score de base est 21 on le renvoie
        if (score <= 21) {
            return score;
        }

        // sinon on décrémente le score si la main contient des As
        else {
            if (containAs(main)) {

                int nbAs = 0;
                for (Carte c : main.getCartes()) {
                    if (c.getHauteur().getSymbole().equals("A")) {
                        nbAs += 1;
                    }

                }

                return countScoreWithAs(main, nbAs, score);

            }

        }

        // sinon on retourne le score dépassé
        return score;
    }

    // Cette méthode vérifie si la main est un blackjack
    public static boolean estBlackjack(Paquet main) {
        // 2 cartes + score = 21

        return (main.getCartes().size() == 2) && containAs(main) && (calculerScore(main) == 21);
    }

    // Cette méthode vérifie si le score de la main est superieur à 21
    public static boolean aDepasse(Paquet main) {
        // score > 21

        return calculerScore(main) > 21;
    }

    // Cette méthode vérifie si le paquet contient un As 
    public static boolean containAs(Paquet main) {

        for (Carte c : main.getCartes()) {
            if (c.getHauteur().getSymbole().equals("A"))
                return true;
        }

        return false;
    }


    //Cette méthode ramène une à une la valeur de chaque As de la main à 1 tant que le score est supérieur à 21
    public static int countScoreWithAs(Paquet main, int nbAs, int score) {
        for (int i = 0; i < nbAs; i++) {
            score -= 10;
            if (score <= 21)
                return score;
        }

        return score;
    }
}