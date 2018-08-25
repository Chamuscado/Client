import GameLib.Constants;
import GameLib.Player;
import GameLib.Token;
import three_in_row.logic.ObservableGame;

import three_in_row.ui.gui.ThreeInRowView;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Observable;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class Cliente extends Observable implements ObservableGame, Constants {
    private final String NULL = "null";
    private Socket socket;
    private boolean messages;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private Thread reciver;
    private ThreeInRowView threeInRowView;
    private BlockingDeque<Object> fila;


    public static void main(String[] args) {
        Cliente view = new Cliente();
        view.threeInRowView = new ThreeInRowView(view);
        view.threeInRowView.start();
    }

    public Cliente(String hostname, int port, int timeout) {
        messages = false;
        fila = new LinkedBlockingDeque<>();
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

            if (messages)
                System.out.println("Conectado");

        }
    }


    public Cliente(String hostname, int port) {
        this(hostname, port, 40000);
    }

    public Cliente(int port) {
        this("127.0.0.1", port);
    }

    public Cliente(String hostname) {
        this(hostname, PORT);
    }

    public Cliente() {
        this("127.0.0.1");
    }


    public void sendObject(Object obj) {
        System.out.println("A enviar: <" + obj.toString() + ">");
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            out.writeObject(obj);
            out.write(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object readObject() {
        while (true)
            try {
                return in.readObject();
            } catch (SocketTimeoutException ex) {
                System.out.println("Timeout - readObj");
                continue;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                break;
            }
        return null;
    }

    public void close() {
        try {
            socket.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Byte getState() {
        fila.clear();
        sendObject("getState");
        Object obj = getNext();
        if (obj == null || (obj instanceof String && ((String) obj).compareTo(NULL) == 0)) {
            return -1;
        } else if (obj instanceof Byte)
            return (byte) obj;
        else {
            System.out.println("O obj não era instacia de IStates <" + obj.toString() + ">");
            return null;
        }
    }

    @Override
    public void setPlayerName(int i, String text) {
        fila.clear();
        sendObject("setPlayerName");
        sendObject(i);
        sendObject(text);
    }

    @Override
    public void startGame() {
        fila.clear();
        sendObject("startGame");
    }

    @Override
    public Player getPlayer0() {
        fila.clear();
        sendObject("getPlayer0");
        Object obj = getNext();
        return objToPlayer(obj);
    }

    @Override
    public Player getPlayer1() {
        fila.clear();
        sendObject("getPlayer1");
        Object obj = getNext();
        return objToPlayer(obj);
    }

    @Override
    public Player getCurrentPlayer() {
        fila.clear();
        sendObject("getCurrentPlayer");
        Object obj = getNext();
        return objToPlayer(obj);
    }

    @Override
    public boolean isOver() {
        fila.clear();
        sendObject("isOver");
        Object obj = getNext();
        return objToBoolean(obj);
    }

    @Override
    public boolean hasWon(Player player) {
        fila.clear();
        sendObject("hasWon");
        sendObject(player);
        Object obj = getNext();
        return objToBoolean(obj);
    }

    @Override
    public void placeToken(int row, int col) {
        fila.clear();
        sendObject(row);
        sendObject("placeToken");
        sendObject(row);
        sendObject(col);
    }

    @Override
    public void returnToken(int row, int col) {
        fila.clear();
        sendObject(row);
        sendObject("returnToken");
        sendObject(row);
        sendObject(col);
    }

    @Override
    public Token getToken(int row, int col) {
        fila.clear();
        sendObject(row);
        sendObject("getToken");
        sendObject(row);
        sendObject(col);
        Object obj = getNext();
        if (obj == null || (obj instanceof String && ((String) obj).compareTo(NULL) == 0)) {
            return null;
        } else if (obj instanceof Token)
            return (Token) obj;
        else {
            System.out.println("O obj não era instacia de Token <" + obj.toString() + ">");
            return null;
        }
    }

    @Override
    public int getMyId() {
        fila.clear();
        sendObject("getMyId");
        Object obj = getNext();
        if (obj instanceof Integer)
            return (int) obj;
        else
            return -1;
    }

    private Player objToPlayer(Object obj) {
        if (obj == null || (obj instanceof String && ((String) obj).compareTo(NULL) == 0)) {
            return null;
        } else if (obj instanceof Player)
            return (Player) obj;
        else {
            System.out.println("O obj não era instacia de Player <" + obj.toString() + ">");
            return null;
        }
    }

    private boolean objToBoolean(Object obj) {
        if (obj == null || (obj instanceof String && ((String) obj).compareTo(NULL) == 0)) {
            return false;
        } else if (obj instanceof Boolean)
            return (Boolean) obj;
        else {
            System.out.println("O obj não era instacia de Boolean <" + obj.toString() + ">");
            return false;
        }
    }

    private Object getNext() {
        try {
            return fila.poll(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    class Receiver implements Runnable {
        boolean run = true;

        @Override
        public void run() {
            while (run) {
                Object obj = readObject();
                System.out.println("Recevido: <" + obj.toString() + ">");
                if (obj instanceof String && ((String) obj).compareTo(NULL) != 0) {
                    String str = (String) obj;
                    if (str.compareTo("update") == 0) {

                        SwingUtilities.invokeLater(() -> {

                            setChanged();
                            notifyObservers();
                        });
                    } else
                        System.out.println(str);

                } else {
                    fila.add(obj);
                }
            }
        }

        void stop() {
            run = false;
        }
    }
}
