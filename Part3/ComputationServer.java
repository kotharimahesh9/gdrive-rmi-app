
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Arrays;

public class ComputationServer extends UnicastRemoteObject implements ComputationServerInterface {
    protected ComputationServer() throws RemoteException {
        super();
    }

    @Override
    public int add(int i, int j) throws RemoteException {
        return i + j;
    }

    @Override
    public int[] sort(int[] array) throws RemoteException {
        int[] newArray = array.clone();
        Arrays.sort(newArray);
        return newArray;
    }
}

