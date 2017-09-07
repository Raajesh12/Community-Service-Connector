package com.example.raajesharunachalam.communityserviceconnector;

/**
 * Represents the outer class that holds all the results of the call to the Google Maps API as well
 * as a status code on the results.
 * <p>
 * Created by raajesharunachalam on 4/8/17.
 *
 * @author arnchlm2
 */

public class MapsList {
    private MapsResultsComponents[] results;

    private String status;

    public MapsResultsComponents[] getResults() {
        return results;
    }

    public String getStatus() {
        return status;
    }
}
