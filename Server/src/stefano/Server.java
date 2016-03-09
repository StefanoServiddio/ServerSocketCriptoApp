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
import com.stefano.android.*;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;


public class Server {
    
     private boolean running=true;
     private ServerSocket serv = null;
     private NewRSA algorRSA;
     
     private ArrayList<Client>users;
     private Window form;
     
     

    
    public Server(Window form) throws UnsupportedEncodingException, Exception{
          
        
        this.form=form;
        this.users=new ArrayList();
        algorRSA=new NewRSA();
        
       algorRSA.generateRsaKeyPair(1024, BigInteger.probablePrime(15, new Random()));
       
       
      
       
       
    }
    public void close() throws IOException
    {
        serv.close();
    }
    
    public void listener() throws ClassNotFoundException{
         try {
            serv = new ServerSocket(2400);
            
            
        } catch (IOException e1) {
            
            e1.printStackTrace();
        }
        try {

            while (running) {

                
                form.textArea1.setText("Server in ascolto sulla Porta 2400");
               
                
                Socket sock = serv.accept();
                form.textArea1.append("\nclient acettato");
                form.textArea1.append("\nil client è in ascolto");
                
                                  
                if(users==null)
                    System.out.println(" è a nullo");
               
               
                new ServerThread(sock,algorRSA,users).start();  
                
            }
        } catch (IOException e) {
            System.err.println("Avvio Server");

        }
    }

    private Object InetAddress() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    public class ServerThread extends Thread{
        
        
        
        private Socket sock;
        private NewRSA myRSA;
        private ObjectOutputStream output;
        private ObjectInputStream input;
        
        //database binding
        private Connection conn;
        
        //per la creazione del client
       private NewRSA sendRSA;
//        private AES clientAESSend;
//        private TripleDES clientDES3Send;
//        private Blowfish clientBlowSend;
//        private HmacSha1 clientHmacSend;
        private ArrayList<Client> listaUser;
        Client myUser;
        
        
        
        
        public ServerThread(Socket sock,NewRSA myRSA,ArrayList users) throws IOException, ClassNotFoundException
        {
           
                this.sock=sock;
                this.myRSA=myRSA;
                this.input=null;
                
                this.sendRSA=new NewRSA();    
                this.listaUser=users;
                if(listaUser!=null)
                System.out.println("non sono a null");
                this.output=new ObjectOutputStream(sock.getOutputStream());
                myUser =new Client();
                
               
                 
               
             
        }
        public void accessDB(){
             
             try {
                  Class.forName("org.sqlite.JDBC");
                  conn = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\lukie\\Downloads\\sqlite-tools-win32-x86-3110000\\Client.db");
                  
                   } catch ( Exception e ) {
                     System.err.println( e.getClass().getName() + ": " + e.getMessage() );
                     System.exit(0);
                     }
                    form.textArea1.append("\nOpened database successfully");
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
                form.textArea1.append("\nRecords created successfully");
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
            
            form.textArea1.append( "\nUSER = " + user_name );
            form.textArea1.append( "\nPASSWORD = " + password );
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
        private boolean attemptLog() throws IOException, InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, SQLException, ClassNotFoundException, InvalidKeySpecException, InvalidAlgorithmParameterException, SignatureException{
            
            
            byte[] algKey=null;
           
            boolean check;
            
                   
               
                     
            algKey=(byte[])this.input.readObject();
            String us=new String((algKey=myRSA.rsaDecrypt(algKey,myRSA.getKPair().getPrivate())));
            form.textArea1.append("\nuser: "+us);
            algKey=(byte[])this.input.readObject();
            String passw=new String((algKey=myRSA.rsaDecrypt(algKey,myRSA.getKPair().getPrivate())));
            form.textArea1.append("\npassword: "+passw);
            
            
            algKey=(byte[])input.readObject();
            PublicKey chiave=KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(algKey));
            sendRSA.setKPu(chiave);
            form.textArea1.append("\nClient Pubblica RSA: "+Base64.getEncoder().encodeToString(chiave.getEncoded()));
           
           //invio chiavi pubbliche
           
          
                     
             algKey=(byte[])input.readObject();
             algKey=myRSA.rsaDecrypt(algKey,myRSA.getKPair().getPrivate());
             SecretKey keyAes=new SecretKeySpec(algKey,"AES");
            form.textArea1.append("\nChiave AES: "+Base64.getEncoder().encodeToString(keyAes.getEncoded())+
                    "\nlunghezza chiave: "+algKey.length);
            
            algKey=(byte[])input.readObject();
             algKey=myRSA.rsaDecrypt(algKey,myRSA.getKPair().getPrivate());
             this.myUser.clientAES=new AES(keyAes,algKey);
//            form.textArea1.append("\n Iv ottenuto: "+Base64.getEncoder().encodeToString(keyAes.getEncoded())+
//                    "\nlunghezza chiave: "+algKey.length);
            
            
           
             
             algKey=(byte[])input.readObject();
             algKey=myRSA.rsaDecrypt(algKey,myRSA.getKPair().getPrivate());
             SecretKey keyDes=new SecretKeySpec(algKey,"DESede");
            form.textArea1.append("\nChiave DESede: "+Base64.getEncoder().encodeToString(keyDes.getEncoded())+
                                  "\nlunghezza chiave: "+algKey.length);
            
             algKey=(byte[])input.readObject();
             algKey=myRSA.rsaDecrypt(algKey,myRSA.getKPair().getPrivate());
             this.myUser.clientDES=new TripleDES(keyDes,algKey);
            
            algKey=(byte[])input.readObject();
             algKey=myRSA.rsaDecrypt(algKey,myRSA.getKPair().getPrivate());
             SecretKey keyBlow=new SecretKeySpec(algKey,"Blowfish");
            form.textArea1.append("\nChiave Blowfish: "+Base64.getEncoder().encodeToString(keyBlow.getEncoded())+
                    "\nlunghezza chiave: "+algKey.length);
            algKey=(byte[])input.readObject();
             algKey=myRSA.rsaDecrypt(algKey,myRSA.getKPair().getPrivate());
            this.myUser.clientBlow=new Blowfish(keyBlow,algKey);
            
            
            algKey=(byte[])input.readObject();
             algKey=myRSA.rsaDecrypt(algKey,myRSA.getKPair().getPrivate());
             SecretKey keyHmac=new SecretKeySpec(algKey,"HmacSHA1");
            form.textArea1.append("\nChiave Hmac: "+Base64.getEncoder().encodeToString(algKey)+
                    "\nlunghezza chiave: "+algKey.length);
            this.myUser.clientHash=new HmacSha1(keyHmac);
            
            
            
            
                      


             //controllo Login
             check=selectDB(us,passw);
            if(check==true){
                
                
                output.writeObject(sendRSA.rsaEncrypt("OK".getBytes(),sendRSA.getPu()));
                output.flush();
                myUser.userName=us;
                myUser.state=Envelop.Mode.NO;
                this.myUser.clientRSA=sendRSA;               
                this.myUser.sent=output;
               
               
              
                //aggiungo RSA alla lista di utenti
               
                if(listaUser!=null)
                    System.out.println("lista aggiornata");
                if((this.listaUser.add(myUser)))
                    System.out.println("sono qui dentro 2");
                return false;
            }
               
            else{
                 output.writeObject(sendRSA.rsaEncrypt("NoUser".getBytes(),sendRSA.getPu()));
                 output.flush();
                
            return true;    
            }
            
            
            
        }
        
        private void registration() throws IOException, ClassNotFoundException, SQLException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
            
            
            boolean check;
            byte[] datakey=null;
            
            
            datakey=(byte[])this.input.readObject();
            String us=new String((datakey=myRSA.rsaDecrypt(datakey,myRSA.getKPair().getPrivate())));
            form.textArea1.append("\nuser: "+us);
            
               
            
             datakey=(byte[])this.input.readObject();
            String passw=new String((datakey=myRSA.rsaDecrypt(datakey,myRSA.getKPair().getPrivate())));
             form.textArea1.append("\npassword: "+passw);
            
             datakey=(byte[])this.input.readObject();
            String name=new String((datakey=myRSA.rsaDecrypt(datakey,myRSA.getKPair().getPrivate())));
            
            form.textArea1.append("\nname: "+passw);
               datakey=(byte[])this.input.readObject();
            String mail=new String((datakey=myRSA.rsaDecrypt(datakey,myRSA.getKPair().getPrivate())));
           
            
            form.textArea1.append("mail: "+mail);
            check=insertDB(us,passw,name,mail);
            if(check==true){
                output.writeObject("ACK");
                output.flush();
            }
            else{
                output.writeObject("ERROR");
                output.flush();
                
            }
        } 
        
        private void handshake() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, InvalidAlgorithmParameterException, SignatureException{
            //fase iniziale di connessiion da spostare qui dentro
             try{
                   byte[] answ;  
                   String s="";
		
                boolean check=true;
                
                form.textArea1.append("\nInvio chiave Server Pubblica: "+myRSA.getKPair().getPublic());
                
                output.writeObject(myRSA.getKPair().getPublic());
                output.flush();
                this.input=new ObjectInputStream(sock.getInputStream());
                
                //aggiungi decrittografia ack
                 while(check){
                     answ=(byte[])input.readObject();
                    form.textArea1.append("\nRicevo ACK Criptato:\n"+s);
                    form.textArea1.append("\nDecripto:\n"+s);
                     answ=myRSA.rsaDecrypt(answ,myRSA.getKPair().getPrivate());
                    s=new String(answ);
                     form.textArea1.append("in:\n"+s);
                     
                     
                    if(s.equals("ACK")){
                        form.textArea1.append("\nACK ricevuto");
                       check=false;
                    }
                }
                 
                 boolean login=true;
               while(login)
               {
                   accessDB();
                   //fase di Login o Registrazione
                   
                   answ=(byte[])input.readObject();
                   answ=myRSA.rsaDecrypt(answ,myRSA.getKPair().getPrivate());
                   s=new String(answ);
                   
                   if(s.equals("log"))
                   {
                    
                      login=attemptLog(); 
                      
                   }
                   else
                   {
                       registration();
                   }
                       
                   
               }
               form.textArea1.append("\nLogin Accepted");
                }catch (IOException e) {
			System.err.println(" Buffer input e output");
			e.printStackTrace();
		} catch (ClassNotFoundException ex) {
                System.err.println("No classe ");
                ex.printStackTrace();
            } catch (SQLException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
        }
        public byte[] decryptoSimm( Envelop.Mode crittoValue, byte[] data) throws InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException{
            form.textArea1.append("Dati Ricevuti: "+Base64.getEncoder().encodeToString(data));
            switch (crittoValue)
            {
                case AES:
                    System.out.println("inizio decriptazione AES");
                    
                   
                    data=myUser.clientAES.decrypt(data);
                    
                     form.textArea1.append(" Decriptazione AES riuscita");
                    break;
                case DES3:
                    data=myUser.clientDES.decrypt(data);
                    form.textArea1.append(" Decriptazione DES3 riuscita");
                    break;
                    
                case Blow:
                    data=myUser.clientBlow.decrypt(data);
                    form.textArea1.append(" Decriptazione Blowfish riuscita");
                    break;
                
            }
            
            return data;
            
        }
        public byte[] cryptoSimm( byte[] data , Client receiver) throws InvalidKeyException, IllegalBlockSizeException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, InvalidAlgorithmParameterException, ShortBufferException{
            switch (receiver.state)
            {
                case AES:
                    
                    data=receiver.clientAES.encrypt(data);
                    break;
                case DES3:
                    data=receiver.clientDES.encrypt(data);
                    break;
                    
                case Blow:
                    data=receiver.clientBlow.encrypt(data);
                    break;
                default:
                    System.out.println("No crypto system Enable");
                    break;
                
            }
            
            return data;
            
        }
        public void run(){
            try{
                form.textArea1.append("\nRunning.....");
                handshake();
                Envelop.Mode crittoNow=Envelop.Mode.NO;
                
                try{
                    boolean running=true;
                    
                    while(running){
                        ObjectOutputStream sendMessage;
                        byte[] dataRecByte=null;
                        byte[] sendMess=null;
                        
                        Client destin;
                       
                        Envelop e=new Envelop();
                        
                        
                       
                        //Blocco Ricezione
                        
                        
                        if((dataRecByte=(byte[])input.readObject())!=null)
                        {
                           
                            //decriptazione e controllo modalità di cripting
                            //aggiungi firma digitale per decriptare l'hash
                            dataRecByte=decryptoSimm(crittoNow,dataRecByte);
                            System.out.println("Ho decriptato!!");
                            //Blocco Trasmissione
                            e=e.convByteEnv(dataRecByte);
                            System.out.println("messaggio convertito");
                            
//                              ricevuto il messaggio Envelop controllo il digest criptato con chiave pubblica
                       
                               
//                                 Calcolo hashing del testo ricevuto
                                 byte[] digest=myUser.clientHash.hashing(e.getText().getBytes());
                                 form.textArea1.append("\nCalcolo del Digest Client: "+Base64.getEncoder().encodeToString(digest)+
                                         "\ndi lunghezza: "+digest.length);
                                // Inserisci controllo firma
                                System.out.println("Problemi in questo punto\n");
                               
                                
                                 byte[]checkDigest=myRSA.rsaDecrypt(e.getMac(),myRSA.getKPair().getPrivate());
                                 form.textArea1.append("\nControllo   RSA del Client: "+Base64.getEncoder().encodeToString(checkDigest)+
                                         "\ndi lunghezza: "+checkDigest.length);
                                 System.out.println("Problemi in questo punto2\n");
                                if(MessageDigest.isEqual(checkDigest,digest)){
                                    form.textArea1.append("\nDigest  Corrispondenti");
                                
                                
                                
                            
                            form.textArea1.append("\n Ricevo da:\n"+e.getFrom()+": "+e.getText());
                            if(e.getText().equals("Change Crypto"))
                            {
                                crittoNow=e.getCripto();
                               //modifica modalità crittazione in users
                               System.out.println("new critto State"+ e.getCripto()+ "in "+crittoNow);
                               myUser.state=crittoNow;
                               //cerchiamo il client di cui vogliamo modificare lo stato di crittografia
                               for(Iterator<Client> iter=listaUser.iterator(); iter.hasNext();)
                                {
                                     destin=iter.next();
                                    if(destin.userName.equals(e.getFrom()))
                                        destin.state=crittoNow;
                        
                                   }
                            }
                            
                            else{
                            if(e.getTo().equals("") && e.getCripto()==Envelop.Mode.NO)
                            {
                                //trasmetto a tutti un messaggio non criptato
                                System.out.println("Ora invio a tutti");
                                for(Iterator<Client> iter=users.iterator(); iter.hasNext();){
                                    //controlare la possiblità di eleiminare sendMessage
                                    destin=iter.next();
                                    sendMessage=destin.sent;
                                    Envelop mess=new Envelop();
                                    mess=mess.convByteEnv(dataRecByte);
                                      digest=destin.clientHash.hashing(mess.getText().getBytes());

                        
                                    mess.setMac(destin.clientRSA.rsaEncrypt(digest,destin.clientRSA.getPu()));
                                   
                                    //INVIO DEL MESSAGGIO RICEVUTO in base al critto mode
                                    dataRecByte=mess.convEnvByte(mess);
                                    sendMess=cryptoSimm(dataRecByte,destin);
                                    sendMessage.writeObject(sendMess);
                                    
                                    sendMessage.flush();
                                    
                                }
                                System.out.println("Invio al gruppo completato con successo");
                            }
                            else{
                                //Trasmetto a tutti un messaggio con requisiti di critttografia
                                if(e.getTo().equals("")){
                                    switch(e.getCripto()){
                                        case AES:
                                            //crittoNow=Envelop.Mode.AES;
                                           
                                            for(Iterator<Client> iter=users.iterator(); iter.hasNext();){
                                            destin=iter.next();
                                            System.out.println("invio AES starting");
                                            if(destin.state.equals(Envelop.Mode.AES)){
                                                sendMessage=destin.sent;
                                             Envelop mess=new Envelop();
                                              mess=mess.convByteEnv(dataRecByte);
                                             digest=destin.clientHash.hashing(mess.getText().getBytes());

                        
                                           mess.setMac(destin.clientRSA.rsaEncrypt(digest,destin.clientRSA.getPu()));
                                                    dataRecByte=mess.convEnvByte(mess);
                                                //INVIO DEL MESSAGGIO RICEVUTO in base al critto mode
                                                 sendMess=cryptoSimm(dataRecByte,destin);
                                                 
                                                sendMessage.writeObject(sendMess);
                                                System.out.println("invio AES: "+Base64.getEncoder().encodeToString(sendMess)+
                                                     "\nlunghezza: "+sendMess.length);
                                                sendMessage.flush();
                                    
                                                }
                                            else{
                                                //inserisci modifica modalità critto per ricevere corretto
                                                }
                                            }
                                            break;
                                        case DES3:
                                           // crittoNow=Envelop.Mode.DES3;
                                            for(Iterator<Client> iter=users.iterator(); iter.hasNext();){
                                            destin=iter.next();
                                            if(destin.state.equals(Envelop.Mode.DES3) ||
                                                    destin.state.equals(Envelop.Mode.AES) ||
                                                    destin.state.equals(Envelop.Mode.Blow)){
                                            sendMessage=destin.sent;
                                            Envelop mess=new Envelop();
                                    mess=mess.convByteEnv(dataRecByte);
                                      digest=destin.clientHash.hashing(mess.getText().getBytes());

                        
                                    mess.setMac(destin.clientRSA.rsaEncrypt(digest,destin.clientRSA.getPu()));
                                              dataRecByte=mess.convEnvByte(mess);
                                            //INVIO DEL MESSAGGIO RICEVUTO in base al critto mode
                                            sendMess=cryptoSimm(dataRecByte,destin);
                                            sendMessage.writeObject(sendMess);
                                    
                                            sendMessage.flush();
                                    
                                                }
                                            else{
                                                //inserisci modifica modalità critto per ricevere corretto
                                                }
                                            }
                                            
                                            break;
                                        case Blow:
                                           // crittoNow=Envelop.Mode.Blow;
                                            for(Iterator<Client> iter=users.iterator(); iter.hasNext();){
                                            destin=iter.next();
                                            if(destin.state.equals(Envelop.Mode.Blow)||
                                                    destin.state.equals(Envelop.Mode.AES)){
                                            sendMessage=destin.sent;
                                            Envelop mess=new Envelop();
                                            mess=mess.convByteEnv(dataRecByte);
                                             digest=destin.clientHash.hashing(mess.getText().getBytes());

                        
                                    mess.setMac(destin.clientRSA.rsaEncrypt(digest,destin.clientRSA.getPu()));
                                               dataRecByte=mess.convEnvByte(mess);
                                            //INVIO DEL MESSAGGIO RICEVUTO in base al critto mode
                                            sendMess=cryptoSimm(dataRecByte,destin);
                                            sendMessage.writeObject(sendMess);
                                    
                                            sendMessage.flush();
                                    
                                                }
                                            else{
                                                //inserisci modifica modalità critto per ricevere corretto
                                                }
                                            }
                                            
                                            break;
                                            
                                            
                                        default:
                                            System.out.println("invio a vuoto");
                                            break;
                                            
                                    }
                                    
                                    
                                    
                                    
                                }
                                else{
                                    // Trasmetto ad un solo utente
                                    switch(e.getCripto()){
                                        
                                        case NO:
                                            //trova utente ed invia il messaggio senza crittografia
                                            
                                            for(Iterator<Client> iter=users.iterator(); iter.hasNext();){
                                            destin=iter.next();
                                            if(destin.userName.equals(e.getTo()) 
                                                    ||destin.userName.equals(e.getFrom())){
                                            sendMessage=destin.sent;
                                             Envelop mess=new Envelop();
                                            mess=mess.convByteEnv(dataRecByte);
                                             digest=destin.clientHash.hashing(mess.getText().getBytes());

                        
                                    mess.setMac(destin.clientRSA.rsaEncrypt(digest,destin.clientRSA.getPu()));
                                               dataRecByte=mess.convEnvByte(mess);
                                   
                                            //INVIO DEL MESSAGGIO RICEVUTO in base al critto mode
                                            sendMess=cryptoSimm(dataRecByte,destin);
                                            sendMessage.writeObject(sendMess);
                                    
                                            sendMessage.flush();
                                    
                                                }
                                            else{
                                                //inserisci modifica modalità critto per ricevere corretto
                                                }
                                            }
                                            
                                            break;
                                            //invia solo se ha AES
                                            
                                        case AES:
                                            
                                            
                                            for(Iterator<Client> iter=users.iterator(); iter.hasNext();){
                                            destin=iter.next();
                                            if((destin.state.equals(Envelop.Mode.AES)  ||
                                                    destin.state.equals(Envelop.Mode.Blow))
                                                    && 
                                                    (destin.userName.equals(e.getTo())|| destin.userName.equals(e.getFrom()))){
                                            sendMessage=destin.sent;
                                             Envelop mess=new Envelop();
                                            mess=mess.convByteEnv(dataRecByte);
                                             digest=destin.clientHash.hashing(mess.getText().getBytes());

                        
                                    mess.setMac(destin.clientRSA.rsaEncrypt(digest,destin.clientRSA.getPu()));
                                               dataRecByte=mess.convEnvByte(mess);
                                   
                                            //INVIO DEL MESSAGGIO RICEVUTO in base al critto mode
                                            sendMess=cryptoSimm(dataRecByte,destin);
                                            sendMessage.writeObject(sendMess);
                                    
                                            sendMessage.flush();
                                    
                                                }
                                            else{
                                                //inserisci modifica modalità critto per ricevere corretto
                                                }
                                            }
                                            break;
                                        case DES3:
                                               
                                            for(Iterator<Client> iter=users.iterator(); iter.hasNext();){
                                            destin=iter.next();
                                            if((destin.state.equals(Envelop.Mode.DES3) ||
                                                    destin.state.equals(Envelop.Mode.AES) ||
                                                    destin.state.equals(Envelop.Mode.Blow)) 
                                                    && (destin.userName.equals(e.getTo())||destin.userName.equals(e.getFrom()))){
                                            sendMessage=destin.sent;
                                            Envelop mess=new Envelop();
                                            mess=mess.convByteEnv(dataRecByte);
                                             digest=destin.clientHash.hashing(mess.getText().getBytes());

                        
                                    mess.setMac(destin.clientRSA.rsaEncrypt(digest,destin.clientRSA.getPu()));
                                               dataRecByte=mess.convEnvByte(mess);
                                   
                                            //INVIO DEL MESSAGGIO RICEVUTO in base al critto mode
                                            sendMess=cryptoSimm(dataRecByte,destin);
                                            sendMessage.writeObject(sendMess);
                                    
                                            sendMessage.flush();
                                    
                                                }
                                            else{
                                                //inserisci modifica modalità critto per ricevere corretto
                                                }
                                            }
                                            break;
                                            
                                        case Blow:
                                               
                                            for(Iterator<Client> iter=users.iterator(); iter.hasNext();){
                                            destin=iter.next();
                                            if((destin.state.equals(Envelop.Mode.Blow))
                                                    && (destin.userName.equals(e.getTo())||
                                                    destin.userName.equals(e.getFrom()))
                                                    ){
                                            sendMessage=destin.sent;
                                            Envelop mess=new Envelop();
                                            mess=mess.convByteEnv(dataRecByte);
                                             digest=destin.clientHash.hashing(mess.getText().getBytes());

                        
                                       mess.setMac(destin.clientRSA.rsaEncrypt(digest,destin.clientRSA.getPu()));
                                               dataRecByte=mess.convEnvByte(mess);
                                   
                                            //INVIO DEL MESSAGGIO RICEVUTO in base al critto mode
                                            sendMess=cryptoSimm(dataRecByte,destin);
                                            sendMessage.writeObject(sendMess);
                                    
                                            sendMessage.flush();
                                    
                                                }
                                            else{
                                                //inserisci modifica modalità critto per ricevere corretto
                                                }
                                            }
                                            break;
                                            
                                        default:
                                            break;
                                            
                                            
                                    }
                                }
                              }
                            }
                            
                        } else{
                                    form.textArea1.append("\nDigest  Differenti,\nil messaggio sarà rimosso ");
                                    String warning="Warning: Integrità del messaggio violata";
                                    Envelop env=new Envelop();
                                    env.setCripto(e.getCripto());
                                    env.setText(warning);
                                    env.setFrom("Server");
                                     digest=myUser.clientHash.hashing(env.getText().getBytes());
                       
                                    env.setMac(myUser.clientRSA.rsaEncrypt(digest,myUser.clientRSA.getPu()));
                                     //INVIO DEL MESSAGGIO RICEVUTO in base al critto mode
                                    dataRecByte=env.convEnvByte(env);
                                    byte[] warn=env.convEnvByte(env);
                                    myUser.sent.writeObject(warn);
                                    myUser.sent.flush();
                        }
                     }
                       
                        
                        
                        //fine blocco running
                    }
                    
                    //fine blocco try
                } catch (IOException ex) {
                    try {
                        for(Iterator<Client> iter=users.iterator(); iter.hasNext();)
                        {
                        if(iter.next().userName.equals(myUser.userName))
                            iter.remove();
                        
                        }
                        sock.shutdownOutput();
                    } catch (IOException ex1) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                    System.out.println("Questo errore deve essere risolto");
                    ex.printStackTrace();
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidAlgorithmParameterException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ShortBufferException ex) {
                    Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                } 

            
        }   catch (NoSuchAlgorithmException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NoSuchPaddingException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeyException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalBlockSizeException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BadPaddingException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidKeySpecException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvalidAlgorithmParameterException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SignatureException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }   
    }
     }}   
  
				


