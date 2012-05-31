package com.kfuntak.gwt.json.serialization.client;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.json.client.JSONObject;

public class Serializer {

    private static Map SERIALIZABLE_TYPES;

    private static Map serializableTypes() {
        if (SERIALIZABLE_TYPES == null) {
            SERIALIZABLE_TYPES = new HashMap();
        }
        return SERIALIZABLE_TYPES;
    }

    protected void addObjectSerializer(String name, ObjectSerializer obj) {
        serializableTypes().put(name, obj);
    }

    protected ObjectSerializer getObjectSerializer(String name) {
        if (name.equals("java.util.ArrayList")) {
            return new ArrayListSerializer();
        } else if (name.equals("java.util.HashMap")) {
            return new HashMapSerializer();
        }

        if(serializableTypes().containsKey(name)){
            return (ObjectSerializer) serializableTypes().get(name);
        } else {
            throw new SerializationException("Can't find object serializer for " + name);
        }
    }

    private String extractClassName(JSONValue jsonValue) {
        JSONObject obj = jsonValue.isObject();
        if (obj != null) {
            if (obj.containsKey("class") && obj.get("class").isString() != null) {
                return obj.get("class").isString().stringValue();
            }
        }
        return null;
    }

    protected Serializer() {
    }

    public String serialize(Object pojo) {
        return serializeToJson(pojo).toString();
    }

    public JSONValue serializeToJson(Object pojo) {
        if (pojo == null) {
            return null;
        }

        String name = pojo.getClass().getName();
        return getObjectSerializer(name).serializeToJson(pojo);
    }

    public Object deSerialize(JSONValue jsonValue, String className) throws JSONException {
        String serializeClassName = extractClassName(jsonValue);
        if(serializeClassName != null && !serializeClassName.equals(className)){
            className = serializeClassName;
        }

        if (className == null) {
            throw new IllegalArgumentException("Json string must contain \"class\" key.");
        }

        return getObjectSerializer(className).deSerialize(jsonValue);
    }

    public Object deSerialize(String jsonString, String className) throws JSONException {
        JSONValue jsonValue = JSONParser.parseLenient(jsonString);
        return deSerialize(jsonValue, className);
    }

    public Object deSerializeArray(String jsonString, String className) throws JSONException {
        JSONValue jsonValue = JSONParser.parseLenient(jsonString);
        return new ArrayListSerializer(className).deSerialize(jsonValue);
    }

    public Object deSerializeArray(JSONValue jsonValue, String className) throws JSONException {
        return new ArrayListSerializer(className).deSerialize(jsonValue);
    }

    public Object deSerializeMap(String jsonString, String className) throws JSONException {
        JSONValue jsonValue = JSONParser.parseLenient(jsonString);
        return new HashMapSerializer(className).deSerialize(jsonValue);
    }

    public Object deSerializeMap(JSONValue jsonValue, String className) throws JSONException {
        return new HashMapSerializer(className).deSerialize(jsonValue);
    }


    public Object deSerialize(String jsonString) {
        return deSerialize(jsonString, null);
    }

    public Object deSerialize(JSONValue jsonValue) throws JSONException {
        return deSerialize(jsonValue, null);
    }

    public static <T> T marshall(String data, String typeString) {
        return marshall(data, typeString, null);
    }

    public static <T> T marshall(String data) {
        return marshall(data, null, null);
    }

    public static <T> T marshall(String data, String typeString, T defaultValue) {
        if(GWT.isClient() && data != null && !data.isEmpty()){
            Serializer serializer = new Serializer();
            T object = (T)serializer.deSerialize(data, typeString);
            if (object == null) {
                return defaultValue;
            } else {
                return object;
            }

        }
        return defaultValue;
    }

    public static <T> T marshall(String data, T defaultValue) {
		return marshall(data, null, defaultValue);
    }

    public static String marshall(Object object, String defaultValue) {
        if (GWT.isClient() && object != null) {
            Serializer serializer = new Serializer();
            return serializer.serialize(object);
        }
        return defaultValue;
    }

    public static String marshall(Object object) {
        return marshall(object, "");
    }
}
