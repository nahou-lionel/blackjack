package modele.strategie;

import modele.partie.Action;

public class StrategieCroupier {
    public Action decider(int scoreCroupier) {

        if (scoreCroupier < 17) {
            return Action.TIRER;
        }

        else
            return Action.RESTER;
    }
}
