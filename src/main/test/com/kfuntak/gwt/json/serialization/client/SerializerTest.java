package com.kfuntak.gwt.json.serialization.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.kfuntak.gwt.json.serialization.client.domain.*;
import org.junit.Test;

import java.util.*;

public class SerializerTest extends GWTTestCase {
    public String getModuleName() {
        return "com.kfuntak.gwt.json.serialization.GWTProJsonSerializer";
    }

    PhoneNumber createTestPhone() {
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setNumber("123-123-1234");
        phoneNumber.setExt("123");
        phoneNumber.setListedStatus("New");
        phoneNumber.setType("Home");
        return phoneNumber;
    }

    String createTestPhoneJson() {
        String json = "{\"number\":\"123-123-1234\", \"ext\":\"123\", \"type\":\"Home\", \"listedStatus\":\"New\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.PhoneNumber\"}";
        return json;
    }

    @Test
    public void testSerializeArbirtraryArrayList() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        ArrayList<String> list = new ArrayList<String>();
        list.add("Heath");
        list.add("Pax");
        list.add("Soren");
        list.add("Gage");
        String names = serializer.serialize(list);
        assertEquals("[\"Heath\",\"Pax\",\"Soren\",\"Gage\"]",names);
    }

    @Test
    public void testSerializeArbitraryHashMap() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", "Heath");
        map.put("age", "36");
        map.put("city", "Temple");
        String info = serializer.serialize(map);
        assertEquals("{\"age\":\"36\", \"name\":\"Heath\", \"city\":\"Temple\"}",info);
    }

    @Test
    public void testDeserializeArbitraryArrayList() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        String listJson = "[\"Heath\",\"Pax\",\"Soren\",\"Gage\"]";
        ArrayList list = (ArrayList) serializer.deSerialize(listJson, "java.util.ArrayList");
        ArrayList<String> elist = new ArrayList<String>();
        elist.add("Heath");
        elist.add("Pax");
        elist.add("Soren");
        elist.add("Gage");
        assertEquals(elist,list);
    }

    @Test
    public void testDeserializeArbitraryHashMap() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        String mapJson = "{\"age\":\"36\", \"name\":\"Heath\", \"city\":\"Temple\"}";
        HashMap map = (HashMap) serializer.deSerialize(mapJson, "java.util.HashMap");
        HashMap<String, String> emap = new HashMap<String, String>();
        emap.put("name", "Heath");
        emap.put("age", "36");
        emap.put("city", "Temple");
        assertEquals(emap,map);
    }

    @Test
    public void testEnum() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        Address address = new Address();
        address.setType(AddressType.WORK);
        String json = serializer.serialize(address);
        Address newAddress = (Address)serializer.deSerialize(json);
        assertEquals(AddressType.WORK,newAddress.getType());
    }

    @Test
    public void testSimpleSerialization() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        PhoneNumber phoneNumber = createTestPhone();
        String phoneJson = serializer.serialize(phoneNumber);
        String referenceJson = createTestPhoneJson();
        assertEquals(referenceJson, phoneJson);
    }

    @Test
    public void testSimpleDeserialization() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        PhoneNumber referencePhone = createTestPhone();
        PhoneNumber phoneNumber = (PhoneNumber)serializer.deSerialize(createTestPhoneJson(), "com.kfuntak.gwt.json.serialization.client.domain.PhoneNumber");
        assertEquals(referencePhone.toString(), phoneNumber.toString());
    }

    @Test
    public void testCollectionSerialization() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        School school = new School();
        school.setGradeLevels(Arrays.asList("1","2","3"));
        String schoolJson = serializer.serialize(school);
        String referenceJson = "{\"refIdKey\":null, \"refId\":null, \"schoolName\":null, \"schoolShortName\":null, \"schoolUrl\":null, \"status\":0, \"gradeLevels\":[\"1\",\"2\",\"3\"], \"startDate\":null, \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.School\"}";
        assertEquals(referenceJson,schoolJson);
    }

    @Test
    public void testCollectionDeserialization() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        String schoolJson = "{\"refIdKey\":null, \"refId\":null, \"schoolName\":null, \"schoolShortName\":null, \"schoolUrl\":null, \"status\":0, \"gradeLevels\":[\"1\",\"2\",\"3\"], \"startDate\":null, \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.School\"}";
        School school = (School) serializer.deSerialize(schoolJson, "com.kfuntak.gwt.json.serialization.client.domain.School");
        assertEquals(Arrays.asList("1", "2", "3"), school.getGradeLevels());
    }

    @Test
    public void testHashMapSerialization() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        Address address = new Address();
        HashMap<String, String> phoneMap = new HashMap<String, String>();
        phoneMap.put("Home", "123-123-1234");
        phoneMap.put("Work", "521-521-5231");
        address.setPhoneNumbers(phoneMap);
        String addressJson = serializer.serialize(address);
        String referenceJson = "{\"line1\":null, \"line2\":null, \"city\":null, \"state\":null, \"country\":null, \"zipCode\":null, \"phoneNumbers\":{\"Home\":\"123-123-1234\", \"Work\":\"521-521-5231\"}, \"type\":\"HOME\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Address\"}";
        assertEquals(referenceJson,addressJson);
    }

    @Test
    public void testHashMapDeserialization() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        HashMap<String, String> phoneMap = new HashMap<String, String>();
        phoneMap.put("Home", "123-123-1234");
        phoneMap.put("Work", "521-521-5231");
        String json = "{\"line1\":null, \"line2\":null, \"city\":null, \"state\":null, \"country\":null, \"zipCode\":null, \"phoneNumbers\":{\"Home\":\"123-123-1234\", \"Work\":\"521-521-5231\"}, \"type\":\"HOME\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Address\"}";
        Address address = (Address) serializer.deSerialize(json, "com.kfuntak.gwt.json.serialization.client.domain.Address");
        assertEquals(phoneMap,address.getPhoneNumbers());
    }

    @Test
    public void testNestedHashMapDeSerialization() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        HashMap<String, HashMap<String, Contact>> familyMap = new HashMap<String, HashMap<String, Contact>>();
        HashMap<String, Contact> familyBranchMap = new HashMap<String, Contact>();
        familyBranchMap.put("uncle", new Contact("Bill"));
        familyBranchMap.put("aunt", new Contact("Jenny"));
        familyMap.put("maternal",familyBranchMap);
        familyBranchMap = new HashMap<String, Contact>();
        familyBranchMap.put("uncle", new Contact("John"));
        familyBranchMap.put("aunt", new Contact("Ruth"));
        familyMap.put("paternal", familyBranchMap);
        String json = "{\"refId\":null, \"family\":{\"paternal\":{\"aunt\":{\"refId\":null, \"family\":{}, \"name\":\"Ruth\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Contact\"}, \"uncle\":{\"refId\":null, \"family\":{}, \"name\":\"John\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Contact\"}}, \"maternal\":{\"aunt\":{\"refId\":null, \"family\":{}, \"name\":\"Jenny\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Contact\"}, \"uncle\":{\"refId\":null, \"family\":{}, \"name\":\"Bill\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Contact\"}}}, \"name\":\"Mark\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Contact\"}";
        Contact contact = (Contact)serializer.deSerialize(json, "com.kfuntak.gwt.json.serialization.client.domain.Contact");
        assertEquals(familyMap.toString(),contact.getFamily().toString());
    }

    @Test
    public void testNestedHashMapSerialization() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);
        HashMap<String, HashMap<String, Contact>> familyMap = new HashMap<String, HashMap<String, Contact>>();
        HashMap<String, Contact> familyBranchMap = new HashMap<String, Contact>();
        familyBranchMap.put("uncle", new Contact("Bill"));
        familyBranchMap.put("aunt", new Contact("Jenny"));
        familyMap.put("maternal",familyBranchMap);
        familyBranchMap = new HashMap<String, Contact>();
        familyBranchMap.put("uncle", new Contact("John"));
        familyBranchMap.put("aunt", new Contact("Ruth"));
        familyMap.put("paternal", familyBranchMap);
        Contact contact = new Contact("Mark");
        contact.setFamily(familyMap);
        String refJson = "{\"refId\":null, \"family\":{\"paternal\":{\"aunt\":{\"refId\":null, \"family\":{}, \"name\":\"Ruth\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Contact\"}, \"uncle\":{\"refId\":null, \"family\":{}, \"name\":\"John\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Contact\"}}, \"maternal\":{\"aunt\":{\"refId\":null, \"family\":{}, \"name\":\"Jenny\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Contact\"}, \"uncle\":{\"refId\":null, \"family\":{}, \"name\":\"Bill\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Contact\"}}}, \"name\":\"Mark\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Contact\"}";
        String json = serializer.serialize(contact);
        assertEquals(refJson,json);
    }


    @Test
    public void testSerialization() {
        Serializer serializer = (Serializer) GWT.create(Serializer.class);

        String jsonText = "{"
                + "\"class\":\"com.kfuntak.gwt.json.serialization.client.domain.University\""
                + ",\"contactInfo\":"
                + "[{\"address\":"
                + "{\"city\":null,"
                + "\"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Address\","
                + "\"country\":\"India\","
                + "\"line1\":null,"
                + "\"line2\":null,"
                + "\"state\":\"Tamilnadu\","
                + "\"zipCode\":null},"
                + "\"name\":\"Peter\","
                + "\"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Contact\","
                + "\"family\":{\"paternal\":{\"aunt\":{\"refId\":null, \"family\":{}, \"name\":\"Ruth\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Contact\"}, \"uncle\":{\"refId\":null, \"family\":{}, \"name\":\"John\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Contact\"}}, \"maternal\":{\"aunt\":{\"refId\":null, \"family\":{}, \"name\":\"Jenny\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Contact\"}, \"uncle\":{\"refId\":null, \"family\":{}, \"name\":\"Bill\", \"class\":\"com.kfuntak.gwt.json.serialization.client.domain.Contact\"}}},"
                + "\"phoneNumber\":{\"class\":\"com.kfuntak.gwt.json.serialization.client.domain.PhoneNumber\","
                + "\"ext\":null,"
                + "\"listedStatus\":null,"
                + "\"type\":null},"
                + "\"refId\":null}],"
                + "\"forVerification\":\"Really for verification\","
                + "\"gradeLevels\":[\"12\",\"11\"],"
                + "\"refId\":\"cms\","
                + "\"refIdKey\":null,"
                + "\"schoolName\":\"CMS\","
                + "\"schoolShortName\":null,"
                + "\"schoolUrl\":\"http://cms.in\","
                + "\"startDate\":1252046885585,"
                + "\"status\":11}";
        University university = (University) serializer.deSerialize(jsonText, "com.kfuntak.gwt.json.serialization.client.domain.University");
        University refUniversity = createRefUniversity();
        assertEquals(refUniversity.toString(),university.toString());
    }

    private University createRefUniversity() {
        University university = new University();
        Set<Contact> contactInfo = new HashSet<Contact>();
        university.setContactInfo(contactInfo);
        Contact contact = new Contact("Peter");
        contactInfo.add(contact);
        Address address = new Address();
        contact.setAddress(address);
        address.setState("Tamilnadu");
        address.setCountry("India");
        PhoneNumber phoneNumber = new PhoneNumber();
        contact.setPhoneNumber(phoneNumber);
        HashMap<String, HashMap<String, Contact>> familyMap = new HashMap<String, HashMap<String, Contact>>();
        HashMap<String, Contact> familyBranchMap = new HashMap<String, Contact>();
        familyBranchMap.put("uncle", new Contact("Bill"));
        familyBranchMap.put("aunt", new Contact("Jenny"));
        familyMap.put("maternal",familyBranchMap);
        familyBranchMap = new HashMap<String, Contact>();
        familyBranchMap.put("uncle", new Contact("John"));
        familyBranchMap.put("aunt", new Contact("Ruth"));
        familyMap.put("paternal", familyBranchMap);
        contact.setFamily(familyMap);
        phoneNumber = new PhoneNumber();
        contact.setPhoneNumber(phoneNumber);
        university.setGradeLevels(Arrays.asList("12","11"));
        university.setForVerification("Really for verification");
        university.setSchoolName("CMS");
        university.setSchoolUrl("http://cms.in");
        university.setStartDate(new Date(109,8,4,1,48,5));
        university.setStatus(11);
        university.setRefId("cms");
        return university;
    }
}
