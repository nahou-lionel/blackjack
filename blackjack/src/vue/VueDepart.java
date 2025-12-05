package vue;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

public class VueDepart extends JPanel {
    private Image imageDeFond;
    private static  String CHEMIN_IMAGE = "/assets/blackjack_image.png";
    private  JButton boutonPlay;
    private boolean avecRobot;
    private String typeStrategie;

    /**
     * Constructeur avec paramètres pour configuration du robot
     *
     * @param avecRobot true pour jouer avec un robot
     * @param typeStrategie "simple" ou "optimal"
     */
    public VueDepart(boolean avecRobot, String typeStrategie) {
        this.avecRobot = avecRobot;
        this.typeStrategie = typeStrategie;

        setPreferredSize(new Dimension(1080, 720));
        setLayout(new BorderLayout());
        setBackground(Color.BLACK);

        URL url = VueDepart.class.getResource(CHEMIN_IMAGE);
        if (url != null) {
            this.imageDeFond = new ImageIcon(url).getImage();
        } else {
            this.imageDeFond = null; 
        }

        this.boutonPlay = new JButton("play");
        this.boutonPlay.setFont(new Font("SansSerif", Font.BOLD, 28));
        Color colorBoutonPlay = new Color(0,114,187);
        this.boutonPlay.setBackground(colorBoutonPlay);
        this.boutonPlay.setForeground(Color.WHITE);
        this.boutonPlay.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        this.boutonPlay.setFocusPainted(false);
        this.boutonPlay.setRolloverEnabled(true);
        this.boutonPlay.setPreferredSize(new Dimension(320, 72));
        
        JPanel bandeauBas = new JPanel();
        bandeauBas.setLayout(new GridBagLayout());
        bandeauBas.setOpaque(false);
        bandeauBas.setBorder(BorderFactory.createEmptyBorder(20, 20, 28, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.PAGE_END;
        bandeauBas.add(this.boutonPlay, gbc);

        add(bandeauBas, BorderLayout.SOUTH);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imageDeFond != null) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(imageDeFond, 0, 0, getWidth(), getHeight(), this);
            g2.dispose();
        }
    }

    public JButton getBoutonPlay() { 
        return boutonPlay; 
    }

    /**
     * Démarre une partie de Blackjack avec les paramètres configurés
     *
     * @param frame La fenêtre principale
     */
    public void demarrerPartie(JFrame frame) {
        VuePartie partie = new VuePartie(avecRobot, typeStrategie);
        frame.setContentPane(partie);
        frame.repaint();
        frame.revalidate();
    }
}
