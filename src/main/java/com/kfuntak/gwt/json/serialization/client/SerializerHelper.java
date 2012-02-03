package com.kfuntak.gwt.json.serialization.client;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONNull;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.json.client.JSONBoolean;

public class SerializerHelper {

    public static JSONValue getString(String string) {
        if (string == null) {
            return JSONNull.getInstance();
        }
        return new JSONString(string);
    }

    public static JSONValue getBoolean(Boolean boolValue) {
        if (boolValue == null) {
            return JSONNull.getInstance();
        }
        return JSONBoolean.getInstance(boolValue);
    }

    public static JSONValue getNumber(Number number) {
        if (number == null) {
            return JSONNull.getInstance();
        }
        return new JSONNumber(number.doubleValue());
    }

    public static JSONValue getChar(Character character) {
        if (character == null) {
            return JSONNull.getInstance();
        }
        return new JSONString(new String(new char[]{character}));
    }

    public static JSONValue getDate(Date date) {
        if (date == null) {
            return JSONNull.getInstance();
        }
        return new JSONNumber(date.getTime());
    }

    public static JSONValue getValue(Object o) {
        if (o == null) {
            return JSONNull.getInstance();
        }
        String typeName = o.getClass().getName();
        if (typeName.equals("java.lang.String")) {
            return getString((String) o);
        } else if (typeName.equals("java.lang.Boolean")) {
            return getBoolean((Boolean) o);
        } else if (typeName.equals("java.lang.Date")) {
            return getDate((Date) o);
        } else if (typeName.equals("java.lang.Character")) {
            return getChar((Character)o);
        } else {
            Serializer serializer = GWT.create(Serializer.class);
            ObjectSerializer ser = serializer.getObjectSerializer(typeName);
            if (ser != null) {
                return serializer.serializeToJson(o);
            }
        }
        return getString(o.toString());
    }
}
