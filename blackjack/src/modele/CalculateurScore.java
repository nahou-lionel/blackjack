package modele;

import cartes.modele.Carte;
import cartes.modele.Paquet;

public class CalculateurScore {

    public static int calculerScore(Paquet main) {
        int score = 0;
        int nbAs = 0;

        for (Carte c : main.getCartes()) {
            if (c.getHauteur().getValeur() >= 11 && c.getHauteur().getValeur() <= 13) {
                score += 10;
            } else if (c.getHauteur().getValeur() == 14) {
                score += 11;
                nbAs++;
            } else {
                score += c.getHauteur().getValeur();
            }
        }

        while (score > 21 && nbAs > 0) {
            score -= 10;
            nbAs--;
        }

        return score;
    }

    public static boolean estBlackjack(Paquet main) {
        if (main.getCartes().size() != 2) return false;
        
        boolean hasAs = false;
        boolean hasTenValue = false;
        
        for (Carte c : main.getCartes()) {
            if (c.getHauteur().getValeur() == 14) {
                hasAs = true;
            } else if (c.getHauteur().getValeur() >= 10 && c.getHauteur().getValeur() <= 13) {
                hasTenValue = true;
            }
        }
        
        return hasAs && hasTenValue;
    }

    public static boolean aDepasse(Paquet main) {
        return calculerScore(main) > 21;
    }

    public static boolean containAs(Paquet main) {
        for (Carte c : main.getCartes()) {
            if (c.getHauteur().getValeur() == 14)
                return true;
        }
        return false;
    }

    public static int countScoreWithAs(Paquet main, int nbAs, int score) {
        for (int i = 0; i < nbAs; i++) {
            score -= 10;
            if (score <= 21)
                return score;
        }
        return score;
    }
}