/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stefano;

/**
 *
 * @author lukie
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import sun.misc.BASE64Encoder;


public final class Blowfish {
  public static void esegui() throws Exception {
    KeyGenerator keyGenerator = KeyGenerator.getInstance("Blowfish");

    keyGenerator.init(128);
    Key secretKey = keyGenerator.generateKey();

    Cipher cipherOut = Cipher.getInstance("Blowfish/CFB/NoPadding");

    cipherOut.init(Cipher.ENCRYPT_MODE, secretKey);
    BASE64Encoder encoder = new BASE64Encoder();
    byte iv[] = cipherOut.getIV();
    if (iv != null) {
      System.out.println("Initialization Vector of the Cipher:\n" + encoder.encode(iv));
    }
    FileInputStream fin = new FileInputStream("inputFile.txt");
    FileOutputStream fout = new FileOutputStream("outputFile.txt");
    CipherOutputStream cout = new CipherOutputStream(fout, cipherOut);
    int input = 0;
    while ((input = fin.read()) != -1) {
      cout.write(input);
    }

    fin.close();
    cout.close();
  }
}
