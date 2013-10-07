package com.kfuntak.gwt.json.serialization.client;

public class DeserializationDateHelper {

    public static TimeZoneAdjuster TIMEZONEADJUSTER = new DefaultTimeZoneAdjuster();
    
    /***
     * By default performs no time adjustment
     * 
     * Set the static TIMEZONEADJUSTER with your own implementation to 
     * adjust the time as required by your client
     *  
     * @param time
     * @return outtime
     */
    public static long adjustTime(long time) {
        return TIMEZONEADJUSTER.adjust(time);
    }
}
