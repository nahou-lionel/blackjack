package modele.strategie;

import modele.partie.Action;

public class StrategieConservatrice implements StrategieCroupier {

    // Cette méthode décide si le croupier doit rester ou tirer en fonction que son
    // soit inférieur ou supérieur à 17
    @Override
    public Action decider(int scoreCroupier) {

        if (scoreCroupier < 17) {
            return Action.TIRER;
        }

        else
            return Action.RESTER;
    }

}
