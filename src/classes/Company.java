package classes;

import java.io.Serializable;
import java.time.Duration;

/**
 * Created by james on 1/24/2017.
 */
public class Company implements Serializable {
    private Player owner;
    private String name;
    private Industry industry;
    private Field field;
    private Cubit cubits;


    public Company(Player owner, Industry industry, Field field, String name) { //Player-run
        this.owner = owner;
        this.industry = industry;
        this.field = field;
        this.name = name;
    }

    public Company(String name, Industry industry, AIProfile profile, Duration duration) { //AI Company

    }

    public Player getOwner() {
        return owner;
    }

    public Field getField() {
        return field;
    }

    public Industry getIndustry() {
        return industry;
    }

    public String getCompanyName() {
        return name;
    }

    public Cubit getCubits() {
        return cubits;

    }

}
