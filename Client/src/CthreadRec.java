import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.logging.Level;
import java.util.logging.Logger;




public class CthreadRec extends Thread {
	
	public ObjectInputStream input=null;

	public CthreadRec(ObjectInputStream input){
		this.input=input;
		
		
	}
	public void run(){
		boolean running =true;
		String message="";
		
		while(running){
			 try {
				if((message=(String)input.readObject())!=null)
                                
				System.out.println("Server dice: "+message);
                                
                                
                                
			} catch (IOException e) {
				System.err.println("problemi ricezione messaggi");
				running=false;
				input=null;
				e.printStackTrace();
			} catch (ClassNotFoundException ex) {
                        System.err.println("problemi ricezione messaggi 2");
                    }
			 
			
			
		}
		
		
			
	}

}







