package com.jaigo.androiddevkit;

/**
 * Created by jeff.gosling on 24/07/2014.
 */
public abstract class RequestCallback<T>
{
    public void onStarted()
    {

    }

    public void onError(Throwable throwable)
    {

    }

    public abstract void onComplete(T result);
}
