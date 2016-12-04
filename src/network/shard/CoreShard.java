package network.shard;

import network.*;
import network.shard.inif.InifCore;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JD Isenhart on 11/17/2016.
 * Testing RMI creation in Java 8
 */
public class CoreShard extends Shard implements InifCore {
    private static Timer timer;

    public CoreShard() {
        super("Core");
    }

    @Override
    public void startShard(Array data, Node n) {
        super.startShard(data, n);
        startPing(data);
    }

    private void startPing(Array data) {
        timer = new Timer();
        System.out.println("Server Health Check Started!");
        timer.scheduleAtFixedRate(timerTask(data), 3000, 1000); //Task, delay, update speed
    }

    private static TimerTask timerTask(Array data) {
        return new TimerTask() {
            boolean dissolved = false;

            @Override
            public void run() {
                data.getNodeList().stream().filter(n -> !n.getShard().getRole().equals("Core") && !dissolved).forEach(n -> {
                    try {
                        Registry registry = LocateRegistry.getRegistry(n.getNodeIP(), n.getNodePort()); //IP Address of RMI Server, port of RMIRegistry
                        InifServer stub = (InifServer) registry.lookup("AdminServer"); //Name of RMI Server in registry
                        stub.ping();
                    } catch (Exception e) {
                        try {
                            System.err.println("Node IP: " + n.getNodeIP());
                            System.err.println("Node timed out!");
                            System.err.println("Node Role: " + n.getShard().getRole());
                            System.err.println("Node Port: " + n.getNodePort());
                            System.err.println("Dissolving Array");
                            timer.cancel();
                            dissolveArray(data);
                            dissolved = true;
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        }
                    }
                });
                System.out.println("System Integrity Good!");
            }
        };
    }

    private static void dissolveArray(Array data) {
        try {
            Registry queryRegistry = LocateRegistry.getRegistry(data.getQueryIP(), data.getQueryPort()); //IP Address of RMI Server, port of RMIRegistry
            InifQuery queryStub = (InifQuery) queryRegistry.lookup("Query");
            queryStub.removeArray(data);
            for (Node n : data.getNodeList()) {
                try {
                    Registry nodeRegistry = LocateRegistry.getRegistry(n.getNodeIP(), n.getNodePort()); //IP Address of RMI Server, port of RMIRegistry
                    InifNode nodeStub = (InifNode) nodeRegistry.lookup("AdminNode"); //Name of RMI Server in registry
                    nodeStub.unassignNode("Node Timeout", n.getNodePort());
                } catch (Exception e1) {
                    System.err.println("Can't Contact Node!");
                }
            }
        } catch (Exception e) {
            System.err.println("Can't dissolve Array!");
            e.printStackTrace();
        }
    }
}
