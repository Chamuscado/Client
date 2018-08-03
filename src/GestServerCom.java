import Elements.User;
import Interfaces.IGestServerRmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public class GestServerCom {

    private IGestServerRmi guestServer = null;

    public GestServerCom(String registry, String serviceStr) {
        try {
            String registration = "rmi://" + registry + "/" + serviceStr;
            Remote service = Naming.lookup(registration);
            guestServer = (IGestServerRmi) service;
        } catch (RemoteException | NotBoundException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public boolean registUser(String name, String username, String password) {
        try {
            return guestServer.registUser(name, username, password);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }

    }

    boolean login(String username, String password) {
        try {
            return guestServer.login(username, password);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    List<User> getLoginUsers() {
        try {
            return guestServer.getLoginUsers();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    boolean creatPair(String user0, String user1) {
        try {
            return guestServer.createPair(user0, user1);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

}
