import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.Naming;
import java.util.Scanner;

public class FileClientWrapper {

    public static void main(String[] args) {
        String serverHost = args.length > 0 ? args[0] : "localhost";
        String serverUrl = "rmi://" + serverHost + "/FileServer";

        try {
            FileServerInterface fileServer = (FileServerInterface) Naming.lookup(serverUrl);

            Scanner scanner = new Scanner(System.in);
            boolean running = true;

            while (running) {
                System.out.println("Select an operation:");
                System.out.println("1. Upload file");
                System.out.println("2. Delete file");
                System.out.println("3. Rename file");
                System.out.println("4. Exit");

                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        uploadFile(fileServer, scanner);
                        break;
                    case 2:
                        deleteFile(fileServer, scanner);
                        break;
                    case 3:
                        renameFile(fileServer, scanner);
                        break;
                    case 4:
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            }

            System.out.println("Exiting...");

        } catch (Exception e) {
            System.err.println("File Client exception: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static void uploadFile(FileServerInterface fileServer, Scanner scanner) throws Exception {
        System.out.print("Enter file path to upload: ");
        String filePath = scanner.nextLine();

        byte[] data = Files.readAllBytes(Paths.get(filePath));

        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

        fileServer.uploadFile(fileName, data);
        System.out.println("File uploaded successfully.");
    }

    private static void deleteFile(FileServerInterface fileServer, Scanner scanner) throws Exception {
        System.out.print("Enter file name to delete: ");
        String fileName = scanner.nextLine();

        if(fileServer.deleteFile(fileName)){
            System.out.println("File deleted successfully");
        }else{
            System.out.println("Failed to Delete File");
        }
    }

    private static void renameFile(FileServerInterface fileServer, Scanner scanner) throws Exception {
        System.out.print("Enter old file name: ");
        String oldName = scanner.nextLine();
        System.out.print("Enter new file name: ");
        String newName = scanner.nextLine();

        fileServer.renameFile(oldName, newName);

    }
}
