package shards;

import classes.Company;
import classes.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by JD Isenhart on 1/17/2017.
 * Testing RMI creation in Java 8
 */
public interface RecordsInif extends Remote {

    boolean checkIfNew(Player p) throws RemoteException;

    boolean checkPassword(Player p) throws RemoteException;

    boolean registerNewClient(Player p, Company com) throws RemoteException;

    Company getCompany(Player p) throws RemoteException;
}
