package com.jaigo.androiddevkit.utils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.jaigo.androiddevkit.utils.ConvertUtils;

public class JsonUtils
{
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static ArrayList vectorFromJsonReader(Class c, JsonReader reader) throws IOException
	{
		ArrayList retval = new ArrayList();
		
		reader.beginArray();
		
		while (reader.hasNext())
		{
			retval.add(ConvertUtils.fromString(c, reader.nextString()));
		}
		
		reader.endArray();
		
		return retval;
	}
	
	public static JsonSerializer<Date> getJsonDateSerializer() {
        return new JsonSerializer<Date>() {
            @Override
            public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
                SimpleDateFormat sdf = new SimpleDateFormat("Z");
                String timezone = sdf.format(src);

                return new JsonPrimitive("/Date(" + src.getTime() + timezone + ")/");
            }
        };
    }
}