package com.jaigo.androiddevkit.utils;
// JKitLog
//

import java.io.*;
import java.util.Date;

public class Log
{
    private static String logFilePath = null;
    private static Log instance;
    private static Thread.UncaughtExceptionHandler uncaughtExceptionHandler;

	private static Log instance()
	{
		if (instance == null)
		{
			instance = new Log();

            uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler()
            {
                public void uncaughtException(Thread thread, Throwable ex)
                {
                    e("UncaughtException", "UncaughtException", ex, thread);
                }
            };

            Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
		}

		return instance;
	}

	public static void setLogFile(String logFilePath)
	{
        instance().logFilePath = logFilePath;
	}

    public static void v(String message)
    {
        v(null, message);
    }

    public static void v(String tag, String message)
    {
        v(tag, message, null);
    }

    public static void v(String tag, String message, Throwable throwable)
    {
        v(tag, message, throwable, null);
    }

    public static void v(String tag, String message, Throwable throwable, Thread thread)
    {
        instance().logMessage(android.util.Log.VERBOSE, tag, message, throwable, thread);
    }

    public static void d(String message)
    {
        d(null, message);
    }

    public static void d(String tag, String message)
    {
        d(tag, message, null);
    }

    public static void d(String tag, String message, Throwable throwable)
    {
        d(tag, message, throwable, null);
    }

    public static void d(String tag, String message, Throwable throwable, Thread thread)
    {
        instance().logMessage(android.util.Log.DEBUG, tag, message, throwable, thread);
    }

    public static void i(String message)
    {
        i(null, message);
    }

    public static void i(String tag, String message)
    {
        i(tag, message, null);
    }

    public static void i(String tag, String message, Throwable throwable)
    {
        i(tag, message, throwable, null);
    }

    public static void i(String tag, String message, Throwable throwable, Thread thread)
    {
        instance().logMessage(android.util.Log.INFO, tag, message, throwable, thread);
    }

    public static void w(String message)
    {
        w(null, message);
    }

    public static void w(String tag, String message)
    {
        w(tag, message, null);
    }

    public static void w(String tag, String message, Throwable throwable)
    {
        w(tag, message, throwable, null);
    }

    public static void w(String tag, String message, Throwable throwable, Thread thread)
    {
        instance().logMessage(android.util.Log.WARN, tag, message, throwable, thread);
    }

    public static void e(String message)
    {
        e(null, message);
    }

    public static void e(String tag, String message)
    {
        e(tag, message, null);
    }

    public static void e(String tag, String message, Throwable throwable)
    {
        e(tag, message, throwable, null);
    }

    public static void e(String tag, String message, Throwable throwable, Thread thread)
    {
        instance().logMessage(android.util.Log.ERROR, tag, message, throwable, thread);
    }

    public static void logStream(int logLevel, String tag, String message, InputStream is)
    {
        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;

            while ((len = is.read(buffer)) > -1)
            {
                baos.write(buffer, 0, len);
            }

            baos.flush();

            is = new ByteArrayInputStream(baos.toByteArray());
            InputStream is2 = new ByteArrayInputStream(baos.toByteArray());
            java.util.Scanner s = new java.util.Scanner(is2).useDelimiter("\\A");
            String log = s.hasNext() ? s.next() : "";

            instance().logMessage(logLevel, tag, message + "\n" + log, null, null);
        }
        catch (Exception e)
        {
            instance().logMessage(android.util.Log.ERROR, tag, message, e, null);
        }
    }

	public void logMessage(int logLevel, String tag, String message, Throwable throwable, Thread thread)
	{
        if (tag == null && message == null && throwable == null && thread == null)
        {
            return;
        }

        if (message == null)
        {
            message = "";
        }

        if (thread != null)
        {
            message += "\nThread: " + thread.getName() + " - ";
        }

        if (throwable != null)
        {
            message += "\nException:" + throwable.getMessage() + " - ";
        }

        android.util.Log.println(logLevel, tag, message);

        if (logFilePath == null)
		{
			return;
		}

        String level = "";

        switch (logLevel)
        {
            case android.util.Log.VERBOSE:
                level = "VERBOSE";
                break;
            case android.util.Log.DEBUG:
                level = "DEBUG";
                break;
            case android.util.Log.INFO:
                level = "INFO";
                break;
            case android.util.Log.WARN:
                level = "WARN";
                break;
            case android.util.Log.ERROR:
                level = "ERROR";
                break;
        }

		try
		{
			File file = new File(logFilePath);

			if (!file.exists())
			{
				file.createNewFile();
			}

            String rightNow = (new Date()).toString();
			FileWriter fw = new FileWriter(file, true);

			fw.write("\n");
			fw.write(level + " - " + tag + " : " + rightNow + "\n");
			fw.write(message + "\n");
			fw.close();
		}
		catch (Exception e)
		{
			// Unable to create file, likely because external storage is
			// not currently mounted.
		}
	}
}
