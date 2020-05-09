/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.util.ArrayList;

/**
 *
 * @author kelvin
 */
public class Room {
    
    private ArrayList<User> users;    
    private String name;
    private String endMulti;
    private String letras;

    public String getLetras() {
        return letras;
    }

    public void setLetras(String letras) {
        this.letras = letras;
    }
    
    
    public Room(User dono, String name, String endMulti) {        
        this.name = name;
        this.endMulti = endMulti;
        this.users = new ArrayList<User>();
        this.users.add(dono);
    }

    public String getEndMulti() {
        return endMulti;
    }

    public void setEndMulti(String endMulti) {
        this.endMulti = endMulti;
    }            

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    } 
    
}
