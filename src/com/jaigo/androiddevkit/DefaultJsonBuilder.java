package com.jaigo.androiddevkit;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jaigo.androiddevkit.utils.JsonUtils;

import java.util.Date;

/**
 * Created by jeff.gosling on 23/07/2014.
 */
public class DefaultJsonBuilder
{
	private static class GsonSingleton
	{
		private static final Gson INSTANCE = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
				.disableHtmlEscaping()
				.registerTypeAdapter(Date.class, JsonUtils.getJsonDateSerializer())
				.create();
	}

	public static Gson instance()
	{
		return GsonSingleton.INSTANCE;
	}
}
