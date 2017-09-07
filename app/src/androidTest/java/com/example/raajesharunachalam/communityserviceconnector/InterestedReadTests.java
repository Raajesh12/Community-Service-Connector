package com.example.raajesharunachalam.communityserviceconnector;

import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for reading from the Database about what users are interested in a given event and what
 * events given user is interested in.
 * Created by raajesharunachalam on 4/18/17.
 *
 * @author arnchlm2
 */
@RunWith(AndroidJUnit4.class)
public class InterestedReadTests {
    FirebaseDatabase database;

    @Before
    public void setUp() throws Exception {
        database = FirebaseDatabase.getInstance();
    }

    /**
     * Test that if an event has users interested in it, the list of people interested that is returned
     * has the right size and values.
     *
     * @throws Exception An Exception from an Assertion or from an InterruptedException caused by
     *                   the CountDownLatch.
     */
    @Test
    public void readUsersInterestedInEventTest() throws Exception {
        DatabaseReference engineeringEvent = database.getReference("Events/Mahomet Public Library Volunteering");
        DatabaseReference peopleInterestedReference = engineeringEvent.child("People Interested");
        HashMap<String, String> peopleInterestedEvent = new HashMap<>();
        DatabaseOperations.readUsersInterestedInEvent(peopleInterestedReference, peopleInterestedEvent);

        int expectedNumberInterested = 1;
        assertEquals(expectedNumberInterested, peopleInterestedEvent.size());

        String expectedPersonInterested = "Raajesh Arunachalam";
        assertEquals(expectedPersonInterested, peopleInterestedEvent.values().iterator().next());
    }

    /**
     * Tests that if an event has no users are interested in an event, the list of people interested
     * in that event that is returned has length zero.
     *
     * @throws Exception An Exception from an Assertion or from an InterruptedException caused by
     *                   the CountDownLatch.
     */
    @Test
    public void readNoOneInterestedInEventTest() throws Exception {
        DatabaseReference peopleInterestedReference = database.getReference("Events/Walk For Chicago/"
                + "People Interested");
        HashMap<String, String> peopleInterestedEvent = new HashMap<>();
        DatabaseOperations.readUsersInterestedInEvent(peopleInterestedReference, peopleInterestedEvent);

        int expectedNumberInterested = 0;
        assertEquals(expectedNumberInterested, peopleInterestedEvent.size());
    }

    /**
     * Tests that if a user is interested in any events, the list of events that they are interested
     * in that is returned has the right size and values
     *
     * @throws Exception An Exception from an Assertion or from an InterruptedException caused by
     *                   the CountDownLatch.
     */
    @Test
    public void readEventsUserInterestedTest() throws Exception {
        String formattedEmail = "matt\\jones3@gmail\\com";
        HashMap<String, String> eventsUserInterested = new HashMap<>();
        DatabaseOperations.readEventsUserInterested(formattedEmail, eventsUserInterested);

        int expectedNumberInterested = 1;
        assertEquals(expectedNumberInterested, eventsUserInterested.size());

        String expectedPersonInterested = "Champaign Public School Outreach";
        assertEquals(expectedPersonInterested, eventsUserInterested.keySet().iterator().next());
    }

    /**
     * Tests that if the user is not interested in any events, the list of events they are
     * interested in that is returned has length zero.
     *
     * @throws Exception An Exception from an Assertion or from an InterruptedException caused by
     *                   the CountDownLatch.
     */
    @Test
    public void readNoEventsUserInterestedInTest() throws Exception {
        String formattedEmail = "howard\\wolowitz@yahoo\\com";
        HashMap<String, String> eventsUserInterestedIn = new HashMap<>();
        DatabaseOperations.readEventsUserInterested(formattedEmail, eventsUserInterestedIn);

        int expectedNumberInterested = 0;
        assertEquals(expectedNumberInterested, eventsUserInterestedIn.size());
    }
}
