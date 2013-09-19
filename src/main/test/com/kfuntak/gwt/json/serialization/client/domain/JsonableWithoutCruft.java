package com.kfuntak.gwt.json.serialization.client.domain;

import com.kfuntak.gwt.json.serialization.client.JsonSerializable;
import com.kfuntak.gwt.json.serialization.client.SerializeClassField;
import com.kfuntak.gwt.json.serialization.client.SkipMissingDeserializationFields;
import com.kfuntak.gwt.json.serialization.client.SkipNullSerialization;

@SerializeClassField(false)
@SkipNullSerialization(true)
@SkipMissingDeserializationFields(true)
public class JsonableWithoutCruft implements JsonSerializable {
}
