package model;

import Game.ui.gui.ThreeInRowView;
import GameLib.*;
import Game.logic.ObservableGame;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;

public class GameModel extends Observable implements ObservableGame, Constants {

    private String username;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean messages;
    private Thread reciver;

    private Byte state;
    private Player[] players;
    private Player currentPlayer;
    private boolean isOver;
    private Token[][] tokens;
    ThreeInRowView view;
    private GameModel game;
    private boolean ready = false;
    private int wonPlayerId = -1;
    private static final long delayToClose = 1000;
    private CallBack callBack_end;

    public GameModel(String username, String hostname, int port, int timeout, CallBack callBack) {
        game = this;
        this.callBack_end = callBack;
        messages = false;
        this.username = username;
        players = new Player[PLAYER_NUMBER];
        tokens = new Token[DIM][DIM];
        try {
            socket = new Socket(hostname, port);
            socket.setSoTimeout(timeout);

        } catch (IOException ex) {
            System.out.println("Erro de IO" + ex);
        }
        if (socket.isConnected()) {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            } catch (SocketTimeoutException ex) {
                System.out.println("SocketTimeoutException: " + ex);
            } catch (IOException ex) {
                System.out.println("Erro de IO" + ex);
            }

            reciver = new Thread(new Receiver());
            reciver.start();

            sendResquest(new ServerRequest(ServerRequestKey.LOGIN, username));

            if (messages)
                System.out.println("Conectado");

        }
    }


    public GameModel(String username, String hostname, int port, CallBack callBack) {
        this(username, hostname, port, 40000, callBack);
    }

    public GameModel(String username, int port, CallBack callBack) {
        this(username, "127.0.0.1", port, callBack);
    }

    public GameModel(String username, String hostname, CallBack callBack) {
        this(username, hostname, PORT, callBack);
    }

    public GameModel(String username, CallBack callBack) {
        this(username, "127.0.0.1", callBack);
    }


    public void setState(Byte state) {
        this.state = state;
        refreshView();
    }


    public void setPlayer(Player player, int id) {
        this.players[id] = player;
        refreshView();
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
        refreshView();
    }

    public void setOver(boolean over) {
        isOver = over;
        refreshView();
    }

    public void setTokens(Token[][] tokens) {
        this.tokens = tokens;
        refreshView();
    }

    public void setToken(int line, int column, Token token) {
        this.tokens[line][column] = token;

        refreshView();
    }

    private void refreshView() {
        if (ready)
            SwingUtilities.invokeLater(() -> {
                setChanged();
                notifyObservers();
            });
    }

    @Override
    public Byte getState() {
        return state;
    }

    @Override
    public Player getPlayer0() {
        return players[0];
    }

    @Override
    public Player getPlayer1() {
        return players[1];
    }

    @Override
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    @Override
    public boolean isOver() {
        return isOver;
    }

    @Override
    public boolean hasWon(Player player) {
        return player.id == wonPlayerId;
    }

    @Override
    public void placeToken(int row, int col) {  // TODO -> Resques to Server
        sendResquest(new ServerRequest(ServerRequestKey.PLACETOKEN, new Object[]{row, col}));
    }

    @Override
    public void returnToken(int row, int col) { // TODO -> Resques to Server
        sendResquest(new ServerRequest(ServerRequestKey.RETURNTOKEN, new Object[]{row, col}));
    }

    @Override
    public Token getToken(int row, int col) {
        return tokens[row][col];
    }

    public void sendResquest(ServerRequest request) {
        System.out.println("A enviar: <" + request.toString() + ">");
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            out.writeObject(request);
            out.write(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            socket.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Receiver implements Runnable {
        boolean run = true;

        @Override
        public void run() {
            System.out.println("StartReciver");
            while (run) {
                ClientRequest request = readRequest();
                if (request == null)
                    break;
                System.out.println("Recevido: <" + request.toString() + ">");

                switch (request.getRequestKey()) {
                    case NULL:
                        break;
                    case READY:
                        ready = true;
                        if (view == null) {
                            view = new ThreeInRowView(game);
                            view.start();
                        } else {
                            refreshView();
                        }
                        break;
                    case SETPLAYER_0:
                        setPlayer((Player) request.getParams(), 0);
                        break;
                    case SETPLAYER_1:
                        setPlayer((Player) request.getParams(), 1);
                        break;
                    case SETCURRENTPLAYER:
                        setCurrentPlayer((Player) request.getParams());
                        break;
                    case ISOVER:
                        setOver((Boolean) request.getParams());
                        break;
                    case SETSTATE:
                        setState((Byte) request.getParams());
                        break;
                    case HASWON:
                        setWon((int) request.getParams());
                        break;
                    case SETTOKEN:
                        setToken((int) ((Object[]) request.getParams())[0], (int) ((Object[]) request.getParams())[1], (Token) ((Object[]) request.getParams())[2]);
                        break;
                    case UPDATE:
                        refreshView();
                        break;

                    case WAIT://TODO -> mostrar ao user que esta à espera do adversario
                        break;
                    case ENDWAIT:
                        break;
                    case ENDGAME:
                        closeView();
                        break;
                    default:
                }
            }

        }

        public ClientRequest readRequest() {

            while (true)
                try {
                    Object obj;
                    do {
                        obj = in.readObject();
                    } while (!(obj instanceof ClientRequest));
                    return (ClientRequest) obj;
                } catch (SocketTimeoutException ex) {
                    System.out.println("Timeout - readObj");
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break;
                }
            return null;
        }

        void stop() {
            run = false;
        }
    }

    private void closeView() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                view.dispatchEvent(new WindowEvent(view, WindowEvent.WINDOW_CLOSING));
                if (callBack_end != null)
                    callBack_end.execute();
            }
        }, delayToClose);

    }

    private void setWon(int id) {
        wonPlayerId = id;
    }
}
