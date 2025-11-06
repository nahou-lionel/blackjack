package modele;

public class Croupier extends Joueur{
    private boolean cartesCachees ;
    private StrategieCroupier strategie;

    // Constructeur
    public Croupier(){
        super();
        this.cartesCachees = true;
        this.strategie = new StrategieConservatrice();
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
