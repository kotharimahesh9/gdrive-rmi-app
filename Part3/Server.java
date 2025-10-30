
import java.rmi.*;
import java.rmi.registry.LocateRegistry;

public class Server {
    public static void main(String[] args) {
        try {
            ComputationServer server = new ComputationServer();
            LocateRegistry.createRegistry(1099);
            Naming.rebind("computationServer", server);

            System.out.println("Server is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
