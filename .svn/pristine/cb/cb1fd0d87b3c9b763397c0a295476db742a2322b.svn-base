package com.example.raajesharunachalam.communityserviceconnector;

import org.junit.Test;

import java.util.HashMap;

/**
 * Tests for updating the Database schema when a user adds interest for an event or removes interest
 * for an event
 * Created by raajesharunachalam on 4/18/17.
 *
 * @author arnchlm2
 */

public class InterestedWriteTests {

    /**
     * Tests a user that is trying to be interested in an event that they were not previously
     * interested in.
     *
     * @throws Exception An Exception from an Assertion or from an InterruptedException caused by
     *                   the CountDownLatch.
     */
    @Test
    public void addNewInterestTest() throws Exception {
        String eventName = "Green The 2017 Puerto Rican Day Parade!";
        String email = "prashsub@yahoo\\com";
        String displayName = "Matt Jones";
        DatabaseOperations.addInterest(eventName, email, displayName);
    }

    /**
     * Tests that the same user cannot be interested in the an event that they are already
     * interested in.
     *
     * @throws Exception An Exception from an Assertion or from an InterruptedException caused by
     *                   the CountDownLatch.
     */
    @Test
    public void addSameInterestTest() throws Exception {
        String eventName = "Green The 2017 Puerto Rican Day Parade!";
        String email = "prashsub@yahoo\\com";
        String displayName = "Matt Jones";
        DatabaseOperations.addInterest(eventName, email, displayName);
    }

    /**
     * Tests a unique user different from the above 2 tests that is trying to be interested in an
     * event that they were not previously interested in.
     *
     * @throws Exception An Exception from an Assertion or from an InterruptedException caused by
     *                   the CountDownLatch.
     */
    @Test
    public void addDifferentInterestTest() throws Exception {
        String eventName = "Green The 2017 Puerto Rican Day Parade!";
        String email = "john\\smith@gmail\\com";
        String displayName = "John Smith";
        DatabaseOperations.addInterest(eventName, email, displayName);
    }

    /**
     * Tests that a given user is removed from people interested of a given event and the event is
     * removed from the events that the given user is interested in.
     *
     * @throws Exception An Exception from an Assertion or from an InterruptedException caused by
     *                   the CountDownLatch.
     */
    @Test
    public void removeInterestTest() throws Exception {
        String formattedEmail = "pranav24srinivas@gmail\\com";
        String eventName = "Urbana Food Bank Volunteering";

        HashMap<String, String> eventsUserInterested = new HashMap<>();
        eventsUserInterested.put("Champaign Public School Outreach", "");
        eventsUserInterested.put("Engineering Open House", "");
        eventsUserInterested.put("Urbana Food Bank Volunteering", "");

        HashMap<String, String> peopleInterestedEvent = new HashMap<>();
        peopleInterestedEvent.put("pranav24srinivas@gmail\\com", "Pranav Srinivas");
        peopleInterestedEvent.put("sheldon\\cooper@yahoo\\com", "Sheldon Cooper");

        DatabaseOperations.removeInterest(formattedEmail, eventName, eventsUserInterested, peopleInterestedEvent);
    }
}
