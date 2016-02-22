/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stefano;

import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author lukie
 */
 enum ModeClient{NO,AES,DES3,Blow};
public class Client {
    
    public String userName;
    public ModeClient cripto;
    public RSASend passwordRSA;
    public ObjectOutputStream sent;
    
    
}
