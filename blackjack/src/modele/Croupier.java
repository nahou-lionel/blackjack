package modele;

import cartes.modele.Paquet;

public class Croupier {
    private Paquet main;
    private boolean cartesCachees ;
    private StrategieCroupier strategie;

    // Constructeur
    public Croupier(){
        this.main = new Paquet();
        this.cartesCachees = true;
        this.strategie = new StrategieConservatrice();
    }

    public Paquet getMain(){
        return this.main;
    }

    public StrategieCroupier getStrategieCroupier(){
        return this.strategie;
    }

    public boolean getCartesCachees(){
        return this.cartesCachees;
    }

    // Cette méthode permet de révéler les cartes du croupier
    public void revelerCartes() {
        this.cartesCachees = false;
    }

    // Cette méthode permet de définir la stratégie utilisée par le croupier
    public void useStrategie(StrategieCroupier strategieCroupier){
        this.strategie = strategieCroupier;
    }
}
