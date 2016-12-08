package network;

import java.net.Inet4Address;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created by JD Isenhart on 12/7/2016.
 * Testing RMI creation in Java 8
 */
public class Query {

    public void startQuery(int port) {
        String ip = null;
        try {
            LocateRegistry.createRegistry(port);
            ip = Inet4Address.getLocalHost().getHostAddress();
        } catch (Exception e) {
            System.err.println("Unable to create Query Registry");
            System.exit(1);
        }
        startQueryServer(port);
        startQueryClient(port);
        System.out.println("IP Address: " + ip);
        System.out.println("Port: " + port);
        new QueryIOConsole(ip, port).run();
    }

    public void startQueryServer(int port) {
        try {
            QueryServer obj = new QueryServer(port);// Create new instance of content for RMI to use
            InifQueryServer stub = (InifQueryServer) UnicastRemoteObject.exportObject(obj, 0); //create stub
            Registry registry = LocateRegistry.getRegistry(port);//Denote port to get registry from
            registry.bind("QueryServer", stub); //Bind stub to registry

            System.out.println("Query Server \"QueryServer\" Started!");

        } catch (Exception e) {
            System.err.println("Can't create Query: Server");
            e.printStackTrace();
            System.exit(0);
        }
    }

    public void startQueryClient(int port) {
        try {
            QueryClient obj = new QueryClient();// Create new instance of content for RMI to use
            InifQueryClient stub = (InifQueryClient) UnicastRemoteObject.exportObject(obj, 0); //create stub
            Registry registry = LocateRegistry.getRegistry(port);//Denote port to get registry from
            registry.bind("QueryClient", stub); //Bind stub to registry

            System.out.println("Query Server \"QueryClient\" Started!");
        } catch (Exception e) {
            System.err.println("Can't create Query: Client");
            e.printStackTrace();
            System.exit(0);
        }
    }
}
