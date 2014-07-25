package com.jaigo.androiddevkit.encryption;

import com.jaigo.androiddevkit.utils.Log;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class SecurityManager
{
	private static final String LOG_TAG = "SecurityManager";

	public static final int ENCRYPTION_KEY_LENGTH = 16; //bytes

	private byte[] deviceEncryptionKey = new byte[]{23, 56, 112, 118, 14, 1, 10, 47, 93, 31, 10, 111, 123, 13, 104, 5};

	private HashMap<UUID, UUID> securityTokens = new HashMap<UUID, UUID>();

	private static SecurityManager instance;
	private String IABKey;

	public static SecurityManager instance()
	{
		return instance;
	}

	public static void create(String salt)
	{
		if (instance == null)
		{
			instance = new SecurityManager(salt);
		}
	}

	private SecurityManager(String salt)
	{
		if (salt == null || salt.length() <= 0)
		{
			buildDefaultDeviceKey(String.valueOf(deviceEncryptionKey));
		}

		try
		{
			byte[] in = new byte[ENCRYPTION_KEY_LENGTH + salt.length()];
			System.arraycopy(salt.getBytes(), 0, in, 0, salt.getBytes().length);
			System.arraycopy(deviceEncryptionKey, 0, in, salt.getBytes().length, ENCRYPTION_KEY_LENGTH);
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(in);
			deviceEncryptionKey = mDigest.digest();
		}
		catch (NoSuchAlgorithmException e)
		{
			buildDefaultDeviceKey(salt);
		}
	}

	private void buildDefaultDeviceKey(String salt)
	{
		int idx = 0;

		while ((idx + 2) < salt.length())
		{
			String hexString = salt.substring(idx, idx + 2);
			byte dec = (byte) (Integer.parseInt(hexString, 16) - 128);

			if (idx < ENCRYPTION_KEY_LENGTH)
			{
				deviceEncryptionKey[idx] = dec;
			}

			idx += 2;
		}
	}

	public byte[] generateRandomKey()
	{
		byte[] result = new byte[ENCRYPTION_KEY_LENGTH];

		for (int i = 0; i < ENCRYPTION_KEY_LENGTH; i++)
		{
			result[i] = (byte) (Math.random() * 256);
		}

		return result;
	}

	public byte[] encryptWithDeviceKey(byte[] data)
	{
		return aesEncrypt(data, deviceEncryptionKey);
	}

	public byte[] decryptWithDeviceKey(byte[] data)
	{
		return aesDecrypt(data, deviceEncryptionKey);
	}

	private byte[] aesEncrypt(byte[] data, byte[] key)
	{
		try
		{
			Key cipherKey = new SecretKeySpec(key, 0, ENCRYPTION_KEY_LENGTH, "AES");

			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE, cipherKey);

			byte[] encrypted = cipher.doFinal(data);

			return encrypted;
		}
		catch (Exception e)
		{
			Log.e(LOG_TAG, e.getMessage());
		}

		return null;
	}

	private byte[] aesDecrypt(byte[] data, byte[] key)
	{
		try
		{
			Key cipherKey = new SecretKeySpec(key, 0, ENCRYPTION_KEY_LENGTH, "AES");

			Cipher cipher = Cipher.getInstance("AES/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, cipherKey);

			byte[] plain = cipher.doFinal(data);

			return plain;
		}
		catch (Exception e)
		{
            Log.e(LOG_TAG, e.getMessage());
		}

		return null;
	}

	public UUID getSecurityToken(UUID target)
	{
		securityTokens.clear();

		UUID result = UUID.randomUUID();

		securityTokens.put(result, target);

		return result;
	}

	public UUID resolveSecurityToken(UUID token)
	{
		return securityTokens.get(token);
	}
}
