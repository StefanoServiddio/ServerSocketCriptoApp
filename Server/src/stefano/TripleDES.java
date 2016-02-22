/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stefano;

/**
 *
 * @author Stefano Serviddio
 */
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public   class TripleDES {
    
    SecretKey keySpec;
    SecretKey keySpec2;
    
    public TripleDES(byte [] rawkey) throws Exception
    {
       
    }
}
