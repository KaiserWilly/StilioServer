package shards;

import classes.Company;
import classes.Player;
import network.Array;
import network.Node;
import network.Shard;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by JD Isenhart on 1/17/2017.
 * Testing RMI creation in Java 8
 */
public class Records extends Shard implements RecordsInif, Serializable {
    ArrayList<String> playerNames = new ArrayList<>();
    HashMap<String, String> playerPasswords = new HashMap<>();
    HashMap<Player, Company> playerData = new HashMap<>();

    public Records() {
        super("Records");
    }


    public void startShard(Array data, Node n) {
        try {
            Registry registry = LocateRegistry.getRegistry(n.getNodePort());
            registry.bind(this.getRole(), UnicastRemoteObject.exportObject(new Records(), n.getNodePort()));
            System.out.println("Client Server (Records) started!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public boolean checkIfNew(Player player) throws RemoteException {
        return !playerNames.contains(player.getUsername());
    }

    @Deprecated
    public boolean checkPassword(Player player) throws RemoteException {
        if (playerPasswords.containsKey(player.getUsername())) {
            if (playerPasswords.get(player.getUsername()).equals(player.getPassword())) {
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public boolean registerNewClient(Player p, Company c) throws RemoteException {
        return !(!checkIfNew(p) || !checkPassword(p));
    }
    @Deprecated
    public Company getCompany(Player p) throws RemoteException {
        return playerData.get(p);
    }

}
