package com.jaigo.androiddevkit.encryption;

import com.jaigo.androiddevkit.utils.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class EncryptedFileWriter
{
	public static final String ENCRYPTED_FILE_EXTENSION  = "blm";
	public static final int DEFAULT_CHUNK_SIZE_IN_BYTES  = (32 * 1024);
	
	private static final String LOG_TAG = "EncryptedFileWriter"; 
	
	private SecurityManager securityManager;
	
	private OutputStream outputStream;
	
	private int chunkSizeInBytes;
	private byte [] currentChunk = null;
	private int bytesCopiedToCurrentChunk = 0;
	private byte [] encryptedChunk = null;
	private byte [] initialisationVector = null;
	private Cipher aesCipher;

	public EncryptedFileWriter()
	{
		this.securityManager = SecurityManager.instance();
	}
	
	public boolean openFile(String path, long fileLength)
	{
		File outputFile = new File(path);
		
		if (outputFile.exists())
		{
            Log.i(LOG_TAG, "Deleting existing file " + path);
			outputFile.delete();
		}
		
		try
		{
			outputFile.createNewFile();
		}
		catch (IOException e)
		{
            Log.e(LOG_TAG, "Error creating output file - " + e.getMessage());
			
			return false;
		}
		
		byte [] encryptionKey = securityManager.generateRandomKey();
		byte [] randomSalt = securityManager.generateRandomKey();
		
		byte [] saltedEncryptionKey = new byte [encryptionKey.length + randomSalt.length];
		System.arraycopy(randomSalt, 0, saltedEncryptionKey, 0, randomSalt.length);
		System.arraycopy(encryptionKey, 0, saltedEncryptionKey, randomSalt.length, encryptionKey.length);
		
		byte [] encryptedSaltedEncryptionKey = securityManager.encryptWithDeviceKey(saltedEncryptionKey);
		
		//set up header		
		EncryptedFileHeader header = new EncryptedFileHeader();

		System.arraycopy(ENCRYPTED_FILE_EXTENSION.getBytes(), 0, header.magicBytes, 0, header.magicBytes.length);
		header.headerVersion = 1;
		header.headerLength = EncryptedFileHeader.ENCRYPTED_FILE_HEADER_LENGTH;
		header.fileLength = fileLength;
		header.chunkSizeInKB = DEFAULT_CHUNK_SIZE_IN_BYTES / 1024;
		chunkSizeInBytes = DEFAULT_CHUNK_SIZE_IN_BYTES;

		System.arraycopy(randomSalt, 0, header.salt, 0, header.salt.length);
		System.arraycopy(encryptedSaltedEncryptionKey, 0, header.saltedEncryptedKey, 0, header.saltedEncryptedKey.length);

		initialisationVector = securityManager.generateRandomKey();
		
		System.arraycopy(initialisationVector, 0, header.iv, 0, header.iv.length);
		
		try
		{
			outputStream = new BufferedOutputStream( new FileOutputStream(outputFile));
			outputStream.write(header.toBytes());
		}
		catch (Exception e)
		{
            Log.e(LOG_TAG, "Error writing file header - " + e.getMessage());
			
			return false;
		}
		
		currentChunk = new byte [chunkSizeInBytes];
		encryptedChunk = new byte [chunkSizeInBytes];
		
		try
		{
			Key cipherKey = new SecretKeySpec(encryptionKey, 0, SecurityManager.ENCRYPTION_KEY_LENGTH, "AES");
			aesCipher = Cipher.getInstance("AES/CBC/NOPADDING");
			aesCipher.init(Cipher.ENCRYPT_MODE, cipherKey, new IvParameterSpec(initialisationVector));
		}
		catch (Exception e)
		{
            Log.e(LOG_TAG, "Error initialising encryptor - " + e.getClass().getName() + " : " + e.getMessage());
			
			return false;
		}
		
		return true;
	}
	
	public boolean write(byte [] buffer, int byteCount)
	{
		int sourceBytesRead = 0;
		
		while (sourceBytesRead != byteCount)
		{
			int bytesToWrite = Math.min(byteCount - sourceBytesRead, chunkSizeInBytes - bytesCopiedToCurrentChunk);
			
			System.arraycopy(buffer, sourceBytesRead, currentChunk, bytesCopiedToCurrentChunk, bytesToWrite);
			
			sourceBytesRead += bytesToWrite;
			bytesCopiedToCurrentChunk += bytesToWrite;
			
			if (bytesCopiedToCurrentChunk == chunkSizeInBytes)
			{
				bytesCopiedToCurrentChunk = 0;
				
				if (!writeCurrentChunk())
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	private boolean writeCurrentChunk()
	{
		try
		{
			aesCipher.doFinal(currentChunk, 0, chunkSizeInBytes, encryptedChunk);
			
			outputStream.write(encryptedChunk);
		}
		catch (Exception e)
		{
            Log.e(LOG_TAG, "Error initialising encryptor - " + e.getClass().getName() + " : " + e.getMessage());
			
			return false;
		}
		
		return true;
	}
	
	public void closeFile()
	{
		try
		{
			if (bytesCopiedToCurrentChunk != 0)
			{
				for (int i = bytesCopiedToCurrentChunk; i < chunkSizeInBytes; i++)
				{
					currentChunk[i] = 0;
				}
			}
			
			writeCurrentChunk();
			
			outputStream.close();
		}
		catch (Exception e)
		{
            Log.e(LOG_TAG, "Error closing file - " + e.getMessage());
		}
		
	}
}
