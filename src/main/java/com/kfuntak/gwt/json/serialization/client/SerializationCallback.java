package com.kfuntak.gwt.json.serialization.client;

import com.google.gwt.json.client.JSONValue;

public interface SerializationCallback {
    JSONValue serialize(Object value);
}
