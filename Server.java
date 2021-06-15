import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server{

    private static final int PORT = 8181;
    private static final int playerNumber = 3;


    private static ArrayList<ClientHandler>clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(playerNumber);
    private static ArrayList<String>names = new ArrayList<>();
    private ArrayList<Player>players = new ArrayList<>();
    private static int serverCount = 0;
    public int threadCheck = 0;





    public void execute() throws IOException, InterruptedException {
        System.out.println("Server is running...");
        ServerSocket listener = new ServerSocket(PORT);

        while (serverCount < playerNumber) {
            System.out.println("[server] waiting for client connection");

            Socket client = listener.accept();

            System.out.println("[server] connected to client!");

            ClientHandler clientThread = new ClientHandler(client, clients, names, serverCount, players, threadCheck);

            clients.add(clientThread);
            serverCount++;
            pool.execute(clientThread);
        }



        GameHandler gameHandler = new GameHandler(this);
        gameHandler.execute();



    }
    public static void main (String[]args) throws IOException, InterruptedException {
        Server server = new Server();
        server.execute();
    }
}