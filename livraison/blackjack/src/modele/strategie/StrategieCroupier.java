package modele.strategie;

import modele.partie.Action;

public interface StrategieCroupier {
    Action decider(int scoreCroupier);
}
