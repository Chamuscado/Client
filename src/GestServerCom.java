import Elements.Message;
import Elements.User;
import Elements.ValidationUser;
import Exceptions.AccessDeniedException;
import Exceptions.UserAlreadyLoggedException;
import Interfaces.IClientRmi;
import Interfaces.IGestServerRmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public class GestServerCom {

    private IGestServerRmi guestServer = null;
    private IClientRmi clientRmi;
    private GestInterface gui;

    public String getUsername() {
        return username;
    }

    private String username = "";

    public GestServerCom(String registry, String serviceStr, IClientRmi clientRmi) {
        this.clientRmi = clientRmi;
        try {
            String registration = "rmi://" + registry + "/" + serviceStr;
            Remote service = Naming.lookup(registration);
            guestServer = (IGestServerRmi) service;
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
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
            return guestServer.login(this.username = username, password, clientRmi);
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
            guestServer.logOut(username, clientRmi);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    List<User> getLoginUsers() throws AccessDeniedException {
        try {
            return guestServer.getLoginUsers(new ValidationUser(username, clientRmi.getCode()));
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    boolean createPair(String user0, String user1) throws AccessDeniedException {
        try {
            return guestServer.createPair(user0, user1, new ValidationUser(username, clientRmi.getCode()));
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setGui(GestInterface gui) {

        ((ClientRmi) clientRmi).setGui(this.gui = gui);
    }

    public boolean sendMensage(String dest, String msg) {
        try {
            return guestServer.sendMensage(new Message(username, msg, dest), new ValidationUser(username, ((ClientRmi) clientRmi)._getCode()));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
}
