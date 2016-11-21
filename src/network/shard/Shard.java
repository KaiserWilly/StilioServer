package network.shard;

import network.*;
import network.shard.inif.InifContract;
import network.shard.inif.InifCore;

import java.io.Serializable;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by JD Isenhart on 10/24/2016.
 * Testing RMI creation in Java 8
 */
public class Shard implements Serializable {
    private String role = "Unassigned";

    Shard(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void startShard(Array data, Node n) {
        try {
            Registry registry = LocateRegistry.getRegistry(n.getNodePort());
        switch (n.getShard().getRole()) {
            case "Core":
                CoreShard coreShard = new CoreShard();
                InifCore coreStub = (InifCore) UnicastRemoteObject.exportObject(coreShard, 0);
                registry.bind("Core", coreStub);
                break;
            case "Contract":
                ContractShard contractShard = new ContractShard();
                InifContract contractStub = (InifContract) UnicastRemoteObject.exportObject(contractShard, 0);
                registry.bind("Core", contractStub);
                break;
            case "Chat":
                ChatShard chatShard = new ChatShard();
                InifCore chatStub = (InifCore) UnicastRemoteObject.exportObject(chatShard, 0);
                registry.bind("Core", chatStub);
                break;
            case "Filing":
                FilingShard filingShard = new FilingShard();
                InifCore filingStub = (InifCore) UnicastRemoteObject.exportObject(filingShard, 0);
                registry.bind("Core", filingStub);
                break;
        }
        } catch (Exception e) {

        }
        System.out.println("Client Server (InifShard) started!");
    }

}
