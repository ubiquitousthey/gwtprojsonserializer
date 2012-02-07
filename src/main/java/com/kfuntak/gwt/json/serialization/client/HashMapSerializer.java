package com.kfuntak.gwt.json.serialization.client;

import com.google.gwt.json.client.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: hrobinson
 * Date: 2/2/12
 * Time: 2:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class HashMapSerializer implements ObjectSerializer{
    public String serialize(Object pojo) {
        return serializeToJson(pojo).toString();
    }

    public JSONValue serializeToJson(Object pojo) {
        if(!(pojo instanceof Map)){
            throw new IllegalArgumentException();
        }
        Map<String,?> map = (Map<String,?>)pojo;
        JSONObject json = new JSONObject();
        for (String key : map.keySet()) {
            json.put(key, SerializerHelper.getValue(map.get(key)));
        }
        return json;
    }

    public Object deSerialize(JSONValue jsonValue, String className) throws JSONException {
        JSONObject jsonObject = jsonValue.isObject();
        if (jsonObject == null) {
            throw new IllegalArgumentException("Json value was not a json object");
        }

        HashMap<String,Object> map = new HashMap<String, Object>();
        for (String key : jsonObject.keySet()) {
            map.put(key, DeserializerHelper.getValue(jsonObject.get(key)));
        }

        return map;
    }

    public Object deSerialize(String jsonString, String className) throws JSONException {
        return deSerialize(JSONParser.parseLenient(jsonString), className);
    }
}
