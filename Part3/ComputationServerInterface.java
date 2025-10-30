
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ComputationServerInterface extends Remote {
    int add(int i, int j) throws RemoteException;
    int[] sort(int[] array) throws RemoteException;
}

