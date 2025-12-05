package modele;

import cartes.modele.Carte;

/**
 * Représente un joueur robot qui prend des décisions automatiquement
 * en utilisant une stratégie prédéfinie
 */
public class JoueurRobot extends Joueur {

    private StrategieJoueur strategie;

    /**
     * Constructeur d'un joueur robot
     *
     * @param nom Le nom du robot
     * @param banqueInitiale La banque de départ
     * @param strategie La stratégie à utiliser pour les décisions
     */
    public JoueurRobot(String nom, int banqueInitiale, StrategieJoueur strategie) {
        super(nom);
        this.setBanque(banqueInitiale);
        this.strategie = strategie;
    }

    /**
     * Le robot choisit son action en fonction de sa stratégie
     *
     * @param carteVisibleCroupier La carte visible du croupier
     * @return L'action choisie par le robot
     */
    public Action choisirAction(Carte carteVisibleCroupier) {
        int score = this.getScore();
        return strategie.decider(score, carteVisibleCroupier);
    }

    /**
     * Le robot choisit sa mise en fonction de sa stratégie
     *
     * @return Le montant de la mise
     */
    public int choisirMise() {
        return strategie.determinerMise(this.getBanque());
    }

    /**
     * Retourne la stratégie utilisée par le robot
     *
     * @return La stratégie du robot
     */
    public StrategieJoueur getStrategie() {
        return this.strategie;
    }

    /**
     * Change la stratégie du robot
     *
     * @param strategie La nouvelle stratégie
     */
    public void setStrategie(StrategieJoueur strategie) {
        this.strategie = strategie;
    }

    /**
     * Indique si ce joueur est un robot
     *
     * @return true car c'est un robot
     */
    public boolean estRobot() {
        return true;
    }
}
