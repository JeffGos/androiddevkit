package com.jaigo.androiddevkit;

import android.os.Handler;
import android.os.Message;

public class HttpRequestHandler extends Handler
{
	public static final int STARTED = 0;
	public static final int COMPLETE = 1;
	public static final int ERROR = 2;

    private RequestCallback requestCallback;

	public HttpRequestHandler()
	{
	}

	public HttpRequestHandler(RequestCallback requestCallback)
	{
        this.requestCallback = requestCallback;
	}

    public void start()
    {
        sendMessage(Message.obtain(this, STARTED));
    }

    public void error(Exception e)
    {
        sendMessage(Message.obtain(this, ERROR, e));
    }

    public void complete(Object result)
    {
        sendMessage(Message.obtain(this, COMPLETE, result));
    }

    @Override
    public void handleMessage(Message message)
    {
        switch (message.what)
        {
            case STARTED:
            {
                onStarted();

                break;
            }
            case COMPLETE:
            {
                onComplete(message.obj);

                break;
            }
            case ERROR:
            {
                onError((Throwable) message.obj);

                break;
            }
        }
    }

    protected void onStarted()
    {
        if (requestCallback != null)
        {
            requestCallback.onStarted();
        }
    }

    protected void onComplete(Object result)
    {
        if (requestCallback != null)
        {
            requestCallback.onComplete(result);
        }
    }

    protected void onError(Throwable e)
    {
        if (requestCallback != null)
        {
            requestCallback.onError(e);
        }
    }
}