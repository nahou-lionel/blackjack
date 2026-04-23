package cartes.controleur;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import cartes.modele.Carte;
import cartes.modele.Paquet;
import cartes.vue.VuePaquet;

public class ControleurPiocheVersPaquet implements MouseListener {
    private VuePaquet vueSource;
    private Paquet destination;

    public ControleurPiocheVersPaquet(VuePaquet vueSource, Paquet destination) {
        this.vueSource = vueSource;
        this.destination = destination;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!vueSource.getPaquet().estVide()) {
            Carte cartePiochee = vueSource.getPaquet().retirerPremiereCarte();
            destination.ajouterCarte(cartePiochee);
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
    }

}
