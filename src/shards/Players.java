package shards;

import classes.Player;
import network.Array;
import network.Node;
import network.Shard;
import org.apache.derby.jdbc.EmbeddedDriver;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.Instant;
import java.util.HashMap;

/**
 * Created by JD Isenhart on 4/30/2017.
 * Testing RMI creation in Java 8
 */
public class Players extends Shard implements PlayersInif {
    private Connection conn = null;

    public Players() {
        super("Players");
    }

    public void startShard(Array data, Node n) {
        try {
            Registry registry = LocateRegistry.getRegistry(n.getNodePort());
            registry.bind("Players", UnicastRemoteObject.exportObject(this, 0));
            System.out.println("Client Server (Players) started!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        startPlayerDatabase();
    }

    private void startPlayerDatabase() {
        Statement stmt;
        String createSQL = "create table players (" +
                "playerid integer not null generated always as identity (start with 10000, increment by 1), " +
                "playername varchar(15) not null, " +
                "playerpassword varchar(30) not null, " +
                "displayname varchar(15), " +
                "timestamp long varchar not null, " +
                "createdstamp long varchar not null," +
                "constraint pk_id primary key (playerid))";
        try {
            Driver derbyEmbeddedDriver = new EmbeddedDriver();
            DriverManager.registerDriver(derbyEmbeddedDriver);
            if (Files.isDirectory(Paths.get("./playerDB"))) {
                conn = DriverManager.getConnection("jdbc:derby:playerDB;", "adminP", "12345");
                System.out.println("Existing Database found!");
            } else {
                conn = DriverManager.getConnection("jdbc:derby:playerDB;create=true", "adminP", "12345");
                stmt = conn.createStatement();
                stmt.execute(createSQL);
                System.out.println("DB (Players) created!");
                System.out.println("Table (Players) inserted!");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public boolean createPlayer(Player p) {
        try {
            PreparedStatement prepStmt;
            prepStmt = conn.prepareStatement("insert into players (playername, playerpassword, displayname, timestamp, createdstamp) values(?,?,?,?,?)");
            prepStmt.setMaxFieldSize(40);
            long timestamp = Instant.now().getEpochSecond();
            prepStmt.setString(1, p.getUsername());
            prepStmt.setString(2, p.getPassword());
            prepStmt.setString(3, p.getDisplayName());
            prepStmt.setLong(4, timestamp);
            prepStmt.setLong(5, timestamp);
            prepStmt.executeUpdate();
            prepStmt.close();

        } catch (SQLException e) {
            System.out.println("Can't create player!");
            System.out.println("Player: " + p.toString());
            return false;
        }
        return true;
    }

    public synchronized boolean updatePlayer(int playerID, HashMap<String, String> updatedFields) {
        StringBuilder sql = new StringBuilder("update players set ");
        for (String key : updatedFields.keySet()) {
            try {
                int value = Integer.parseInt(key);
                sql.append(key).append(" = ").append(String.valueOf(value)).append(", ");
            } catch (NumberFormatException e) {
                sql.append(key).append(" = '").append(updatedFields.get(key)).append("', ");
            }
        }
        sql.append("where playerid=").append(playerID).append(";");
        try {
            conn.createStatement().execute(sql.toString());
            conn.close();
        } catch (SQLException e) {
            System.out.println("Can't update player!");
            System.out.println("SQL: " + sql.toString());
            return false;
        }
        return true;

    }

    public synchronized Player login(Player p, boolean recursive) {
        String user = p.getUsername(), pass = p.getPassword();
        StringBuilder sql = new StringBuilder("select * from players where playername='");
        sql.append(user).append("' and ").append("playerpassword='").append(pass).append("'");
        ResultSet rs;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql.toString());
            int rsfs = rs.getFetchSize();
            System.out.println(rsfs);
            while (rs.next()) {
                String playerID = rs.getString("playerid");
                System.out.println("Request for ID:" + playerID);
                String playerName = rs.getString("playername"), displayName = rs.getString("displayname");
                Player pNew = new Player(Integer.parseInt(playerID), playerName, displayName, recursive);
                HashMap<String, String> update = new HashMap<>();
                update.put("timestamp", String.valueOf(Instant.now().getEpochSecond()));
                updatePlayer(Integer.parseInt(playerID), update);
                System.out.println(pNew.isNewPlayer());
                stmt.close();
                return pNew;
            }
            if (rsfs == 1) {
                stmt.close();
                createPlayer(p);
                return login(p, recursive);
            }
        } catch (SQLException e) {
            System.out.println("Can't query database!");
            System.out.println("SQL: " + sql.toString());
            e.printStackTrace();
            return null;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
