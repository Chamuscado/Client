import Elements.User;
import Exceptions.AccessDeniedException;
import Interfaces.IGestServerRmi;
import model.GameModel;
import Game.ui.gui.ThreeInRowView;

import java.util.List;

public class Client {
    static public String GestServerIP = "192.168.1.96";

    static public void main(String[] args) throws InterruptedException {


        GestServerCom gestServer = new GestServerCom(GestServerIP, IGestServerRmi.ServiceName, ClientRmi.getClientRmi());
        GestInterface inter = new GestInterface(gestServer);
        inter.start();

        List<User> list = null;
        try {
            list = gestServer.getLoginUsers();
            for (User user : list) {
                System.out.println(user);
            }
        } catch (AccessDeniedException e) {
            System.out.println("Acesso Negado");
        }

    }


}
