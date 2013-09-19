package com.kfuntak.gwt.json.serialization.client.domain;

import java.util.ArrayList;
import java.util.HashMap;

public class Animal extends JsonableWithoutCruft {
    public Long refId = -1L;
    public String name;
    public String species;
    public Integer age;
    public HashMap<String, String> tricks = new HashMap<String, String>();
    public ArrayList<String> toys = new ArrayList<String>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Animal animal = (Animal) o;

        if (age != null ? !age.equals(animal.age) : animal.age != null) return false;
        if (name != null ? !name.equals(animal.name) : animal.name != null) return false;
        if (refId != null ? !refId.equals(animal.refId) : animal.refId != null) return false;
        if (species != null ? !species.equals(animal.species) : animal.species != null) return false;
        if (toys != null ? !toys.equals(animal.toys) : animal.toys != null) return false;
        if (tricks != null ? !tricks.equals(animal.tricks) : animal.tricks != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = refId != null ? refId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (species != null ? species.hashCode() : 0);
        result = 31 * result + (age != null ? age.hashCode() : 0);
        result = 31 * result + (tricks != null ? tricks.hashCode() : 0);
        result = 31 * result + (toys != null ? toys.hashCode() : 0);
        return result;
    }


    @Override
    public String toString() {
        return "Animal{" +
                "\n  refId=" + refId +
                "\n  name='" + name + '\'' +
                "\n  species='" + species + '\'' +
                "\n  age=" + age +
                "\n  tricks=" + tricks +
                "\n  toys=" + toys +
                "\n}";
    }
}
