import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server{

    private static final int PORT = 9090;

    private static ArrayList<ClientHandler>clients = new ArrayList<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(4);
    private static ArrayList<String>names = new ArrayList<>();
    private ArrayList<Player>players = new ArrayList<>();
    private static int serverCount = 0;
    public int threadCheck = 0;





    public void execute() throws IOException, InterruptedException {
        //serverSocket object listens for client connections
        System.out.println("Server is running...");
        ServerSocket listener = new ServerSocket(PORT);

        while(serverCount<3) {
            System.out.println("[server] waiting for client connection");

            //makes listener object make a connection
            //client corresponds to the connection
            Socket client = listener.accept();

            System.out.println("[server] connected to client!");

            ClientHandler clientThread = new ClientHandler(client, clients, names , serverCount, players, threadCheck);

            clients.add(clientThread);
            serverCount++;
            pool.execute(clientThread);
//            clientThread.start();
        }
//        pool.shutdown();
//        pool.awaitTermination(100, TimeUnit.DAYS);


        GameHandler gameHandler = new GameHandler(this);
        gameHandler.execute();


//        sendToUser("mafia", "shoma mafia hastid");

        }





//    public String  getUserName(String role){
//        for (ClientHandler client : clients){
//            if (client.getRole().equals(role)){
//                return client.getName();
//            }
//        return "wrng";
//    }
//   public void notifyMafia(){
////        sendToUser("mafia", );
//   }








    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server();
        server.execute();
    }


}