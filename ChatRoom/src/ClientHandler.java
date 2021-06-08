import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable{

    private Socket client;
    private String name;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<ClientHandler> clients;
    private ArrayList<String> names;
    private ArrayList<String>roles;
    private String role;





    private Scanner ScannerIn;

    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler>clients,ArrayList<String> names ) throws IOException {
        this.client = clientSocket;
        this.clients = clients;
        this.names = names;

        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        ScannerIn = new Scanner(client.getInputStream());
        out = new PrintWriter(client.getOutputStream(), true);

    }


    @Override
    public void run() {

        try {

            welcomeUser();
//            RoleToAll();
            while (true) {
                try {
                    String request = in.readLine();
                    outToAll(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out.println("salam");
            }
        }finally {
            System.out.println("[server] did its job");

            //closing our socket
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

//    private void RoleToAll() {
//        Role.addRoles();
//
//        for(ClientHandler aClient : clients){
//
//            aClient.out.println("Hey " +name+" -> " +"your role is "+role);
//
//        }
//    }
    private void outToAll(String msg) {
        for(ClientHandler aClient : clients){

            aClient.out.println(name +"-> " + msg);

        }
    }
    private void welcomeUser(){

        out.println("Submit your name");
        name = ScannerIn.nextLine();

        if(name == null){
            return;
        }

        synchronized (name){
            while (names.contains(name)){
                out.println("Username already taken, please enter another name");
                name = ScannerIn.nextLine();
            }
            names.add(name);
        }
      ;
        out.println("NAME ACCEPTED "+name);
        outToAll(" has joined the chat");
    }
}
