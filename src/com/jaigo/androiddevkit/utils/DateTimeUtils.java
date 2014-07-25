package com.jaigo.androiddevkit.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils
{
	public static String toDateTimeString(Date value)
	{
		if (value != null)
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

			return dateFormat.format(value);
		}
		else
		{
			return "";
		}
	}

	public static String toDateString(Date value)
	{
		if (value != null)
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

			return dateFormat.format(value);
		}
		else
		{
			return "";
		}
	}

	public static String toTimeString(Date value)
	{
		if (value != null)
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

			return dateFormat.format(value);
		}
		else
		{
			return "";
		}
	}

	public static String toTimeString(long milliseconds)
	{
		long secs = milliseconds / 1000;
		long mins = secs / 60;
		long hours = mins / 60; 
		
		secs = secs % 60;
		mins = mins % 60;
		
		if (hours > 0)
		{
			return String.format("%d:%02d:%02d", hours, mins, secs);
		}
			
		return String.format("%d:%02d", mins, secs);
	}
	
	public static String toString(Date value, String format)
	{
		if (value != null)
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

			return dateFormat.format(value);
		}
		else
		{
			return "";
		}
	}

    @SuppressWarnings("deprecation")
	public static String toRelativeDateString(Date value)
	{
		if (value != null)
		{
			String result = "";

			Date dateNow = new Date();

			if ((dateNow.getYear() == value.getYear()) && (dateNow.getMonth() == value.getMonth()))
			{
				int daysApart = dateNow.getDate() - value.getDate();

				if (daysApart >= 28)
				{
					result = "4 weeks ago";
				}
				else if (daysApart >= 21)
				{
					result = "3 weeks ago";
				}
				else if (daysApart >= 14)
				{
					result = "2 weeks ago";
				}
				else if (daysApart >= 7)
				{
					result = "a week ago";
				}
				else if (daysApart >= 2)
				{
					result = Integer.toString(daysApart) + " days ago";
				}
				else if (daysApart >= 1)
				{
					result = "1 day ago";
				}
				else
				{
					int hoursApart = dateNow.getHours() - value.getHours();
					if (hoursApart > 1)
					{
						result = Integer.toString(hoursApart) + " hours ago";
					}
					else
					{
						int minsApart = dateNow.getMinutes() - value.getMinutes();

						if (minsApart > 10)
						{
							result = Integer.toString(minsApart) + " minutes ago";
						}
						else
						{
							result = "a moment ago";
						}
					}
				}
			}
			else
			{
				result = toDateTimeString(value);
			}

			return result;
		}
		else
		{
			return "";
		}
	}
}
