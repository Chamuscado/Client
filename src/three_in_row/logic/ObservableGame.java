package three_in_row.logic;

import GameLib.Player;
import GameLib.Token;
import java.util.Observer;

public interface ObservableGame {

    Byte getState();

    void addObserver(Observer startOptionPanel);

    void setPlayerName(int i, String text);

    void startGame();

    Player getPlayer0();

    Player getPlayer1();

    Player getCurrentPlayer();

    boolean isOver();

    boolean hasWon(Player player);

    void placeToken(int row, int col);

    void returnToken(int row, int col);

    Token getToken(int row, int col);

    int getMyId();
}
