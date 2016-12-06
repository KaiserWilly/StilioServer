package network;

import network.shard.CoreShard;
import network.shard.Shard;

import java.io.Serializable;
import java.net.Inet4Address;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JD Isenhart on 10/24/2016.
 * Testing RMI creation in Java 8
 */
public class Node implements InifNode, InifServer, Serializable {
    private Array arrayData;
    private Shard shard;
    private String queryIP, nodeIP;
    private int nodePort = 1180, qport = 1180;
//    private boolean coreCheck = true;
    private Timer timer;

    public Node() {
    }

    public Node(String queryIP, int qPort) {
        createRegistry();
        startAdminServer();
        registerWithQuery(queryIP, qPort);
        this.queryIP = queryIP;
        this.qport = qPort;
    }

    public Node(String nodeIP, int port, Shard shard) {
        this.nodeIP = nodeIP;
        this.nodePort = port;
        this.shard = shard;
    }

    private void createRegistry() {
        try {
            LocateRegistry.createRegistry(nodePort);
        } catch (RemoteException e) {
            if (nodePort > 1200) {
                System.err.println("Unable to bind to a port!");
                System.exit(0);
            }
            nodePort++;
            createRegistry();
        }
    }

    private void startAdminServer() {
        try {
            Node obj = new Node();// Create new instance of content for RMI to use
            InifServer stub = (InifServer) UnicastRemoteObject.exportObject(obj, 0); //create stub
            Registry registry = LocateRegistry.getRegistry(nodePort);//Denote nodePort to get registry from
            registry.bind("AdminServer", stub); //Bind stub to registry
            System.out.println("Admin Server (InifServer) Ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }

        try {
            Node obj = new Node();// Create new instance of content for RMI to use
            InifNode stub = (InifNode) UnicastRemoteObject.exportObject(obj, 0); //create stub
            Registry registry = LocateRegistry.getRegistry(nodePort);//Denote nodePort to get registry from
            registry.bind("AdminNode", stub); //Bind stub to registry
            System.out.println("Admin Server (InifNode) Ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    private void registerWithQuery(String queryIP, int port) {
        try {
            Registry registry = LocateRegistry.getRegistry(queryIP, port); //IP Address of RMI Server, nodePort of RMIRegistry
            InifQuery stub = (InifQuery) registry.lookup("Query"); //Name of RMI Server in registry
            stub.registerNode(Inet4Address.getLocalHost().getHostAddress(), nodePort);
            System.out.println("Successfully Registered with Query! Port: " + nodePort);
        } catch (Exception e) {
            System.err.println("Can't connect to Query Server!");
            System.err.println("IP Address: " + queryIP + "  Port: " + port);
            System.err.println("Terminating Node");
            System.exit(0);
        }
    }

    public void startService() throws RemoteException {
        verifyNodePort();
        System.out.println("Current Port: " + nodePort);
        System.out.println("Service Started!");
        System.out.println("Core IP: " + arrayData.getShardMap().get(new CoreShard().getRole()).getNodeIP());
        System.out.println("Port: " + arrayData.getShardMap().get(new CoreShard().getRole()).getNodePort());
        System.out.println("Role of this server: " + shard.getRole());
        if (!shard.getRole().equals("Core")) {
            System.out.println("");
            startCoreCheck();
        }
        shard.startShard(arrayData, this);

    }

    public void unassignNode(String reason) throws RemoteException {
        verifyNodePort();
        System.out.println("Current Port: " + nodePort);
        System.err.println("Node Unassigned! Reason: " + reason);
        arrayData = null;
        shard = null;
        registerWithQuery(this.queryIP, this.qport);
    }

    public void setShard(Shard shard) {
        this.shard = shard;
    }

    public Shard getShard() {
        try {
            return shard;
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getNodeIP() throws RemoteException {
        return nodeIP;
    }

    public int getNodePort() {
        return nodePort;
    }

    public void setArrayData(Array data) {
        this.arrayData = data;
    }

//    public boolean ping() {
//        setCoreCheck(true);
//        System.out.println("Check in From Core");
//        return true;
//    }

//    private boolean getCoreCheck(){
//        System.out.println("Core Get: "+coreCheck);
//        return this.coreCheck;
//    }
//
//    private  void setCoreCheck(boolean check){
//        System.out.println("Core Set: "+check);
//        this.coreCheck = check;
//    }

    private void startCoreCheck() {
        timer = new Timer();
        System.out.println("Core Integrity Check Started!");
        timer.schedule(timerTask(), 7000, 4000); //Task, delay, update speed
    }

    private TimerTask timerTask() {
        return new TimerTask() {

            @Override
            public void run() {
//                if (getCoreCheck()) {
//                    setCoreCheck(false);
//                    System.out.println("Core Integrity Verified!");
//                } else {
//                    System.out.println("Core Integrity Compromised!");
//                    try {
//                        unassignNode("Core timed out!");
//                    } catch (RemoteException e) {
//                        e.printStackTrace();
//                    }
//                }
                try {
                    Registry registry = LocateRegistry.getRegistry(arrayData.getShardMap().get("Core").getNodeIP(), arrayData.getShardMap().get("Core").getNodePort()); //IP Address of RMI Server, port of RMIRegistry
                    InifServer stub = (InifServer) registry.lookup("AdminServer");
                }catch(Exception e){
                    try {
                        unassignNode("Core timeout!");
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
    }

    private void verifyNodePort() {
        try {
            int certPort = arrayData.getShardMap().get(this.shard.getRole()).getNodePort();
            if (nodePort != certPort) {
                nodePort = certPort;
                System.out.println("Node Port Reset! Port: "+certPort);
            }
            else{
                System.out.println("Node Port Verified!");
            }
        } catch (Exception e) {
            System.out.println("Node Port Verification Failed!");
        }

    }
}
