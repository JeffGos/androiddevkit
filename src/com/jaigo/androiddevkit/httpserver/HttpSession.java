package com.jaigo.androiddevkit.httpserver;

import com.jaigo.androiddevkit.utils.Log;

import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Handles one session, i.log. parses the HTTP request
 * and returns the response.
 */

class HttpSession implements Runnable
{
	private Socket socket;

	private static final String LOG_TAG = "HttpServer.HttpSession";

	private SimpleDateFormat dateFormatter;
	private ContentInputStreamAdapter inputStreamAdapter;

	public HttpSession(Socket s, SimpleDateFormat dateFormatter, ContentInputStreamAdapter inputStreamAdapter)
	{
		this.socket = s;
		this.dateFormatter = dateFormatter;
		this.inputStreamAdapter = inputStreamAdapter;

        Log.i(LOG_TAG, " *** New session started ***");
	}

	public void run()
	{
		try
		{
            Log.i(LOG_TAG, " Session in progress");

			InputStream is = socket.getInputStream();

			HttpRequestHeader hdr = new HttpRequestHeader(is);

			sendResponse(getResponse(hdr));

			is.close();
		}
		catch (Exception e)
		{
            Log.e(LOG_TAG, "error in run", e);
			sendResponse(new HttpResponse(HttpResponse.HTTP_INTERNALERROR, "IOException: " + e.getMessage()));
		}
	}

	public HttpResponse getResponse(HttpRequestHeader hdr)
	{
        Log.i(LOG_TAG, " getResponse() started");

		HttpResponse response;

		if (hdr.getHeaderParams() == null)
		{
            Log.e(LOG_TAG, "No header params found in request");
			return new HttpResponse(HttpResponse.HTTP_BADREQUEST, "No header params found in request");
		}

		String method = hdr.getHeaderParams().getProperty("method");

		if (method == null)
		{
            Log.e(LOG_TAG, "No method in request headers");
			return new HttpResponse(HttpResponse.HTTP_BADREQUEST, "No method in request headers");
		}

		if (!method.equalsIgnoreCase("GET"))
		{
            Log.e(LOG_TAG, "getResponse() - only GET supported");
			return new HttpResponse(HttpResponse.HTTP_NOTIMPLEMENTED, method + " not supported");
		}

		String filename = hdr.getHeaderParams().getProperty("uri");

		if (filename == null || filename.startsWith("..") || filename.endsWith("..") || filename.indexOf("../") >= 0)
		{
            Log.e(LOG_TAG, "getResponse() - file not found");

			return new HttpResponse(HttpResponse.HTTP_FORBIDDEN, "Relative paths not allowed");
		}

		try
		{
			String mime = "";
			ContentInputStreamAdapter.ContentInputStream responseData = inputStreamAdapter.getContentInputStream(filename);

			if (responseData == null)
			{
				return new HttpResponse(HttpResponse.HTTP_NOTFOUND, "File not found");
			}

			long startFrom = -1;
			long endAt = -1;
			String range = hdr.getHeaderParams().getProperty("range");

			if (range != null)
			{
                Log.i(LOG_TAG, "Request contains a range: " + range);

				if (range.startsWith("bytes="))
				{
					range = range.substring("bytes=".length());
					int minus = range.indexOf('-');
					try
					{
						if (minus > 0)
						{
							startFrom = Long.parseLong(range.substring(0, minus));
							endAt = Long.parseLong(range.substring(minus + 1));
						}
					}
					catch (NumberFormatException nfe)
					{
                        Log.w(LOG_TAG, "Non Fatal Error extracting range :" + range, nfe);
						startFrom = -1;
						endAt = -1;
					}
				}
			}

			if (startFrom >= 0)
			{
				if (startFrom >= responseData.contentLength)
				{
					response = new HttpResponse(HttpResponse.HTTP_RANGE_NOT_SATISFIABLE, HttpResponse.MIME_PLAINTEXT, "");
					response.addHeader("Content-Range", "bytes 0-0/" + responseData.contentLength);

					if (mime.startsWith("application/"))
					{
						response.addHeader("Content-Disposition", "attachment; filename=\"" + responseData.filename + "\"");
					}
				}
				else
				{
					response = new HttpResponse(HttpResponse.HTTP_PARTIALCONTENT, responseData.mime, responseData.content);

					if (endAt < 0)
					{
						endAt = responseData.contentLength - 1;
					}

					long length = endAt - startFrom + 1;
					if (length < 0)
					{
						length = 0;
					}

					if (startFrom > 0)
					{
						responseData.content.skip(startFrom);
					}

					response.addHeader("Content-Length", "" + length);
					response.addHeader("Content-Range", "bytes " + startFrom + "-" + endAt + "/" + responseData.contentLength);
				}
			}
			else
			{
				response = new HttpResponse(HttpResponse.HTTP_OK, responseData.mime, responseData.content);
				response.addHeader("Content-Length", "" + responseData.contentLength);
				response.addHeader("Accept-Ranges", "bytes");
			}

			response.addHeader("Original-Length", "" +  responseData.contentLength);
			response.addHeader("Last-Modified", dateFormatter.format(new Date(responseData.lastModifiedDate)));
		}
		catch (IOException ioe)
		{
			response = new HttpResponse(HttpResponse.HTTP_INTERNALERROR, "IOException: " + ioe.getMessage());
            Log.e(LOG_TAG, "Error Reading file", ioe);
		}

		response.addHeader("Date", dateFormatter.format(new Date()));
		response.addHeader("Content-Type", "audio/x-m4a");
		response.addHeader("Connection", "close");
		response.addHeader("Cache-Control", "no-cache");
		response.addHeader("Expires", "-1");
		response.addHeader("Pragma", "no-cache");

        Log.i(LOG_TAG, " getResponse() ended. Response = " + response);

		return response;
	}

	private void sendResponse(HttpResponse response)
	{
        Log.i(LOG_TAG, " sendResponse() started. Response = " + response);

		try
		{
			OutputStream out = socket.getOutputStream();

			response.write(out);

			out.flush();
			out.close();
			response.close();
		}
		catch (IOException ioe)
		{
            Log.e(LOG_TAG, "IOException writing to socket.", ioe);
		}

        Log.i(LOG_TAG, " sendResponse() ended.");
	}
}