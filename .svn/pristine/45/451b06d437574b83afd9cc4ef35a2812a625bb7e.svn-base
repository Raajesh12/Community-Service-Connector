package com.example.raajesharunachalam.communityserviceconnector;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import android.util.Log;

/**
 * The EventListActivity which displays a list of Event objects and their associated details.
 *
 * @author arnchlm2
 */
public class EventListActivity extends AppCompatActivity {

    private static final String TAG = "EventListActivity";

    private static final int CALENDAR_ID = 1;
    private static final int USER_PHOTO_DIMENSION = 40;
    private static final double METERS_TO_MILES = 0.000621371;

    EventAdapter adapter;
    ImageView userPhoto;
    TextView username;
    RecyclerView eventsRecyclerView;
    ProgressBar progressBar;

    /**
     * Sets up the RecyclerView and its associated adapter by loading the proper events for the
     * RecyclerView to display in this activity
     *
     * @param savedInstanceState A previous instance of this Activity and associated data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        userPhoto = (ImageView) findViewById(R.id.event_list_user_photo);
        username = (TextView) findViewById(R.id.event_list_username);

        //Sets the username and photo of the user that is currently logged in
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        username.setText(getString(R.string.username_label) + currentUser.getDisplayName());
        Picasso.with(this).load(currentUser.getPhotoUrl()).resize(USER_PHOTO_DIMENSION, USER_PHOTO_DIMENSION).into(userPhoto);

        //Makes the progress bar visible while the events load
        progressBar = (ProgressBar) findViewById(R.id.progress_bar_events);
        progressBar.setVisibility(View.VISIBLE);

        //Makes the RecyclerView invisible while the events load
        eventsRecyclerView = (RecyclerView) findViewById(R.id.events_recycler_view);
        eventsRecyclerView.setVisibility(View.INVISIBLE);

        //Sets the appropriate LinearLayout style for the RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        eventsRecyclerView.setLayoutManager(layoutManager);

        ArrayList<Event> emptyEventList = new ArrayList<>();
        adapter = new EventAdapter(this, emptyEventList, CALENDAR_ID);
        eventsRecyclerView.setAdapter(adapter);

        Intent intent = getIntent();

        //Begin loading the events with the arguments from the intent that started this activity
        new LoadEventsAsyncTask().execute(intent);
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
            Intent intent = new Intent(EventListActivity.this, AuthActivity.class);
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
     * Runs on a background thread and goes through all the events in the database, determining which
     * events fit the search criteria the user has specified (maximum distance to search within).
     */
    public class LoadEventsAsyncTask extends AsyncTask<Intent, Void, ArrayList<Event>> {

        /**
         * Loops through the events and adds them to a list of events if they satisfy the
         * search criteria specified by the user (all events or events within a certain distance)
         *
         * @param intents The intent that begin the activity which says what parameters to display
         * @return A List of Events that are within the distance specified by the user
         */
        @Override
        protected ArrayList<Event> doInBackground(Intent... intents) {

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference events = database.getReference("Events");

            ArrayList<Event> displayEvents = new ArrayList<>();

            Intent intent = intents[0];

            if (intent.hasExtra(getString(R.string.view_all_events_message_key))) {
                displayEvents = readAllEvents(events);
            } else if (intent.hasExtra(getString(R.string.view_nearby_events_message_key))) {
                displayEvents = readNearbyEvents(events, intent);
            }

            return displayEvents;
        }

        /**
         * Sets up the adapter with the new list of events and resets the UI so that the progressBar
         * is invisible while the RecyclerView is visible.
         *
         * @param events
         */
        @Override
        protected void onPostExecute(ArrayList<Event> events) {
            adapter.setEvents(events);
            adapter.notifyDataSetChanged();

            //Hides the progress bar and shows the RecyclerView with the loaded events
            progressBar.setVisibility(View.INVISIBLE);
            eventsRecyclerView.setVisibility(View.VISIBLE);
        }

        /**
         * Reads all events from the database and adds all of them to a list of events.
         *
         * @param events DatabaseReference to the event objects in the Firebase Database.
         * @return A List of all the events stored in the database
         */
        private ArrayList<Event> readAllEvents(DatabaseReference events) {
            final CountDownLatch readEvents = new CountDownLatch(1);

            final ArrayList<Event> displayEvents = new ArrayList<>();

            events.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                            Event event = DatabaseOperations.readEvent(eventSnapshot);
                            displayEvents.add(event);
                        }
                    } catch (InterruptedException i) {
                        Log.d(TAG, getString(R.string.database_operation_interrupted));
                    }
                    readEvents.countDown();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            try {
                readEvents.await();
            } catch (InterruptedException i) {
                Log.d(TAG, getString(R.string.database_operation_interrupted));
            }

            return displayEvents;
        }

        private ArrayList<Event> readNearbyEvents(DatabaseReference events, Intent intent) {
            final CountDownLatch readEvents = new CountDownLatch(1);

            //Gets the arguments from the intent
            final double currentLatitude = intent.getDoubleExtra(getString(R.string.latitude_key), 0.0);
            final double currentLongitude = intent.getDoubleExtra(getString(R.string.longitude_key), 0.0);
            final double distanceSearchWithin = intent.getDoubleExtra(getString(R.string.distance_search_key), 0.0);

            //Creates a location object with the user's current latitude and longitude
            final Location currentLocation = new Location("");
            currentLocation.setLatitude(currentLatitude);
            currentLocation.setLongitude(currentLongitude);

            final ArrayList<Event> displayEvents = new ArrayList<>();

            events.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {

                            double distance = calculateDistance(currentLocation, eventSnapshot);

                            //If the event is within the proper distance, add it to the RecyclerView
                            if (distance < distanceSearchWithin) {
                                //Serialize the event if it is within the user's desired distance
                                Event event = DatabaseOperations.readEvent(eventSnapshot);
                                displayEvents.add(event);
                            }
                        }

                        //Read Events is complete after the loop is done
                        readEvents.countDown();
                    } catch (InterruptedException i) {
                        Log.d(TAG, getString(R.string.database_read_interrupted));
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            try {
                readEvents.await();
            } catch (InterruptedException i) {
                Log.d(TAG, getString(R.string.database_read_interrupted));
            }

            return displayEvents;
        }

        /**
         * Uses a DataSnapshot of another event to get its location coordinates and calculate the
         * distance between the event's location and the given current location using a standard
         * formula for calculating distance between latitude/longitude coordinates.
         *
         * @param currentLocation Represents the user's current location (latitude/longitude)
         * @param eventSnapshot   A Datasnapshot of an event object
         * @return A number that represents the distance between the event's location and the current
         * location in miles
         */
        public double calculateDistance(Location currentLocation, DataSnapshot eventSnapshot) {

            Location eventLocation = new Location("");

            //Gets the latitude of the event that we are checking the distance to
            String latitudeString = (String) eventSnapshot.child("Latitude").getValue();
            double latitude = Double.parseDouble(latitudeString);
            eventLocation.setLatitude(latitude);

            //Gets the longitude of the event that we are checking the distance to
            String longitudeString = (String) eventSnapshot.child("Longitude").getValue();
            double longitude = Double.parseDouble(longitudeString);
            eventLocation.setLongitude(longitude);

            //Calculates the distance between the current location and the event
            float distanceMeters = currentLocation.distanceTo(eventLocation);

            //We get distance in meters but we want to convert to miles
            double distanceMiles = distanceMeters * METERS_TO_MILES;

            return distanceMiles;
        }
    }
}
