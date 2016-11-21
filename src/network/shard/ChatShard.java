package network.shard;

import network.shard.inif.InifChat;

/**
 * Created by james on 10/24/2016.
 */
public class ChatShard extends Shard implements InifChat {
    public ChatShard() {
        super("Chat");
    }
}
