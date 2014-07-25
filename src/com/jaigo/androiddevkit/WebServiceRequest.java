package com.jaigo.androiddevkit;

import com.geronimo.globalradio.geronimo.servicemodel.ServiceErrorCodes;
import com.google.gson.stream.JsonReader;
import com.jaigo.androiddevkit.utils.Log;
import org.apache.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class WebServiceRequest<T> extends HttpRequest
{
    private static final String LOG_TAG = "WebServiceRequest";

    protected Class<T> responseClass;

    public WebServiceRequest(String requestUrl, HttpRequestMethod httpRequestMethod, Class<T> responseClass)
    {
        this(requestUrl, httpRequestMethod, null, responseClass);
    }

    public WebServiceRequest(String requestUrl, HttpRequestMethod httpRequestMethod, String requestPayload, Class<T> responseClass)
    {
        super(requestUrl, httpRequestMethod, requestPayload);

        this.responseClass = responseClass;
    }

    @Override
    protected void onComplete(Object result)
    {
		try
		{
			InputStream is = ((HttpEntity) result).getContent();
			JsonReader reader = new JsonReader(new InputStreamReader(is));

			try
			{
				Method deserialize = responseClass.getMethod("deserialize", InputStream.class);

				@SuppressWarnings("unchecked")
				T responseDTO = (T) deserialize.invoke(null, is);

				if (requestHandler != null)
				{
					requestHandler.complete(responseDTO);
				}
			}
			finally
			{
				reader.close();
			}
		}
		catch (Exception e)
		{
			onError(e);
		}
    }

	@Override
	protected void onError(Throwable error)
	{
		Log.e(LOG_TAG, "onError: " + requestUrl, error);

		hasStarted = false;
		isCancelled = false;
		hasCompleted = true;

		try
		{
			Method createError = responseClass.getMethod("createError", String.class, String.class, String.class);

			@SuppressWarnings("unchecked")
			T responseDTO = (T) createError.invoke(null, ServiceErrorCodes.UnknownError.toString(), error.getMessage(), Log.getStackTraceString(error));

			if (requestHandler != null)
			{
				requestHandler.complete(responseDTO);
			}
		}
		catch (Exception e)
		{
			onError(e);
		}
	}
}