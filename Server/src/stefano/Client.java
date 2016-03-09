/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stefano;

import java.io.ObjectOutputStream;
import com.stefano.android.*;

/**
 *
 * @author lukie
 */
 
public class Client {
    
    public String userName;
    public Envelop.Mode state;
    public NewRSA clientRSA;
    public AES clientAES;
    public TripleDES clientDES;
    public Blowfish clientBlow;
    public HmacSha1 clientHash;
    public ObjectOutputStream sent;
    
    public Client(){
     userName="";
     state=Envelop.Mode.NO;
     clientRSA=null;
     clientAES=null;
     clientDES=null;
     clientBlow=null;
     clientHash=null;
     sent=null;
        
    }
    
    
}
