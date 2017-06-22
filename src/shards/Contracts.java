package shards;

import classes.Contract;
import network.Array;
import network.Node;
import network.Shard;
import org.apache.derby.jdbc.EmbeddedDriver;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by JD Isenhart on 1/17/2017.
 * Testing RMI creation in Java 8
 */
public class Contracts extends Shard implements ContractsInif {
    private Connection conn = null;
    String shardName = "Contracts";

    public Contracts() {
        super("Contracts");
    }

    public void startShard(Array data, Node n) {
        try {
            Registry registry = LocateRegistry.getRegistry(n.getNodePort());
            registry.bind(shardName, UnicastRemoteObject.exportObject(this, 0));
            System.out.println("Client Server (" + shardName + ") started!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        startContractDatabase();

    }

    private void startContractDatabase() {
        Statement stmt;
        String createSQL = "create table contracts ("
                + "contid integer not null generated always as identity (start with 1, increment by 1), "
                + "sellerid integer not null, " + //Apache DB does not support variable precision integer
                "companyname varchar(30) not null, " +
                "itemid integer not null, " +
                "itemqty integer not null, " +
                "cont_time varchar(20), "
                + "constraint primary_key primary key (contid))";
        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            if (Files.isDirectory(Paths.get("./contractDB"))) {
                conn = DriverManager.getConnection("jdbc:derby:contractDB;", "adminC", "12345");
                System.out.println("Existing Database found!");
            } else {
                conn = DriverManager.getConnection("jdbc:derby:contractDB;create=true", "adminC", "12345");
                stmt = conn.createStatement();
                stmt.execute(createSQL);
                System.out.println("DB (Contracts) created!");
                System.out.println("Table (contracts) inserted!");
            }
            conn.setAutoCommit(false);
            conn.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println(createSQL.substring(120, 130));
        }

    }

    public synchronized void insertContract(Contract c) {
        try {
            PreparedStatement prepStmt;
            prepStmt = conn.prepareStatement("insert into contracts (sellerid,companyname,itemid,itemqty,cont_time) values(?,?,?,?,?)");
            prepStmt.setString(1, String.valueOf(c.getSellerID()));
            prepStmt.setString(2, c.getCompanyName());
            prepStmt.setString(3, String.valueOf(c.getItemID()));
            prepStmt.setString(4, String.valueOf(c.getItemQty()));
            prepStmt.setString(5, c.getDurationISO());
            prepStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Can't add contract to database");
        }
    }

    public synchronized void removeContract(int contractID) {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute("delete from contracts where cont_id=" + String.valueOf(contractID) + ";");
        } catch (SQLException e) {
            System.out.println("Can't remove contract (ID:" + contractID + ")");
        }
    }

    public synchronized ResultSet getResultSet(String sql) {
        try {
            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.out.println("Can't retrieve ResultSet!");
            System.out.println("SQL: " + sql);
            return null;
        }
    }

    public synchronized List<Contract> getResultSetList(String sql) {
        List<Contract> rsL = new ArrayList<>();
        try {
            ResultSet rs = getResultSet(sql);
            while (rs.next()) {
                Duration d = Duration.parse(rs.getString("cont_time"));
                int contID = rs.getInt("contid"), sellerID = rs.getInt("sellerid"), itemID = rs.getInt("itemid"), itemQty = rs.getInt("itemqty");
                String companyName = rs.getString("companyname");
                Contract c = new Contract(contID, sellerID, companyName, itemID, itemQty, d);
                rsL.add(c);
            }
            return rsL;
        } catch (SQLException e) {
            System.out.println("Can't retrieve result set!");
            System.out.println("SQL: " + sql);
            return null;
        }
    }

    public List<Contract> contractsBySeller(int sellerID) throws RemoteException {
        return getResultSetList("select * from contracts where sellerid=" + sellerID + ";");
    }


}
