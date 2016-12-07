package network;

import network.shard.*;

import java.net.Inet4Address;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

/**
 * Created by JD Isenhart on 11/17/2016.
 * Testing RMI creation in Java 8
 */
public class Query implements InifQuery {
    private ArrayList<Node> nodeList = new ArrayList<>();
    private ArrayList<Array> arrayList = new ArrayList<>();
    private final Shard[] SHARDS = new Shard[]{new ChatShard(), new ContractShard(), new FilingShard(), new CoreShard()};

    public void startQuery(int port) {
        try {
            LocateRegistry.createRegistry(port);
            Query obj = new Query();// Create new instance of content for RMI to use
            InifQuery stub = (InifQuery) UnicastRemoteObject.exportObject(obj, 0); //create stub
            Registry registry = LocateRegistry.getRegistry(port);//Denote port to get registry from
            registry.bind("Query", stub); //Bind stub to registry

            System.out.println("Query Server Started!");
            System.out.println("IP Address: " + Inet4Address.getLocalHost().getHostAddress());
            System.out.println("Port: " + port);
        } catch (Exception e) {
            System.err.println("Can't create Query Server");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void registerNode(String ip, int port) throws RemoteException {
        nodeList.add(new Node(ip, port, null));
        System.out.println("New Node! IP: " + ip + " Port: " + port);
        if (nodeList.size() >= SHARDS.length) {
            System.out.println("Creating new Array!");
            new ArrayCreate().run();
        }
    }

    @Override
    public void removeArray(Array a) throws RemoteException {
        arrayList.remove(a);
        System.err.println("Array Dissolved!");
    }

    public void queryErrState(String statement) throws RemoteException {
        System.err.println(statement);
    }

    private class ArrayCreate extends Thread {
        Array data = new Array();

        public void run() {
            try {
                data.setQueryIP(Inet4Address.getLocalHost().getHostAddress());
                data.setQueryPort(1180);
                for (Shard SHARD : SHARDS) { //Verify Node is active
                    Node n = nodeList.get(0);
                    nodeList.remove(0);
                    n.setShard(SHARD);

                    data.addShardMap(n);
                    Registry registry = LocateRegistry.getRegistry(n.getNodeIP(), n.getNodePort()); //IP Address of RMI Server, port of RMIRegistry
                    InifServer stub = (InifServer) registry.lookup("AdminServer"); //Name of RMI Server in registry
                    data.addNode(n);
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Unable to create new Array! (Ping)");
                nodeList.addAll(data.getNodeList());
                System.out.println("Returned good Nodes to List!");
                return;
            }

            for (Node n : data.getNodeList()) {  //Transcribe data to Nodes
                startServices startNodes = new startServices(data, n);
                startNodes.run();
            }

            arrayList.add(data);
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
}



