import network.Node;
import network.Query;
import network.Shard;
import shards.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Created 10/24/2016
 * Software Development - Team 2063-1
 * Colorado TSA Conference - Feb 2017
 * <p>
 * Purpose: Main is the launching point of the program.
 * Both the Node and Query classes can be launched through
 * here via the command line.
 */
public class Main {


    static private final String IPV4_REGEX = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
    static private Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

    public static void main(String[] args) {
        ArrayList<String> argsList = new ArrayList<>();
        Collections.addAll(argsList, args);

        if (!argsList.contains("nogui")) {
//            FXMenu.run(args);
        } else if (argsList.get(0).equals("q")) {
            Query server = new Query(createShardList());
            server.startQuery(Integer.parseInt(argsList.get(1)));
        } else if (isValidIPV4(argsList.get(0))) {
            new Node(argsList.get(0), Integer.parseInt(argsList.get(1)));
        }

    }

    public static boolean isValidIPV4(final String s) {
        return IPV4_PATTERN.matcher(s).matches();
    }

    public static ArrayList<Shard> createShardList() {
        ArrayList<Shard> shardList = new ArrayList<>();
        shardList.add(new Contracts());
        shardList.add(new Players());
        shardList.add(new Items());
        shardList.add(new Companies());
        shardList.add(new Cubits());

        return shardList;
    }

}

