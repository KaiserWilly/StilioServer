package shards;

import classes.Item;
import network.Array;
import network.Node;
import network.Shard;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.List;

/**
 * Created by JD Isenhart on 5/3/2017.
 * Testing RMI creation in Java 8
 */
public class Items extends Shard implements ItemsInif {
    private Connection conn = null;

    public Items() {
        super("Items");
    }

    public void startShard(Array data, Node n) {
        try {
            Registry registry = LocateRegistry.getRegistry(n.getNodePort());
            String shardName = "Items";
            registry.bind(shardName, UnicastRemoteObject.exportObject(this, 0));
            System.out.println("Client Server (" + shardName + ") started!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        startItemDatabase();
    }

    private void startItemDatabase() {
        Statement stmt;
        String createSQL = "create table items ("
                + "itemid integer not null generated always as identity (start with 1, increment by 1), "
                + "itemname varchar(30) not null, " +
                "basecost float not null, " +
                "constraint primary_key primary key (itemid))";
        try {
            if (Files.isDirectory(Paths.get("./itemDB"))) {
                conn = DriverManager.getConnection("jdbc:derby:itemDB;", "adminC", "12345");
                System.out.println("Existing Database found!");
            } else {
                conn = DriverManager.getConnection("jdbc:derby:itemDB;create=true", "adminC", "12345");
                stmt = conn.createStatement();
                stmt.execute(createSQL);
                System.out.println("DB (Items) created!");
                System.out.println("Table (Items) inserted!");
                initializeItemCatalog();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            System.out.println(createSQL.substring(120, 130));
        }
    }

    private void initializeItemCatalog() {
        try {
            PreparedStatement prepStmt;
            List<String> rawItems = Files.readAllLines(Paths.get("rsc/items_default.txt"));
            for (String s : rawItems) {
                String[] sArray = s.split(",");
                prepStmt = conn.prepareStatement("insert into items (itemname,basecost) values(?,?)");
                prepStmt.setString(1, sArray[0]);
                prepStmt.setString(2, sArray[1]);
                prepStmt.executeUpdate();
            }
        } catch (IOException e) {
            System.out.println("Can't Initialize Items!");
        } catch (SQLException e) {
            System.out.println("Can't Insert Items!");
        }
    }

    public Item getItem(int itemID) {
        String sql = "select * from items where itemid=" + itemID + ";";
        try {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            return new Item(rs.getInt("itemid"), rs.getString("itemname"));
        } catch (SQLException e) {
            System.out.println("Can't fetch Item!");
            System.out.println("SQL: " + sql);
            return null;
        }
    }
}
