package com.example.raajesharunachalam.communityserviceconnector;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity for the user to create a new event to add to the app with the proper details and input
 * boxes to do so.
 *
 * @author arnchlm2
 */
public class CreateEventActivity extends AppCompatActivity {

    private static final int USER_PHOTO_DIMENSION = 40;

    TextView userName;
    ImageView userIcon;
    double latitude;
    double longitude;
    EditText title;
    EditText description;
    EditText address;
    DatePicker startDate;
    TimePicker startTime;
    DatePicker endDate;
    TimePicker endTime;
    EditText eventLink;
    EditText imageUrl;
    Button submitButton;

    /**
     * Initializes the layout and input fields for the user to enter data into as well as the
     * onClickListener for when they have completed the form and would like to submit it
     *
     * @param savedInstanceState A previous version of this activity with associated saved data.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        userName = (TextView) findViewById(R.id.create_event_user_name);
        userIcon = (ImageView) findViewById(R.id.create_event_user_photo);

        //Sets up the user name and display photo at the top right of the activity
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        userName.setText(getString(R.string.username_label) + currentUser.getDisplayName());
        Picasso.with(this).load(currentUser.getPhotoUrl()).resize(USER_PHOTO_DIMENSION, USER_PHOTO_DIMENSION)
                .into(userIcon);

        //Initializes all the user input fields such as EditTexts, DatePickers, and TimePickers
        title = (EditText) findViewById(R.id.create_event_title_actual);
        description = (EditText) findViewById(R.id.create_event_description_actual);
        address = (EditText) findViewById(R.id.create_event_address_actual);
        startDate = (DatePicker) findViewById(R.id.create_event_start_date_actual);
        startTime = (TimePicker) findViewById(R.id.create_event_start_time_actual);
        endDate = (DatePicker) findViewById(R.id.create_event_end_date_actual);
        endTime = (TimePicker) findViewById(R.id.create_event_end_time_actual);
        eventLink = (EditText) findViewById(R.id.create_event_link_actual);
        imageUrl = (EditText) findViewById(R.id.create_event_image_actual);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference eventsReference = database.getReference("events");

        submitButton = (Button) findViewById(R.id.create_event_submit_button);

        //Grabs the data the user has entered and begins the process of pushing it to the database
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String titleInput = title.getText().toString();

                String descriptionInput = description.getText().toString();

                String addressInput = address.getText().toString();

                String startDateInput = (startDate.getMonth() + 1) + "/" + startDate.getDayOfMonth() + "/"
                        + startDate.getYear();

                //Uses the appropriate methods for getting the hour/minute from TimePicker based on the SDK
                String startTimeInput = Format.formatTime(startTime);

                String endDateInput = (endDate.getMonth() + 1) + "/" + endDate.getDayOfMonth() + "/"
                        + endDate.getYear();

                //Uses the appropriate methods for getting the hour/minute from TimePicker based on the SDK
                String endTimeInput = Format.formatTime(endTime);

                /**
                 * Checks if any of the above required fields was blank. If so, stops the add event
                 * procedure and displays a Toast to the user
                 */
                if (titleInput.isEmpty() || descriptionInput.isEmpty() || addressInput.isEmpty() ||
                        startDateInput.isEmpty() || startTimeInput.isEmpty() || endDateInput.isEmpty()
                        || endTimeInput.isEmpty()) {
                    String message = "You Cannot Leave Any Of The Starred Fields Blank!";
                    Toast.makeText(CreateEventActivity.this, message, Toast.LENGTH_LONG).show();
                    return;
                }

                //Event Link is optional, so if nothing is entered a default of "None" is used
                String link = eventLink.getText().toString();
                if (link.isEmpty()) {
                    link = Event.EVENT_LINK_DEFAULT;
                }

                //Image URL is optional, so if nothing is entered a default image URL is used
                String imageUrlInput = imageUrl.getText().toString();
                if (imageUrlInput.isEmpty()) {
                    imageUrlInput = Event.IMAGE_URL_DEFAULT_LINK;
                }

                //Adds all the above details for the event to a HashMap
                Map<String, String> eventDetails = new HashMap();
                eventDetails.put(Event.DESCRIPTION_KEY, descriptionInput);
                eventDetails.put(Event.START_DATE_KEY, startDateInput);
                eventDetails.put(Event.START_TIME_KEY, startTimeInput);
                eventDetails.put(Event.END_DATE_KEY, endDateInput);
                eventDetails.put(Event.END_TIME_KEY, endTimeInput);
                eventDetails.put(Event.EVENT_LINK, link);
                eventDetails.put(Event.IMAGE_URL_KEY, imageUrlInput);
                eventDetails.put(Event.PEOPLE_INTERESTED_KEY, Event.NO_PEOPLE_INTERESTED_VALUE);

                /**
                 * Calls an AsyncTask that will take the event details found so far and find the
                 * formal address of the address the user has entered as well as the latitude/longitude
                 * of the location and then push it to the database.
                 */
                AddEventAsyncTask finishAddEvent = new AddEventAsyncTask(CreateEventActivity.this,
                        titleInput, eventDetails);
                finishAddEvent.execute(addressInput);

                //Finished adding new event, so go back to the ChooseActionActivity
                Intent intent = new Intent(CreateEventActivity.this, ChooseActionActivity.class);
                startActivity(intent);
            }
        });
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
     * Makes the sign out button cause the user to sign out from Firebase when clicked. Then takes
     * the user back to the home page sign in screen.
     *
     * @param item The specific item that was clicked
     * @return A boolean signifying if the handling of the click on the menu item was successful
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_sign_out_button) {
            AuthUI.getInstance().signOut(this);
            Intent intent = new Intent(CreateEventActivity.this, AuthActivity.class);
            intent.putExtra(getString(R.string.sign_out_key), "Signed Out");
            startActivity(intent);

            //Deletes this current activity so it cannot be reaccessed after the user has signed out
            finish();

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
}
