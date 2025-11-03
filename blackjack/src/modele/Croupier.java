package modele;

import cartes.modele.Paquet;

public class Croupier {
    private Paquet main;
    private boolean cartesCachees = true;

    public boolean doitTirer() {
        return getScore() < 17;
    }

    public void revelerCartes() {
        cartesCachees = false;
    }
}
