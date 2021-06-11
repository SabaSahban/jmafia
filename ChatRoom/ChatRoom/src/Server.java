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
    private static int serverCount = 0;


    public static int getServerCount() {
        return serverCount;
    }


    public void execute() throws IOException {
        //serverSocket object listens for client connections
        System.out.println("Server is running...");
        ServerSocket listener = new ServerSocket(PORT);

        while(serverCount<3) {
            System.out.println("[server] waiting for client connection");

            //makes listener object make a connection
            //client corresponds to the connection
            Socket client = listener.accept();

            System.out.println("[server] connected to client!");

            ClientHandler clientThread = new ClientHandler(client, clients, names , serverCount);
            clients.add(clientThread);
            serverCount++;
            pool.execute(clientThread);
        }

        GameHandler gameHandler = new GameHandler(this);
        gameHandler.execute();

        }



    public void sendToAll(String msg){
        for(ClientHandler client : clients){
            client.sendMessage(msg);
        }
    }

    public void sendToUser(String user, String msg){

        for (ClientHandler client : clients){
            if (client.getName().equals("user")){
                System.out.println(msg);
            }
        }
    }








    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.execute();
    }


}