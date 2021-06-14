public class Player {

    private String name;
    private boolean isAlive;
    private String role;
    public int vote = 0;
    private boolean isMuted;
    public int check;
    private int attempt;


    public boolean isMuted() {
        return isMuted;
    }

    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    public Player(String name, String role) {
        this.name = name;
        this.isAlive = true;
        this.isMuted = false;
        this.role = role;
        this.check = 0;

    }



    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}



