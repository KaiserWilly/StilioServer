package network.shard.inif;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by JD Isenhart on 11/17/2016.
 * Testing RMI creation in Java 8
 */
public interface InifContract extends Remote{

    int getContractNumber() throws RemoteException;

}
