/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe responsável por realizar requisições TCP para o servidor.
 * @author kelvin
 */
public class ClienteTCP{
    
    private Socket cliente;   
    private String userName;
    private String textName;
    private HashMap<Character,HashSet<String>> dic;
    ObjectOutputStream output;
    ObjectInputStream input;
    MulticastReceiver receiver;   
    MulticastPublisher publisher;
    private String ipGroup;
    private String letras;
    private boolean joining;
    private String nameRoom;
    private boolean key;
    public int count;

    public boolean isKey() {
        return key;
    }

    public void setKey(boolean key) {
        this.key = key;
    }

    public String getLetras() {
        return letras;
    }

    public void setLetras(String letras) {
        this.letras = letras;
    }
    
    public ClienteTCP(String textName, String userName){
        this.userName = textName;        
        this.userName = userName;
        this.joining = false;
        this.key = false;
        this.count = 0;
        ManagerFiles files = new ManagerFiles();  
        this.dic = files.loadWords();
        
    }
    
    public void sendMulticast(String msg) throws UnknownHostException {
        MulticastPublisher request = new MulticastPublisher(msg, this.ipGroup);
        Thread nova = new Thread(request);
        nova.start();
    }
    
    public boolean verifyWord(String word){
        
        HashSet<String> value = this.dic.get(word.charAt(0));
        boolean res = value.contains(word);        
        return res;
    }

    public String getIpGroup() {
        return ipGroup;
    }

    public void setIpGroup(String ipGroup) {
        this.ipGroup = ipGroup;
    }          
    
    public boolean conectServer(String ip, String port) throws IOException{
        // para se conectar ao servidor, cria-se objeto Socket.
        // O primeiro parâmetro é o IP ou endereço da máquina que
        // se quer conectar e o segundo é a porta da aplicação.
        // Neste caso, usa-se o IP da máquina local (127.0.0.1)
        // e a porta da aplicação ServidorDeEco (12345).
         this.cliente = new Socket(ip, Integer.parseInt(port));         
        
        System.out.println("Você se conectou ao servidor!");
        output = new ObjectOutputStream(this.cliente.getOutputStream());
        input = new ObjectInputStream(this.cliente.getInputStream());
        return true;        
                    
    }
    
    public MulticastReceiver getReceiver() {
        return receiver;
    }

    public void setReceiver(MulticastReceiver receiver) {
        this.receiver = receiver;
    }
    
    public String sendReq(String msg) throws IOException {                
        
        output.writeUTF(msg);
        output.flush();
        String res = input.readUTF();        
        if(res.split(";")[0].equals("1")){
            this.ipGroup = res.split(";")[1];
            this.letras = res.split(";")[2];
            System.out.println(res.split(";")[3]);
            this.nameRoom = res.split(";")[3];
            this.joining = true;
            this.key = true;
            this.conectRoom(this.ipGroup);
        }
        if(res.split(";")[0].equals("2")){            
            this.ipGroup = res.split(";")[1];
            this.letras = res.split(";")[2];
            this.joining = true;
            this.nameRoom = res.split(";")[3];
            this.conectRoom(this.ipGroup);
        }        
        System.out.println(res);
        int length = res.split(";").length;
        return res.split(";")[length-1];
        
    }
    
    public void conectRoom(String port) {
        this.receiver = new MulticastReceiver(port);
        Thread nova = new Thread(this.receiver);
        nova.start();
    }

    public String getNameRoom() {
        return nameRoom;
    }

    public void setNameRoom(String nameRoom) {
        this.nameRoom = nameRoom;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTextName() {
        return textName;
    }

    public void setTextName(String textName) {
        this.textName = textName;
    }
    
    public Socket getCliente() {
        return cliente;
    }

    public void setCliente(Socket cliente) {
        this.cliente = cliente;
    }    
        

    
    
}
