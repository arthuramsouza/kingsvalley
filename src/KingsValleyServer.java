import java.rmi.Naming;
import java.rmi.RemoteException;

public class KingsValleyServer {

    public static void main(String[] args) {

        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry ready!");
        }
        catch(RemoteException e) {
            System.err.println("RMI registry already running!");
        }

        try {
            KingsValley game = new KingsValley();
            Naming.rebind("KingsValley", game);
            System.out.println("KingsValley server ready!");

            game.garbageCollector();
        }
        catch(Exception e) {
            System.err.println("KingsValley server failed!");
            //System.exit(1);
        }
    }
}
