package com.kfuntak.gwt.json.serialization.client;

public class DefaultTimeZoneAdjuster implements TimeZoneAdjuster {

    /***
     * This is a passthrough that does not adjust the input time;
     */
    public long adjust(long intime) {
        return intime;
    }

}
