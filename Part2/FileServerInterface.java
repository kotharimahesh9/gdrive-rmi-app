import java.rmi.Remote;
import java.rmi.RemoteException;
import java.io.IOException;

public interface FileServerInterface extends Remote {
    void uploadFile(String fileName, byte[] data, long lastModified) throws RemoteException, IOException;
    void deleteFile(String fileName) throws RemoteException;
    void renameFile(String oldName, String newName) throws RemoteException;
}
