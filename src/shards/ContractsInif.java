package shards;

import classes.Contract;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.ResultSet;
import java.util.List;

/**
 * Created by JD Isenhart on 1/17/2017.
 * Testing RMI creation in Java 8
 */
public interface ContractsInif extends Remote {

    void insertContract(Contract c) throws RemoteException;

    void removeContract(int contractID) throws RemoteException;

    ResultSet getResultSet(String sql) throws RemoteException;

    List<Contract> getResultSetList(String sql) throws RemoteException;

    List<Contract> contractsBySeller(int sellerID) throws RemoteException;


}
