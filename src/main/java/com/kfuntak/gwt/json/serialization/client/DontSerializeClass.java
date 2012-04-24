package com.kfuntak.gwt.json.serialization.client;

/**
 * Any {@link JsonSerializable} class that is annotated
 * with this annotation will not have it's class inserted
 * into the serialized JSON.
 *
 * Without Annotation:
 * {"name":"Bob", "class":"com.foo.Person"}
 *
 * With Annotation:
 * {"name":"Bob"}
 */
public @interface DontSerializeClass {
}
