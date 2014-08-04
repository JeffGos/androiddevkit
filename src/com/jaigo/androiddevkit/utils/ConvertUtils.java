package com.jaigo.androiddevkit.utils;

import com.jaigo.androiddevkit.TimeSpan;
import com.jaigo.androiddevkit.Urn;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class ConvertUtils
{
	public static boolean toboolean(String value)
	{
		return value.compareToIgnoreCase("true") == 0;
	}

	public static int[] toIntArray(ArrayList<Integer> integerList)
	{
		Iterator<Integer> iterator = integerList.iterator();
		int[] result = new int[integerList.size()];

		for (int i = 0; i < result.length; i++)
		{
			result[i] = iterator.next().intValue();
		}

		return result;
	}

	public static Boolean toBoolean(String value)
	{
		return new Boolean(value.compareToIgnoreCase("true") == 0);
	}

	public static byte tobyte(String value)
	{
		return Byte.parseByte(value);
	}

	public static Byte toByte(String value)
	{
		return Byte.parseByte(value);
	}

	public static short toshort(String value)
	{
		return Short.parseShort(value);
	}

	public static Short toShort(String value)
	{
		return new Short(value);
	}

	public static int toint(String value) throws Exception
	{
		try
		{
			return Integer.parseInt(value);
		}
		catch (NumberFormatException ex)
		{
			Log.e("ConvertUtils", "toint(String)", ex);
			throw ex;
		}
	}

	public static Integer toInteger(String value)
	{
		return new Integer(value);
	}

	public static long tolong(String value)
	{
		return Long.parseLong(value);
	}

	public static Long toLong(String value)
	{
		return new Long(value);
	}

	public static float tofloat(String value)
	{
		try
		{
			return Float.parseFloat(value);
		} catch (Exception e)
		{
		}

		return 0;
	}

	public static Float toFloat(String value)
	{
		return new Float(value);
	}

	public static double todouble(String value)
	{
		return Double.parseDouble(value);
	}

	public static Double toDouble(String value)
	{
		return new Double(value);
	}

	public static UUID toUUID(String value)
	{
		if (value == null || value.length() < 32)
		{
			return null;
		}

		try
		{
			if (!value.contains("-"))
			{
				String fixedString = value.substring(0, 8) + "-" + value.substring(8, 12) + "-" + value.substring(12, 16) + "-"
						+ value.substring(16, 20) + "-" + value.substring(20, 32);

				return UUID.fromString(fixedString);
			}
			else
			{
				return UUID.fromString(value);
			}
		}
        catch (Exception e)
		{
			Log.e("ConvertUtils.toUUID", "Error converting string to UUID - " + value, e);
		}

		return null;
	}

	public static Date toDate(String value)
    {
        return toDate(value, "yyyy-MM-dd'T'HH:mm:ss", "UTC");
    }

    public static Date toDate(String value, String format, String timeZone)
    {

		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone));

		Date retval;

		try
		{
			retval = dateFormat.parse(value);
		}
        catch (Exception ex)
		{
			retval = null;
		}

		if (retval == null)
		{
			try
			{
				// datetime json string = \/Date(1324492241979+0000)\/

				// The timezone part (+0000) can be ignored for converting to UTC.
				// The main numeric part represents milliseconds since epoch (1/1/1970 UTC),
				// the time zone part just informs you of the local timezone on the server

				int idxStart = value.indexOf("/Date(") + 6;
				int idxEnd = value.indexOf("+");

				String dateStr = null;

				if (idxEnd < 0)
				{
					idxEnd = value.indexOf(")");
				}

				dateStr = value.substring(idxStart, idxEnd);
				long dateMs = Long.parseLong(dateStr);
				retval = new Date(dateMs);
			}
            catch (Exception ex)
			{
				retval = null;
			}
		}

		if (retval == null)
		{
			Log.w("ConvertUtils.toDate", "Could not parse DateTime string - " + value);
		}

		return retval;
	}

	public static TimeSpan toTimeSpan(String value) throws IllegalArgumentException
	{
		return TimeSpan.parse(value);
	}

	public static Urn toUrn(String value)
	{
		return Urn.parse(value);
	}

	public static URL toURL(String value)
	{
		try
		{
			return new URL(value);
		} catch (MalformedURLException ex)
		{
			Log.e("ConvertUtils.toUrl", value, ex);
			return null;
		}
	}

	public static String toString(boolean x)
	{
		return x ? "true" : "false";
	}

	public static String toString(String x)
	{
		return x;
	}

	public static String toString(Boolean x)
	{
		if (x != null)
		{
			return x.booleanValue() ? "true" : "false";
		}
		else
		{
			return "";
		}
	}

	public static String toString(byte x)
	{
		return Integer.toString(x);
	}

	public static String toString(Byte x)
	{
		return x != null ? x.toString() : "";
	}

	public static String toString(int x)
	{
		return Integer.toString(x);
	}

	public static String toString(Integer x)
	{
		return x != null ? x.toString() : "";
	}

	public static String toString(int x, int digits)
	{
		if (digits <= 0)
		{
			return Integer.toString(x);
		}
		else
		{
			String retval = Integer.toString(x);

			int delta = digits - retval.length();

			if (delta > 0)
			{
				StringBuffer buffer = new StringBuffer();

				for (int i = 0; i < delta; i++)
				{
					buffer.append('0');
				}

				buffer.append(retval);

				retval = buffer.toString();
			}

			return retval;
		}
	}

	public static String toString(Integer x, int digits)
	{
		if (x == null)
		{
			return "";
		}

		return toString(x.intValue());
	}

	public static String toString(long x)
	{
		return Long.toString(x);
	}

	public static String toString(Long x)
	{
		return x != null ? x.toString() : "";
	}

	public static String toString(float x)
	{
		return Float.toString(x);
	}

	public static String toString(Float x)
	{
		return x != null ? x.toString() : "";
	}

	public static String toString(double x)
	{
		return Double.toString(x);
	}

	public static String toString(Double x)
	{
		return x != null ? x.toString() : "";
	}

	public static String toString(UUID x)
	{
		if (x != null)
		{
			Pattern p = Pattern.compile("[\\{\\}\\-]");
			Matcher m = p.matcher(x.toString());

			return m.replaceAll("");
		}
		else
		{
			return "";
		}
	}

	public static String toString(Date value)
	{
		if (value != null)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("Z");
			String timezone = sdf.format(value);

			String result = "/Date(" + String.valueOf(value.getTime()) + timezone + ")/";

			return result;
		}
		else
		{
			return "";
		}
	}

	public static String toString(TimeSpan value)
	{
		return value.toString();
	}

	public static String toString(Urn value)
	{
		return value.toString();
	}

	public static String toString(URL value)
	{
		return value.toString();
	}

	public static String toString(InputStream is) throws Exception
	{
		final char[] buffer = new char[1024];
		final StringBuilder out = new StringBuilder();
		try
		{
			final Reader in = new InputStreamReader(is, "UTF-8");
			try
			{
				for (; ; )
				{
					int rsz = in.read(buffer, 0, buffer.length);
					if (rsz < 0)
					{
						break;
					}
					out.append(buffer, 0, rsz);
				}
			} finally
			{
				in.close();
			}
		}
		catch (UnsupportedEncodingException ex)
		{
			Log.e("ConvertUtils", "toString(InputStream) - UnsupportedEncodingException", ex);
			throw ex;
		}
		catch (IOException ex)
		{
			Log.e("ConvertUtils", "toString(InputStream) - IOException", ex);
			throw ex;
		}

		return out.toString();
	}

	public static Object fromString(@SuppressWarnings("rawtypes") Class type, String value)
	{
		if (type == Boolean.class)
		{
			return ConvertUtils.toBoolean(value);
		}
		else if (type == Byte.class)
		{
			return ConvertUtils.toByte(value);
		}
		else if (type == Integer.class)
		{
			return ConvertUtils.toInteger(value);
		}
		else if (type == Long.class)
		{
			return ConvertUtils.toLong(value);
		}
		else if (type == Float.class)
		{
			return ConvertUtils.toFloat(value);
		}
		else if (type == Double.class)
		{
			return ConvertUtils.toDouble(value);
		}
		else if (type == TimeSpan.class)
		{
			return ConvertUtils.toTimeSpan(value);
		}
		else if (type == Urn.class)
		{
			return ConvertUtils.toUrn(value);
		}
		else if (type == Date.class)
		{
			return ConvertUtils.toDate(value);
		}
		else if (type == URL.class)
		{
			return ConvertUtils.toURL(value);
		}
		else if (type == UUID.class)
		{
			return ConvertUtils.toUUID(value);
		}
		else if (type == String.class)
		{
			return value;
		}

		return null;
	}


}
