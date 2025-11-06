package blackjack;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import cartes.modele.*;
import modele.CalculateurScore;
import modele.Croupier;
import modele.Joueur;
import modele.PartieBlackjack;

public class MainClass {

    public static void main(String[] args) {

        // // Test 1: Blackjack naturel
        // Paquet test1 = new Paquet();
        // test1.ajouterCarte(new Carte(Couleur.CARREAU, Hauteur.AS));
        // test1.ajouterCarte(new Carte(Couleur.PIQUE, Hauteur.ROI));
        // System.out.println("As + Roi: " + CalculateurScore.calculerScore(test1) + "
        // (attendu: 21)");
        // System.out.println("Est BlackJack : " +
        // CalculateurScore.estBlackjack(test1));
        // System.out.println("A dépassé : " + CalculateurScore.aDepasse(test1));
        // System.out.println("============================================================="
        // );

        // // Test 2: Deux figures
        // Paquet test2 = new Paquet();
        // test2.ajouterCarte(new Carte(Couleur.COEUR, Hauteur.DAME));
        // test2.ajouterCarte(new Carte(Couleur.TREFLE, Hauteur.VALET));
        // System.out.println("Dame + Valet: " + CalculateurScore.calculerScore(test2) +
        // " (attendu: 20)");
        // System.out.println("Est BlackJack : "+ CalculateurScore.estBlackjack(test2));
        // System.out.println("A dépassé : " + CalculateurScore.aDepasse(test2));

        // System.out.println("============================================================="
        // );

        // // Test 3: As souple (As = 11)
        // Paquet test3 = new Paquet();
        // test3.ajouterCarte(new Carte(Couleur.CARREAU, Hauteur.AS));
        // test3.ajouterCarte(new Carte(Couleur.PIQUE, Hauteur.CINQ));
        // System.out.println("As + 5: " + CalculateurScore.calculerScore(test3) + "
        // (attendu: 16)");
        // System.out.println("Est BlackJack : " +
        // CalculateurScore.estBlackjack(test3));
        // System.out.println("A dépassé : " + CalculateurScore.aDepasse(test3));

        // System.out.println("============================================================="
        // );

        // // Test 4: As qui devient 1 (bust évité)
        // Paquet test4 = new Paquet();
        // test4.ajouterCarte(new Carte(Couleur.CARREAU, Hauteur.AS));
        // test4.ajouterCarte(new Carte(Couleur.PIQUE, Hauteur.NEUF));
        // test4.ajouterCarte(new Carte(Couleur.COEUR, Hauteur.SIX));
        // System.out.println("As + 9 + 6: " + CalculateurScore.calculerScore(test4) + "
        // (attendu: 16)");
        // System.out.println("Est BlackJack : "+ CalculateurScore.estBlackjack(test4));
        // System.out.println("A dépassé : " + CalculateurScore.aDepasse(test4));

        // System.out.println("============================================================="
        // );

        // // Test 5: Deux As
        // Paquet test5 = new Paquet();
        // test5.ajouterCarte(new Carte(Couleur.CARREAU, Hauteur.AS));
        // test5.ajouterCarte(new Carte(Couleur.PIQUE, Hauteur.AS));
        // System.out.println("As + As: " + CalculateurScore.calculerScore(test5) + "
        // (attendu: 12)");
        // System.out.println("Est BlackJack : " +
        // CalculateurScore.estBlackjack(test5));
        // System.out.println("A dépassé : " + CalculateurScore.aDepasse(test5));

        // System.out.println("============================================================="
        // );

        // // Test 6: Trois As
        // Paquet test6 = new Paquet();
        // test6.ajouterCarte(new Carte(Couleur.CARREAU, Hauteur.AS));
        // test6.ajouterCarte(new Carte(Couleur.PIQUE, Hauteur.AS));
        // test6.ajouterCarte(new Carte(Couleur.COEUR, Hauteur.AS));
        // System.out.println("As + As + As: " + CalculateurScore.calculerScore(test6) +
        // " (attendu: 13)");
        // System.out.println("Est BlackJack : " +
        // CalculateurScore.estBlackjack(test6));
        // System.out.println("A dépassé : " + CalculateurScore.aDepasse(test6));

        // System.out.println("============================================================="
        // );

        // // Test 7: Bust classique
        // Paquet test7 = new Paquet();
        // test7.ajouterCarte(new Carte(Couleur.CARREAU, Hauteur.DIX));
        // test7.ajouterCarte(new Carte(Couleur.PIQUE, Hauteur.SEPT));
        // test7.ajouterCarte(new Carte(Couleur.COEUR, Hauteur.HUIT));
        // System.out.println("10 + 7 + 8: " + CalculateurScore.calculerScore(test7) + "
        // (attendu: 25 ou bust)");
        // System.out.println("Est BlackJack : " +
        // CalculateurScore.estBlackjack(test7));
        // System.out.println("A dépassé : " + CalculateurScore.aDepasse(test7));

        // System.out.println("============================================================="
        // );

        // // Test 8: 21 avec plusieurs cartes
        // Paquet test8 = new Paquet();
        // test8.ajouterCarte(new Carte(Couleur.CARREAU, Hauteur.CINQ));
        // test8.ajouterCarte(new Carte(Couleur.PIQUE, Hauteur.CINQ));
        // test8.ajouterCarte(new Carte(Couleur.COEUR, Hauteur.CINQ));
        // test8.ajouterCarte(new Carte(Couleur.TREFLE, Hauteur.SIX));
        // System.out.println("5 + 5 + 5 + 6: " + CalculateurScore.calculerScore(test8)
        // + " (attendu: 21)");
        // System.out.println("Est BlackJack : " +
        // CalculateurScore.estBlackjack(test8));
        // System.out.println("A dépassé : " + CalculateurScore.aDepasse(test8));

        Scanner scanner = new Scanner(System.in);

        Paquet sabot = Paquet.creerPaquet52();
        sabot.melanger();
        sabot.melanger();
        sabot.melanger();
        sabot.melanger();

        Croupier croupier = new Croupier();
        List<Joueur> joueurs = new ArrayList<>();
        joueurs.add(new Joueur("Jean"));
        PartieBlackjack nouvellePartie = new PartieBlackjack(sabot, croupier, joueurs, 0);
        // 2. Distribution
        nouvellePartie.distribuerCartesInitiales();

        // 3. Vérifier blackjack naturel du croupier
        if (CalculateurScore.estBlackjack(nouvellePartie.getCroupier().getMain())) {
            System.out.println("Le croupier a un Blackjack naturel avec les cartes : ");

            for (Carte c : croupier.getMain().getCartes()) {
                 System.out.println(c);
            }
            
            nouvellePartie.determinerGagnants();
            scanner.close();
            return; // Fin de la partie
        }

        // 4. Tour des joueurs

        for (int i = 0; i < joueurs.size(); i++) {
            Joueur joueur = joueurs.get(i);

            System.out.println("\n=== Tour de " + joueur.getNom() + " ===");
            System.out.println("Carte visible du croupier : " + croupier.getMain().getCartes().get(0));
            System.out.println("Votre main : " + joueur.getMain());
            System.out.println("Votre score : " + joueur.getScore());

            // Tant que le joueur n'a pas bust et n'a pas décidé de rester
            boolean continuerTour = true;

            while (continuerTour) {
                if (joueur.getScore() == 21) {
                    nouvellePartie.joueurReste(i);
                    continuerTour = false;
                    
                }

                else {

                    System.out.println("\nQue voulez-vous faire ?");
                    System.out.println("1. Tirer une carte");
                    System.out.println("2. Rester");
                    // System.out.println("3. Doubler");
                    System.out.print("Votre choix (1/2) : ");

                    int choix = scanner.nextInt();
                   

                    switch (choix) {
                        case 1: // TIRER
                            System.out.println("\n" + joueur.getNom() + " tire une carte");
                            nouvellePartie.joueurTire(i);
                            
                            System.out.println("Nouveau score : " + joueur.getScore());

                            // Vérifier si le joueur a bust
                            if (joueur.getScore() > 21) {
                                System.out.println("❌ Vous avez dépassé 21 ! (Bust)");
                                continuerTour = false;
                            }
                            break;

                        case 2: // RESTER 
                            continuerTour = false;
                            break;

                        
                        default:
                            System.out.println("⚠️  Choix invalide, veuillez choisir 1, 2 ou 3");
                            break;
                    }
                }
            }

        }

        // 5. Tour du croupier (seulement si au moins un joueur n'a pas bust)
        boolean auMoinsUnJoueurEnJeu = false;
        for (Joueur joueur : joueurs) {
            if (joueur.getScore() <= 21) {
                auMoinsUnJoueurEnJeu = true;
                break;
            }
        }

        if (auMoinsUnJoueurEnJeu) {
            nouvellePartie.jouerTourCroupier();

            System.out.println("Le croupier a un score de " + croupier.getScore()+  " avec les cartes : ");
            
            for (Carte c : croupier.getMain().getCartes()) {
                 System.out.println(c);
            }



            // 6. Déterminer les gagnants
            List<Joueur> gagnants = nouvellePartie.determinerGagnants();

            System.out.println(gagnants);

           
            for (Joueur joueur : gagnants) {
                System.out.println("Le gagnant est " + joueur.getNom());
            }

        

            System.out.println("Score du croupier est " + croupier.getScore());
        
    }

        scanner.close();

    }

}
