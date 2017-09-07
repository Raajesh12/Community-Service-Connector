package com.example.raajesharunachalam.communityserviceconnector;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * An AsyncTask that takes in part of an event's details and then uses the address to make a call to
 * the Google Maps API and find the latitude/longitude of the address. Then pushes the new event to
 * the database.
 * <p>
 * Created by raajesharunachalam on 4/21/17.
 *
 * @author arnchlm2
 */

public class AddEventAsyncTask extends AsyncTask<String, Void, MapsList> {

    private static final String TAG = "AddEventAsyncTask";
    private static final String CORRECT_STATUS = "OK";

    Context context;
    String eventTitle;
    Map<String, String> eventDetails;

    public AddEventAsyncTask(Context context, String eventTitle, Map<String, String> eventDetails) {
        this.context = context;
        this.eventTitle = eventTitle;
        this.eventDetails = eventDetails;
    }

    /**
     * Calls the Google Maps API with the address/location entered by the user and serializes
     * the JSON received into objects.
     *
     * @param addresses The address the user enters. It will always only be one String.
     * @return A MapsList object that contains the response from the Google Maps API
     */
    @Override
    protected MapsList doInBackground(String... addresses) {
        try {
            //Format the address by replacing spaces with +'s for the API call URL
            String address = addresses[0];
            String addressFormatted = address.replace(' ', '+');
            String urlString = GoogleAPIConstants.GEOCODING_BASE_URL + addressFormatted +
                    "&key=" + GoogleAPIConstants.Maps_API_KEY;

            //Make the network request and serialize the resulting JSON into a MapsList object
            URL url = new URL(urlString);
            InputStream inStream = url.openStream();
            InputStreamReader reader = new InputStreamReader(inStream, Charset.forName("UTF-8"));
            JsonReader jsonReader = new JsonReader(reader);
            Gson gson = new Gson();
            MapsList mapsList = gson.fromJson(jsonReader, MapsList.class);
            return mapsList;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException i) {
            i.printStackTrace();
            return null;
        }
    }

    /**
     * Uses the coordinates retrieved by the Google Maps API and the other associated event details
     * passed into this Async Task to add the new event to the database.
     *
     * @param mapsList A MapsList object which contains the serialized response from the Google
     *                 Maps API.
     */
    @Override
    protected void onPostExecute(MapsList mapsList) {
        boolean correctResult = (mapsList != null) && (mapsList.getStatus().equals(CORRECT_STATUS));
        if (correctResult) {
            MapsResultsComponents[] results = mapsList.getResults();

            //Add the properly formatted address from the Google Maps API to the event's details
            String formattedAddress = results[0].getFormattedAddress();
            eventDetails.put(Event.ADDRESS_KEY, formattedAddress);

            /**
             * First results component returned by the API is always the most accurate one, so we
             * get the location from that component always.
             */
            MapsLocation location = results[0].getGeometry().getLocation();

            //Adds the latitude to the event's details
            String latitutde = String.valueOf(location.getLatitude());
            eventDetails.put(Event.LATITUDE_KEY, latitutde);

            //Adds the longitude to the event's details
            String longitude = String.valueOf(location.getLongitude());
            eventDetails.put(Event.LONGITUDE_KEY, longitude);

            //Add the new event with all of the associated details to the database
            try {
                DatabaseOperations.addEvent(eventTitle, eventDetails, context);
            } catch (InterruptedException i) {
                Log.d(TAG, context.getString(R.string.database_operation_interrupted));
            }

        } else {
            //Only occurs if connection failed or if entered address was not found by Google Maps API
            String errorMessage = context.getString(R.string.add_event_async_task_error_message);
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show();
        }
    }
}
