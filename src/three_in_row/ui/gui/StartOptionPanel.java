
package three_in_row.ui.gui;

import GameLib.States;
import three_in_row.logic.ObservableGame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

/**
 * Painel que apresenta as varias opcoes de configuracao e permite iniciar o game
 * Observa o game para se tornar invisivel/visivel conforme um game esteja em curso ou nao.
 *
 * @author JMSousa (base)
 */

class StartOptionPanel extends JPanel implements Observer {
    ObservableGame game;

    JButton start = new JButton("Start");
    PlayerNameBox player0Name;
    PlayerNameBox player1Name;

    StartOptionPanel(ObservableGame g) {
        game = g;
        game.addObserver(this);

        setBackground(Color.GREEN);
        setupComponents();
        setupLayout();

        setVisible(game.getState().compareTo(States.AwaitBeginning) == 0);
    }


    private void setupLayout() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        start.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalStrut(10));
        add(start);

        player0Name.setMinimumSize(new Dimension(120, 20));
        player1Name.setMinimumSize(new Dimension(120, 20));

        player0Name.setAlignmentX(Component.CENTER_ALIGNMENT);
        player1Name.setAlignmentX(Component.CENTER_ALIGNMENT);
        player0Name.setOpaque(false);
        player1Name.setOpaque(false);

        add(Box.createVerticalStrut(10));
        add(player0Name);

        add(Box.createVerticalStrut(10));
        add(player1Name);

        validate();
    }


    private void setupComponents() {
        player0Name = new PlayerNameBox(game, 0);
        player1Name = new PlayerNameBox(game, 1);

        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                game.setPlayerName(0, player0Name.getText());
                game.setPlayerName(1, player1Name.getText());
                game.startGame();
            }
        });

    }

    @Override
    public void update(Observable o, Object arg) {
        setVisible(game.getState().compareTo(States.AwaitBeginning) == 0);
        int id = game.getMyId();
        if (id == 0)
            player1Name.setText(game.getPlayer1().getName());
        else
            player0Name.setText(game.getPlayer0().getName());
    }

}
