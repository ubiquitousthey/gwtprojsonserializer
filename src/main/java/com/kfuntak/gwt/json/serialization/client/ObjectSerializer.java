package com.kfuntak.gwt.json.serialization.client;

import com.google.gwt.json.client.JSONException;
import com.google.gwt.json.client.JSONValue;

public interface ObjectSerializer {

    JSONValue serializeToJson(Object pojo);

    Object deSerialize(JSONValue jsonValue) throws JSONException;
}
