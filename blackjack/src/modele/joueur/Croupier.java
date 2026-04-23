package modele.joueur;

import modele.strategie.StrategieCroupier;

public class Croupier extends Joueur {
    private boolean cartesCachees;
    private StrategieCroupier strategie;

    public Croupier() {
        super("Croupier");
        this.cartesCachees = true;
        this.strategie = new StrategieCroupier();
    }

    public StrategieCroupier getStrategieCroupier() {
        return this.strategie;
    }

    public boolean getCartesCachees() {
        return this.cartesCachees;
    }

    public void revelerCartes() {
        this.cartesCachees = false;
    }

    public void useStrategie(StrategieCroupier strategieCroupier) {
        this.strategie = strategieCroupier;
    }
}