package network;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by JD Isenhart on 11/17/2016.
 * Testing RMI creation in Java 8
 */
public interface InifQuery extends Remote {
    void registerNode(String ip, int port) throws RemoteException;

    void removeArray(Array a) throws RemoteException;
}
