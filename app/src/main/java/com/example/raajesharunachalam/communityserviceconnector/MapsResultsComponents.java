package com.example.raajesharunachalam.communityserviceconnector;

import com.google.gson.annotations.SerializedName;

/**
 * Holds the formatted address and the geometry of a result component returned by the Google Maps API.
 * <p>
 * Created by raajesharunachalam on 4/8/17.
 *
 * @author arnchlm2
 */

public class MapsResultsComponents {
    @SerializedName("formatted_address")
    private String formattedAddress;

    private MapsGeometry geometry;

    public String getFormattedAddress() {
        return formattedAddress;
    }

    public MapsGeometry getGeometry() {
        return geometry;
    }
}
