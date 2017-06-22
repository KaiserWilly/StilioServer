package classes;

import java.io.Serializable;
import java.time.Duration;

/**
 * Created by JD Isenhart on 4/11/2017.
 * Testing RMI creation in Java 8
 */
public class Contract implements Serializable {
    private String companyName;
    private int sellerid, itemID, itemQty;
    private Duration contLength;
    private int contractID;

    public Contract(int sellerId, String companyName, int itemID, int itemQty) {
        this.sellerid = sellerId;
        this.companyName = companyName;
        this.itemID = itemID;
        this.itemQty = itemQty;
    }

    public Contract(int sellerId, String companyName, int itemID, int itemQty, Duration contLength) {
        this.sellerid = sellerId;
        this.companyName = companyName;
        this.itemID = itemID;
        this.itemQty = itemQty;
        this.contLength = contLength;
    }

    public Contract(int contID, int sellerId, String companyName, int itemID, int itemQty, Duration contLength) {
        this.contractID = contID;
        this.sellerid = sellerId;
        this.companyName = companyName;
        this.itemID = itemID;
        this.itemQty = itemQty;
        this.contLength = contLength;
    }

    public int getSellerID() {
        return sellerid;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getItemID() {
        return itemID;
    }

    public int getItemQty() {
        return itemQty;
    }

    public void setDuration(Duration d) {
        contLength = d;
    }

    public String getDurationISO() {
        return contLength.toString();
    }

    public Duration getDurationObject() {
        return contLength;
    }
}
