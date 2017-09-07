package com.example.raajesharunachalam.communityserviceconnector;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Tests the reading of existing Events from the Database and the writing of new events to the
 * Database
 * <p>
 * Created by raajesharunachalam on 4/18/17.
 *
 * @author arnchlm2
 */
@RunWith(AndroidJUnit4.class)
public class EventsTest {
    Event event;
    FirebaseDatabase database;

    @Before
    public void setUp() throws Exception {
        database = FirebaseDatabase.getInstance();
    }

    /**
     * Test the reading of an Event and all its associated details from the Database schema.
     *
     * @throws Exception An Exception from an Assertion or from an InterruptedException caused by
     *                   the CountDownLatch.
     */
    @Test
    public void readEventData() throws Exception {
        DatabaseReference engineeringEvent = database.getReference("Events/Engineering Open House");

        final CountDownLatch readEvent = new CountDownLatch(1);
        engineeringEvent.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    event = DatabaseOperations.readEvent(dataSnapshot);
                    String expectedTitle = "Engineering Open House";
                    Log.d("EventsTest", String.valueOf(event == null));
                    assertEquals(expectedTitle, event.getTitle());

                    String expectedAddress = "1401 W Green St, Urbana, IL 61801";
                    assertEquals(expectedAddress, event.getAddress());

                    String expectedDescription = "Showcase of engineering research to young, aspiring STEM students. " +
                            "Many research groups and active RSOs in STEM based majors will have interactive " +
                            "presentations and exhibits for their research.";
                    assertEquals(expectedDescription, event.getDescription());

                    String expectedEndDate = "4/23/2017";
                    assertEquals(expectedEndDate, event.getEndDate());

                    String expectedEndTime = "4:00 PM";
                    assertEquals(expectedEndTime, event.getEndTime());

                    int expectedNumberPeopleInterested = 5;
                    assertEquals(expectedNumberPeopleInterested, event.getPeopleInterested().size());

                    String expectedStartDate = "4/21/2017";
                    assertEquals(expectedStartDate, event.getStartDate());

                    String expectedStartTime = "12:00 PM";
                    assertEquals(expectedStartTime, event.getStartTime());

                    String expectedImageURL = "http://uihistories.library.illinois.edu/photoarchive/wallpapers/" +
                            "uiuc/full/50235.jpg";
                    assertEquals(expectedImageURL, event.getImageURL());

                    String expectedEventLink = "http://eoh.ec.illinois.edu";
                    assertEquals(expectedEventLink, event.getEventLink());
                } catch (InterruptedException i){
                    Log.d("EventsTest", "Read Interrupted");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        readEvent.await(2, TimeUnit.SECONDS);
    }

    /**
     * Tests the creation of a new Event and its associated details in the Database.
     *
     * @throws Exception An Exception from an Assertion or from an InterruptedException caused by
     *                   the CountDownLatch.
     */
    @Test
    public void addNewEvent() throws Exception {

        String eventTitle = "Champaign Public School Outreach";

        Map<String, String> eventDetails = new HashMap<>();

        String address = "306 W Green St, Champaign, IL 61820";
        eventDetails.put(Event.ADDRESS_KEY, address);

        String description = "Introduce elementary/middle school students to Computer Science";
        eventDetails.put(Event.DESCRIPTION_KEY, description);

        String imageURL = "https://edison.champaignschools.org/sites/edison_d7/files/" +
                "glazed-assets/edison_0.jpg?fid=31";
        eventDetails.put(Event.IMAGE_URL_KEY, imageURL);

        String startDate = "4/18/2017";
        eventDetails.put(Event.START_DATE_KEY, startDate);

        String startTime = "11:00 AM";
        eventDetails.put(Event.START_TIME_KEY, startTime);

        String endDate = "4/18/2017";
        eventDetails.put(Event.END_DATE_KEY, endDate);

        String endTime = "3:00 PM";
        eventDetails.put(Event.END_TIME_KEY, endTime);

        eventDetails.put(Event.PEOPLE_INTERESTED_KEY, Event.NO_PEOPLE_INTERESTED_VALUE);

        DatabaseOperations.addEvent(eventTitle, eventDetails, InstrumentationRegistry.getTargetContext());
    }

    /**
     * Tests that an event with the same title will not be added to the Database schema.
     *
     * @throws Exception An Exception from an Assertion or from an InterruptedException caused by
     *                   the CountDownLatch.
     */
    @Test
    public void addSameEvent() throws Exception {
        String eventTitle = "Playing It Safe 2017";

        Map<String, String> eventDetails = new HashMap<>();

        String address = "306 W Green St, Champaign, IL 61820";
        eventDetails.put(Event.ADDRESS_KEY, address);

        String description = "Introduce elementary/middle school students to Computer Science";
        eventDetails.put(Event.DESCRIPTION_KEY, description);

        String imageURL = "https://edison.champaignschools.org/sites/edison_d7/files/" +
                "glazed-assets/edison_0.jpg?fid=31";
        eventDetails.put(Event.IMAGE_URL_KEY, imageURL);

        String startDate = "4/18/2017";
        eventDetails.put(Event.START_DATE_KEY, startDate);

        String startTime = "11:00 AM";
        eventDetails.put(Event.START_TIME_KEY, startTime);

        String endDate = "4/18/2017";
        eventDetails.put(Event.END_DATE_KEY, endDate);

        String endTime = "3:00 PM";
        eventDetails.put(Event.END_TIME_KEY, endTime);

        eventDetails.put(Event.PEOPLE_INTERESTED_KEY, Event.NO_PEOPLE_INTERESTED_VALUE);

        DatabaseOperations.addEvent(eventTitle, eventDetails, InstrumentationRegistry.getTargetContext());
    }
}
