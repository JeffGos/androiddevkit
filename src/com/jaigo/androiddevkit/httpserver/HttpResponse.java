package com.jaigo.androiddevkit.httpserver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;

public class HttpResponse
{
	private static final String LOG_TAG = "HttpServer.HttpResponse";
	private static int writeBufferSize = 32 * 1024;

	public String status = HttpResponse.HTTP_INTERNALERROR;
	public String mimeType = HttpResponse.MIME_PLAINTEXT;
	public InputStream data;
	public Properties header = new Properties();

	//HTTP response status codes
	public static final String
			HTTP_OK = "200 OK",
			HTTP_PARTIALCONTENT = "206 Partial Content",
			HTTP_RANGE_NOT_SATISFIABLE = "416 Requested Range Not Satisfiable",
			HTTP_FORBIDDEN = "403 Forbidden",
			HTTP_NOTFOUND = "404 Not Found",
			HTTP_BADREQUEST = "400 Bad Request",
			HTTP_INTERNALERROR = "500 Internal Server Error",
			HTTP_NOTIMPLEMENTED = "501 Not Implemented";

	public static final String
			MIME_PLAINTEXT = "text/plain",
			MIME_HTML = "text/html",
			MIME_DEFAULT_BINARY = "application/octet-stream";

	public HttpResponse()
	{
		this.status = HTTP_OK;
	}

	public HttpResponse(String status, String message)
	{
		this.status = status;
		this.data = new ByteArrayInputStream(message.getBytes());
	}

	public HttpResponse(String status, String mimeType, InputStream data)
	{
		this.status = status;
		this.mimeType = mimeType;
		this.data = data;
	}

	public HttpResponse(String status, String mimeType, String txt)
	{
		this.status = status;
		this.mimeType = mimeType;
		try
		{
			this.data = new ByteArrayInputStream(txt.getBytes("UTF-8"));
		}
		catch (java.io.UnsupportedEncodingException uee)
		{
			uee.printStackTrace();
		}
	}

	public void addHeader(String name, String value)
	{
		header.put(name, value);
	}

	public void close() throws IOException
	{
		if (data != null)
		{
			data.close();
		}
	}

	public void write(OutputStream out) throws IOException
	{
		PrintWriter pw = new PrintWriter(out);

		String line = "HTTP/1.0 " + status + " \r\n";

		pw.print(line);

		if (mimeType != null)
		{
			line = "Content-Type: " + mimeType + "\r\n";
			pw.print(line);
		}

		if (header != null)
		{
			Enumeration e = header.keys();
			while (e.hasMoreElements())
			{
				String key = (String) e.nextElement();
				String value = header.getProperty(key);
				line = key + ": " + value + "\r\n";
				pw.print(line);
			}
		}

		pw.print("\r\n");
		pw.flush();

		if (data != null)
		{
			int totalRead = 0;
			int size = data.available();
			byte[] buff = new byte[writeBufferSize];

			while (true)
			{
				int read = data.read(buff);

				if (read <= 0)
				{
					break;
				}

				totalRead += read;

				try
				{
					out.write(buff, 0, read);
				}
				catch (IOException e)
				{
					break;
				}
			}
		}
	}
}