/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.Socket;

/**
 *
 * @author kelvin
 */
public class User {
    
    private Socket cliente;
    private String ip; 
    private String userName;
    
    public User(Socket socket, String userName) {
        this.cliente = socket;
        this.ip = socket.getInetAddress().getHostAddress();        
        this.userName = userName;
    }  

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Socket getCliente() {
        return cliente;
    }

    public void setCliente(Socket cliente) {
        this.cliente = cliente;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    
}
