import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * The type Server.
 */
public class Server{

    private static final int PORT = 9696;
    private static final int playerNumber = 10;


    private static ArrayList<ClientHandler>clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(playerNumber);
    private static ArrayList<String>names = new ArrayList<>();
    private ArrayList<Player>players = new ArrayList<>();
    private ArrayList<String>roles = new ArrayList<>();
    private HashMap<String,Integer> votes = new HashMap<>();
    private static int serverCount = 0;


    /**
     * Execute.
     *
     * @throws IOException the io exception
     */
    public void execute() throws IOException{
        System.out.println("Server is running...");
        ServerSocket listener = new ServerSocket(PORT);

        while (serverCount < playerNumber) {
            System.out.println("[server] waiting for client connection");

            Socket client = listener.accept();

            System.out.println("[server] connected to client!");

            ClientHandler clientThread = new ClientHandler(client, clients, names, serverCount, players, votes, roles);

            roles.add("mafia");
            roles.add("mayor");
            roles.add("godFather");
            roles.add("mafiaDoctor");
            roles.add("civilian");
            roles.add("therapist");
            roles.add("sniper");
            roles.add("detective");
            roles.add("armor");
            roles.add("civilianDoctor");
            if (serverCount==0) Collections.shuffle(roles);
            clients.add(clientThread);
            serverCount++;
            pool.execute(clientThread);
        }
    }

    /**
     * Main.
     *
     * @param args the args
     * @throws IOException the io exception
     */
    public static void main (String[]args) throws IOException{
        Server server = new Server();
        server.execute();
    }
}