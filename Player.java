public class Player {
    private String name;
    private boolean isAlive;
    private String role;
    public int vote = 0;

    public Player(String name, String role) {
        this.name = name;
        this.isAlive = true;
        this.role = role;

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



