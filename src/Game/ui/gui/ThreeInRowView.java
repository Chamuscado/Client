package Game.ui.gui;

import Game.logic.ObservableGame;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JFrame;


public class ThreeInRowView extends JFrame implements Observer {
    ObservableGame game;
    ThreeInRowGamePanel panel;

    public ThreeInRowView(ObservableGame j) {
        super("Three in a row");
        game = j;
    }

    public void start() {

        game.addObserver(this);
        panel = new ThreeInRowGamePanel(game);

        addComponents();

        setVisible(true);
        this.setSize(700, 500);
        this.setMinimumSize(new Dimension(650, 450));
        validate();
    }


    private void addComponents() {
        Container cp = getContentPane();

        cp.setLayout(new BorderLayout());
        cp.add(panel, BorderLayout.CENTER);
    }

    @Override
    public void update(Observable o, Object arg) {
        repaint();
    }


}
