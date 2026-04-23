package cartes.controleur;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import cartes.modele.Carte;
import cartes.modele.Paquet;
import cartes.vue.*;

public class ControleurChoixCarteVersPaquet implements MouseListener, MouseMotionListener {
    private VuePaquetVisible vueSource;
    private Paquet destination;

    public ControleurChoixCarteVersPaquet(VuePaquetVisible vueSource, Paquet destination) {
        this.vueSource = vueSource;
        this.destination = destination;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int index = vueSource.getIndiceCarte(e.getX(), e.getY());
        if (index >= 0) {
            Carte c = vueSource.getPaquet().retirerCarte(index);
            destination.ajouterCarte(c);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {
        vueSource.setCarteEnSurbrillance(-1);
        vueSource.setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int index = vueSource.getIndiceCarte(e.getX(), e.getY());
        vueSource.setCarteEnSurbrillance(index);
        if (index >= 0) {
            vueSource.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            vueSource.setCursor(Cursor.getDefaultCursor());
        }
    }

}
