package com.jaigo.androiddevkit;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Created by jeff.gosling on 24/07/2014.
 */
public class WebServiceClient
{
    private static final String LOG_TAG = "WebServiceClient";

    public static final int DEFAULT_THREADS_PER_POOL = 10;
    ExecutorService requestExecutor;

    private boolean acceptSelfSignedSSLCertificates;
    private Map<String, String> requestHeaders;

    public WebServiceClient()
    {
        createExecutor(DEFAULT_THREADS_PER_POOL, null);
    }

    public WebServiceClient(int threadsPerPool, ThreadFactory threadFactory)
    {
        createExecutor(threadsPerPool, threadFactory);
    }

    public WebServiceClient(ExecutorService requestExecutor)
    {
        requestExecutor = requestExecutor;
    }

    public void setRequestExecutor(ExecutorService requestExecutor)
    {
        this.requestExecutor = requestExecutor;
    }

    public void setAcceptSelfSignedSSLCertificates(boolean acceptSelfSignedSSLCertificates)
    {
        this.acceptSelfSignedSSLCertificates = acceptSelfSignedSSLCertificates;
    }

    public void setRequestHeaders(Map<String, String> requestHeaders)
    {
        this.requestHeaders = requestHeaders;
    }

    public void get(String url, Class responseClass, RequestCallback callback)
    {
        WebServiceRequest request = new WebServiceRequest(url, HttpRequestMethod.Get, responseClass);
        executeRequest(request, callback);
    }

    public void delete(String url, Class responseClass, RequestCallback callback)
    {
        WebServiceRequest request = new WebServiceRequest(url, HttpRequestMethod.Delete, responseClass);
        executeRequest(request, callback);
    }

    public void post(String url, Class responseClass, String requestPayload, RequestCallback callback)
    {
        WebServiceRequest request = new WebServiceRequest(url, HttpRequestMethod.Post, requestPayload, responseClass);
        executeRequest(request, callback);
    }

    public void put(String url, Class responseClass, String requestPayload, RequestCallback callback)
    {
        WebServiceRequest request = new WebServiceRequest(url, HttpRequestMethod.Put, requestPayload, responseClass);
        executeRequest(request, callback);
    }

    private void executeRequest(WebServiceRequest request, RequestCallback callback)
    {
        request.setRequestHandler(new HttpRequestHandler(callback));
        request.setAcceptSelfSignedSSLCertificates(acceptSelfSignedSSLCertificates);
        request.setRequestHeaders(requestHeaders);

        request.execute(requestExecutor);
    }

    private void createExecutor(int threadsPerPool, ThreadFactory threadFactory)
    {
        if (threadFactory != null)
        {
            requestExecutor = Executors.newFixedThreadPool(threadsPerPool, threadFactory);
        }
        else
        {
            requestExecutor = Executors.newFixedThreadPool(threadsPerPool);
        }
    }
}
