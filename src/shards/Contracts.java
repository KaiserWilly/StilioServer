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
public class Contracts extends Shard implements Serializable, ContractsInif {
    public Contracts() {
        super("Contracts");
    }

    @Override
    public void startShard(Array data, Node n) {
        try {
            Registry registry = LocateRegistry.getRegistry(n.getNodePort());
            registry.bind("Core", UnicastRemoteObject.exportObject(this, 0));
            System.out.println("Client Server (Contracts) started!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
