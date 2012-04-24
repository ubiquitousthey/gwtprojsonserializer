package com.kfuntak.gwt.json.serialization.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Serialization implements EntryPoint {

    public void onModuleLoad() {
		/* Lets make sure the serializers get created. */
		GWT.create(Serializer.class);
    }
}
