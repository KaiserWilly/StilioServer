package classes;

import java.io.Serializable;

/**
 * Created by JD Isenhart on 5/3/2017.
 * Testing RMI creation in Java 8
 */
public class Item implements Serializable {

    private int itemID;
    private String itemName;


    public Item(int itemID, String name) {
        this.itemID = itemID;
        this.itemName = name;
    }

    public int getID() {
        return itemID;
    }

    public String getItemName() {
        return itemName;

    }
}
