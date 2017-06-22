package classes;

import java.io.Serializable;

/**
 * Created by james on 1/24/2017.
 */
public class Player implements Serializable {
    private int playerID;
    private String username, password, displayname;
    private boolean newPlayer = false;
    Company playerCompany = null;

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.displayname = username;
    }

    public Player(int playerID, String username, String displayName, boolean newPlayer) {
        this.playerID = playerID;
        this.username = username;
        this.displayname = displayName;
        this.newPlayer = newPlayer;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDisplayName() {
        return displayname;
    }

    public boolean isNewPlayer() {return newPlayer;}

    public String toString() {
        return username + "," + password + "," + displayname;
    }

}
