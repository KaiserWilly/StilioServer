package shards;

import classes.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

/**
 * Created by JD Isenhart on 4/30/2017.
 * Testing RMI creation in Java 8
 */
public interface PlayersInif extends Remote {

    boolean createPlayer(Player p) throws RemoteException;

    boolean updatePlayer(int playerID, HashMap<String, String> updatedFields) throws RemoteException;

    Player login(Player p, boolean recursive) throws RemoteException;


}
