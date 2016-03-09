import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Scanner;



public class CthreadSend extends Thread {
	
       private ObjectOutputStream output=null;
       private RSA algRSA=null;
       private Socket sock;
       
    
    public CthreadSend(ObjectOutputStream output,RSA algRSA, Socket sock){
		this.output=output;
                this.algRSA=algRSA;
                this.sock=sock;
                
		
		
	}
	public void run(){
		
		
		boolean running =true;
		String message="";
                String cript="";
		Scanner scan=new Scanner(System.in);
		while(running){
			
                    try {
                        message=scan.nextLine();
                        cript=algRSA.encrypt(message);
                        System.out.println("invio messaggio criptato: "+cript);
                          output.writeObject(cript); 
                          output.flush();
                          if(message.equals("quit")){
                              sock.close();
                              running=false;
                          }
                         
                        } catch (IOException ex) {
                        System.err.println("Errore Ricezione");
                    }
                    
			
				
			
		}
	}
}










