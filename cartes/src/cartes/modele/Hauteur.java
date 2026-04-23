package cartes.modele;

public enum Hauteur {
    DEUX("2", 2),
    TROIS("3", 3),
    QUATRE("4", 4),
    CINQ("5", 5),
    SIX("6", 6),
    SEPT("7", 7),
    HUIT("8", 8),
    NEUF("9", 9),
    DIX("10", 10),
    VALET("V", 11),
    DAME("D", 12),
    ROI("R", 13),
    AS("A", 14); // Valeur haute pour le tri, mais peut valoir 1 ou 11 au blackjack

    private final String symbole;
    private final int valeur;

    Hauteur(String symbole, int valeur) {
        this.symbole = symbole;
        this.valeur = valeur;
    }

    public String getSymbole() {
        return symbole;
    }

    public int getValeur() {
        return valeur;
    }
}