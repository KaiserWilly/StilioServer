package shards;

import network.Array;
import network.Node;
import network.Shard;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by JD Isenhart on 1/17/2017.
 * Testing RMI creation in Java 8
 */
public class Records extends Shard implements RecordsInif, Serializable{
    public Records() {
        super("Records");
    }

    @Override
    public void startShard(Array data, Node n) {
        try {
            Registry registry = LocateRegistry.getRegistry(n.getNodePort());
            registry.bind("Core", UnicastRemoteObject.exportObject(this, 0));
            System.out.println("Client Server (Records) started!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
