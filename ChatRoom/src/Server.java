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

    private static final int PORT = 9090;

    private static ArrayList<ClientHandler>clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(4);
    private static ArrayList<String>names = new ArrayList<>();
    private static ArrayList<Player>players = new ArrayList<>();

//    public static ArrayList<String> getNames() {
//        return names;
//    }

    public static void main(String[] args) throws IOException {
        //serverSocket object listens for client connections
        System.out.println("Server is running...");
        ServerSocket listener = new ServerSocket(PORT);

        while(true) {
            System.out.println("[server] waiting for client connection");

            //makes listener object make a connection
            //client corresponds to the connection
            Socket client = listener.accept();

            System.out.println("[server] connected to client!");
            ClientHandler clientThread = new ClientHandler(client, clients, names);
            clients.add(clientThread);
            pool.execute(clientThread);
        }

    }
}