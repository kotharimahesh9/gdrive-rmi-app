import java.io.File;
import java.io.FileInputStream;
import java.nio.file.*;
import java.rmi.Naming;

public class FileClientImpl {
    private static final String SYNC_DIR = "client_sync";

    public FileClientImpl() {
        boolean fileCreate = new File(SYNC_DIR).mkdirs();
        if(!fileCreate){
            throw new RuntimeException("Not able to Create Directory !");
        }
        System.out.println("Client Sync folder is created ! Now perform any operation inside this folder in your explorer");
    }


    public static void main(String[] args) {
        try {
            FileServerInterface server = (FileServerInterface) Naming.lookup("rmi://localhost/FileServer");
            FileClientImpl client = new FileClientImpl();
            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(SYNC_DIR);
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path fileName = (Path) event.context();

                    File file = new File(SYNC_DIR, fileName.toString());

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE || kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        if (file.isFile()) {
                            try (FileInputStream fis = new FileInputStream(file)) {
                                byte[] fileData = new byte[(int) file.length()];
                                fis.read(fileData);
                                server.uploadFile(fileName.toString(), fileData, file.lastModified());
                            }
                        }
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        server.deleteFile(fileName.toString());
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
