package com.jaigo.androiddevkit;

/**
 * Created by jeff.gosling on 23/07/2014.
 */
public enum HttpRequestMethod
{
	Get("GET"),
    Post("POST"),
    Delete("DELETE"),
    Put("PUT");

    String methodString;

    HttpRequestMethod(String methodString)
    {
        this.methodString = methodString;
    }
}
