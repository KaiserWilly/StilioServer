package network.shard;

import network.shard.inif.InifContract;

import java.rmi.RemoteException;

/**
 * Created by JD Isenhart on 11/17/2016.
 * Testing RMI creation in Java 8
 */
public class ContractShard extends Shard implements InifContract{
    public ContractShard(){
        super("Contract");
    }

    public int getContractNumber() throws RemoteException {
        return 0;
    }
}
