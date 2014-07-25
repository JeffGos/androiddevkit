package com.jaigo.androiddevkit;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import com.jaigo.androiddevkit.ssl.SSLSocketFactory;
import com.jaigo.androiddevkit.utils.Log;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

public class HttpRequest
{
    private static final String LOG_TAG = "HttpRequest";

    protected String requestUrl;
    protected HttpRequestMethod httpRequestMethod;
    protected String requestPayload;

    protected Map<String, String> requestHeaders;
    protected boolean acceptSelfSignedSSLCertificates = false;
    protected HttpRequestHandler requestHandler;

	protected HttpClient httpClient;
    protected Future requestFuture;

    protected Runnable requestRunner = new Runnable() {
        @Override
        public void run() {
            execute();
        }
    };

    protected boolean hasStarted;
    protected boolean hasCompleted;
    protected boolean isCancelled;

    public HttpRequest(String requestUrl, HttpRequestMethod httpRequestMethod)
    {
        this(requestUrl,httpRequestMethod, null);
    }

    public HttpRequest(String requestUrl, HttpRequestMethod httpRequestMethod, String requestPayload)
    {
        this.requestUrl = requestUrl;
        this.httpRequestMethod = httpRequestMethod;
        this.requestPayload = requestPayload;
    }

    public void execute(ExecutorService requestExecutor)
    {
        requestFuture = requestExecutor.submit(requestRunner);
    }

    public void setRequestHeaders(Map<String, String> requestHeaders)
    {
        this.requestHeaders = requestHeaders;
    }

    public void setAcceptSelfSignedSSLCertificates(boolean acceptSelfSignedSSLCertificates)
    {
        this.acceptSelfSignedSSLCertificates = acceptSelfSignedSSLCertificates;
    }

    public void setRequestHandler(HttpRequestHandler requestHandler)
    {
        this.requestHandler = requestHandler;
    }

    public boolean hasStarted()
    {
        return hasStarted;
    }

    public boolean hasCompleted()
    {
        return hasStarted;
    }

    public boolean isCancelled()
    {
        return hasStarted;
    }

    public boolean cancel()
    {
        if (requestFuture == null)
        {
            return false;
        }

        isCancelled = false;

        return requestFuture.cancel(true);
    }

    protected void onStarted()
    {
        Log.i(LOG_TAG, "onStarted" + requestUrl);

        hasCompleted = false;
        isCancelled = false;
        hasStarted = true;

        if (requestHandler != null)
        {
            requestHandler.onStarted();
        }
    }

    protected void onError(Throwable error)
    {
        Log.e(LOG_TAG, "onError: " + requestUrl, error);

        hasStarted = false;
        isCancelled = false;
        hasCompleted = true;

        if (requestHandler != null)
        {
            requestHandler.onError(error);
        }
    }

    protected void onComplete(Object result)
    {
        Log.i(LOG_TAG, "onComplete: " + requestUrl);

        hasStarted = false;
        hasCompleted = true;

        if (requestHandler != null)
        {
            requestHandler.onComplete(result);
        }
    }

	private void execute()
	{
		onStarted();

		SchemeRegistry schemeRegistry = new SchemeRegistry();

		schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

		if (acceptSelfSignedSSLCertificates)
		{
			schemeRegistry.register(new Scheme("https", new SSLSocketFactory(), 443));
		}
		else
		{
			schemeRegistry.register(new Scheme("https", org.apache.http.conn.ssl.SSLSocketFactory.getSocketFactory(), 443));
		}

		HttpParams params = new BasicHttpParams();

		params.setParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 30);
		params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(30));
		params.setParameter(HttpProtocolParams.USE_EXPECT_CONTINUE, false);
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpConnectionParams.setConnectionTimeout(params, 0);
		HttpConnectionParams.setSoTimeout(params, 0);

		SingleClientConnManager mgr = new SingleClientConnManager(params, schemeRegistry);

		httpClient = new DefaultHttpClient(mgr, params);

		try
		{
			HttpUriRequest request = null;

			switch (httpRequestMethod)
			{
				case Get:
					request = new HttpGet(requestUrl);
					break;

				case Post:
					request = new HttpPost(requestUrl);

					if (requestPayload != null)
					{
						((HttpPost) request).setEntity(new StringEntity(requestPayload, "UTF-8"));
					}
					break;

				case Delete:
					request = new HttpDelete(requestUrl);
					break;

				case Put:
					request = new HttpPut(requestUrl);
					break;
			}

            if (requestHeaders != null)
            {
                for (String key : requestHeaders.keySet())
                {
                    String val = requestHeaders.get(key);
                    request.addHeader(key, val);
                }
            }

            Log.i(LOG_TAG, "Request: " + requestUrl);

			HttpResponse response = httpClient.execute(request);

			onComplete(response.getEntity());
		}
		catch (Exception e)
		{
			onError(e);
		}
	}
}