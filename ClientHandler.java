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
    private ArrayList<Player> players;
    private ArrayList<String> roles = new ArrayList();
    public String role;
    private boolean isAlive;
    private int serverCount;
    private Player player;
    private Scanner ScannerIn;
    private int state;
    private int threadCheck=0;




    public String getName() {
        return name;
    }


    private void addRoles(){

        if(serverCount==1) Collections.shuffle(roles);
        roles.add("mafia");
        roles.add("godFather");
        roles.add("civilian");
        roles.add("civilianDoctor");
        roles.add("sniper");
        roles.add("therapist");
        roles.add("detective");
        roles.add("mayor");
        roles.add("armor");
        roles.add("mafiaDoctor");
    }



    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler>clients,ArrayList<String> names, int serverCount, ArrayList<Player> players, int threadCheck) throws IOException {

        this.client = clientSocket;
        this.clients = clients;
        this.names = names;
        this.serverCount =serverCount;
        this.players = players;
        this.threadCheck = threadCheck;

        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        ScannerIn = new Scanner(client.getInputStream());
        out = new PrintWriter(client.getOutputStream(), true);

        addRoles();
    }




    @Override
    public void run() {
        welcomeUser();
        giveRoles();

        if (names.size() == 3) {
            presentationNight();
//            try {
//                dayChat();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }




        try {
            while (true) {
                try {
                    String request = in.readLine();
                    outToAll(request);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                out.println("salam");
            }
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
    public void sendToAll(String msg){
        for(ClientHandler aClient : clients){
            aClient.out.println(msg);
        }
    }

    public  void welcomeUser(){
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
    private  void giveRoles(){
        this.role = roles.get(serverCount);
        out.println(name + " your role is " + this.role);
        Player player = new Player(name , this.role);
        this.player = player;
        players.add(player);
    }

    public synchronized void start(){
        out.println("Type start for the game to begin");
        String keyword= ScannerIn.nextLine();
        while (!keyword.equals("start")){
            out.println("BEzan start dige");
            keyword = ScannerIn.nextLine();
        }
    }

    public String findNameByRole(String role){
        for(Player player : players){
            if(player.getRole().equals(role))
                return player.getName();
        }
        return "Player not found";
    }

    public void presentMafia() {
        for( ClientHandler client : clients){
            if(client.player.getRole().equals("mafia")){
                client.out.println(findNameByRole("godFather")+" is our godfather");
                client.out.println(findNameByRole("mafiaDoctor")+" is our mafiaDoctor");
            }
            else if(client.player.getRole().equals("godFather")){
                client.out.println(findNameByRole("mafia")+" is our mafia");
                client.out.println(findNameByRole("mafiaDoctor")+" is our mafiaDoctor");
            }
            else if(client.player.getRole().equals("mafiaDoctor")){
                client.out.println(findNameByRole("godFather")+" is our godfather");
                client.out.println(findNameByRole("mafia")+" is ou r mafia");
            }
        }
    }
    public void presentMayorCivilianDoctor() {
        for (ClientHandler client : clients) {
            if(client.player.getRole().equals("civilianDoctor")) {
                client.out.println(findNameByRole("mayor")+" is our mayor");
            }
            else if(client.player.getRole().equals("mayor")) {
                client.out.println(findNameByRole("civilianDoctor")+" is our civilianDoctor");
            }
        }
    }
    public void presentAllRoles() {
        for (ClientHandler client : clients) {
            client.out.println("Welcome "+client.player.getRole());
        }
    }
    public void presentationNight(){
        sendToAll("\n---------------------------------------\n");
        sendToAll("Introduction night has been started");
        presentAllRoles();
        presentMafia();
        presentMayorCivilianDoctor();
    }
    public void dayChat() throws IOException {
        sendToAll("Type 'ready' if you're ready to vote");
        while(true){
            String request = in.readLine();
            if(request.equals("ready"))
                break;
            outToAll(request);
        }
    }

    public void voting() throws IOException {
        for (ClientHandler clientHandler : clients){
            clientHandler.out.println("Vote a player out");
            clientHandler.out.println("Please type a name:");
            String name = in.readLine();
            for(Player player:players){
                if (player.getName().equals(name)){
                    player.vote++;
                }
            }
        }
    }
    public void mafia() throws IOException {
        for(ClientHandler clientHandler : clients){
            if(clientHandler.player.getRole().equals("mafia")||clientHandler.player.getRole().equals("mafiaDoctor")){
                clientHandler.out.println("Choose a player to kill");
                String name = in.readLine();
                for (ClientHandler clientHandler1:clients){
                    if (clientHandler1.player.getRole().equals("godFather")){
                        clientHandler1.out.println(clientHandler.player.getRole()+"'s vote is "+name);
                    }
                }
            }
        }
    }
    public void mafiaDoctor() throws IOException {
        for(ClientHandler clientHandler : clients){
            if(clientHandler.player.getRole().equals("mafiaDoctor")){
                clientHandler.out.println("Who do you wanna treat? (Choose from mafia team)");
                clientHandler.out.println("Please enter a name");
                String name = in.readLine();
                for(Player player:players){
                    if (player.getName().equals(name)){
                        player.setAlive(true);
                    }
                }
            }
        }
    }
    public void godFather() throws IOException {
        for (ClientHandler clientHandler : clients) {
            if(clientHandler.player.getRole().equals("godFather")){
                System.out.println("Who do you wanna kill?");
                System.out.println("Please enter a name:");
                String name = in.readLine();
                for(Player player:players){
                    if (player.getName().equals(name)){
                        player.setAlive(false);
                    }
                }
            }
        }
    }
    public String therapist() throws IOException {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.player.getRole().equals("therapist")) {
                clientHandler.out.println("Choose a player to stop from talking");
                String player = in.readLine();
                for (ClientHandler clientHandler1 : clients) {
                    if (clientHandler1.player.getName().equals(player)) {
                        return clientHandler1.player.getName();
                    }
                }
            }
        }
        return "not found";
    }


        }




}
