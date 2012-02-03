package com.kfuntak.gwt.json.serialization.client.domain;

import com.kfuntak.gwt.json.serialization.client.JsonSerializable;

import java.lang.String;
import java.util.HashMap;

public class Address implements JsonSerializable {
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String country;
    private String zipCode;
    private HashMap<String, String> phoneNumbers = new HashMap<String, String>();
    private AddressType type = AddressType.HOME;

    public AddressType getType() {
        return type;
    }

    public void setType(AddressType type) {
        this.type = type;
    }

    public HashMap<String, String> getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(HashMap<String, String> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getLine1() {
        return line1;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public String getLine2() {
        return line2;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("{");
        buffer.append("line1:");
        buffer.append(line1 + ",");
        buffer.append("line2:");
        buffer.append(line2 + ",");
        buffer.append("city:");
        buffer.append(city + ",");
        buffer.append("state:");
        buffer.append(state + ",");
        buffer.append("country:");
        buffer.append(country + ",");
        buffer.append("zipCode:");
        buffer.append(zipCode + ",");
        buffer.append("phoneNumbers : {");
        buffer.append("office : \"123-123-1234\",");
        buffer.append("fax : \"123-123-1235\"");
        buffer.append("}");
        buffer.append("}");
        return buffer.toString();
    }
}
