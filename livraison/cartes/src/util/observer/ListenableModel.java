/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.observer;

/**
 *
 * @author mathet
 */
public interface ListenableModel {
    void addModelListener(ModelListener l);
    void removeModelListener(ModelListener l);
}
