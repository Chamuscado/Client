
package Game.ui.gui;

import java.awt.GridLayout;
import javax.swing.JPanel;

import Game.logic.ObservableGame;

/**
 * Grelha de celulas... E' apenas um contentor
 *
 * @author JMSousa (base)
 */
class GameGrid extends JPanel {
    public static final int DIM = 3;
    ObservableGame game;

    GameGrid(ObservableGame g) {
        game = g;
        setupLayout();
    }

    void setupLayout() {
        JPanel p = new JPanel();

        p.setLayout(new GridLayout(DIM, DIM, 10, 10));

        for (int i = 0; i < DIM; i++) {


            for (int j = 0; j < DIM; j++) {
                GameCell cell = new GameCell(game, i, j);
                p.add(cell);
            }


        }
        add(p);
    }

}

