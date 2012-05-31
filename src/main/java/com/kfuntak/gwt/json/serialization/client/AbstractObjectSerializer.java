package com.kfuntak.gwt.json.serialization.client;

import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

public abstract class AbstractObjectSerializer implements ObjectSerializer {
    public String serialize(Object pojo) {
        return serializeToJson(pojo).toString();
    }

    public abstract JSONValue serializeToJson(Object pojo);

    public abstract Object deSerialize(JSONValue jsonValue, String className) throws JSONException;

    public Object deSerialize(String jsonString, String className) throws JSONException {
        return deSerialize(JSONParser.parseLenient(jsonString), className);
    }

    public Object deSerialize(JSONValue jsonValue) throws JSONException {
        return deSerialize(jsonValue, null);
    }
}
