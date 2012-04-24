package com.kfuntak.gwt.json.serialization.client;

import com.google.gwt.json.client.*;

import java.util.ArrayList;
import java.util.Collection;

public class ArrayListSerializer implements ObjectSerializer{
    public JSONValue serializeToJson(Object pojo) {
        if(!(pojo instanceof Collection)){
            throw new IllegalArgumentException();
        }
        Collection list = (Collection)pojo;
        JSONArray jsonList = new JSONArray();
        int index = 0;
        for (Object item : list) {
            jsonList.set(index++, SerializerHelper.getValue(item));
        }

        return jsonList;
    }

    public Object deSerialize(JSONValue jsonValue) throws JSONException {
        JSONArray jsonArray = jsonValue.isArray();
        if (jsonArray == null) {
            throw new IllegalArgumentException("Json value was not an array");
        }
        ArrayList<Object> list = new ArrayList<Object>();
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONValue value = jsonArray.get(i);
            list.add(DeserializerHelper.getValue(value));
        }

        return list;
    }
}
