import java.rmi.*;
import java.util.*;

public class ClientWrapper {
    private String serverUrl;

    public ClientWrapper(String serverHost) {
        this.serverUrl = "rmi://" + serverHost + "/computationServer";
    }

    public static void main(String[] args) {
        String serverHost = args.length > 0 ? args[0] : "localhost";
        ClientWrapper wrapper = new ClientWrapper(serverHost);

        try {
            ComputationServerInterface computationServer = (ComputationServerInterface) Naming.lookup(wrapper.serverUrl);
            Scanner userInputScanner = new Scanner(System.in);

            System.out.println("Please choose an operation:");
            System.out.println("1. Perform Addition");
            System.out.println("2. Perform Sorting");
            System.out.print("Enter your choice (1 or 2): ");
            int userChoice = userInputScanner.nextInt();

            if (userChoice == 1) {
                performAddition(computationServer, userInputScanner);
            } else if (userChoice == 2) {
                performSorting(computationServer, userInputScanner);
            } else {
                System.out.println("Invalid input. Please enter either 1 or 2.");
            }

            userInputScanner.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void performAddition(ComputationServerInterface server, Scanner scanner) {
        try {
            System.out.print("Enter the first number: ");
            int num1 = scanner.nextInt();
            System.out.print("Enter the second number: ");
            int num2 = scanner.nextInt();

            int result = server.add(num1, num2);
            System.out.println("Result of addition: " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void performSorting(ComputationServerInterface server, Scanner scanner) {
        try {
            System.out.print("Enter the number of elements to sort: ");
            int n = scanner.nextInt();
            int[] array = new int[n];

            System.out.println("Enter the elements:");
            for (int i = 0; i < n; i++) {
                array[i] = scanner.nextInt();
            }

            int[] sortedArray = server.sort(array);
            System.out.println("Sorted array: " + Arrays.toString(sortedArray));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
