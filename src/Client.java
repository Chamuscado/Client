import Interfaces.IGestServerRmi;

public class Client {
    static public String GestServerIP = "192.168.1.97";

    static public void main(String[] args) {
        GestServerCom gestServer = new GestServerCom(GestServerIP, IGestServerRmi.ServiceName);
        gestServer.registUser("cliente","cliente2","pass");
    }
}
