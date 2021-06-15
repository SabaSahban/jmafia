/**
 * The type Player.
 */
public class Player {

    private String name;
    private boolean isAlive;
    private String role;
    /**
     * The Vote.
     */
    public int vote = 0;
    private boolean isMuted;
    /**
     * The Check night.
     */
    public int checkNight;
    /**
     * The Check vote.
     */
    public int checkVote;
    /**
     * The Attempt.
     */
    public int attempt;
    /**
     * The Live attempt.
     */
    public int liveAttempt;


    /**
     * Is muted boolean.
     *
     * @return the boolean
     */
    public boolean isMuted() {
        return isMuted;
    }


    /**
     * Instantiates a new Player.
     *
     * @param name the name
     * @param role the role
     */
    public Player(String name, String role) {
        this.name = name;
        this.isAlive = true;
        this.isMuted = false;
        this.role = role;
        this.checkNight = 0;
        this.attempt = 0;
        this.liveAttempt = 0;


    }

    /**
     * Sets muted.
     *
     * @param muted the muted
     */
    public void setMuted(boolean muted) {
        isMuted = muted;
    }

    /**
     * Gets attempt.
     *
     * @return the attempt
     */
    public int getAttempt() {
        return attempt;
    }

    /**
     * Sets attempt.
     *
     * @param attempt the attempt
     */
    public void setAttempt(int attempt) {
        this.attempt = attempt;
    }

    /**
     * Gets live attempt.
     *
     * @return the live attempt
     */
    public int getLiveAttempt() {
        return liveAttempt;
    }

    /**
     * Sets live attempt.
     *
     * @param liveAttempt the live attempt
     */
    public void setLiveAttempt(int liveAttempt) {
        this.liveAttempt = liveAttempt;
    }

    /**
     * Gets vote.
     *
     * @return the vote
     */
    public int getVote() {
        return vote;
    }

    /**
     * Sets vote.
     *
     * @param vote the vote
     */
    public void setVote(int vote) {
        this.vote = vote;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Is alive boolean.
     *
     * @return the boolean
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Sets alive.
     *
     * @param alive the alive
     */
    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    /**
     * Gets role.
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets role.
     *
     * @param role the role
     */
    public void setRole(String role) {
        this.role = role;
    }
}



