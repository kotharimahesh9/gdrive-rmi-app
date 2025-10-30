import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface FileServerInterface extends Remote {

    void uploadFile(String fileName, byte[] data) throws RemoteException, IOException;
    boolean deleteFile(String fileName) throws RemoteException, IOException;
    void renameFile(String oldName, String newName) throws RemoteException, IOException;
}
