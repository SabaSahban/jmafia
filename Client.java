import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * The type Client.
 */
public class Client {

    private static final String SERVER_IP = "127.0.0.1";

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     * @throws IOException the io exception
     */
    public static void main(String[] args)throws IOException {


        Scanner scanner = new Scanner(System.in);
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter a port");
        int portNumber = scanner.nextInt();
        String start= "";
        while (!start.equals("start")){
            System.out.println("Enter start");
            start = sc.nextLine();
        }
        while (portNumber!=9696){
            System.out.println("Enter a valid port");
            portNumber = scanner.nextInt();
        }

        Socket socket = new Socket(SERVER_IP , 9696);
        ServerConnection serverConn = new ServerConnection(socket);
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        new Thread(serverConn).start();

        while(true) {

            System.out.println(">");
            String command = keyboard.readLine();
            if(command.equals("exit")) break;
            out.println(command);

        }
        socket.close();
        System.exit(0);
    }

}