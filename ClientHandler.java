import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ClientHandler implements Runnable {
    private static final int playerNumber = 10;
    private Socket client;
    private String name;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<ClientHandler> clients;
    private ArrayList<String> names;
    private ArrayList<Player> players;
    private ArrayList<String> roles = new ArrayList();
    private HashMap<String,Integer> votes;
    public String role;
    private int serverCount;
    private Player player;
    private Scanner ScannerIn;
    private int state;
    private int threadCheck;
    int time = 20000;


    public String getName() {
        return name;
    }


    private void addRoles() {
        roles.add("mafia");
        roles.add("godFather");
        roles.add("mafiaDoctor");
        roles.add("civilian");
        roles.add("therapist");
        roles.add("sniper");
        roles.add("detective");
        roles.add("mayor");
        roles.add("armor");
        roles.add("civilianDoctor");
//        Collections.shuffle(roles);
    }


    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> clients, ArrayList<String> names, int serverCount, ArrayList<Player> players,HashMap<String, Integer>votes) throws IOException {

        this.client = clientSocket;
        this.clients = clients;
        this.names = names;
        this.serverCount = serverCount;
        this.players = players;
        this.threadCheck = threadCheck;
        this.votes = votes;

        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        ScannerIn = new Scanner(client.getInputStream());
        out = new PrintWriter(client.getOutputStream(), true);

        addRoles();
    }


    @Override
    public void run() {
        try {
//            while (serverCount!=playerNumber){
//                TimeUnit.SECONDS.sleep(5);
//            }
//            addRoles();
            welcomeUser();
            giveRoles();
            if (names.size()==playerNumber)
                presentationNight();
            nightGame();
            int i=1;
            while (true) {
                for (int j=0 ; j<players.size() ; j++){
                    if (players.get(j).checkNight !=i){
                        TimeUnit.SECONDS.sleep(1);
                        j--;
                    }
                }
                chatRoom();
                voting();
                for (int j=0 ; j<players.size() ; j++){
                    if (players.get(j).checkVote !=i){
                        TimeUnit.SECONDS.sleep(1);
                        j--;
                    }
                }
//                mayor();
//                if (mayor().equals("1"))
                    votingResult();
//                else
//                    out.println("Mayor didn't validate your votes");
//                    out.println("  ");
                nightGame();
                i++;
            }
        } catch (IOException | InterruptedException e) {
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
        for (ClientHandler aClient : clients) {
            aClient.out.println(name + "-> " + msg);

        }
    }
    public void sendToAll(String msg) {
        for (ClientHandler aClient : clients) {
            aClient.out.println(msg);
        }
    }
    public void welcomeUser() {
        out.println("Submit your name");
        name = ScannerIn.nextLine();
        if (name == null) {
            return;
        }
        synchronized (name) {
            while (names.contains(name)) {
                out.println("Username already taken, please enter another name");
                name = ScannerIn.nextLine();
            }
            names.add(name);
        }
        out.println("NAME ACCEPTED " + name);
        outToAll(name + " has joined the chat");
    }
    private void giveRoles() {
        this.role = roles.get(serverCount);
        out.println(name + " your role is " + this.role);
        Player player = new Player(name, this.role);
        this.player = player;
        players.add(player);
    }
    public synchronized void start() {
        out.println("Type start for the game to begin");
        String keyword = ScannerIn.nextLine();
        while (!keyword.equals("start")) {
            out.println("BEzan start dige");
            keyword = ScannerIn.nextLine();
        }
    }
    public String findNameByRole(String role) {
        for (Player player : players) {
            if (player.getRole().equals(role))
                return player.getName();
        }
        return "Player not found";
    }
    public String findRoleByName(String name) {
        for (Player player : players) {
            if (player.getName().equals(name))
                return player.getRole();
        }
        return "Not found";
    }
    public void presentMafia() {
        for (ClientHandler client : clients) {
            if (client.player.getRole().equals("mafia")) {
                client.out.println(findNameByRole("godFather") + " is our godfather");
                client.out.println(findNameByRole("mafiaDoctor") + " is our mafiaDoctor");
            } else if (client.player.getRole().equals("godFather")) {
                client.out.println(findNameByRole("mafia") + " is our mafia");
                client.out.println(findNameByRole("mafiaDoctor") + " is our mafiaDoctor");
            } else if (client.player.getRole().equals("mafiaDoctor")) {
                client.out.println(findNameByRole("godFather") + " is our godfather");
                client.out.println(findNameByRole("mafia") + " is our mafia");
            }
        }
    }
    public void presentMayorCivilianDoctor() {
        for (ClientHandler client : clients) {
            if (client.player.getRole().equals("civilianDoctor")) {
                client.out.println(findNameByRole("mayor") + " is our mayor");
            } else if (client.player.getRole().equals("mayor")) {
                client.out.println(findNameByRole("civilianDoctor") + " is our civilianDoctor");
            }
        }
    }
    public void presentAllRoles() {
        for (ClientHandler client : clients) {
            client.out.println("Welcome " + client.player.getRole());
            client.out.println("\n---------------------------------------\n");
        }
    }
    public void presentationNight() {
        sendToAll("\n---------------------------------------\n");
        sendToAll("Introduction night has been started");
        presentAllRoles();
        presentMafia();
        presentMayorCivilianDoctor();
    }
    public void mafia() throws IOException {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.player.getRole().equals("mafia")) {
                clientHandler.out.println("Choose a player to kill");
                for (Player player : players){
                    if (!player.getRole().equals("mafia")&&!player.getRole().equals("godFather")&&!player.getRole().equals("mafiaDoctor")){
                        clientHandler.out.println(player.getName());
                    }
                }
                String name = in.readLine();
                while(findRoleByName(name).equals("mafia")||findRoleByName(name).equals("godFather")||findRoleByName(name).equals("mafiaDoctor")){
                    clientHandler.out.println("Invalid name, please choose from civilian team");
                    name = in.readLine();
                }
                clientHandler.out.println("You killed "+name);
                for (ClientHandler clientHandler1 : clients) {
                    if (clientHandler1.player.getRole().equals("godFather")) {
                        clientHandler1.out.println("mafia's vote is " + name);
                    }
                }
            }
        }
    }
    public String mafiaDoctor() throws IOException {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.player.getRole().equals("mafiaDoctor")) {
                clientHandler.out.println("Choose a player to kill");
                for (Player player : players){
                    if (!player.getRole().equals("mafia")&&!player.getRole().equals("godFather")&&!player.getRole().equals("mafiaDoctor")){
                        clientHandler.out.println(player.getName());
                    }
                }
                String name = in.readLine();
                while(findRoleByName(name).equals("mafia")||findRoleByName(name).equals("godFather")||findRoleByName(name).equals("mafiaDoctor")){
                    clientHandler.out.println("Invalid name, please choose from civilian team");
                    name = in.readLine();
                }
                clientHandler.out.println("You killed "+name);
                for (ClientHandler clientHandler1 : clients) {
                    if (clientHandler1.player.getRole().equals("godFather")) {
                        clientHandler1.out.println("mafiaDoctor's vote is " + name);
                    }
                }
                //treatment
                clientHandler.out.println("Who do you wanna treat? (Choose from mafia team)");
                clientHandler.out.println("Please enter a name");
                name = in.readLine();
//                while(!(findRoleByName(name).equals("mafia")||findRoleByName(name).equals("godFather")||findRoleByName(name).equals("mafiaDoctor"))){
//                    clientHandler.out.println("You can only choose from mafia team, try again");
//                    name = in.readLine();
//                }
                clientHandler.out.println("You treated "+name);
                return name;
            }
        }
        return "not found";
    }
    public void godFather() throws IOException {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.player.getRole().equals("godFather")) {
                clientHandler.out.println("Who do you wanna kill?");
                for (Player p : players) {
                    if (!p.getRole().equals("mafia") && !p.getRole().equals("mafiaDoctor") && !p.getRole().equals("godFather")) {
                        clientHandler.out.println(p.getName());
                    }
                }
                clientHandler.out.println("Please enter a name:");
                String name = in.readLine();
                while(findRoleByName(name).equals("mafia")||findRoleByName(name).equals("godFather")||findRoleByName(name).equals("mafiaDoctor")){
                    clientHandler.out.println("Invalid name, please choose from civilian team");
                    name = in.readLine();
                }
                clientHandler.out.println("You killed "+name);
                for (Player player : players) {
                    if (player.getName().equals(name)) {
                        player.setAlive(false);
                    }
                }
            }
        }
    }
    public void sniper() throws IOException {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.player.getRole().equals("sniper")) {
                clientHandler.out.println("Do you wanna use your power?");
                clientHandler.out.println("Enter a number");
                clientHandler.out.println("1- yes");
                clientHandler.out.println("2- no");
                String number =in.readLine();
                if (number.equals("1")) {
                    clientHandler.out.println("Choose a player to shoot");
                    clientHandler.out.println("Please enter a name");
                    String name = in.readLine();
                    clientHandler.out.println("You shot "+name);
                    if (!findRoleByName(name).equals("mafia") || !findRoleByName(name).equals("godFather") || !findRoleByName(name).equals("mafia")) {
                        clientHandler.player.setAlive(false);
                    }
                    else {
                        for (ClientHandler clientHandler1 : clients) {
                            if (clientHandler1.player.getName().equals(name)) {
                                clientHandler.player.setAlive(false);
                            }
                        }
                    }
                }
                else {
                    while (!number.equals("1")&&!number.equals("2")){
                        clientHandler.out.println("Invalid answer");
                        clientHandler.out.println("Please enter 1 or 2");
                        number = in.readLine();
                    }
                }
            }
        }
    }
    public String civilianDoctor() throws IOException {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.player.getRole().equals("civilianDoctor")) {
                out.println("Who do you wanna treat?");
                for (Player p : players) {
                    if (!p.getRole().equals("civilianDoctor")) {
                        out.println(p.getName());
                    }
                }
                out.println("Please enter a name");
                String name = in.readLine();
                out.println("You treated "+name);
            }
        }
        return name;
    }
    public String therapist() throws IOException {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.player.getRole().equals("therapist")) {
                clientHandler.out.println("Choose a player to stop from talking");
                clientHandler.out.println("Player name's are listed here: ");
                for (Player p : players) {
                    if (!p.getRole().equals("therapist")) {
                        clientHandler.out.println(p.getName());
                    }
                }
                String player = in.readLine();
                clientHandler.out.println("You stopped "+player+ " from talking");
                for (ClientHandler clientHandler1 : clients) {
                    if (clientHandler1.player.getName().equals(player)) {
                        return clientHandler1.player.getName();
                    }
                }
            }
        }
        return "not found";
    }
    public void detective() throws IOException {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.player.getRole().equals("detective")) {
                clientHandler.out.println("Enter a player's name to know its role");
                for (Player player : players){
                    clientHandler.out.println(player.getName());
                }
                String name = in.readLine();
                if (findRoleByName(name).equals("mafia")||findRoleByName(name).equals("mafiaDoctor")){
                    clientHandler.out.println(name + " is in mafia team");
                }
                else {
                    clientHandler.out.println(name + " is in civilian team");
                }
            }
        }
    }
    public void armor() throws IOException {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.player.getRole().equals("armor")) {
                clientHandler.out.println("Do you wanna use your power?");
                clientHandler.out.println("Enter a number");
                clientHandler.out.println("1- yes");
                clientHandler.out.println("2- no");
                String number = in.readLine();
                while (!number.equals("1") && !number.equals("2")) {
                    clientHandler.out.println("Invalid answer");
                    clientHandler.out.println("Please enter 1 or 2");
                    number = in.readLine();
                }
                if(number.equals("1")){
                    for (Player player : players){
                        if (!player.isAlive()){
                            clientHandler.out.println(player.getName()+" is dead");
                        }
                        else {
                            clientHandler.out.println("No one has died yet");
                        }
                    }
                }
            }
        }
    }
    public String mayor() throws IOException {
        for (ClientHandler clientHandler : clients) {
            if (clientHandler.player.getRole().equals("mayor")) {
                clientHandler.out.println("Do you validate players' votes?");
                clientHandler.out.println("Enter a number");
                clientHandler.out.println("1- yes");
                clientHandler.out.println("2- no");
                String number = in.readLine();
                while (!number.equals("1") && !number.equals("2")) {
                    clientHandler.out.println("Invalid answer");
                    clientHandler.out.println("Please enter 1 or 2");
                    number = in.readLine();
                }
                return number;
            }
        }
        return "not found";
    }
    public boolean isContinued(){
        int mafiaCount=0;
        int civilianCount=0;
        for(Player player: players){
            if(player.isAlive()){
                if (player.getRole().equals("mafiaDoctor")||player.getRole().equals("godFather")||player.getRole().equals("mafia")){
                    mafiaCount++;
                }
                else {
                    civilianCount++;
                }
            }
        }
        if(mafiaCount>=civilianCount || mafiaCount==0){
            return false;
        }
        return true;
    }
    private void nightGame() throws IOException, InterruptedException {
        while (names.size()!=playerNumber){
            TimeUnit.SECONDS.sleep(1);
        }
        if (role.equals("mafia")) {
            mafia();
            player.checkNight++;
            return;
        }
        if (role.equals("godFather")) {
            godFather();
            player.checkNight++;
            return;
        }
        if (role.equals("sniper")) {
            sniper();
            player.checkNight++;
            return;
        }
        if (role.equals("mafiaDoctor")) {
            mafiaDoctor();
            player.checkNight++;
            return;
        }
        if (role.equals("civilianDoctor")) {
            civilianDoctor();
            player.checkNight++;
            return;
        }
        if (role.equals("detective")) {
            detective();
            player.checkNight++;
            return;
        }
        if (role.equals("armor")) {
            armor();
            player.checkNight++;
            return;
        }
        if (role.equals("therapist")) {
            sendToAll(therapist()+" can't talk tomorrow");
            player.checkNight++;
            return;
        }
        if (role.equals("mayor")){
            player.checkNight++;
            return;
        }
        if (role.equals("civilian")){
            player.checkNight++;
            return;
        }
    }
    public void chatRoom() throws InterruptedException {
        out.println("\n-----------------------------\n");
        out.println("Chat room has started...");
        out.println("All players have 5 minutes to chat...");
        long timeS = System.currentTimeMillis();
        long timeF = System.currentTimeMillis();
        while (timeF - timeS < time) {
            if(!player.isMuted()){
                try {
                    String request = in.readLine();
                    outToAll(request);
                    timeF = System.currentTimeMillis();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else{
                out.println("Sorry "+ player.getName() +" you can't talk today.");
                TimeUnit.SECONDS.sleep(300);
            }
        }
    }
    public void nightReport(){
        for (Player player : players){
            if(!player.isAlive()){
                out.println(player.getName()+" is dead.");
                players.remove(player);
            }
        }
    }
    public void voting() throws IOException {
        out.println("Vote a player out");
        out.println("Please type a name:");
        String name = in.readLine();
        for (Player player : players) {
            if (player.getName().equals(name)) {
                player.vote++;
                votes.put(name,player.getVote());
            }
        }
        player.checkVote++;
    }
    public void votingResult(){
        Map.Entry<String, Integer> maxEntry = null;
        for (Map.Entry<String, Integer> entry : votes.entrySet())
        {
            if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0)
            {
                maxEntry = entry;
            }
        }
        out.println("Players voted out "+maxEntry+" votes");
    }


























}