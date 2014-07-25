package com.jaigo.androiddevkit.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils
{

	public static String convertToHex(byte[] data)
	{
		StringBuilder buf = new StringBuilder();

		for (int i = 0; i < data.length; i++)
		{

			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do
			{
				if ((0 <= halfbyte) && (halfbyte <= 9))
				{
					buf.append((char) ('0' + halfbyte));
				}
				else
				{
					buf.append((char) ('a' + (halfbyte - 10)));
				}
				halfbyte = data[i] & 0x0F;
			} while (two_halfs++ < 1);
		}

		return buf.toString();
	}

	public static byte[] SHA1(String text) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA-1");

		md.update(text.getBytes());

		return md.digest();
	}

	public static byte[] SHA256(String text) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("SHA-256");

		md.update(text.getBytes());

		return md.digest();
	}

	public static byte[] MD5(String text) throws NoSuchAlgorithmException
	{
		MessageDigest md = MessageDigest.getInstance("MD5");

		md.update(text.getBytes());

		return md.digest();
	}
}
