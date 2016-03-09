import java.io.*;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
public class Client {

	private Socket sock=null;
	private int port;

        private ObjectInputStream input;
        private ObjectOutputStream output;
        private  BigInteger[] PuKeyServ;
	
	
	public Client(String address, int port) 
	{
		initialize(address,port);
		
	}
        public ObjectOutputStream getOutput(){
            return output;
        }
        
        public ObjectInputStream getInput(){
            return input;
            
        }  
         public Socket getSocket(){
             return sock;
            
        }
                
        private void initialize(String address, int port){
            try {
			
			
			this.sock=new Socket(InetAddress.getByName(address),port);
			//this.input=new BufferedReader(new InputStreamReader(sock.getInputStream()));
			//this.output=new PrintWriter(sock.getOutputStream());
                         System.out.println("Inizia l'handshake");
                         keyHandShake(this.sock);
                       //  this.output=new PrintWriter(sock.getOutputStream());
			
		} catch (UnknownHostException e) {
			System.err.println("Connessione Client non riuscita");
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public  void keyHandShake(Socket sock) throws IOException, ClassNotFoundException{
             input = new ObjectInputStream(sock.getInputStream());
                  
                     PuKeyServ=(BigInteger[])input.readObject();
                     
                      System.out.println("ho ricevuto E: "+PuKeyServ[0]);
                      System.out.println("ho ricevuto N: "+PuKeyServ[1]);
                       output=new ObjectOutputStream(sock.getOutputStream());
                      output.writeObject("ACK");
                      output.flush();
                      System.out.println("ACK inviato");
                        
        }
        
        public void createStream(ObjectInputStream input, ObjectOutputStream output,Socket sock){
            
            CthreadRec rec=new CthreadRec(input);
		rec.start();
		RSA algRSA=new RSA(PuKeyServ[0],PuKeyServ[1]);
		CthreadSend sen=new CthreadSend(output,algRSA,sock);
		sen.start();
            
        }
	

	
	
	public static void main(String[] args) throws ClassNotFoundException
	{
		Client c=new Client("127.0.0.1",2400);
		c.createStream(c.getInput(), c.getOutput(),c.getSocket());
			
	
		
	}
	
	
	
}
