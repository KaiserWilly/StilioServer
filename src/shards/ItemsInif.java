package shards;

import classes.Item;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by JD Isenhart on 5/3/2017.
 * Testing RMI creation in Java 8
 */
public interface ItemsInif extends Remote {
    Item getItem(int itemID) throws RemoteException;
}
