package stefano;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.logging.Level;
import java.util.logging.Logger;
import com.stefano.android.Envelop;


public class Server {
    
    private boolean running=true;
     private ServerSocket serv = null;
     private RSA algorRSA;
     private AES algorAES;
     private ArrayList<Client>users;
     
     

//    public static void main(String[] args) throws UnsupportedEncodingException, Exception {
//       
//       
//       Server s=new Server();
//        s.listener();
//        
//       
//
//    }
    
    public Server() throws UnsupportedEncodingException, Exception{
          
        
        
        algorRSA=new RSA();
        users=new ArrayList<Client>();
       
       String s=algorRSA.encryptPu("AUTORE STEFANO");
          s=algorRSA.decryptPr(s);
          System.out.println(s);
          
          byte[] data=algorRSA.encryptPuByte(s.getBytes());
        
       
       
    }
    
    public void listener() throws ClassNotFoundException{
         try {
            serv = new ServerSocket(2400);
            
            
        } catch (IOException e1) {
            
            e1.printStackTrace();
        }
        try {

            while (running) {

                System.out.println("Server in ascolto");
                Socket sock = serv.accept();
                System.out.println("client accettato");
                
                   
               
                
                //sockList.add(new PrintWriter(sock.getOutputStream()));
                
                //passaggio chiavi pubbliche e privata
               // new ServThread(sock,sockList, id, algorRSA.getPuKey(),algorRSA.getPrKey()).start();
               
                new ServerThread(sock, algorRSA.getPuKey(),users).start();  
                
            }
        } catch (IOException e) {
            System.err.println("Avvio Server");

        }
    }
    
    
    
    public class ServerThread extends Thread{
        
        
        
        private Socket sock;
        private BigInteger[] PuKey;
        private BigInteger[] PrKey;
        private ObjectOutputStream output;
        private ObjectInputStream input;
        private Connection conn;
        private RSASend clientRSASend;
        private ArrayList<Client> users;
        Client myUsers;
        
        
        
        public ServerThread(Socket sock,BigInteger[] PuKey,ArrayList<Client> users) throws IOException, ClassNotFoundException
        {
           
                this.sock=sock;
                this.PuKey=PuKey;
                this.input=input;
                
                this.clientRSASend=new RSASend();    
                this.users=users;
                myUsers=new Client();
                 
                 this.output=new ObjectOutputStream(sock.getOutputStream());
                 
               
             
        }
        public void accessDB(){
             
             try {
                  Class.forName("org.sqlite.JDBC");
                  conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\lukie\\Downloads\\sqlite-tools-win32-x86-3110000\\Client.db");
                  
                   } catch ( Exception e ) {
                     System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                     System.exit(0);
                     }
                    System.out.println("Opened database successfully");
                  }
        
        public boolean insertDB(String user, String passw,String name, String mail) throws SQLException{
         Statement stmt = null;
            try {
      
                 conn.setAutoCommit(false);
                 stmt = conn.createStatement();
                   String sql = "INSERT INTO Client(USER,PASSWORD,NAME,MAIL) " +
                   "VALUES ('"+user+"','"+passw+"','"+name+"','"+mail+"' );"; 
                    stmt.executeUpdate(sql);
                 stmt.close();
                 conn.commit();
                 conn.close();
                 } catch ( Exception e ) {
                    System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                   return false;
                }
                System.out.println("Records created successfully");
                return true;
            
        }
        
        public boolean selectDB(String user, String passw) throws SQLException{
            
    
      try {
     
        Statement stmt = null;
        stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery( "SELECT * FROM Client;" );
         while ( rs.next() ) {
         
         String  user_name = rs.getString("USER");
         String password =rs.getString("PASSWORD");
         if(user.equals(user_name) && password.equals(passw))
         {           
            
            System.out.println( "USER = " + user_name );
            System.out.println( "PASSWORD = " + password );
            rs.close();
            stmt.close();
            conn.close();
            return true;
         }
         
      }
        rs.close();
        stmt.close();
        conn.close();
        return false;
      
    } catch ( Exception e ) {
      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
      System.exit(0);
    }
           return false; 
            
        }
        private boolean attemptLog() throws ClassNotFoundException, IOException, SQLException{
            
            
            boolean check;
            String us=(String)this.input.readObject();
            us=algorRSA.decryptPr(us);
            System.out.println("user: "+us);
            String passw=(String)this.input.readObject();
            passw=algorRSA.decryptPr(passw);
            System.out.println("password: "+passw);
               
            clientRSASend.setPuKey((BigInteger[])this.input.readObject());
             check=selectDB(us,passw);
            if(check==true){
                output.writeObject(algorRSA.encryptPr("OK").toString());
                output.flush();
                myUsers.userName=us;
                myUsers.cripto=ModeClient.NO;
                myUsers.passwordRSA=clientRSASend;
                myUsers.sent=output;
                users.add(myUsers);
                return false;
            }
               
            else{
                 output.writeObject(algorRSA.encryptPr("ACK").toString());
                 output.flush();
                
            return true;    
            }
            
            
            
        }
        
        private void registration() throws IOException, ClassNotFoundException, SQLException{
            
            //ricevo oggetto registrazione
            boolean check;
            String us=(String)this.input.readObject();
            us=algorRSA.decryptPr(us);
            System.out.println("user: "+us);
            String passw=(String)this.input.readObject();
            passw=algorRSA.decryptPr(passw);
            System.out.println("password: "+passw);
            String name=(String)this.input.readObject();
            name=algorRSA.decryptPr(name);
            System.out.println("name: "+name);
            String mail=(String)this.input.readObject();
            mail=algorRSA.decryptPr(mail);
            System.out.println("mail: "+mail);
            check=insertDB(us,passw,name,mail);
            if(check==true){
                output.writeObject(algorRSA.encryptPr("ACK").toString());
                output.flush();
            }
            else{
                output.writeObject(algorRSA.encryptPr("ERROR").toString());
                output.flush();
                
            }
        } 
        
        private void handshake(){
            //fase iniziale di connessiion da spostare qui dentro
             try{
                   String s;  
		
                boolean check=true;
                
                System.out.println("invio E: "+PuKey[0].toString());
                System.out.println("invio N: "+PuKey[1].toString());
                output.writeObject(this.PuKey);
                output.flush();
                this.input=new ObjectInputStream(sock.getInputStream());
                
                //aggiungi decrittografia ack
                 while(check){
                     s=(String)input.readObject();
                    
                     s=algorRSA.decryptPr(s);
                     System.out.println("stampo ACK: "+s);
                    if(s.equals("ACK")){
                        System.out.println("ACK ricevuto");
                       check=false;
                    }
                }
                 
                 boolean login=true;
               while(login)
               {
                   accessDB();
                   //fase di Login o Registrazione
                   
                   String c=(String)input.readObject();
                   c=algorRSA.decryptPr(c);
                   if(c.equals("log"))
                   {
                      
                      login=attemptLog(); 
                   }
                   else
                   {
                       registration();
                   }
                       
                   
               }
               System.out.println("funziona il login");
                }catch (IOException e) {
			System.err.println(" Buffer input e output");
			e.printStackTrace();
		} catch (ClassNotFoundException ex) {
                System.err.println("No classe ");
            } catch (SQLException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
        public void run(){
                
           handshake();
               
            try{
               boolean running=true;
               Object s;
               while(running){
                   ObjectOutputStream sendMessage;
                        Envelop e=new Envelop();
                       Client c=new Client();
                       
			if((e=(Envelop)input.readObject())!=null)
                         {
                            
                             System.out.println(e.getFrom()+" scrive: "+e.getText());
                        
                          // s=algorRSA.decryptPr(s);
                          
			   for(Iterator<Client> iter=users.iterator(); iter.hasNext();){
                               
                               sendMessage=iter.next().sent;
                               sendMessage.reset();
                               
                               sendMessage.writeObject(e);
                               sendMessage.flush();
                               System.out.println("ho inviato: "+e.getText());
                           }
                          
                         }
                          			  
                   
                   
               }
			
			
		} catch (IOException e) {
			System.err.println("Creazione Buffer input e output");
			e.printStackTrace();
		} catch (ClassNotFoundException ex) {
                System.err.println("Classe inesistente");
            }

            
        }
    }
}   
  
				


