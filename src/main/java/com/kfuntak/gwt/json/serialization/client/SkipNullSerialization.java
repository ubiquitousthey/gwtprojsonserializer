package com.kfuntak.gwt.json.serialization.client;

/**
 * Any {@link JsonSerializable} class that is annotated
 * with this annotation will not have it's null fields
 * inserted into the serialized JSON.
 *
 * Without Annotation:
 * {"name":"Bob", "spouse":null}
 *
 * With Annotation:
 * {"name":"Bob"}
 */
public @interface SkipNullSerialization {
    boolean value() default true;
}
