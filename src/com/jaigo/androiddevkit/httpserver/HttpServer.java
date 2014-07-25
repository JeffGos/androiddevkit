package com.jaigo.androiddevkit.httpserver;

import com.jaigo.androiddevkit.utils.Log;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;

public class HttpServer
{
	public interface HttpServerErrorListener
	{
		public void onError(Exception e);
	}

	private static final int SOCKET_ACCEPT_TIMOUT_MS = 3000;

	private ExecutorService executor;
	private int port;
	private ServerSocket serverSocket;
	private boolean running;
	private boolean allowExternalAccess;
	private SimpleDateFormat dateFormatter;
	private ContentInputStreamAdapter contentInputStreamAdapter;

	private HttpServerErrorListener errorListener;

	private static final String LOG_TAG = "HttpServer";

	public HttpServer(ExecutorService exec, String rootDir, boolean allowExternalAccess)
	{
		executor = exec;
		contentInputStreamAdapter = new ContentInputStreamAdapter(rootDir);
		this.allowExternalAccess = allowExternalAccess;
	}

	public HttpServer(ExecutorService exec, ContentInputStreamAdapter contentInputStreamAdapter, boolean allowExternalAccess)
	{
		executor = exec;
		this.contentInputStreamAdapter = contentInputStreamAdapter;
		this.allowExternalAccess = allowExternalAccess;
	}

	public void setErrorListener(HttpServerErrorListener listener)
	{
		errorListener = listener;
	}

	public void initialise() throws IOException
	{
        Log.i(LOG_TAG, "Initialise");

		stop();
		serverSocket = new ServerSocket(0);
		//serverSocket.setSoTimeout(SOCKET_ACCEPT_TIMOUT_MS);

		port = serverSocket.getLocalPort();
		dateFormatter = new SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
		dateFormatter.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	public void start()
	{
		if (running)
		{
			return;
		}

        Log.i(LOG_TAG, "start()");

		running = true;

		executor.execute(new Runnable()
		{
			public void run()
			{
				try
				{
					while (running)
					{
                        Log.i(LOG_TAG, "Listening for new connections");

						Socket socket = serverSocket.accept();
						String localHostName = socket.getLocalAddress().getHostName();

						if (!allowExternalAccess && localHostName.compareTo("localhost") != 0)
						{
                            Log.e(LOG_TAG, "External Access Request - " + localHostName);
							continue;
						}

                        Log.i(LOG_TAG, "Serving request  - " + socket.getLocalAddress());

						executor.execute(new HttpSession(socket, dateFormatter, contentInputStreamAdapter));

                        Log.i(LOG_TAG, "Completed - " + socket.getLocalAddress());
					}
				}
				catch (Exception e)
				{
					if (running)
					{
						running = false;

						if (e instanceof InterruptedIOException)
						{
                            Log.e(LOG_TAG, "serverSocket.accept() timed out : ", e);
						}
						else
						{
                            Log.e(LOG_TAG, "Error while listening on serverSocket : ", e);
						}
					}
				}
			}
		});
	}

	public void stop()
	{
		if (!running)
		{
			return;
		}

        Log.i(LOG_TAG, "stop()");

		try
		{
			running = false;
			serverSocket.close();
		}
		catch (IOException e)
		{
            Log.e(LOG_TAG, "stop() : ", e);
		}
	}

	public int getPort()
	{
		return port;
	}

	public boolean isRunning()
	{
		return running;
	}
}
