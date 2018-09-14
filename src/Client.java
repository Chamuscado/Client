import Elements.User;
import Exceptions.AccessDeniedException;
import Interfaces.IGestServerRmi;

import java.util.List;

public class Client {
    static public String GestServerIP;

    static public void main(String[] args) {


        if (args.length != 1) {
            System.out.println("Sintaxe: java Client <GestServerIP>");
            return;
        }
        GestServerIP = args[0];
        if (!validIP(GestServerIP)) {
            System.out.println("Endereço de IP inválido");
            return;
        }


        ClientRmi clientRmi = ClientRmi.getClientRmi();
        GestServerCom gestServer = new GestServerCom(GestServerIP, IGestServerRmi.ServiceName, clientRmi);
        GestInterface inter = new GestInterface(gestServer, clientRmi);
        inter.start();
    }

    private static boolean validIP(String ip) {
        try {
            if (ip == null || ip.isEmpty()) {
                return false;
            }
            String[] parts = ip.split("\\.");
            if (parts.length != 4) {
                return false;
            }
            for (String s : parts) {
                int i = Integer.parseInt(s);
                if (i < 0 || i > 255) {
                    return false;
                }
            }
            return true;
        } catch (NumberFormatException ignore) {
            return false;
        }
    }
}
