package network;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by james on 12/7/2016.
 */
public class QueryIOConsole extends Thread {
    private String queryIP;
    private int queryPort;
    Scanner input = new Scanner(System.in);
    static private final String IPV4_REGEX = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
    static private Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

    public QueryIOConsole(String queryIP, int queryPort) {
        this.queryIP = queryIP;
        this.queryPort = queryPort;
    }

    private boolean isValidIPV4(final String s) {
        return IPV4_PATTERN.matcher(s).matches();
    }

    @Override
    public void run() {
        InifQuery stub = null;
        try {
            Registry registry = LocateRegistry.getRegistry(queryIP, queryPort); //IP Address of RMI Server, nodePort of RMIRegistry
            stub = (InifQuery) registry.lookup("Query"); //Name of RMI Server in registry

        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean quit = false;
        while (!quit) {
            try {
                String[] result = input.nextLine().split(" ");
                boolean wrong = false;
                switch (result[0]) {
                    case "sstoptop":
                        if (result.length == 2) {
                            if (Objects.equals(result[1], "-c")) {
                                System.out.println("Shutting down query server!");
                                System.out.println("No Alternative Query Server Set: \n All nodes will be terminated!");
                                quit = true;
                                //Query Command to Terminate all Nodes
                            } else wrong = true;
                        } else if (result.length == 3) {
                            if (isValidIPV4(result[1]) && Integer.parseInt(result[2]) != 0) {
                                System.out.println("Alternative Query IP:" + result[1] + " Port:" + result[2]);
                                //Check Query Validity
                                //Cerate Node comamnds to hand off to other server
                            } else wrong = true;
                        } else {
                            System.out.println("Invalid Arguments! Usage: Stop <args> <AltQIP>");
                            System.out.println("Args:\n -c : No alt Query Server; terminate all Nodes");
                        }
                        if (wrong) {
                            System.out.println("Invalid Arguments! Usage: Stop <args> <AltQIP>");
                            System.out.println("Args:\n -c : No alt Query Server; terminate all Nodes");
                        }
                        break;
                    case "prt":
                        if (result[1].equals("-un")) {
                            if (stub != null) {
                                stub.printUnassignedNodes();
                            }
                        } else wrong = true;

                        if (wrong) {
                            System.out.println("Invalid Arguments! Usage: Stop <args> <AltQIP>");
                            System.out.println("Args:\n un : Print all Unassigned Nodes");
                        }
                        break;
                    default:
                        System.out.println("Invalid Command: \"" + result[0] + "\"");
                        break;

                }

            } catch (Exception e) {

            }

        }
        //Shutdown query server
    }
}