import java.io.File;
import java.io.FileInputStream;
import java.nio.file.*;
import java.rmi.Naming;

public class FileClientWrapper {
    private static final String SYNC_DIR = "client_sync";
    private String serverUrl;

    public FileClientWrapper(String serverHost) {
        this.serverUrl = "rmi://" + serverHost + "/FileServer";
        boolean fileCreate = new File(SYNC_DIR).mkdirs();
        if(!fileCreate){
            System.out.println("Directory already exists or created successfully");
        }
        System.out.println("Client Sync folder is ready! Now perform any operation inside this folder");
    }


    public static void main(String[] args) {
        String serverHost = args.length > 0 ? args[0] : "localhost";

        try {
            FileClientWrapper wrapper = new FileClientWrapper(serverHost);
            FileServerInterface server = (FileServerInterface) Naming.lookup(wrapper.serverUrl);

            WatchService watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(SYNC_DIR);
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);

            System.out.println("Watching for file changes in " + SYNC_DIR + "...");

            while (true) {
                WatchKey key = watchService.take();
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind<?> kind = event.kind();
                    Path fileName = (Path) event.context();

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.out.println("File created: " + fileName);
                        File file = new File(SYNC_DIR + "/" + fileName);
                        if (file.isFile()) {
                            byte[] fileData = new byte[(int) file.length()];
                            try (FileInputStream fis = new FileInputStream(file)) {
                                fis.read(fileData);
                            }
                            server.uploadFile(fileName.toString(), fileData, file.lastModified());
                            System.out.println("File uploaded to server: " + fileName);
                        }
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        System.out.println("File deleted: " + fileName);
                        server.deleteFile(fileName.toString());
                        System.out.println("File deleted from server: " + fileName);
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        System.out.println("File modified: " + fileName);
                        File file = new File(SYNC_DIR + "/" + fileName);
                        if (file.isFile()) {
                            byte[] fileData = new byte[(int) file.length()];
                            try (FileInputStream fis = new FileInputStream(file)) {
                                fis.read(fileData);
                            }
                            server.uploadFile(fileName.toString(), fileData, file.lastModified());
                            System.out.println("File updated on server: " + fileName);
                        }
                    }
                }
                key.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
