/**
 * 
 */
package com.gbli.common;

import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Simple3Des {

	private String key = "123456789012345678901234";
	private String initializationVector = "iloveyou";

	public Simple3Des() {

	}

	public String encryptText(String plainText) throws Exception {
		// ---- Use specified 3DES key and IV from other source --------------
		byte[] plaintext = plainText.trim().getBytes();
		byte[] tdesKeyData = key.getBytes();

		Cipher c3des = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		SecretKeySpec myKey = new SecretKeySpec(tdesKeyData, "DESede");
		IvParameterSpec ivspec = new IvParameterSpec(
				initializationVector.getBytes());

		c3des.init(Cipher.ENCRYPT_MODE, myKey, ivspec);
		byte[] cipherText = c3des.doFinal(plaintext);

		return new sun.misc.BASE64Encoder().encode(cipherText);
	}

	public String decryptText(String cipherText) throws Exception {
		byte[] encData = new sun.misc.BASE64Decoder().decodeBuffer(cipherText);
		Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
		byte[] tdesKeyData = key.getBytes();
		SecretKeySpec myKey = new SecretKeySpec(tdesKeyData, "DESede");
		IvParameterSpec ivspec = new IvParameterSpec(
				initializationVector.getBytes());
		decipher.init(Cipher.DECRYPT_MODE, myKey, ivspec);
		byte[] plainText = decipher.doFinal(encData);
		return new String(plainText);
	}
}