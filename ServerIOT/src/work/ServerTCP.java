import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Classe responsável por rodar o Servidor TCP.
 * @author kelvin
 */
public class ServerTCP implements Runnable{
    
    public Socket server;
    private ManagerFiles files;
    static ArrayList<User> on_users = new ArrayList<User>();
    static ArrayList<Room> rooms = new ArrayList<Room>();
    static String[] ips = {
        "230.0.0.0",
        "230.0.0.1",
        "230.0.0.2",
        "230.0.0.3",
        "230.0.0.4",
        "230.0.0.5",
        "230.0.0.6",
        "230.0.0.7",
        "230.0.0.8",
        "230.0.0.9",
        "230.0.0.10",
        "230.0.0.11",
        "230.0.0.12"
        
        
    };
    private String temp;
    static int somador = 0;
    ObjectOutputStream output;
    ObjectInputStream input;
    String str = null;
    
    
    /**
     * Construtor que aceita um Socket como parâmetro
     * @param server 
     */
    public ServerTCP(Socket server){
        this.server = server;
        this.files = new ManagerFiles();
    }
    /**
     * Método principal para iniciar o SERVER que aceita conexões UDP e TCP.
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
    try {
      // Instancia o ServerSocket ouvindo a porta 12345 {Porta TCP}
      ServerSocket servidor = new ServerSocket(8000);         

      System.out.println("Servidor ouvindo a porta 8000");
      while(true){
        // o método accept() bloqueia a execução até que
        // o servidor receba um pedido de conexão
        Socket server = servidor.accept();
        
        System.out.println("Cliente conectado: " + server.getInetAddress().getHostAddress());
        //Código para atender o cliente em uma Thread, e que faz atender vários cliente de forma
        // simultânea.
        //User user = new User(server);
        //on_users.add(user);
        ServerTCP tratamento = new ServerTCP(server);
          Thread t = new Thread(tratamento);
          // Inicia a thread para o cliente conectado
          t.start();
          //Depois daqui retorna para o método accept(){Aceita uma Conexão nova de outro cliente.}
        }
        
    }   
    catch(IOException e) {
        //Detecção da exceção
       System.out.println("Erro: " + e.getMessage());
       //tratamento aqui
       // ...
    }
    finally { }  
  }     

    @Override
    public void run()  {       
               
        try{
                
        this.output = new ObjectOutputStream(this.server.getOutputStream());        
        this.input = new ObjectInputStream(this.server.getInputStream());
        do{        
        this.str = this.input.readUTF();     
                System.out.println("Cliente enviou: " + str);
        if(str.split(";")[0].equals("1")) {//Criar Sala
            this.temp = ServerTCP.ips[ServerTCP.somador];
            if(this.createRoom(this.server, str.split(";")[1],str.split(";")[2],this.temp)){
                Room room = this.findRoom(str.split(";")[1]);
                System.out.println("Sua sala foi criada");
                String tabuleiro = this.generateTab();
                room.setLetras(tabuleiro);
                output.writeUTF("1"+";"+this.temp+";"+tabuleiro+";"+str.split(";")[1]+";in");
                output.flush();
                System.out.println(str.split(";")[1]);
            }
            else {
                output.writeUTF("ERRO 230");
                output.flush();
            }
        }
        if(str.split(";")[0].equals("2")) {//Entrar na Sala  2;Nome_da_Sala          
            if(this.joinRoom(this.server, str.split(";")[1], str.split(";")[2])){
                Room room = this.findRoom(str.split(";")[1]);
                System.out.println("alguem entrou na sala "+room.getName()+" com "+room.getUsers().size()+" jogadores");                
                output.writeUTF("2"+";"+room.getEndMulti()+";"+room.getLetras()+";"+room.getName()+";"+"in");
                output.flush();                
            }else{
                output.writeUTF("ERROR 400");
                output.flush();
            }
        }
        if(str.equals("exit")) {            
            output.writeUTF("Você se desconectou do servidor");
            output.flush();
            //Fecha conexão de saída de dados
            output.close();
            //fecha conexão de entrada de dados
            input.close();            
            this.server.close();
        }                                                                         
        
        
        }while(!this.str.equals("exit"));                                
                       
    }   catch (IOException ex) {        
            Logger.getLogger(ServerTCP.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
        
    
    public String generateTab(){
        String[] faces = {
            "A;A;E;E;G;N",
            "A;B;B;J;O;O",
            "A;C;H;O;P;S",
            "A;F;F;K;P;S",
            "A;O;O;T;T;W",
            "C;I;M;O;T;U",
            "D;E;I;L;R;X",
            "D;E;L;R;V;Y",
            "D;I;S;T;T;Y",
            "E;E;G;H;N;W",
            "E;E;I;N;S;U",
            "E;H;R;T;V;W",
            "E;I;O;S;S;T",
            "E;L;R;T;T;Y",
            "H;I;M;N;U;Q",
            "H;L;N;N;R;Z" 
        };
        ArrayList<String> temps = new ArrayList<>();
        ArrayList<String> tabuleiro = new ArrayList<>();
        
        for(int i =0; i< faces.length; i++){
            temps.add(faces[i]);
        }
        
        Random random = new Random();
        int x;             
        int y;
        
        while(temps.size()>=1) {
            x = random.nextInt(temps.size());
            y = random.nextInt(6);
            tabuleiro.add(temps.get(x).split(";")[y]);
            temps.remove(x);
        }
        String tab = "";
        for(int i = 0; i< 16; i++) {
            if(i==0) tab = tab+tabuleiro.get(i);
            else tab = tab+","+tabuleiro.get(i);
            
        }
        System.out.println(tab);
        return tab;
    }
    
   public boolean createRoom(Socket socket, String name, String username, String ipMulticast) {
       ServerTCP.somador++;       
       User temp = new User(socket,username);
       Room novo = new Room(temp, name, ipMulticast);
       boolean res = ServerTCP.rooms.add(novo);
       return res;
       
   }
   
   public boolean joinRoom(Socket socket, String name, String username) {
       Room room = findRoom(name);
       if(room==null) return false;
       User user = new User(socket, username);
       boolean res = room.getUsers().add(user);
       return res;
   }
   
    public User findUser(String ip) {
       Iterator<User> temp =  ServerTCP.on_users.iterator();
        while(temp.hasNext()){
            User user = temp.next();
            if(user.getIp().equals(ip)){
                return user;
            }
        }
       
       return null;
       
   }
    
    public Room findRoom(String name) {
       Iterator<Room> temp =  ServerTCP.rooms.iterator();
        while(temp.hasNext()){
            Room room = temp.next();
            if(room.getName().equals(name)){
                return room;
            }
        }
       
       return null;
       
   }
}
