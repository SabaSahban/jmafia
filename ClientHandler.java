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
/**
 * The type Client handler.
 */
public class ClientHandler implements Runnable {
    private static final int playerNumber = 10;
    private Socket client;
    private String name;
    private BufferedReader in;
    private PrintWriter out;
    private ArrayList<ClientHandler> clients;
    private ArrayList<String> names;
    private ArrayList<Player> players;
    private ArrayList<String> roles;
    private HashMap<String,Integer> votes;
    /**
     * The Role.
     */
    public String role;
    private int serverCount;
    private Player player;
    private Scanner ScannerIn;
    private int time = 20000;


    /**
     * Instantiates a new Client handler.
     *
     * @param clientSocket the client socket
     * @param clients      the clients
     * @param names        the names
     * @param serverCount  the server count
     * @param players      the players
     * @param votes        the votes
     * @param roles        the roles
     * @throws IOException the io exception
     */
    public ClientHandler(Socket clientSocket, ArrayList<ClientHandler> clients, ArrayList<String> names, int serverCount, ArrayList<Player> players,HashMap<String, Integer>votes, ArrayList<String>roles) throws IOException {

        this.client = clientSocket;
        this.clients = clients;
        this.names = names;
        this.serverCount = serverCount;
        this.players = players;
        this.votes = votes;
        this.roles = roles;

        in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        ScannerIn = new Scanner(client.getInputStream());
        out = new PrintWriter(client.getOutputStream(), true);
    }

    /**
     * runs each thread
     */
    @Override
    public void run() {
        try {
            welcomeUser();
            giveRoles();

            if (names.size()==playerNumber)
                presentationNight();

            while (names.size()!=playerNumber){
                TimeUnit.SECONDS.sleep(2);
            }

            int i=1;
            nightGame();
            while (isContinued()) {
                for (int j=0 ; j<players.size() ; j++){
                    if (players.get(j).checkNight !=i){
                        TimeUnit.SECONDS.sleep(1);
                        j--;
                    }
                }

                out.println("Day has started");
                chatRoom();


                nightReport();

                if (!player.isAlive()){
                    outToAll(player.getName() + " left us...");
                    out.println("Type exit");
                    players.remove(player);
                    break;
                }



                voting();
                for (int j=0 ; j<players.size() ; j++){
                    if (players.get(j).checkVote !=i){
                        TimeUnit.SECONDS.sleep(1);
                        j--;
                    }
                }
                if (mayor())
                    votingResult();
                else sendToAll("Mayor didn't validate");
                out.println("Do you wanna continue?, type yes or no");
                String answer = in.readLine();
                if (answer.equals("no")){
                    out.println("Please type exit");
                    break;
                }
                nightGame();
                i++;
            }


        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        } finally {
            System.out.println("[server] did its job");
            out.close();
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    //sends message to all

    private void outToAll(String msg) {
        for (ClientHandler aClient : clients) {
            aClient.out.println(name + "-> " + msg);

        }
    }
    private void sendToAll(String msg) {
        for (ClientHandler aClient : clients) {
            aClient.out.println(msg);
        }
    }


    //gives username and roles

    private void welcomeUser() {
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

    //finds roles by name and names by role

    private String findNameByRole(String role) {
        for (Player player : players) {
            if (player.getRole().equals(role))
                return player.getName();
        }
        return "Player not found";
    }
    private String findRoleByName(String name) {
        for (Player player : players) {
            if (player.getName().equals(name))
                return player.getRole();
        }
        return "Not found";
    }

    //introduction night

    private void presentMafia() {
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
    private void presentMayorCivilianDoctor() {
        for (ClientHandler client : clients) {
            if (client.player.getRole().equals("civilianDoctor")) {
                client.out.println(findNameByRole("mayor") + " is our mayor");
            } else if (client.player.getRole().equals("mayor")) {
                client.out.println(findNameByRole("civilianDoctor") + " is our civilianDoctor");
            }
        }
    }
    private void presentAllRoles() {
        for (ClientHandler client : clients) {
            client.out.println("Welcome " + client.player.getRole());
            client.out.println("\n---------------------------------------\n");
        }
    }
    private void presentationNight() {
        sendToAll("\n---------------------------------------\n");
        sendToAll("Introduction night has been started");
        presentAllRoles();
        presentMafia();
        presentMayorCivilianDoctor();
    }

    //different roles methods at night mode

    private void mafia() throws IOException {
            if (player.getRole().equals("mafia")) {
                out.println("Choose a player to kill");
                for (Player player : players){
                    if (!player.getRole().equals("mafia")&&!player.getRole().equals("godFather")&&!player.getRole().equals("mafiaDoctor")){
                        out.println(player.getName());
                    }
                }
                String name = in.readLine();
                while(findRoleByName(name).equals("mafia")||findRoleByName(name).equals("godFather")||findRoleByName(name).equals("mafiaDoctor")){
                    out.println("Invalid name, please choose from civilian team");
                    name = in.readLine();
                }
                out.println("You killed "+name);
                for (ClientHandler clientHandler : clients) {
                    if (clientHandler.player.getRole().equals("godFather")) {
                        clientHandler.out.println("mafia's vote is " + name);
                    }
                }
            }
    }
    private void detective() throws IOException {
        if (player.getRole().equals("detective")) {
            out.println("Enter a player's name to know its team");
            for (Player player : players){
                out.println(player.getName());
            }
            String name = in.readLine();
            if (findRoleByName(name).equals("mafia")||findRoleByName(name).equals("mafiaDoctor")){
                out.println(name + " is in mafia team");
            }
            else {
                out.println(name + " is in civilian team");
            }
        }
    }
    private void mafiaDoctor() throws IOException {
            if (player.getRole().equals("mafiaDoctor")) {
                out.println("Choose a player to kill");
                for (Player player : players){
                    if (!player.getRole().equals("mafia")&&!player.getRole().equals("godFather")&&!player.getRole().equals("mafiaDoctor")){
                        out.println(player.getName());
                    }
                }
                String name = in.readLine();
                while(findRoleByName(name).equals("mafia")||findRoleByName(name).equals("godFather")||findRoleByName(name).equals("mafiaDoctor")){
                    out.println("Invalid name, please choose from civilian team");
                    name = in.readLine();
                }
                out.println("You killed "+name);
                for (ClientHandler clientHandler1 : clients) {
                    if (clientHandler1.player.getRole().equals("godFather")) {
                        clientHandler1.out.println("mafiaDoctor's vote is " + name);
                    }
                }
                //treatment
                out.println("Who do you wanna treat? (Choose from mafia team)");
                out.println("Please enter a name");
                name = in.readLine();
                if (player.liveAttempt==2){
                    while (findRoleByName(name).equals("mafiaDoctor")){
                        out.println("You can't save yourself anymore, try another name");
                        name = in.readLine();
                    }
                }
                else if (name.equals("mafiaDoctor")){
                    player.liveAttempt++;
                }
//                while(!(findRoleByName(name).equals("mafia")||findRoleByName(name).equals("godFather")||findRoleByName(name).equals("mafiaDoctor"))){
//                    clientHandler.out.println("You can only choose from mafia team, try again");
//                    name = in.readLine();
//                }
                out.println("You treated "+name);
                for (ClientHandler clientHandler: clients){
                    if (clientHandler.player.getName().equals(name)){
                        clientHandler.player.setAlive(true);
                    }
                }
            }

    }
    private void godFather() throws IOException {
            if (player.getRole().equals("godFather")) {
                out.println("Who do you wanna kill?");
                for (Player p : players) {
                    if (!p.getRole().equals("mafia") && !p.getRole().equals("mafiaDoctor") && !p.getRole().equals("godFather")) {
                        out.println(p.getName());
                    }
                }
                out.println("Please enter a name:");
                String name = in.readLine();
                while(findRoleByName(name).equals("mafia")||findRoleByName(name).equals("godFather")||findRoleByName(name).equals("mafiaDoctor")){
                    out.println("Invalid name, please choose from civilian team");
                    name = in.readLine();
                }
                out.println("You killed "+name);
                for (ClientHandler clientHandler: clients){
                    if (clientHandler.player.getName().equals(name)){
                        if (player.getRole().equals("armor") && player.liveAttempt<=2){
                            player.liveAttempt++;
                        }
                        else{
                            clientHandler.player.setAlive(false);
                        }

                    }
                }
            }
    }
    private void sniper() throws IOException {
            if (player.getRole().equals("sniper")) {
                out.println("Do you wanna use your power?");
                out.println("Enter a number");
                out.println("1- yes");
                out.println("2- no");
                String number =in.readLine();
                if (number.equals("1")) {
                    out.println("Choose a player to shoot");
                    out.println("Please enter a name");
                    for (Player player : players){
                        out.println(player.getName());
                    }
                    String name = in.readLine();
                    out.println("You shot "+name);
                    if (findRoleByName(name).equals("sniper")){
                        player.setAlive(false);
                    }
                    for (ClientHandler clientHandler: clients){
                        if (clientHandler.player.getName().equals(name)){
                            clientHandler.player.setAlive(false);
                        }
                    }
                }
            }
    }
    private void civilianDoctor() throws IOException {
            if (player.getRole().equals("civilianDoctor")) {
                out.println("Who do you wanna treat?");
                for (Player p : players) {
                    if (!p.getRole().equals("civilianDoctor")) {
                        out.println(p.getName());
                    }
                }
                out.println("Please enter a name");
                String name = in.readLine();
                out.println("You treated "+name);
                for (ClientHandler clientHandler: clients){
                    if (clientHandler.player.getName().equals(name)){
                        clientHandler.player.setAlive(true);
                    }
                }
            }
    }
    private String therapist() throws IOException {
            if (player.getRole().equals("therapist")) {
                out.println("Choose a player to stop from talking");
                out.println("Player name's are listed here: ");
                for (Player p : players) {
                    if (!p.getRole().equals("therapist")) {
                        out.println(p.getName());
                    }
                }
                String name = in.readLine();
                for (ClientHandler clientHandler1 : clients) {
                    if (clientHandler1.player.getName().equals(name)) {
                        out.println("You stopped "+player.getName()+ " from talking");
                        clientHandler1.player.setMuted(true);
                    }
                }
                return name;
            }
        return "not found";
    }
    private boolean armor() throws IOException {
        if (player.getRole().equals("armor")) {
                out.println("Do you wanna use your power?");
                out.println("Enter a number");
                out.println("1- yes");
                out.println("2- no");
                String number = in.readLine();
                if (number.equals("1")){
                    return true;
                }
                else return false;

        }
        return false;
    }
    private boolean mayor() throws IOException {
        if (player.getRole().equals("mayor")) {
            out.println("Do you validate their votes?");
            out.println("Enter a number");
            out.println("1- yes");
            out.println("2- no");
            String number = in.readLine();
            if (number.equals("1")){
                return true;
            }
            if (number.equals("2")){
                return false;
            }

        }
        return true;
    }

    //handles different game stages

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
    private void chatRoom() throws InterruptedException {
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
                TimeUnit.SECONDS.sleep(time/1000);
                player.setMuted(false);

            }
        }
    }
    private void nightReport() throws IOException {
        out.println("Here is what happened last night...\n");
        for (Player player : players){
            if (!player.isAlive()){
                out.println(player.getName()+" is dead");
            }
            else {
                out.println(player.getName()+ " is alive");
            }
        }
    }
    private void voting() throws IOException {
        long timeS = System.currentTimeMillis();
        long timeF = System.currentTimeMillis();
        while (timeF - timeS < 50000) {
            out.println("Vote a player out");
            out.println("Please type a name:");
            String name = in.readLine();
            for (Player player : players) {
                if (player.getName().equals(name)) {
                    player.vote++;
                    votes.put(name, player.getVote());
                }
            }
            player.checkVote++;
            break;
        }
    }
        private void votingResult () {
            Map.Entry<String, Integer> maxEntry = null;
            for (Map.Entry<String, Integer> entry : votes.entrySet()) {
                if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
                    maxEntry = entry;
                }
            }
            out.println("Players voted out "+maxEntry.getKey()+"with" + maxEntry.getValue() + " votes");
            for (Player player : players){
                if (player.getName().equals(maxEntry.getKey()))
                    player.setAlive(false);
            }
        }

        private boolean isContinued () {
            int mafiaCount = 0;
            int civilianCount = 0;
            for (Player player : players) {
                if (player.isAlive()) {
                    if (player.getRole().equals("mafiaDoctor") || player.getRole().equals("godFather") || player.getRole().equals("mafia")) {
                        mafiaCount++;
                    } else {
                        civilianCount++;
                    }
                }
            }
            if (mafiaCount >= civilianCount || mafiaCount == 0) {
                return false;
            }
            return true;
        }
}

