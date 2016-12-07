import fx.FXMenu;
import network.Node;
import network.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Created by JD Isenhart on 10/24/2016.
 * Testing RMI creation in Java 8
 */
public class Main {


    static private final String IPV4_REGEX = "(([0-1]?[0-9]{1,2}\\.)|(2[0-4][0-9]\\.)|(25[0-5]\\.)){3}(([0-1]?[0-9]{1,2})|(2[0-4][0-9])|(25[0-5]))";
    static private Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);

    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        ArrayList<String> argsList = new ArrayList<>();
        Collections.addAll(argsList, args);

        if (!argsList.contains("nogui")) {
            FXMenu.run(args);
        } else if (argsList.get(0).equals("q")) {
            Query server = new Query();
            server.startQuery(Integer.parseInt(argsList.get(1)));
        } else if (isValidIPV4(argsList.get(0))) {
            new Node(argsList.get(0), Integer.parseInt(argsList.get(1)));
        }

    }

    public static boolean isValidIPV4(final String s) {
        return IPV4_PATTERN.matcher(s).matches();
    }

}

