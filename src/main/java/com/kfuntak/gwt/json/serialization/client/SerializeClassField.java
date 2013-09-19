package com.kfuntak.gwt.json.serialization.client;

/**
 * This annotation will toggle the 'class' field in
 * the exported JSON. By default, the class field is
 * present.
 *
 * With "@SerializeClassField(true)" or without annotation:
 * {"name":"Bob", "class":"com.foo.Person"}
 *
 * With "@SerializeClassField(false)":
 * {"name":"Bob"}
 */
public @interface SerializeClassField {
    boolean value();
}
