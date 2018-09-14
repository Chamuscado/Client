import Elements.Message;
import Elements.User;
import Interfaces.IClientRmi;

import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class ClientRmi extends UnicastRemoteObject implements IClientRmi {

    private int code = new Random().nextInt();

    public void setGui(GestInterface gui) {
        this.gui = gui;
    }

    private GestInterface gui;

    static public ClientRmi getClientRmi() {
        ClientRmi clientRmi = null;
        try {
            clientRmi = new ClientRmi();
        } catch (RemoteException e) {
            System.err.println("Remote Error (ClientRmi) - " + e);
        }
        return clientRmi;
    }

    protected ClientRmi() throws RemoteException {
    }

    protected ClientRmi(int port) throws RemoteException {
        super(port);
    }

    protected ClientRmi(int port, RMIClientSocketFactory csf, RMIServerSocketFactory ssf) throws RemoteException {
        super(port, csf, ssf);
    }

    @Override
    public int getCode() throws RemoteException {
        return code;
    }

    public int _getCode() {
        return code;
    }

    @Override
    public void refreshLoginUsers() throws RemoteException {
        if (gui != null)
            gui.refreshPlayerList();
    }

    @Override
    public void recivePairInvite(User user) throws RemoteException {
        gui.showPairInvite(user);
    }

    @Override
    public void answerPairInvite(User user, boolean answer) throws RemoteException {
        gui.showAnswerOfPairInvite(user, answer);
    }

    @Override
    public void refreshStatus() throws RemoteException {
        gui.refreshStatusPanel();
    }

    @Override
    public void setReadyToPlay(boolean ready) throws RemoteException {
        gui.setReadyToPlay(ready);
    }

    @Override
    public void refreshMessagesFor(String source) throws RemoteException {
        if (source.equals(IClientRmi.ForAll))
            gui.refreshMessagesGeral();
        else
            gui.refreshMessages(source);
    }
}
