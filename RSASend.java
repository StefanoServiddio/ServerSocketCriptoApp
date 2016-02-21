/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stefano;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 *
 * @author lukie
 */
public class RSASend {



        //scegli due primi p e q
        private BigInteger n;

        private BigInteger e;


        public RSASend(){

            this.n=null;

            this.e=null;


        }
    public void setPuKey(BigInteger[] v){
        this.e=v[0];
        this.n=v[1];


    }

    public BigInteger getE(){

        return this.e;
    }
    public BigInteger getN(){

        return this.n;
    }




    public String encryptPu(String s) 
        {
            //si cripta trovando c:=plaintext^e mod n;
            BigInteger c=new BigInteger(s.getBytes());
            c=c.modPow(this.e, this.n);

            String encr = new BASE64Encoder().encode(c.toByteArray());
            return encr;
        }
    public String decryptPu(String s) throws UnsupportedEncodingException, IOException
     {
         
         byte[] decod = new BASE64Decoder().decodeBuffer(s);
         BigInteger crypto=new BigInteger(decod);
         crypto=crypto.modPow(this.e,this.n);
         String plaintext=new String(crypto.toByteArray());
         return plaintext;
     }
    
    public byte[] encryptPuByte(byte[] s) throws UnsupportedEncodingException
    {
         //si cripta trovando c:=plaintext^e mod n;
        BigInteger c=new BigInteger(s);
        c=c.modPow(this.e, this.n);
        return c.toByteArray();
    }
    
    public byte[] decryptPuByte(byte[] decod) throws UnsupportedEncodingException, IOException
     {
         
         
         BigInteger crypto=new BigInteger(decod);
         crypto=crypto.modPow(this.e,this.n);
         
         return crypto.toByteArray();
     }




}