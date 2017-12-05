package com.example.ady.getiingcurrentlocation;

/**
 * Created by Ady on 12/4/2017.
 */

public class LocationCoord {
    String address;
    String Lat;
    String Atl;

    public LocationCoord(String address, String lat, String atl) {
        this.address = address;
        Lat = lat;
        Atl = atl;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return Lat;
    }

    public void setLat(String lat) {
        Lat = lat;
    }

    public String getAtl() {
        return Atl;
    }

    public void setAtl(String atl) {
        Atl = atl;
    }
}
