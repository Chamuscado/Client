import Elements.Message;
import Elements.User;
import Interfaces.IClientRmi;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;

public class ClientRmi extends UnicastRemoteObject implements IClientRmi, Serializable {

    private int code = new Random().nextInt();

    public void setGui(GestInterface gui) {
        this.gui = gui;
    }

    private GestInterface gui;

    static public IClientRmi getClientRmi() {
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
    public void sendMensage(Message message) throws RemoteException {
        gui.addMessageToList(message.getSource(), message.getMessage());
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
    public void RecivePairInvite(User user) throws RemoteException {

    }

    @Override
    public void AnswerPairInvite(String s, String s1, int i) throws RemoteException {

    }
}
