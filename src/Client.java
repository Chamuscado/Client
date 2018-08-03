import Elements.User;
import Interfaces.IGestServerRmi;

import java.util.List;

public class Client {
    static public String GestServerIP = "192.168.1.96";

    static public void main(String[] args) {
        GestServerCom gestServer = new GestServerCom(GestServerIP, IGestServerRmi.ServiceName);
        gestServer.registUser("clienteName0", "cliente0", "pass");
        gestServer.registUser("clienteName1", "cliente1", "pass");
        gestServer.creatPair("cliente0", "cliente1");
        List<User> list = gestServer.getLoginUsers();
        for (User user : list) {
            System.out.println(user);
        }
        gestServer.login("teste", "teste");
    }
}
