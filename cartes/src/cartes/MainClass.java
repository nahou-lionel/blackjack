package cartes;

import cartes.modele.Carte;
import cartes.modele.Paquet;

public class MainClass {
    public static void main(String[] args) {
        // Créer un jeu de 52 cartes
        Paquet jeu52 = Paquet.creerPaquet52();
        System.out.println("Jeu de 52 cartes: " + jeu52.getTaille() + " cartes");

        // Créer un jeu de 32 cartes
        Paquet jeu32 = Paquet.creerPaquet32();
        System.out.println("Jeu de 32 cartes: " + jeu32.getTaille() + " cartes");

        // Créer un sabot de blackjack (6 jeux)
        Paquet sabot = Paquet.creerPaquetMultiple(6);
        System.out.println("Sabot blackjack: " + sabot.getTaille() + " cartes");

        // Mélanger et piocher
        jeu52.melanger();
        Carte carte1 = jeu52.retirerPremiereCarte();
        System.out.println("Carte piochée: " + carte1);

        // Test de la méthode couper
        Paquet paquetTest = Paquet.creerPaquet32();
        System.out.println("\nAvant coupe: " + paquetTest.getTaille() + " cartes");
        Paquet paquetCoupe = paquetTest.couper();
        System.out.println("Après coupe:");
        System.out.println("  - Paquet original: " + paquetTest.getTaille() + " cartes");
        System.out.println("  - Paquet coupé: " + paquetCoupe.getTaille() + " cartes");
        System.out.println("  - Total: " + (paquetTest.getTaille() + paquetCoupe.getTaille()) + " cartes");

        // Créer une main de joueur
        Paquet mainJoueur = Paquet.creerPaquetVide();
        mainJoueur.ajouterCarte(carte1);
        mainJoueur.ajouterCarte(jeu52.retirerPremiereCarte());
        System.out.println("\nMain du joueur: " + mainJoueur.getTaille() + " cartes");

        // Afficher les cartes de la main
        for (Carte c : mainJoueur.getCartes()) {
            System.out.println("  - " + c);
        }

    }
}
