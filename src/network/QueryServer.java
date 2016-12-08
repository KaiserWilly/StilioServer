package network;

import network.shard.*;

import java.net.Inet4Address;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JD Isenhart on 11/17/2016.
 * Testing RMI creation in Java 8
 */
public class QueryServer implements InifQueryServer {
    private ArrayList<Node> nodeList = new ArrayList<>();
    private ArrayList<Array> arrayList = new ArrayList<>();
    private final Shard[] SHARDS = new Shard[]{new ChatShard(), new ContractShard(), new FilingShard(), new CoreShard()};
    private final int QUERYPORT;

    public QueryServer(int port) {
        this.QUERYPORT = port;
    }

    public void registerNode(String ip, int port) throws RemoteException {
        Node nNode = new Node(ip, port, null);
        boolean update = false;

        for (int i = 0; i < nodeList.size() && !update; i++) {
            Node n = nodeList.get(i);
            if (n.getNodeIP().equals(ip) && n.getNodePort() == port) {
                nodeList.set(i, nNode);
                System.out.println("Reconnected Node! IP: " + ip + " Port: " + port);
                update = true;
            }
        }
        if (!update) {
            nodeList.add(new Node(ip, port, null));
            System.out.println("New Node! IP: " + ip + " Port: " + port);
        }


        if (nodeList.size() >= SHARDS.length) {
            System.out.println("Creating new Array!");
            new ArrayCreate().run();
        }
    }

    @Override
    public void removeArray(Array a) throws RemoteException {
        arrayList.remove(a);
        try {
            Registry registry = LocateRegistry.getRegistry(Inet4Address.getLocalHost().getHostAddress(), QUERYPORT); //IP Address of RMI Server, port of RMIRegistry
            InifQueryClient stub = (InifQueryClient) registry.lookup("QueryClient");
            stub.closeArray(a);
        } catch (Exception e) {
            System.out.println("Can't open Array to clients!");
        }

        System.err.println("Array Dissolved!");
    }

    public void queryErrState(String statement) throws RemoteException {
        System.err.println(statement);
    }

    private class ArrayCreate extends Thread {
        Array data = new Array();

        public void run() {
            List<Node> aList = new ArrayList<>();
            Node n = null;
            try {
                data.setQueryIP(Inet4Address.getLocalHost().getHostAddress());
                data.setQueryPort(1180);
                for (int i = 0; i < SHARDS.length; i++) { //Verify Node is active
                    n = nodeList.get(i);
                    aList.add(n);
                    n.setShard(SHARDS[i]);
                    data.addShardMap(n);
                    Registry registry = LocateRegistry.getRegistry(n.getNodeIP(), n.getNodePort()); //IP Address of RMI Server, port of RMIRegistry
                    registry.lookup("AdminServer"); //Name of RMI Server in registry
                    data.addNode(n);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Unable to create new Array! (Ping)");
                nodeList.remove(nodeList.indexOf(n));
                System.out.println("Returned good Nodes to List!");
                return;
            }
            nodeList.removeAll(aList);

            for (Node o : data.getNodeList()) {  //Transcribe data to Nodes
                startServices startNodes = new startServices(data, o);
                startNodes.run();
            }

            arrayList.add(data);
            try {
                Registry registry = LocateRegistry.getRegistry(Inet4Address.getLocalHost().getHostAddress(), QUERYPORT); //IP Address of RMI Server, port of RMIRegistry
                InifQueryClient stub = (InifQueryClient) registry.lookup("QueryClient");
                stub.openArray(data);
            } catch (Exception e) {
                System.out.println("Can't open Array to clients!");
            }
        }

        class startServices extends Thread {
            Array data;
            Node n;

            startServices(Array data, Node n) {
                this.data = data;
                this.n = n;
            }

            public void run() {
                try {
                    Registry registry = LocateRegistry.getRegistry(n.getNodeIP(), n.getNodePort()); //IP Address of RMI Server, port of RMIRegistry
                    InifNode stub = (InifNode) registry.lookup("AdminNode"); //Name of RMI Server in registry
                    stub.setArrayData(data);
                    stub.setShard(n.getShard());
                    stub.startService();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void printUnassignedNodes() {
        try {
            System.out.println("Nodes Unassigned: " + nodeList.size());
            for (Node n : nodeList) {
                System.out.println("Node IP: " + n.getNodeIP() + " Port: " + n.getNodePort());
            }
        } catch (Exception e) {
            System.out.println("Can't print Unassigned Nodes!");
        }
    }

    public void stopQuery(String altQryIP, int altQryPrt) throws RemoteException {

    }

    public void stopQuery(String reason) throws RemoteException {
//        System.err.println("QueryServer Server Terminated! Reason: " + reason);
        ArrayList<Array> aList = arrayList;
        for (Array a : aList) {
            ArrayList<Node> nList = a.getNodeList();
            for (Node n : nList) {
                try {
                    Registry registry = LocateRegistry.getRegistry(n.getNodeIP(), n.getNodePort()); //IP Address of RMI Server, port of RMIRegistry
                    InifNode stub = (InifNode) registry.lookup("AdminNode");
                    try {
                        stub.terminateNode(reason);
                    } catch (Exception e) {
                        System.err.println("Node Terminated: IP:" + n.getNodeIP() + " Port:" + n.getNodePort());
                    }
                } catch (Exception e) {
                    System.out.println("\nCan't Contact Node!(Array)\n IP:" + n.getNodeIP() + " Port:" + n.getNodePort());
                }
            }
        }
        for (Node n : nodeList) {
            try {
                Registry registry = LocateRegistry.getRegistry(n.getNodeIP(), n.getNodePort()); //IP Address of RMI Server, port of RMIRegistry
                InifNode stub = (InifNode) registry.lookup("AdminNode");
                try {
                    stub.terminateNode(reason);
                } catch (Exception e) {
                    System.err.println("Node Terminated: IP:" + n.getNodeIP() + " Port:" + n.getNodePort());
                }
            } catch (Exception e) {
                System.out.println("\nCan't Contact Node! (nodeList)\n IP:" + n.getNodeIP() + " Port:" + n.getNodePort());
            }
        }
        System.exit(1);
    }

}



