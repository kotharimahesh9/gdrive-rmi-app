
import java.rmi.*;
import java.util.*;

public class Client {
    private static final String REMOTE_SERVER_URL = "rmi://localhost/computationServer";

    public static void main(String[] args) {
        try {
            ComputationServerInterface computationServer = (ComputationServerInterface) Naming.lookup(REMOTE_SERVER_URL);
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

    private static void performAddition(ComputationServerInterface computationServer, Scanner userInputScanner) {
        System.out.print("Enter the first number: ");
        int firstNumber = userInputScanner.nextInt();
        System.out.print("Enter the second number: ");
        int secondNumber = userInputScanner.nextInt();

        try {
            int additionResult = computationServer.add(firstNumber, secondNumber);
            System.out.println("Result of Synchronous Addition: " + additionResult);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        System.out.print("Would you like to perform asynchronous addition as well? (yes/no): ");
        String asyncAdditionChoice = userInputScanner.next().toLowerCase();

        if (asyncAdditionChoice.equals("yes")) {
            System.out.println("Initiating asynchronous addition...");
            Thread asyncAdditionThread = new Thread(() -> {
                try {
                    int asyncAdditionResult = computationServer.add(firstNumber, secondNumber);
                    System.out.println("Result of Asynchronous Addition: " + asyncAdditionResult);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("Asynchronous addition request sent to the server.");
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            asyncAdditionThread.start();
        }
    }

    private static void performSorting(ComputationServerInterface computationServer, Scanner userInputScanner) {
        System.out.print("Enter the number of elements in the array: ");
        int arraySize = userInputScanner.nextInt();
        int[] arrayToSort = new int[arraySize];
        System.out.println("Enter the elements of the array:");
        for (int i = 0; i < arraySize; i++) {
            arrayToSort[i] = userInputScanner.nextInt();
        }

        try {
            int[] sortedArray = computationServer.sort(arrayToSort);
            System.out.println("Result of Synchronous Sorting: " + Arrays.toString(sortedArray));
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        System.out.print("Would you like to perform asynchronous sorting as well? (yes/no): ");
        String asyncSortingChoice = userInputScanner.next().toLowerCase();

        if (asyncSortingChoice.equals("yes")) {
            System.out.println("Initiating asynchronous sorting...");
            Thread asyncSortingThread = new Thread(() -> {
                try {
                    int[] asyncSortedArray = computationServer.sort(arrayToSort);
                    System.out.println("Result of Asynchronous Sorting: " + Arrays.toString(asyncSortedArray));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
            System.out.println("Asynchronous sorting request sent to the server.");
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            asyncSortingThread.start();
        }
    }
}
