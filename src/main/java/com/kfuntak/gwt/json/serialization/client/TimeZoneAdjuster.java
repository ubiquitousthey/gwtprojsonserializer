package com.kfuntak.gwt.json.serialization.client;

public interface TimeZoneAdjuster {

    /***
     * Given a time in milliseconds since Jan 1 1970 
     * adjust the time by adding or subtracting milliseconds then
     * return the adjusted time in milliseconds since Jan 1 1970
     * 
     * @param intime - the time to be adjusted
     * @return The adjusted time 
     */
    public long adjust(long intime);
}
