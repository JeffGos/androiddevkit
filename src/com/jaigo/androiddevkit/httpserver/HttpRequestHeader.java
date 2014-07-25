package com.jaigo.androiddevkit.httpserver;

// HttpRequestHeader
//
// Created by jeff.gosling on 08/04/13
// Copyright (c) 2012 DDN Ltd. All rights reserved.
//

import com.jaigo.androiddevkit.utils.Log;

import java.io.*;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;

public class HttpRequestHeader
{
	private static final String LOG_TAG = "HttpServer.HttpRequestHeader";
	private final int bufSize = 8 * 1024;
	private Properties headerParams = new Properties();

	public HttpRequestHeader()
	{
	}

	public HttpRequestHeader(InputStream is) throws  IOException
	{
		Log.i(LOG_TAG, "created");
		read(is);
	}

	//reads header from the inputstream and returns any bytes read that were read after the end of the header
	public byte[] read(InputStream is) throws IOException
	{
		byte[] readBuf = new byte[bufSize];
		int totalRead = 0;
		int hdrEndIdx = 0;
		byte[] leftoverBuf = null;
		byte[] headerBuf = new byte[bufSize];
		int headerBufRead = 0;
		int read;

        System.out.println("");
        Log.i(LOG_TAG, "read started");

		while ((read = is.read(readBuf)) > 0)
		{
			totalRead += read;

			hdrEndIdx = getHeaderEndIdx(readBuf);

			//increase header buffer size if needed
			if (totalRead > headerBuf.length)
			{
				byte[] temp = new byte[headerBuf.length * 2];
				System.arraycopy(headerBuf, 0, temp, 0, headerBuf.length);
				headerBuf = temp;
			}

			int headerBytesLength = hdrEndIdx == 0 ? readBuf.length : hdrEndIdx;
			System.arraycopy(readBuf, 0, headerBuf, headerBufRead, headerBytesLength);
			headerBufRead += headerBytesLength;

			if (hdrEndIdx != 0)
			{
				if (hdrEndIdx != totalRead)
				{
					leftoverBuf = new byte[totalRead - hdrEndIdx];
					System.arraycopy(readBuf, hdrEndIdx, leftoverBuf, 0, leftoverBuf.length);
				}

				break;
			}
		}

		if (totalRead > 0)
		{
			decodeHeader(headerBuf);
		}

        Log.i(LOG_TAG, "read ended");

		return leftoverBuf;
	}

	private void decodeHeader(byte[] headerBuf) throws IOException
	{
        Log.i(LOG_TAG, "decodeHeader started");

		ByteArrayInputStream bis = new ByteArrayInputStream(headerBuf);
		BufferedReader br = new BufferedReader(new InputStreamReader(bis));

		String props = br.readLine();
		if (props == null)
		{
			return;
		}

		StringTokenizer st = new StringTokenizer(props);
		if (!st.hasMoreTokens())
		{
			return;
		}

		String method = st.nextToken();
		headerParams.put("method", method);
		if (!st.hasMoreTokens())
		{
			return;
		}

		String uri = st.nextToken();

		int uriParamsIdx = uri.indexOf('?');

		if (uriParamsIdx >= 0)
		{
			uri = uri.substring(0, uriParamsIdx);
			String params = uri.substring(uriParamsIdx + 1);

			if (params != null)
			{
				StringTokenizer paramTokeniser = new StringTokenizer(params, "&");

				while (paramTokeniser.hasMoreTokens())
				{
					String param = paramTokeniser.nextToken();
					int eqIdx = param.indexOf('=');

					if (eqIdx >= 0)
					{
						String key = URLDecoder.decode(param.substring(0, eqIdx), "UTF-8");
						String value = URLDecoder.decode(param.substring(eqIdx + 1), "UTF-8");

						headerParams.put(key, value);
					}
					else
					{
						String key = URLDecoder.decode(param.substring(0, eqIdx), "UTF-8");

						headerParams.put(key, "");
					}
				}
			}

			uri = uri.substring(0, uriParamsIdx);
		}

		uri = URLDecoder.decode(uri, "UTF-8");

		headerParams.put("uri", uri.trim().replace(File.separatorChar, '/'));

		if (st.hasMoreTokens())
		{
			props = br.readLine();
			while (props != null && props.trim().length() > 0)
			{
				int p = props.indexOf(':');
				if (p >= 0)
				{
					headerParams.put(props.substring(0, p).trim().toLowerCase(), props.substring(p + 1).trim());
				}
				props = br.readLine();
			}
		}

		logHeader();

        Log.i(LOG_TAG, "decodeHeader ended");
	}

	private int getHeaderEndIdx(byte[] buf)
	{
		int idx = 0;

		while (idx + 3 < buf.length)
		{
			if (buf[idx] == '\r' && buf[idx + 1] == '\n' && buf[idx + 2] == '\r' && buf[idx + 3] == '\n')
			{
				return idx + 4;
			}

			idx++;
		}

		return 0;
	}

	public void logHeader()
	{
		Enumeration e =headerParams.propertyNames();
		while (e.hasMoreElements())
		{
			String name = (String) e.nextElement();
            Log.d(LOG_TAG, name + "' = '" + headerParams.getProperty(name) + "'");
		}
	}

	public Properties getHeaderParams()
	{
		return headerParams;
	};
}