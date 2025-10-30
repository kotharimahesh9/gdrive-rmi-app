import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

public class FileServerImpl extends UnicastRemoteObject implements FileServerInterface {

    private static final String UPLOAD_DIR = "server_uploads";

    protected FileServerImpl() throws RemoteException {
        super();
        new File(UPLOAD_DIR).mkdirs();
    }

    @Override
    public void uploadFile(String fileName, byte[] data) throws RemoteException, IOException {
        new Thread(() -> {
            File file = new File(UPLOAD_DIR, fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
                System.out.println("Uploaded file: " + fileName);
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public boolean deleteFile(String fileName) throws RemoteException, IOException {
        File file = new File(UPLOAD_DIR, fileName);
        if (file.delete()) {
            System.out.println("Deleted file: " + fileName);
            return true;
        } else {
            System.out.println("Failed to delete file: " + fileName);
            return false;
        }
    }

    @Override
    public void renameFile(String oldName, String newName) throws RemoteException, IOException {

        new Thread(() -> {
            try{
                File oldFile = new File(UPLOAD_DIR, oldName);
                File newFile = new File(UPLOAD_DIR, newName);
                if (oldFile.renameTo(newFile)) {
                    System.out.println("Renamed file from " + oldName + " to " + newName);
                } else {
                    System.out.println("Failed to rename file from " + oldName + " to " + newName);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();
    }


    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1099);
            FileServerImpl server = new FileServerImpl();
            Naming.rebind("rmi://localhost/FileServer", server);
            System.out.println("File Server is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
