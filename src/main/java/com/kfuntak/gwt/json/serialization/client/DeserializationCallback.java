package com.kfuntak.gwt.json.serialization.client;

import com.google.gwt.json.client.JSONValue;

/**
 * Created by IntelliJ IDEA.
 * User: hrobinson
 * Date: 2/8/12
 * Time: 8:32 AM
 * To change this template use File | Settings | File Templates.
 */
public interface DeserializationCallback<T> {
    T deserialize(JSONValue value);
}
