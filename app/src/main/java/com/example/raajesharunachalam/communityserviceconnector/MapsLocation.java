package com.example.raajesharunachalam.communityserviceconnector;

import com.google.gson.annotations.SerializedName;

/**
 * Represents an individual location and holds the current Latitude and longitude returned by the
 * Google Maps API.
 * <p>
 * Created by raajesharunachalam on 4/8/17.
 *
 * @author arnchlm2
 */

public class MapsLocation {
    @SerializedName("lat")
    private double latitude;

    @SerializedName("lng")
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
