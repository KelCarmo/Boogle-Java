/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kelvin
 */
public class MulticastReceiver implements Runnable {
    
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];
    protected String ipGroup;
    private boolean start;
    private boolean stop;
    private HashMap<String,HashSet<String>> palavras_digitadas;    

    public boolean isStart() {
        return start;
    }

    public void setStart(boolean start) {
        this.start = start;
    }
    
    public MulticastReceiver(String ipGroup) {
        this.ipGroup = ipGroup;
        this.start = false;
        this.stop = false;
        this.palavras_digitadas = new HashMap<String,HashSet<String>>();
    }         

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
    
    public HashMap<String, Integer> calcResults() {
        Set<String> chaves = this.palavras_digitadas.keySet();
        HashMap<String, Integer> temp = new HashMap<String, Integer>();
        int i = 0;
        for (String chave : chaves){
            i = 0;
            if(chave != null) {
                Iterator<String> it = this.palavras_digitadas.get(chave).iterator();
                String tt;
                while(it.hasNext()){
                   tt = it.next();
                   //Chamo função que verifica se já existe essa palavra digitada por alguém
                   if(this.isWord(chave, tt)){
                       i = i + (tt.length()/2);
                   }else{
                       i = i + tt.length();
                   }
                }
                temp.put(chave, i);
                 //System.out.println(chave + this.palavras_digitadas.get(chave));
            }
	}
        return temp;
    }
    
    private boolean isWord(String chave, String word){
        Set<String> chaves = this.palavras_digitadas.keySet();
        
        for(String key : chaves){
            if(key != null) {
                
                if(!key.equals(chave)){
                    Iterator<String> it = this.palavras_digitadas.get(key).iterator();
                    String tt;
                    while(it.hasNext()){
                        tt = it.next();
                        if(tt.equals(word)) return true;
                    }
                }
                
                
            }
        }
        return false;
    }
 
    @Override
    public void run() {
        try {
            socket = new MulticastSocket(4446);
            InetAddress group = InetAddress.getByName(this.ipGroup);
            socket.joinGroup(group);
            System.out.println("Esperando mensagem do grupo ...");
            while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(
              packet.getData(), 0, packet.getLength());
            System.out.println(received);
            if ("end".equals(received)) {
                break;
            }
            if(received.split(";")[0].equals("1")){                
                if(this.palavras_digitadas.containsKey(received.split(";")[2])){
                    this.palavras_digitadas.get(received.split(";")[2]).add(received.split(";")[3]);
                }else{
                    HashSet<String> temp = new HashSet<String>();
                    temp.add(received.split(";")[3]);
                    this.palavras_digitadas.put(received.split(";")[2], temp);
                }
                
            }
            if(received.equals("start")){
                Thread x = new Thread(() -> {
                    try {
                        start = true;
                        Thread.sleep(180000);
                        socket.leaveGroup(InetAddress.getByName(this.ipGroup));
                        socket.close();
                        stop = true;
                    } catch (InterruptedException ex) {
                        Logger.getLogger(MulticastReceiver.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (UnknownHostException ex) {
                        Logger.getLogger(MulticastReceiver.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(MulticastReceiver.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                });
                x.start();
            }
            
            }
            socket.leaveGroup(group);
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(MulticastReceiver.class.getName()).log(Level.SEVERE, null, ex);
        }
                
    }
    
    
    
}
