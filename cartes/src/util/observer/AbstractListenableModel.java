/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.observer;

import java.util.*;

/**
 *
 * @author mathet
 */
public class AbstractListenableModel implements ListenableModel {

    private List<ModelListener> listeners;

    public AbstractListenableModel() {
        listeners = new ArrayList<>();
    }

    @Override
    public void addModelListener(ModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeModelListener(ModelListener l) {
        listeners.remove(l);
    }

    protected void fireChange()
    {
        for (ModelListener l : listeners)
            l.somethingHasChanged(this);
    }
    
}
