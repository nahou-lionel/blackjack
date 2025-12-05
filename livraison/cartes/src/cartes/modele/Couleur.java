package cartes.modele;

public enum Couleur {    
    PIQUE("♠", false),
    CARREAU("♦", true),
    TREFLE("♣", false),
    COEUR("♥", true);

    private final String symbole;
    private final boolean rouge;

    Couleur(String symbole, boolean rouge) {
        this.symbole = symbole;
        this.rouge = rouge;
    }

    public String getSymbole() {
        return symbole;
    }

    public boolean estRouge() {
        return rouge;
    }
}