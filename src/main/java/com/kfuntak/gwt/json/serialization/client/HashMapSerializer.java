package com.kfuntak.gwt.json.serialization.client;

import com.google.gwt.json.client.*;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public class HashMapSerializer extends AbstractObjectSerializer {
    String valueClassName = null;

    public HashMapSerializer() {}

    public HashMapSerializer(String className) {
        valueClassName = className;
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

    @Override
    public Object deSerialize(JSONValue jsonValue, String className) throws JSONException {
        JSONObject jsonObject = jsonValue.isObject();
        if (jsonObject == null) {
            throw new IllegalArgumentException("Json value was not a json object");
        }

        HashMap<String,Object> map = new HashMap<String, Object>();
        for (String key : jsonObject.keySet()) {
            if(valueClassName != null){
                map.put(key, DeserializerHelper.getObject(jsonObject.get(key), className));
            } else {
                map.put(key, DeserializerHelper.getValue(jsonObject.get(key)));
            }
        }

        return map;
    }

    public Object deSerialize(JSONValue jsonValue) throws JSONException {
        return deSerialize(jsonValue, valueClassName);
    }
}
