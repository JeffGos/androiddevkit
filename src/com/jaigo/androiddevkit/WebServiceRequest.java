package com.jaigo.androiddevkit;

import com.jaigo.androiddevkit.utils.Log;
import org.apache.http.HttpEntity;

import java.io.InputStream;
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

            Method deserialize = responseClass.getMethod("deserialize", InputStream.class);

            @SuppressWarnings("unchecked")
            T responseDTO = (T) deserialize.invoke(null, is);

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