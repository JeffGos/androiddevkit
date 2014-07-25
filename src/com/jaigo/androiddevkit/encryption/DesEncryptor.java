package com.jaigo.androiddevkit.encryption;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class DesEncryptor
{
	private byte[] buf = new byte[1024];
	private Cipher ecipher;
	private Cipher dcipher;
	private SecretKeySpec keySpec;
	
	
	private static String DES = "des";
	private static String DES_SECRET_KEY = "desSecretKey";

	public DesEncryptor(String secretKey)
	{
		if (secretKey == "")
		{
			//generate random key
            secretKey = "JEFFTEST";
		}
		
		try
		{
			byte [] keyBytes = secretKey.getBytes("UTF8");
			
			keySpec = new SecretKeySpec(keyBytes, "DES");
			
			init();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void init() throws Exception
	{
		byte[] iv = new byte[] { (byte) 0x8E, 0x12, 0x39, (byte) 0x9C, 0x07, 0x72, 0x6F, 0x5A };
		AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
		ecipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
		dcipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

		ecipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
		dcipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
	}

	public void encrypt(InputStream in, OutputStream out) throws Exception
	{
		out = new CipherOutputStream(out, ecipher);

		int numRead = 0;
		while ((numRead = in.read(buf)) >= 0)
		{
			out.write(buf, 0, numRead);
		}
	}

	public void decrypt(InputStream in, OutputStream out) throws Exception
	{
		in = new CipherInputStream(in, dcipher);

		int numRead = 0;
		while ((numRead = in.read(buf)) >= 0)
		{
			out.write(buf, 0, numRead);
		}
	}
}
