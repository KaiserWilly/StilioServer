package network.shard;

import network.shard.inif.InifFiling;

/**
 * Created by JD Isenhart on 11/17/2016.
 * Testing RMI creation in Java 8
 */
public class FilingShard extends Shard implements InifFiling{
    public FilingShard() {
        super("Filing");
    }


}
