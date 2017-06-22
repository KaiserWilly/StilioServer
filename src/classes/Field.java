package classes;

import java.util.ArrayList;

/**
 * Created by james on 6/21/2017.
 */
public class Field {
    String name;
    ArrayList<Cubit> requires;

    public Field(ArrayList<Cubit> requiredCuibtis, String name){

    }
    public Field(String name){

    }

    public String getName(){
        return name;
    }
    public ArrayList<Cubit> getRequiredCubits(){
        return requires;
    }
}
