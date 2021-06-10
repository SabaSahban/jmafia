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
    private String role;
    private boolean isAlive;
    private int serverCount;
    private int playerNumbers;
    private String [] roles = {"mafia", "godFather", "mafiaDoctor", "civilian", "civilianDoctor", "sniper", "therapist", "detective", "mayor", "armor"};






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
    }


    @Override
    public void run() {
        try {

            System.out.println("counter is"+serverCount);
            if (serverCount<=playerNumbers){
                welcomeUser();
            }
            else{
                start();
                giveRoles();
            }









            while (true) {
                try {
                    String request = in.readLine();
                    outToAll(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out.println("salam");
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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

        out.println("NAME ACCEPTED "+name);
        outToAll(" has joined the chat");
    }
    private void shuffleArray(){
        List<String> strList = Arrays.asList(roles);
        Collections.shuffle(strList);
        roles = strList.toArray(new String[strList.size()]);
    }
    private void giveRoles(){
        int i=0;
        shuffleArray();
            this.role = roles[i];
            out.println(name + " your role is " + this.role);
            i++;

    }
    private void start() throws IOException {
        out.println("Enter keyword 'start' for the game to begin ");
        String keyword = in.readLine();
        while (!keyword.equals("start")){
            out.println("Please enter 'start' for the game to begin");
             keyword = in.readLine();
        }
    }





}
