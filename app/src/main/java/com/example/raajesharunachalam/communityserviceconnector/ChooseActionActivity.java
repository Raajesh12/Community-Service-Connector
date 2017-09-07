package com.example.raajesharunachalam.communityserviceconnector;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Activity that demonstrates the ability to retrieve the current location in GPS coordinates of the
 * user and to calculate distances using the Google Maps Location Services API. Code for getting
 * location is modeled on tutorial from: https://developer.android.com/training/location/index.html
 *
 * @author arnchlm2
 */
public class ChooseActionActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int LOCATION_REQUEST_SPEED = 2000;
    private static final int FASTEST_LOCATION_REQUEST_SPEED = 2000;
    private static final int USER_PHOTO_DIMENSION = 40;
    private static final int WAIT_FOR_LOCATION_TIME = 4;
    private static final int MILLISECONDS_TO_SECONDS = 1000;
    private static final String TAG = "ChooseActionActivity";

    TextView username;
    ImageView userPhoto;
    Button addEvent;
    Button viewAllEvents;
    Button viewNearbyEvents;

    GoogleApiClient mGoogleApiClient;
    private static long timeStartedLocationUpdates = 0;
    private static long lastUpdatedTime = 0;
    Location recentLocation;

    /**
     * Sets up the User Profile and the OnClickListeners for the three buttons that allow the user
     * to take different actions in the app.
     *
     * @param savedInstanceState A previous version of this activity with associated saved data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_action);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference("Events");

        username = (TextView) findViewById(R.id.activity_location_username);
        userPhoto = (ImageView) findViewById(R.id.activity_location_user_photo);
        addEvent = (Button) findViewById(R.id.add_event_button);
        viewAllEvents = (Button) findViewById(R.id.view_all_events_button);
        viewNearbyEvents = (Button) findViewById(R.id.view_nearby_events_button);

        //Sets the user's name and picture based on their login account
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        username.setText(getString(R.string.username_label) + currentUser.getDisplayName());
        Picasso.with(this).load(currentUser.getPhotoUrl()).resize(USER_PHOTO_DIMENSION, USER_PHOTO_DIMENSION).into(userPhoto);

        //Sets up the Google API Client for connecting to the GoogleMaps Location API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Allows the user to move to a form where they can add a new event with associated details
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseActionActivity.this, CreateEventActivity.class);
                startActivity(intent);
            }
        });

        //Allows the user to see all the events in the database in another Activity
        viewAllEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseActionActivity.this, EventListActivity.class);
                intent.putExtra(getString(R.string.view_all_events_message_key), "View All Events");
                intent.putExtra(getString(R.string.latitude_key), recentLocation.getLatitude());
                intent.putExtra(getString(R.string.longitude_key), recentLocation.getLongitude());
                startActivity(intent);
            }
        });


        viewNearbyEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createEnterDistanceDialog();
            }
        });
    }

    /**
     * Creates a Dialog for the user to enter a maximum distance to search within. Once the user
     * enters a valid distance into the Dialog, starts the next activity and passes the user's
     * location to that activity
     */
    private void createEnterDistanceDialog() {
        //An AlertDialog for the user to enter a distance to search within
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ChooseActionActivity.this);
        dialogBuilder.setTitle(getString(R.string.view_nearby_events_dialog_title));
        dialogBuilder.setMessage(getString(R.string.view_nearby_events_dialog_message));

        //The EditText the user can type into which allows the input of decimal numbers
        final EditText distanceEntered = new EditText(ChooseActionActivity.this);
        distanceEntered.setHint(getString(R.string.view_nearby_events_dialog_hint));
        distanceEntered.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        distanceEntered.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        dialogBuilder.setView(distanceEntered);

        //Takes the user's input distance and begins a new activity to display the right events
        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                double distanceToSearch = Double.parseDouble(distanceEntered.getText().toString());
                dialog.cancel();
                passLocationToNextActivity(distanceToSearch);
            }
        });

        //Removes the dialog from the screen if the user clicks on the Cancel button
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialogBuilder.show();
    }

    /**
     * Checks if the user's current location has been detected. If not, pauses to make sure the
     * location is detected. Then passes the location coordinates to the next activity and begins it.
     *
     * @param distance The distance the user has entered to search for events within.
     */
    private void passLocationToNextActivity(double distance) {
        /**
         * If a new location has not been detected since after 4000 milliseconds since location
         * updates have started being listened for, then wait 4 seconds for new location to be detected.
         */
        boolean newLocationNotDetected = lastUpdatedTime <= (timeStartedLocationUpdates +
                (WAIT_FOR_LOCATION_TIME * MILLISECONDS_TO_SECONDS));
        if (newLocationNotDetected) {
            CountDownLatch waitForLocation = new CountDownLatch(1);
            //Pauses for 4 seconds to make sure the user's location has been detected
            try {
                waitForLocation.await(WAIT_FOR_LOCATION_TIME, TimeUnit.SECONDS);
            } catch (InterruptedException i) {
                Log.d(TAG, getString(R.string.database_operation_interrupted));
            }
        }

        Intent intent = new Intent(ChooseActionActivity.this, EventListActivity.class);
        intent.putExtra(getString(R.string.view_nearby_events_message_key), "View Nearby Events");
        intent.putExtra(getString(R.string.distance_search_key), distance);
        intent.putExtra(getString(R.string.latitude_key), recentLocation.getLatitude());
        intent.putExtra(getString(R.string.longitude_key), recentLocation.getLongitude());
        startActivity(intent);
    }

    /**
     * Begins connecting to the Google Play Service Location API.
     */
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    /**
     * Stops connecting to the Google Play Service Location API.
     */
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Sets up the logic for retrieving the location from the Wi-fi/GPS signals from the system
     * and what intervals to use
     *
     * @param bundle Bundle of data provided to clients by Google Play services.
     *               May be null if no content is provided by the service.
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        //Gets the previous location, if there is one, before the new location updates begin
        try {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (lastLocation != null) {
                recentLocation = lastLocation;
            }
        } catch (SecurityException s) {
            s.printStackTrace();
        }

        //Begins the new location updates
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_REQUEST_SPEED);
        mLocationRequest.setFastestInterval(FASTEST_LOCATION_REQUEST_SPEED);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        try {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);
            //Set the current time that the location updates have begun
            timeStartedLocationUpdates = System.currentTimeMillis();
        } catch (SecurityException s) {
            s.printStackTrace();
            String enableGPSPermission = getString(R.string.enable_GPS_permission_message);
            Toast.makeText(this, enableGPSPermission, Toast.LENGTH_LONG);
        }


    }

    /**
     * Handles the case of when the connection to the Google Play Services Location API is suspended.
     *
     * @param i An integer that represents the reason for the disconnection
     */
    @Override
    public void onConnectionSuspended(int i) {
        String connectionSuspended = getString(R.string.connection_suspended_message);
        Toast.makeText(this, connectionSuspended, Toast.LENGTH_SHORT).show();
    }

    /**
     * Handles the case of when the connection to the Google Play Services Location API fails.
     *
     * @param connectionResult A connectionResult that descries the error of why the connection
     *                         failed.
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        String connectionFailed = getString(R.string.connection_failed_message);
        Toast.makeText(this, connectionFailed, Toast.LENGTH_SHORT).show();
    }

    /**
     * Callback method that updates a Location variable whenever a new location has been detected.
     *
     * @param location The most recently received Location by the Wi-Fi/GPS
     */
    @Override
    public void onLocationChanged(Location location) {
        recentLocation = location;
        lastUpdatedTime = System.currentTimeMillis();
    }

    /**
     * Displays the sign out button in the top right of the mneu
     *
     * @param menu The menu of the current Activity
     * @return A boolean signifying if the creation of the menu was successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.constant_menu, menu);
        return true;
    }

    /**
     * Makes the sign out button cause the user to sign out from Firebase when clicked
     *
     * @param item The specific item that was clicked
     * @return A boolean signifying if the handling of the click on the menu item was successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out_button) {
            AuthUI.getInstance().signOut(this);
            Intent intent = new Intent(ChooseActionActivity.this, AuthActivity.class);
            intent.putExtra(getString(R.string.sign_out_key), "Signed Out");
            startActivity(intent);

            //Deletes this current activity so it cannot be reaccessed after the user has signed out
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * If the user wants to go back to the home screen, they are forced to sign out since the home
     * screen's only purpose is to allow the user to sign in.
     */
    @Override
    public void onBackPressed() {
        AuthUI.getInstance().signOut(this);
        super.onBackPressed();

        //Deletes this current activity so it cannot be reaccessed after the user has signed out
        finish();
    }
}
