package com.kfuntak.gwt.json.serialization.client;

/**
 * Any {@link JsonSerializable} class that is annotated
 * with this annotation will not have fields set when
 * the deserialized json does not contain a field.
 *
 * This allows a default to be set on the field.
 *
 * For example, with the class:
 *   class Person{
 *     String name;
 *     Integer age = 50;
 *     String spouse;
 *   }
 *
 * and the supplied JSON:
 *    {"name": "Bob"}
 *
 *
 * Without Annotation:
 *   person.name == "Bob"
 *   person.age == null
 *   person.spouse == null
 *
 * With Annotation:
 *   person.name == "Bob"
 *   person.age == 50
 *   person.spouse == null
 */
public @interface SkipMissingDeserializationFields {
    boolean value() default true;
}
