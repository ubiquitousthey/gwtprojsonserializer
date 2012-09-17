package com.kfuntak.gwt.json.serialization;

import java.io.PrintWriter;
import java.util.*;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.*;
import com.google.gwt.dev.javac.typemodel.JEnumConstant;
import com.google.gwt.dev.javac.typemodel.JEnumType;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import com.kfuntak.gwt.json.serialization.client.*;

public class SerializationGenerator extends Generator {

    private JClassType stringClass;
    private SourceWriter srcWriter;
    private String className;
    private TypeOracle typeOracle;
    private boolean DEBUG = false;
    private JClassType MARKER_INTERFACE;
    private String indention = "";
    private int suffixIndex = 0;

    public SerializationGenerator() throws NotFoundException {
    }

    public void writeLn(String code) {
        srcWriter.println(code);
        if (DEBUG) {
            System.out.println(indention + code);
        }
    }

    public void indent() {
        srcWriter.indent();
        indention += "    ";
    }

    public void outdent() {
        srcWriter.outdent();
        indention = indention.substring(0, indention.length() - 4);
    }

    public String generate(TreeLogger logger, GeneratorContext ctx,
            String requestedClass) throws UnableToCompleteException {
        //get the type oracle
        typeOracle = ctx.getTypeOracle();
        try {
            MARKER_INTERFACE = typeOracle.getType("com.kfuntak.gwt.json.serialization.client.JsonSerializable");
        } catch (NotFoundException e) {
            throw new UnableToCompleteException();
        }
        assert (typeOracle != null);
        assert (MARKER_INTERFACE != null);
        stringClass = typeOracle.findType(String.class.getName());
        assert (stringClass != null);

        //get class from type oracle
        JClassType serializeClass = typeOracle.findType(requestedClass);

        if (serializeClass == null) {
            logger.log(TreeLogger.ERROR, "Unable to find metadata for type '"
                    + requestedClass + "'", null);
            throw new UnableToCompleteException();
        }

        //create source writer
        String packageName = serializeClass.getPackage().getName();
        className = serializeClass.getSimpleSourceName() + "_TypeSerializer";
        PrintWriter printWriter = ctx.tryCreate(logger, packageName, className);
        if (printWriter == null) {
            return packageName + "." + className;
        }
        ClassSourceFileComposerFactory composerFactory =
                new ClassSourceFileComposerFactory(packageName, className);
        composerFactory.setSuperclass("com.kfuntak.gwt.json.serialization.client.Serializer");

        JClassType[] subTypes = addImports(composerFactory);

        srcWriter = composerFactory.createSourceWriter(ctx, printWriter);
        if (srcWriter == null) {
            return packageName + "." + className;
        }

        //create a serializer for each interface that supports Serializable
        HashMap<String, JClassType> serializerMap = writeTypeSerializers(subTypes);

        //in the class constructor, add each serializer
        writeTypeSerializerConstructor(serializerMap);

        srcWriter.commit(logger);
        return packageName + "." + className;
    }

    private HashMap<String, JClassType> writeTypeSerializers(JClassType[] subTypes) {
        HashMap<String, JClassType> serializerMap = new HashMap<String, JClassType>();
        for (JClassType typeToSerialize : subTypes) {
            if (typeToSerialize.isAbstract()) {
                continue;
            }
            String serializerName = writeTypeSerializerClass(typeToSerialize);
            serializerMap.put(serializerName, typeToSerialize);
        }
        return serializerMap;
    }

    private String writeTypeSerializerClass(JClassType typeToSerialize) {
        String serializerName = typeToSerialize.getName().replaceAll("\\.","\\$");
        writeLn("public class " + serializerName + "_SerializableImpl extends AbstractObjectSerializer{");
        indent();

        try {
            generateTypeSerialization(typeToSerialize);
            generateTypeDeserialization(typeToSerialize.getQualifiedSourceName());

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (UnableToCompleteException e) {
            e.printStackTrace();
        }
        outdent();
        writeLn("}");
        return serializerName;
    }

    private JClassType[] addImports(ClassSourceFileComposerFactory composerFactory) throws UnableToCompleteException {
        Set<String> importsList = new HashSet<String>();
        // Java imports
        importsList.add(Collection.class.getName());
        importsList.add(List.class.getName());
        importsList.add(ArrayList.class.getName());
        importsList.add(HashSet.class.getName());
        importsList.add(Date.class.getName());
        importsList.add(HashMap.class.getName());
        importsList.add(Map.class.getName());
//		// GWT imports
        importsList.add(com.google.gwt.core.client.GWT.class.getName());
        importsList.add(com.google.gwt.json.client.JSONNull.class.getName());
        importsList.add(com.google.gwt.json.client.JSONNumber.class.getName());
        importsList.add(com.google.gwt.json.client.JSONString.class.getName());
        importsList.add(com.google.gwt.json.client.JSONValue.class.getName());
        importsList.add(com.google.gwt.json.client.JSONObject.class.getName());
        importsList.add(com.google.gwt.json.client.JSONArray.class.getName());
        importsList.add(com.google.gwt.json.client.JSONBoolean.class.getName());
        importsList.add(com.google.gwt.json.client.JSONParser.class.getName());
        importsList.add(com.google.gwt.json.client.JSONException.class.getName());
//		// Module imports
        importsList.add(AbstractObjectSerializer.class.getName());
        importsList.add(MARKER_INTERFACE.getQualifiedSourceName());
        importsList.add(IncompatibleObjectException.class.getName());
        importsList.add(SerializerHelper.class.getName());
        importsList.add(DeserializerHelper.class.getName());

        JClassType[] subTypes = MARKER_INTERFACE.getSubtypes();
        for (int i = 0; i < subTypes.length; ++i) {
            String serializedType = subTypes[i].getQualifiedSourceName();
            JClassType baseType;
            try {
                baseType = typeOracle.getType(serializedType);
            } catch (NotFoundException e) {
                throw new UnableToCompleteException();
            }
            discoverImports(importsList, baseType);
        }

        for (String typeToImport : importsList) {
            composerFactory.addImport(typeToImport);
        }
        return subTypes;
    }

    private void writeTypeSerializerConstructor(HashMap<String, JClassType> serializerMap) {
        writeLn("public " + className + "(){");
        indent();
        for (Map.Entry<String, JClassType> entry : serializerMap.entrySet()) {
            addObjectSerializer(entry);
        }

        outdent();
        writeLn("}");
    }

    private void addObjectSerializer(Map.Entry<String, JClassType> entry) {
        writeLn("addObjectSerializer(\"" + entry.getValue().getQualifiedSourceName() + "\", new " + entry.getKey() + "_SerializableImpl() );");
        if (!entry.getValue().getQualifiedSourceName().equals(entry.getValue().getQualifiedBinaryName())) {
            writeLn("addObjectSerializer(\"" + entry.getValue().getQualifiedBinaryName() + "\", new " + entry.getKey() + "_SerializableImpl() );");
        }
    }

    private void discoverImports(Set<String> importsList, JClassType baseType) {
        addImports(importsList, baseType);
        for (JField field : baseType.getFields()) {
            if (!field.isStatic() && !field.isTransient()) {
                JType fieldType = field.getType();
                if (fieldType.isClassOrInterface() != null) {
                    addImports(importsList, fieldType.isClassOrInterface());
                }
            }
        }
    }

    private void addImports(Set<String> importsList, JClassType baseType) {
        if (baseType.isEnum() != null) {
            importsList.add(baseType.getQualifiedSourceName());
        } else if (baseType.isParameterized() != null) {
            importsList.add(baseType.getQualifiedSourceName());
            JParameterizedType parameterizedType = baseType.isParameterized();
            for (JClassType typeParm : parameterizedType.getTypeArgs()) {
                if(!importsList.contains(typeParm.getQualifiedSourceName()))
                addImports(importsList, typeParm);
            }
        } else if (baseType.isAssignableTo(MARKER_INTERFACE) && !importsList.contains(baseType.getQualifiedSourceName())) {
            importsList.add(baseType.getQualifiedSourceName());
            discoverImports(importsList, baseType);
        }
    }

    private void generateTypeDeserialization(String typeName) throws NotFoundException, UnableToCompleteException {

        JClassType baseType = typeOracle.getType(typeName);
        String packageName = baseType.getPackage().getName();

        writeLn("public Object deSerialize(JSONValue jsonValue, String className) throws JSONException{");
        indent();
        // Return null if the given object is null
        writeLn("if(jsonValue instanceof JSONNull){");
        indent();
        writeLn("return null;");
        outdent();
        writeLn("}");

        // Throw Incompatible exception is JsonValue is not an instance of
        // JsonObject
        writeLn("if(!(jsonValue instanceof JSONObject)){");
        indent();
        writeLn("throw new IncompatibleObjectException();");
        outdent();
        writeLn("}");

        // Initialise JsonObject then
        String baseTypeName = baseType.getSimpleSourceName();
        writeLn("JSONObject jsonObject=(JSONObject)jsonValue;");
        writeLn(baseTypeName + " mainResult=new " + baseTypeName + "();");
        writeLn("JSONArray inputJsonArray=null;");
        writeLn("int inpJsonArSize=0;");
        writeLn("JSONValue fieldJsonValue=null;");
        writeLn("JSONObject inputJsonObject=null;");

        // Start deSerialisation
        List<JField> allFields = new ArrayList<JField>();
        JField[] fields = baseType.getFields();
        for (JField field : fields) {
            if (!field.isStatic() && !field.isTransient()) {
                allFields.add(field);
            }
        }
        if (baseType.isAssignableTo(MARKER_INTERFACE)) {
            boolean flag = true;
            JClassType superClassType = baseType;
            while (flag) {
                superClassType = superClassType.getSuperclass();
                if (superClassType.isAssignableTo(MARKER_INTERFACE)) {
                    JField[] subClassFields = superClassType.getFields();
                    for (JField subClassField : subClassFields) {
                        if (!subClassField.isStatic() && !subClassField.isTransient()) {
                            allFields.add(subClassField);
                        }
                    }
                } else {
                    flag = false;
                }
            }
        }
        fields = new JField[allFields.size()];
        allFields.toArray(fields);

        for (JField field : fields) {
            JType fieldType = field.getType();
            String fieldName = field.getName();
            writeLn("fieldJsonValue=jsonObject.get(\"" + fieldName + "\");");
            if (fieldType.isPrimitive() != null) {
                JPrimitiveType fieldPrimitiveType = (JPrimitiveType) fieldType;
                JClassType fieldBoxedType = typeOracle.getType(fieldPrimitiveType.getQualifiedBoxedSourceName());
                String valueString = deserializeSimpleType(fieldBoxedType, "fieldJsonValue");
                setValue("mainResult", baseType, field, valueString);
            } else {
                // Return null if JSON object is null
                JClassType fieldClassType = (JClassType) fieldType;
                String value = deserializeValue(fieldClassType, "fieldJsonValue");
                setValue("mainResult", baseType, field, value);
            }
        }

        writeLn("return mainResult;");
        outdent();
        writeLn("}");
    }

    private void setValue(String dest, JClassType classType, JField field, String value) {
        String fieldNameForGS = getNameForGS(field.getName());
        Set<? extends JClassType> classes = classType.getFlattenedSupertypeHierarchy();
        String setter = "set" + fieldNameForGS;
        for (JClassType aClass : classes) {
            JMethod method = aClass.findMethod(setter, new JType[]{field.getType()});
            if (method != null) {
                writeLn(dest + ".set" + fieldNameForGS + "(" + value + ");");
                return;
            }
        }
        writeLn(dest + "." + field.getName() + "=" + value + ";");
    }

    private String deserializeCollection(JClassType colType, String inputColVar) throws NotFoundException, UnableToCompleteException {
        String loopSuffix = getLoopVarSuffix();
        JParameterizedType parameterizedType = (JParameterizedType) colType;
        JClassType valueType = parameterizedType.getTypeArgs()[0];
        String valueTypeString = createTypeString(valueType, false);
        String colVar = "col" + loopSuffix;// Field Collection Result

        writeLn(createTypeString(colType, false) + " " + colVar + " = new " + createTypeString(colType, true) + "();");
        writeLn("DeserializerHelper.fillCollection(" + colVar + ", " + inputColVar + ", new DeserializationCallback() {");
        indent();
        writeLn("public " + valueTypeString + " deserialize(JSONValue jsonValue) {");
        indent();
        String value = deserializeValue(valueType, "jsonValue");
        writeLn("return " + value + ";");
        outdent();
        writeLn("}");
        outdent();
        writeLn("});");
        return colVar;
    }

    private String deserializeValue(JClassType valueType, String valVar) throws NotFoundException, UnableToCompleteException {
        String value;
        if (valueType.isAssignableTo(MARKER_INTERFACE)) {
            value = deserializeType(valueType, valVar);
        } else if (valueType.isEnum() != null) {
            value = deserializeEnum((JEnumType) valueType, valVar);
        } else if (valueType.isAssignableTo(typeOracle.getType("java.util.Map"))) {
            value = deserializeMap(valueType, valVar);
        } else if (valueType.isAssignableTo(typeOracle.getType("java.util.Collection"))) {
            value = deserializeCollection(valueType, valVar);
        } else {
            value = deserializeSimpleType(valueType, valVar);
        }
        return value;
    }

    private String createTypeParmString(JParameterizedType parameterizedType) throws NotFoundException {
        StringBuilder sb = new StringBuilder();
        sb.append('<');
        boolean first = true;
        for (JClassType type : parameterizedType.getTypeArgs()) {
            if (!first) {
                sb.append(',');
            } else {
                first = false;
            }

            sb.append(createTypeString(type, false));
        }
        sb.append('>');
        return sb.toString();
    }

    private String createTypeString(JType fieldType, boolean forCreation) throws NotFoundException {
        JPrimitiveType primitiveType = fieldType.isPrimitive();
        if (primitiveType != null) {
            return createTypeString(typeOracle.getType(primitiveType.getQualifiedBoxedSourceName()),forCreation);
        }

        JParameterizedType parameterizedType = fieldType.isParameterized();
        if (parameterizedType != null) {
            StringBuilder sb = new StringBuilder();
            if (forCreation && parameterizedType.getName().equals("Map")) {
                sb.append("HashMap");
            } else if (forCreation && parameterizedType.getName().equals("List")) {
                sb.append("ArrayList");
            } else if (forCreation && parameterizedType.getName().equals("Set")) {
                sb.append("HashSet");
            } else {
                sb.append(parameterizedType.getName());
            }
            sb.append(createTypeParmString(parameterizedType));
            return sb.toString();
        }

        return ((JClassType) fieldType).getName();
    }
    
    private String deserializeMap(JClassType mapType, String inputMapVar) throws UnableToCompleteException, NotFoundException {
        JParameterizedType parameterizedType = (JParameterizedType) mapType;
        JClassType keyParm = parameterizedType.getTypeArgs()[0];
        JClassType valueParm = parameterizedType.getTypeArgs()[1];
        String valueTypeString = createTypeString(valueParm, false);
        if(!keyParm.getQualifiedSourceName().equals("java.lang.String")) {
            throw new UnableToCompleteException();
        }
        String loopSuffix = getLoopVarSuffix();
        String mapVar = "map" + loopSuffix;// Field Collection Result

        writeLn(createTypeString(mapType, false) + " " + mapVar + " = new " + createTypeString(mapType, true) + "();");
        writeLn("DeserializerHelper.fillMap(" + mapVar + ", " + inputMapVar + ", new DeserializationCallback<"+ valueTypeString +"> () {");
        indent();
        writeLn("public " + valueTypeString + " deserialize(JSONValue jsonValue) {");
        indent();
        String value = deserializeValue(valueParm, "jsonValue");
        writeLn("return " + value + ";");
        outdent();
        writeLn("}");
        outdent();
        writeLn("});");
        return mapVar;
    }

    private String deserializeEnum(JEnumType enumType, String inputValVar) {
        writeLn("//deserializeEnum - " + enumType.toString() + " - " + inputValVar);
        String enumVar = "enum" + getLoopVarSuffix();
        JEnumConstant defaultConstant = enumType.getEnumConstants()[0];
        writeLn(enumType.getSimpleSourceName() + " " + enumVar + " = null;");
        writeLn("if(" + inputValVar + " != null && " + inputValVar + ".isString() != null) {");
        indent();
        writeLn(enumVar + " = " + enumType.getSimpleSourceName() + ".valueOf(" + inputValVar + ".isString().stringValue());");
        outdent();
        writeLn("}");
        return enumVar;
    }

    private String serializeEnum(JEnumType enumType, String inputValVar) {
        writeLn("//Serialize Enum");
        String enumVar = "enum" + getLoopVarSuffix();
        writeLn("JSONValue " + enumVar + " = JSONNull.getInstance();");
        writeLn("if ("+inputValVar+" != null){");
        indent();
        writeLn(enumVar + "= new JSONString((("+enumType.getSimpleSourceName()+")"+inputValVar+").name());");
        outdent();
        writeLn("}");
        return enumVar;
    }

    private String deserializeSimpleType(JClassType fieldClassType, String variable) {
        if (fieldClassType.getQualifiedSourceName().equals("java.lang.Short")) {
            return "DeserializerHelper.getShort(" + variable + ")";
        } else if (fieldClassType.getQualifiedSourceName().equals("java.lang.Byte")) {
            return "DeserializerHelper.getByte(" + variable + ")";
        } else if (fieldClassType.getQualifiedSourceName().equals("java.lang.Long")) {
            return "DeserializerHelper.getLong(" + variable + ")";
        } else if (fieldClassType.getQualifiedSourceName().equals("java.lang.Integer")) {
            return "DeserializerHelper.getInt(" + variable + ")";
        } else if (fieldClassType.getQualifiedSourceName().equals("java.lang.Float")) {
            return "DeserializerHelper.getFloat(" + variable + ")";
        } else if (fieldClassType.getQualifiedSourceName().equals("java.lang.Double")) {
            return "DeserializerHelper.getDouble(" + variable + ")";
        } else if (fieldClassType.getQualifiedSourceName().equals("java.lang.Boolean")) {
            return "DeserializerHelper.getBoolean(" + variable + ")";
        } else if (fieldClassType.getQualifiedSourceName().equals("java.lang.Character")) {
            return "DeserializerHelper.getChar(" + variable + ")";
        } else if (fieldClassType.getQualifiedSourceName().equals("java.util.Date")) {
            return "DeserializerHelper.getDate(" + variable + ")";
        } else {
            return "DeserializerHelper.getString(" + variable + ")";
        }
    }

    private String deserializeType(JClassType fieldClassType, String inputTypeVar) {
        writeLn("//deserializeType - " + fieldClassType + " - " + inputTypeVar);
        String typeVar = "deserType" + getLoopVarSuffix();
        writeLn(fieldClassType.getName() + " " + typeVar + " = null;");
        writeLn("if (" + inputTypeVar + " != null && !(" + inputTypeVar + " instanceof JSONNull)){");
        indent();

        writeLn(typeVar + " = " + "(" + fieldClassType.getSimpleSourceName() + ")Serializer_TypeSerializer.this.deSerialize(" + inputTypeVar + ", \""+fieldClassType.getQualifiedSourceName()+"\");");
        outdent();
        writeLn("}");
        return typeVar;
    }

    private void generateTypeSerialization(JClassType typeToSerialize) throws NotFoundException, UnableToCompleteException {
        String typeName = typeToSerialize.getQualifiedSourceName();

        JClassType baseType = typeOracle.getType(typeName);
        writeLn("public JSONValue serializeToJson(Object object){");
        indent();
        // Return JSONNull instance if object is null
        writeLn("if(object==null){");
        indent();
        writeLn("return JSONNull.getInstance();");
        outdent();
        writeLn("}");

        // Throw Incompatible Exception if object is not of the type it claims
        // to be
        writeLn("if(!(object instanceof " + baseType.getSimpleSourceName() + ")){");
        indent();
        writeLn("throw new IncompatibleObjectException();");
        outdent();
        writeLn("}");

        // Initialise result object
        writeLn("JSONObject mainResult=new JSONObject();");
        writeLn("Object fieldValue=null;");
        writeLn(baseType.getSimpleSourceName() + " mainVariable=(" + baseType.getSimpleSourceName() + ")object;");

        // Serialise fields
        List<JField> allFields = new ArrayList<JField>();
        JField[] fields = baseType.getFields();
        for (JField field : fields) {
            if (!field.isStatic() && !field.isTransient()) {
                allFields.add(field);
            }
        }
        if (baseType.isAssignableTo(MARKER_INTERFACE)) {
            boolean flag = true;
            JClassType superClassType = baseType;
            while (flag) {
                superClassType = superClassType.getSuperclass();
                if (superClassType.isAssignableTo(MARKER_INTERFACE)) {
                    JField[] subClassFields = superClassType.getFields();
                    for (JField subClassField : subClassFields) {
                        if (!subClassField.isStatic() && !subClassField.isTransient()) {
                            allFields.add(subClassField);
                        }
                    }
                } else {
                    flag = false;
                }
            }
        }
        fields = new JField[allFields.size()];
        allFields.toArray(fields);
        for (JField field : fields) {
            JType fieldType = field.getType();
            String fieldName = field.getName();
            assignValue("fieldValue", baseType, field);
            JClassType fieldClassType = boxType(fieldType);
            String value = serializeValue(fieldClassType, "fieldValue");
            writeLn("mainResult.put(\"" + fieldName + "\"," + value + ");");
        }

        if(!typeToSerialize.isAnnotationPresent(DontSerializeClass.class)){
            // Put class type for compatibility with flex JSON [de]serialisation
            writeLn("mainResult.put(\"class\",new JSONString(\"" + baseType.getQualifiedSourceName() + "\"));");
        }

        // Return statement
        writeLn("return mainResult;");
        outdent();
        writeLn("}");
    }

    private void assignValue(String lvalue, JClassType classType, JField field) throws NotFoundException {
        String getter = getGetter(classType, field);
        if(getter != null) {
            writeLn(lvalue + "=mainVariable." + getter + "();");
        } else {
            writeLn(lvalue + "=mainVariable." + field.getName() + ";");
        }
    }

    private String getGetter(JClassType classType, JField field) throws NotFoundException {
        String getter = null;
        String fieldNameForGS = getNameForGS(field.getName());
        Set<? extends JClassType> classes = classType.getFlattenedSupertypeHierarchy();

        if (boxType(field.getType()).getQualifiedSourceName().equals("java.lang.Boolean")) {
            getter = "is" + fieldNameForGS;
            for (JClassType aClass : classes) {
                JMethod method =  aClass.findMethod(getter, new JType[0]);
                if (method != null) {
                    return getter;
                }
            }
        }

        getter = "get" + fieldNameForGS;
        for (JClassType aClass : classes) {
            JMethod method =  aClass.findMethod(getter, new JType[0]);
            if (method != null) {
                return getter;
            }
        }

        return null;
    }

    private JClassType boxType(JType fieldType) throws NotFoundException {
        JPrimitiveType primitiveType = fieldType.isPrimitive();
        if (primitiveType != null) {
            return typeOracle.getType(primitiveType.getQualifiedBoxedSourceName());
        }

        return (JClassType)fieldType;
    }

    private String serializeCollection(JClassType fieldClassType, String variable) throws NotFoundException, UnableToCompleteException {
        writeLn("//Serialize Collection");
        JParameterizedType parameterizedType = (JParameterizedType) fieldClassType;
        JClassType typeParm = parameterizedType.getTypeArgs()[0];
        String typeParmName = createTypeString(typeParm, false);
        String suffix = getLoopVarSuffix();
        String colVar = "col"+suffix;
        writeLn("JSONValue " + colVar + " = SerializerHelper.getCollection((Collection<" + typeParmName + ">)" + variable + ", new SerializationCallback() {");
        indent();
        writeLn("public JSONValue serialize(Object value) {");
        indent();
        typeParm = boxType(typeParm);
        String value = serializeValue(typeParm, "value");
        writeLn("return " + value + ";");
        outdent();
        writeLn("}");
        outdent();
        writeLn("});");

        return colVar;
    }

    private String serializeValue(JClassType typeParm, String valVar) throws NotFoundException, UnableToCompleteException {
        writeLn("//Serialize Value");
        String value;
        if (typeParm.isAssignableTo(typeOracle.getType("java.util.Collection"))) {
            value = serializeCollection(typeParm, valVar);
        } else if (typeParm.isAssignableTo(typeOracle.getType("java.util.Map"))) {
            value = serializeMap(typeParm, valVar);
        } else if (typeParm.isEnum() != null) {
            value = serializeEnum((JEnumType)typeParm, valVar);
        } else {
            value = getTypedValueAssignment(typeParm, valVar);
        }
        return value;
    }

    private String serializeMap(JClassType fieldClassType, String variable) throws NotFoundException, UnableToCompleteException {
        writeLn("//Serialize Map");
        JParameterizedType parameterizedType = (JParameterizedType) fieldClassType;
        JClassType keyParm = parameterizedType.getTypeArgs()[0];
        if(!keyParm.getQualifiedSourceName().equals("java.lang.String")) {
            throw new UnableToCompleteException();
        }
        JClassType valueParm = parameterizedType.getTypeArgs()[1];
        String genericClause = createTypeParmString(parameterizedType);
        String suffix = getLoopVarSuffix();
        String mapVar = "map" + suffix;
        writeLn("JSONValue "+ mapVar + " = SerializerHelper.getMap((Map" + genericClause + ")" + variable + ", new SerializationCallback () {");
        indent();
        writeLn("public JSONValue serialize(Object value) {");
        indent();
        valueParm = boxType(valueParm);
        String value = serializeValue(valueParm, "value");
        writeLn("return " + value + ";");
        outdent();
        writeLn("}");
        outdent();
        writeLn("});");
        return mapVar;
    }

    private String getTypedValueAssignment(JClassType fieldClassType, String variable) throws NotFoundException{
        String fieldClassTypeString = fieldClassType.getQualifiedSourceName();
        if (fieldClassTypeString.equals("java.lang.String")) {
            return "SerializerHelper.getString((String)"+ variable +")";
        } else if (fieldClassType.getQualifiedSourceName().equals("java.lang.Boolean")) {
            return "SerializerHelper.getBoolean((Boolean)"+ variable +")";
        } else if (fieldClassTypeString.equals("java.lang.Character")) {
            return "jsonValue=SerializerHelper.getChar((Character)"+ variable +")";
        } else if (fieldClassType.isAssignableTo(typeOracle.getType("java.lang.Number"))) {
            return "SerializerHelper.getNumber((Number)"+ variable +")";
        } else if (fieldClassTypeString.equals("java.util.Date")) {
            return "SerializerHelper.getDate((Date)"+ variable +")";
        } else if (fieldClassType.isAssignableTo(MARKER_INTERFACE)) {
            return "Serializer_TypeSerializer.this.serializeToJson("+ variable +")";
        } else if (fieldClassType.isEnum() != null) {
            return variable + ".toString()";
        }

        return(variable + ".toString()");
    }

    private static String getNameForGS(String name) {
        StringBuilder buffer = new StringBuilder(name);
        buffer.setCharAt(0, new String(new char[]{name.charAt(0)}).toUpperCase().charAt(0));
        return buffer.toString();
    }

    private String getLoopVarSuffix() {
        suffixIndex += 1;
        return Integer.toString(suffixIndex);
    }
}