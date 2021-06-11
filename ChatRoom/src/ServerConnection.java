import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection implements Runnable{

    private Socket server;
    private BufferedReader in;
    private PrintWriter out;

    public ServerConnection(Socket s) throws IOException {
        server = s;
        in = new BufferedReader(new InputStreamReader(server.getInputStream()));

    }

    @Override
    public void run() {
            String serverResponse = null;
            try {
//                while(true){
//                    System.out.println("Please type 'start' for the game to begin ");
//                }
                while (true){
                   serverResponse = in.readLine();
                   if(serverResponse ==null)break;
                   System.out.println("[Server]"+ serverResponse);
               }
            }

            catch (IOException e) {
                e.printStackTrace();
            }finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

    public void start(){
        System.out.println();
    }













}
