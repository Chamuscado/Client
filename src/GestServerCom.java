import Elements.*;
import Exceptions.*;
import Interfaces.IClientRmi;
import Interfaces.IGestServerRmi;

import javax.swing.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class GestServerCom {

    private IGestServerRmi guestServer = null;
    private ClientRmi clientRmi;
    private GestInterface gui;
    private ValidationUser validationUser;

    public String getUsername() {
        return username;
    }

    private String username = "";

    public GestServerCom(String registry, String serviceStr, ClientRmi clientRmi) {
        this.clientRmi = clientRmi;
        this.validationUser = new ValidationUser(username, ((ClientRmi) clientRmi)._getCode());
        try {
            String registration = "rmi://" + registry + "/" + serviceStr;
            Remote service = Naming.lookup(registration);
            guestServer = (IGestServerRmi) service;
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            JOptionPane.showMessageDialog(null, "O servidor de Gestão não se encontra ativo, ou não foi encontrado, a aplicação vai encerrar!");
            System.exit(0);
        }
    }

    public boolean registerUser(String name, String username, String password) {
        try {
            return guestServer.registUser(name, username, password);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }

    }

    boolean login(String username, String password) {
        try {
            if (guestServer.login(this.username = username, password, clientRmi)) {
                this.validationUser = new ValidationUser(username, ((ClientRmi) clientRmi)._getCode());
                return true;
            }
            return false;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        } catch (UserAlreadyLoggedException e) {
            gui.showError("Error: O Utilizador já se encontra logado");
            return false;
        }
    }

    void logout() {
        try {
            guestServer.logOut(username, clientRmi, validationUser);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    List<User> getLoginUsers() throws AccessDeniedException {
        try {
            return guestServer.getLoginUsers(validationUser);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setGui(GestInterface gui) {

        ((ClientRmi) clientRmi).setGui(this.gui = gui);
    }

    public boolean sendMensage(String dest, String msg) throws AccessDeniedException {
        try {
            Message msg2;
            //if (dest.equals(IClientRmi.ForAll))
            //    msg2 = new Message(IClientRmi.ForAll, msg, IClientRmi.ForAll);
            //else
            msg2 = new Message(username, msg, dest);
            if (guestServer.sendMensage(msg2, validationUser)) {
                return true;
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void sendAnswerPairInvite(User user, boolean answer) throws AccessDeniedException {
        try {
            guestServer.answerPairInvite(user, validationUser, answer);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void sendPairInvite(String username) {
        new Thread(() -> {
            try {
                guestServer.sendPairInvite(username, validationUser);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (AccessDeniedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void endPair() throws AccessDeniedException {
        try {
            guestServer.endPair(validationUser);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public Status getMyStatus() throws AccessDeniedException {
        try {
            return guestServer.getStatus(validationUser);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Status getAdStatus() throws AccessDeniedException {
        try {
            Pair pair = guestServer.getMyPair(validationUser);
            if (pair == null)
                return null;
            User userPair = pair.user0.username.compareTo(username) == 0 ? pair.user1 : pair.user0;
            return guestServer.getStatus(userPair.username, validationUser);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getGameServerIp() throws AccessDeniedException {
        try {
            return guestServer.getGameServerIp(validationUser);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Message> getMessages(String username) {
        try {
            MessagePair pair;
            if (username.equals(IClientRmi.ForAll))
                pair = new MessagePair(IClientRmi.ForAll, IClientRmi.ForAll);
            else
                pair = new MessagePair(username, this.username);
            return guestServer.getMessages(pair, validationUser);
        } catch (RemoteException | AccessDeniedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Game> getMyGames() {
        try {
            return guestServer.getHistGames(validationUser);
        } catch (RemoteException | AccessDeniedException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
