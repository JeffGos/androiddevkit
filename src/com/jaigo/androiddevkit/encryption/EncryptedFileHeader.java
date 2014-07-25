package com.jaigo.androiddevkit.encryption;

import com.jaigo.androiddevkit.utils.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class EncryptedFileHeader
{
	private static final String LOG_TAG = "EncryptedFileHeader";
	
	public static final int ENCRYPTED_FILE_HEADER_LENGTH = 84; //in bytes
	
	public byte [] magicBytes = new byte [3];
	public byte headerVersion;
	public int headerLength;
	public long fileLength;
	public short chunkSizeInKB;
	public short reserved;
	public byte [] salt = new byte [16];
	public byte [] saltedEncryptedKey = new byte [16 * 2];
	public byte [] iv = new byte [16];
	
	public EncryptedFileHeader()
	{
		
	}
	
	public EncryptedFileHeader(byte [] buffer)
	{
		if (buffer.length >= ENCRYPTED_FILE_HEADER_LENGTH)
		{
			fromBytes(buffer);
		}
	}
	
	public byte[] toBytes()
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		
		try
		{
			dos.write(magicBytes);
			dos.write(headerVersion);
			dos.writeInt(headerLength);
			dos.writeLong(fileLength);
			dos.writeShort(chunkSizeInKB);
			dos.writeShort(reserved);
			dos.write(salt);
			dos.write(saltedEncryptedKey);
			dos.write(iv);
			
			dos.close();
		}
		catch (IOException e)
		{
			Log.e(LOG_TAG, "Error converting to bytes - " + e.getMessage());
			
			return null;
		}
		
		return baos.toByteArray();
	}
	
	public boolean fromBytes(byte [] bytes)
	{
		if (bytes.length != ENCRYPTED_FILE_HEADER_LENGTH)
		{
            Log.e(LOG_TAG, "Error inflating from bytes - Wrong length - " + bytes.length);
			
			return false;
		}
		
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dis = new DataInputStream(bais);
		
		try
		{
			dis.read(magicBytes);
			headerVersion = dis.readByte();
			headerLength = dis.readInt();
			fileLength = dis.readLong();
			chunkSizeInKB = dis.readShort();
			reserved = dis.readShort();
			dis.read(salt);
			dis.read(saltedEncryptedKey);
			dis.read(iv);
			
			dis.close();
		}
		catch (IOException e)
		{
            Log.e(LOG_TAG, "Error inflating from bytes - " + e.getMessage());
			
			return false;
		}
		
		return true;
	}
}
