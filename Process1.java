import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.Arrays;

public class Process1 {
    private static final int ID = 1;
    private static final int NUM_PROCESSES = 4;
    private static final int BASE_PORT = 8000;
    private static final ScalarClock scalarClock = new ScalarClock(ID);
    private static final VectorClock vectorClock = new VectorClock(ID, NUM_PROCESSES);
    private static final MatrixClock matrixClock = new MatrixClock(ID, NUM_PROCESSES);
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        try {
            serverSocket = new ServerSocket(BASE_PORT + ID);
            // Démarrer le thread pour accepter les connexions et recevoir les messages
            new Thread(() -> acceptConnections()).start();
            // Exécuter l'interaction avec l'utilisateur
            execute();
        } catch (IOException e) {
            System.err.println("Error starting Process " + ID + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            cleanup();
        }
    }

    private static void acceptConnections() {
        while (!serverSocket.isClosed()) {
            Socket socket = null;
            ObjectOutputStream out = null;
            ObjectInputStream in = null;
            try {
                socket = serverSocket.accept();
                System.out.println("Received connection attempt on port " + socket.getPort());
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
                // Recevoir l'ID du processus source
                int sourceId = in.readInt();
                if (sourceId >= 0 && sourceId < NUM_PROCESSES && sourceId != ID) {
                    // Recevoir le message
                    Object obj = in.readObject();
                    if (obj instanceof Message) {
                        Message message = (Message) obj;
                        // Afficher les horloges du message reçu et les horloges locales
                        System.out.println("Connection from Process " + sourceId);
                        System.out.println("Received Message Scalar Clock: " + message.scalarClock);
                        System.out.println("Received Message Vector Clock: " + Arrays.toString(message.vectorClock));
                        System.out.println("Received Message Matrix Clock:\n" + matrixToString(message.matrixClock));
                        // Mettre à jour les horloges locales
                        scalarClock.receiveEvent(message.scalarClock);
                        vectorClock.receiveEvent(message.vectorClock);
                        matrixClock.receiveEvent(message.matrixClock);
                    } else {
                        System.out.println("Received invalid message type from Process " + sourceId);
                    }
                } else {
                    System.out.println("Invalid source ID: " + sourceId + ", closing connection");
                }
            } catch (IOException | ClassNotFoundException e) {
                if (!serverSocket.isClosed()) {
                    System.err.println("Error processing connection: " + e.getMessage());
                }
            } finally {
                // Fermer les flux et le socket
                try {
                    if (out != null) out.close();
                    if (in != null) in.close();
                    if (socket != null && !socket.isClosed()) socket.close();
                } catch (IOException e) {
                    System.err.println("Error closing resources: " + e.getMessage());
                }
            }
        }
    }

    private static class Connection {
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;

        Connection(Socket socket, ObjectOutputStream out, ObjectInputStream in) {
            this.socket = socket;
            this.out = out;
            this.in = in;
        }
    }

    private static Connection connectToProcess(int targetId) throws IOException {
        if (targetId == ID || targetId < 0 || targetId >= NUM_PROCESSES) {
            throw new IllegalArgumentException("Invalid target process ID: " + targetId);
        }
        Socket socket = null;
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        try {
            System.out.println("Attempting to connect to Process " + targetId + " on port " + (BASE_PORT + targetId));
            socket = new Socket("localhost", BASE_PORT + targetId);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            // Envoyer l'ID du processus actuel au processus cible
            out.writeInt(ID);
            out.flush();
            System.out.println("Successfully connected to Process " + targetId);
            return new Connection(socket, out, in);
        } catch (IOException e) {
            // Fermer les ressources en cas d'erreur
            try {
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null && !socket.isClosed()) socket.close();
            } catch (IOException ex) {
                System.err.println("Error closing resources: " + ex.getMessage());
            }
            throw new IOException("Could not connect to Process " + targetId + ": " + e.getMessage());
        }
    }

    private static void execute() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Process " + ID + " started. Commands: 'local' for local event, 'send <process_id>' to send message, 'exit' to quit");

        while (true) {
            String input = scanner.nextLine().trim();
            try {
                if (input.equals("local")) {
                    scalarClock.localEvent();
                    vectorClock.localEvent();
                    matrixClock.localEvent();
                    /*System.out.println("Local event processed");
                    System.out.println("Local Scalar Clock: " + scalarClock.getClock());
                    System.out.println("Local Vector Clock: " + Arrays.toString(vectorClock.getClock()));
                    System.out.println("Local Matrix Clock:\n" + matrixToString(matrixClock.getClock()));*/
                } else if (input.startsWith("send ")) {
                    int target = Integer.parseInt(input.split(" ")[1]);
                    if (target == ID || target < 0 || target >= NUM_PROCESSES) {
                        System.out.println("Invalid target process ID: " + target);
                        continue;
                    }
                    try {
                        // Établir une nouvelle connexion
                        Connection conn = connectToProcess(target);
                        try {
                            // Créer et envoyer le message
                            Message message = new Message(
                                scalarClock.sendEvent(),
                                vectorClock.sendEvent(),
                                matrixClock.sendEvent(target),
                                ID
                            );
                            conn.out.writeObject(message);
                            conn.out.flush();
                            /*  Afficher les horloges du message envoyé et les horloges locales
                            System.out.println("Connection to Process " + target);
                            System.out.println("Sent Message Scalar Clock: " + message.scalarClock);
                            System.out.println("Sent Message Vector Clock: " + Arrays.toString(message.vectorClock));
                            System.out.println("Sent Message Matrix Clock:\n" + matrixToString(message.matrixClock));
                            System.out.println("Local Scalar Clock: " + scalarClock.getClock());
                            System.out.println("Local Vector Clock: " + Arrays.toString(vectorClock.getClock()));
                            System.out.println("Local Matrix Clock:\n" + matrixToString(matrixClock.getClock()));
                                    
                                    */
                            System.out.println("Message sent to Process " + target);
                        } finally {
                            // Fermer les flux et le socket après l'envoi
                            try {
                                if (conn.out != null) conn.out.close();
                                if (conn.in != null) conn.in.close();
                                if (conn.socket != null && !conn.socket.isClosed()) conn.socket.close();
                            } catch (IOException e) {
                                System.err.println("Error closing resources: " + e.getMessage());
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Failed to send message to Process " + target + ": " + e.getMessage());
                    }
                } else if (input.equals("exit")) {
                    break;
                } else {
                    System.out.println("Invalid command. Use 'local', 'send <process_id>', or 'exit'");
                }
            } catch (Exception e) {
                System.out.println("Error processing command: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static String matrixToString(int[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int[] row : matrix) {
            sb.append(Arrays.toString(row)).append("\n");
        }
        return sb.toString();
    }

    private static void cleanup() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Error during cleanup: " + e.getMessage());
        }
    }
}