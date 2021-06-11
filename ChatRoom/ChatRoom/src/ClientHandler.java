import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable{

    private Socket client;
    private String name;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<ClientHandler> clients;
    private ArrayList<String> names;
    private ArrayList<String> roles = new ArrayList();
    private String role;
    private boolean isAlive;
    private int serverCount;
    private int playerNumbers;
//    private String [] roles = {"mafia", "godFather", "godFather", "civilian", "civilianDoctor", "sniper", "therapist", "detective", "mayor", "armor"};


    private void addRoles(){
        roles.add("mafia");
        roles.add("godFather");
        roles.add("godFather");
        roles.add("civilian");
        roles.add("civilianDoctor");
        roles.add("sniper");
        roles.add("therapist");
        roles.add("detective");
        roles.add("mayor");
        roles.add("armor");
        //roles are randomly given to users
        Collections.shuffle(roles);
    }

    public String getName() {
        return name;
    }

    private Scanner ScannerIn;

    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler>clients,ArrayList<String> names, int serverCount ) throws IOException {
        this.client = clientSocket;
        this.clients = clients;
        this.names = names;
        playerNumbers = 2;
        this.serverCount =serverCount;

        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        ScannerIn = new Scanner(client.getInputStream());
        out = new PrintWriter(client.getOutputStream(), true);

        addRoles();
    }


    @Override
    public void run() {
        if(serverCount<3)
            welcomeUser();

        start();

//        try {
////            System.out.println("counter is"+serverCount);
////            if (serverCount<=playerNumbers){
////                welcomeUser();
////            }
////
////                giveRoles();
//            welcomeUser();
//
//            while (true) {
//                try {
//                    String request = in.readLine();
//                    outToAll(request);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                out.println("salam");
//            }
//
//
//        }
//        finally {
//            System.out.println("[server] did its job");
//            //closing our socket
//            out.close();
//            try {
//                in.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    }

    private void outToAll(String msg) {
        for(ClientHandler aClient : clients){

            aClient.out.println(name +"-> " + msg);

        }
    }
    public void sendMessage(String msg){
        out.println(msg);
    }
    public String readMessage(){
         String msg = ScannerIn.nextLine();
         return msg;
    }
    public void welcomeUser(){
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
        out.println("NAME ACCEPTED "+name);
        outToAll(name +" has joined the chat");
    }



    private void giveRoles(){
            this.role = roles.get(serverCount);
            out.println(name + " your role is " + this.role);
    }
//    private void start() throws IOException {
//        out.println("Enter keyword 'start' for the game to begin ");
//        String keyword = in.readLine();
//        while (!keyword.equals("start")){
//            out.println("Please enter 'start' for the game to begin");
//             keyword = in.readLine();
//        }
//    }

public void start(){
        out.println("Type start for the game to begin");
        String keyword= ScannerIn.nextLine();
        while (!keyword.equals("start")){
            out.println("BEzan start dige");
            keyword = ScannerIn.nextLine();
        }
        giveRoles();


}






}
