package network;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by JD Isenhart on 12/7/2016.
 * Testing RMI creation in Java 8
 */
public class QueryClient implements InifQueryClient {
    List<Array> arrayList = new ArrayList<>();
    HashMap<Array, Integer> balance = new HashMap<>();

    public Array assignToArray() throws RemoteException {
        List<Integer> counts = new ArrayList<>();
        for (Array a : arrayList) {
            counts.add(balance.get(a));
        }
        int min = Integer.MAX_VALUE;
        for (int i : counts) {
            if (min > i) min = i;
        }
        return arrayList.get(counts.indexOf(min));
    }

    public void openArray(Array data) {
        arrayList.add(data);
        balance.put(data, 0);
        System.out.println("Array Opened to Clients!");
    }

    public void closeArray(Array data) {
        arrayList.remove(data);
        System.out.println("Array Closed to Clients!");
    }
}
