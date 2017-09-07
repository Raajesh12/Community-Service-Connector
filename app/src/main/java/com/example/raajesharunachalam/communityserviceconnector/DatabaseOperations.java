package com.example.raajesharunachalam.communityserviceconnector;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Class that insert, remove, and read operations to various parts of the Firebase Database schema
 * such as events, people interested (under a given event), and users.
 * <p>
 * Created by raajesharunachalam on 4/18/17.
 *
 * @author arnchlm2
 */

public class DatabaseOperations {

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();

    /**
     * If the given email does not already exist in the list, adds the given email to a list of
     * emails that identify unique users in the Firebase Database.
     *
     * @param email A email that corresponds to a user.
     * @throws InterruptedException Occurs when the thread is interrupted while waiting for an
     *                              operation to finish.
     */
    public static void addUser(final String email) throws InterruptedException {
        final DatabaseReference usersReference = database.getReference("Users");
        final String formattedEmail = email.replace(".", "\\");

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        usersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    if (user.getKey().equals(formattedEmail)) {
                        countDownLatch.countDown();
                        //Exit the method because the user is already in the Database
                        return;
                    }
                }

                //Only executes if the user was not found in the previous users
                String userEventsInitialValue = "Not Interested In Any Events";
                usersReference.child(formattedEmail).setValue(userEventsInitialValue)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                countDownLatch.countDown();
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Waits for asynchronous events to complete before continuing method
        countDownLatch.await(2, TimeUnit.SECONDS);
    }

    /**
     * Adds the user to a list of people interested in the event and adds the event to a list of
     * events the user is interested in.
     *
     * @param eventName      The name of the event the user wants to say they are interested in.
     * @param formattedEmail The email of the user who is interested in the event formatted for the database
     * @param displayName    The name of the user that is displayed.
     * @throws InterruptedException Occurs when the thread is interrupted while waiting for an
     *                              operation to finish.
     */
    public static void addInterest(String eventName, final String formattedEmail, final String displayName)
            throws InterruptedException {

        DatabaseReference eventReference = database.getReference("Events/" + eventName);
        DatabaseReference peopleInterestedReference = eventReference.child("People Interested");

        final CountDownLatch writeSignal = new CountDownLatch(2);

        //Only execute if the user wasn't previously interested in this event
        peopleInterestedReference.child(formattedEmail).setValue(displayName)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        writeSignal.countDown();
                    }
                });

        DatabaseReference userReference = database.getReference("Users/" + formattedEmail);
        userReference.child(eventName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                writeSignal.countDown();
            }
        });
        //Waits for asynchronous events to complete before continuing method
        writeSignal.await(1, TimeUnit.SECONDS);
    }

    /**
     * Removes the user from a list of people interested in the event and removes the event from the
     * list of events the user is interested in.
     *
     * @param formattedEmail        The user's email formatted to replace periods with backslashes
     * @param eventName             The name of the event to remove the user's interest from
     * @param eventsUserInterested  The current events the user is interested in
     * @param peopleInterestedEvent The current people that are interested in the given event
     * @throws InterruptedException
     */
    public static void removeInterest(String formattedEmail, String eventName, HashMap<String, String> eventsUserInterested,
                                      HashMap<String, String> peopleInterestedEvent) throws InterruptedException {

        DatabaseReference peopleReference = database.getReference("Events/" + eventName +
                "/" + Event.PEOPLE_INTERESTED_KEY);
        peopleInterestedEvent.remove(formattedEmail);

        final CountDownLatch writeSignal = new CountDownLatch(2);
        if (peopleInterestedEvent.size() == 0) {
            peopleReference.setValue(Event.NO_PEOPLE_INTERESTED_VALUE).
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            writeSignal.countDown();
                        }
                    });
        } else {
            peopleReference.setValue(peopleInterestedEvent).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    writeSignal.countDown();
                }
            });
        }

        DatabaseReference userReference = database.getReference("Users/" + formattedEmail);
        eventsUserInterested.remove(eventName);
        if (eventsUserInterested.size() == 0) {
            userReference.setValue("Not Interested In Any Events").
                    addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            writeSignal.countDown();
                        }
                    });
        } else {
            userReference.setValue(eventsUserInterested).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    writeSignal.countDown();
                }
            });
        }
        writeSignal.await(2, TimeUnit.SECONDS);
    }

    /**
     * Reads all of the users who are interested in a given Event from the Database and adds them
     * to a HashMap.
     *
     * @param peopleInterestedReference The People Interested subtree of a given event.
     * @param peopleInterestedEvent     A HashMap of the people that are interested in the event
     * @throws InterruptedException Occurs when the thread is interrupted while waiting for an
     *                              operation to finish.
     */
    public static void readUsersInterestedInEvent(DatabaseReference peopleInterestedReference,
                                                  final HashMap<String, String> peopleInterestedEvent)
            throws InterruptedException {

        final CountDownLatch readSignal = new CountDownLatch(1);

        peopleInterestedReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot user : dataSnapshot.getChildren()) {
                    peopleInterestedEvent.put(user.getKey(), user.getValue(String.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Waits for asynchronous events to complete before continuing method
        readSignal.await(2, TimeUnit.SECONDS);
    }

    /**
     * Reads all the events a given user is interested in from the Database into a HashMap.
     *
     * @param formattedEmail       The currently logged in user's email address formatted as it is in the database
     * @param eventsUserInterested A HashMap of the events a given user is interested in
     * @throws InterruptedException Occurs when the thread is interrupted while waiting for an
     *                              operation to finish.
     */
    public static void readEventsUserInterested(String formattedEmail, final HashMap<String, String> eventsUserInterested)
            throws InterruptedException {

        DatabaseReference userReference = database.getReference("Users/" + formattedEmail);

        final CountDownLatch readSignal = new CountDownLatch(1);

        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot event : dataSnapshot.getChildren()) {
                    eventsUserInterested.put(event.getKey(), event.getValue(String.class));
                }
                readSignal.countDown();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //Waits for asynchronous events to complete before continuing method
        readSignal.await(2, TimeUnit.SECONDS);
    }

    /**
     * If an event with the same title does not already exist, adds a new event with the given event
     * details to the Database schema and displays a success message. Otherwise displays a message
     * that the event add procedure failed.
     *
     * @param eventTitle   the name of the event to add to the database.
     * @param eventDetails The associated details of the event with the given title
     * @param context      The Context of the application
     * @throws InterruptedException Occurs when the thread is interrupted while waiting for an
     *                              operation to finish.
     */
    public static void addEvent(final String eventTitle, final Map<String, String> eventDetails, final Context context)
            throws InterruptedException {

        final DatabaseReference eventsReference = database.getReference("Events");
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        eventsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey().equals(eventTitle)) {
                        String eventAlreadyExists = context.getString(R.string.failed_add_event_message);
                        Toast.makeText(context, eventAlreadyExists, Toast.LENGTH_LONG).show();
                        countDownLatch.countDown();

                        //Returns because an event with the same name was already found
                        return;
                    }
                }

                //Only will execute if the event was not found in the existing events
                eventsReference.child(eventTitle).setValue(eventDetails)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                String eventAddSuccessful = context.getString(R.string.success_add_event_message);
                                Toast.makeText(context, eventAddSuccessful, Toast.LENGTH_LONG).show();
                                countDownLatch.countDown();
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Waits for asynchronous events to complete before continuing method
        countDownLatch.await(2, TimeUnit.SECONDS);
    }

    /**
     * Reads a DataSnapshot of a single event and serializes the data into an Event object
     *
     * @param eventSnapshot A Reference to a single event in the database schema
     * @return An Event object with all the data serialized from the event DataSnapshot
     * @throws InterruptedException Occurs when the thread is interrupted while waiting for an
     *                              operation to finish.
     */
    public static Event readEvent(DataSnapshot eventSnapshot) throws InterruptedException {
        final Event event = new Event();
        event.setTitle(eventSnapshot.getKey());

        final HashMap<String, String> eventDetails = new HashMap<String, String>();

        for (DataSnapshot detail : eventSnapshot.getChildren()) {
            if (detail.getKey().equals(Event.PEOPLE_INTERESTED_KEY)) {
                HashMap<String, String> peopleInterestedList = new HashMap<>();

                for (DataSnapshot people : detail.getChildren()) {
                    peopleInterestedList.put(people.getKey(), people.getValue(String.class));
                }
                event.setPeopleInterested(peopleInterestedList);

            } else {
                eventDetails.put(detail.getKey(), detail.getValue(String.class));
            }
        }

        event.setAddress(eventDetails.get(Event.ADDRESS_KEY));
        event.setDescription(eventDetails.get(Event.DESCRIPTION_KEY));
        event.setStartDate(eventDetails.get(Event.START_DATE_KEY));
        event.setStartTime(eventDetails.get(Event.START_TIME_KEY));
        event.setEndDate(eventDetails.get(Event.END_DATE_KEY));
        event.setEndTime(eventDetails.get(Event.END_TIME_KEY));
        event.setLatitude(Double.parseDouble(eventDetails.get(Event.LATITUDE_KEY)));
        event.setLongitude(Double.parseDouble(eventDetails.get(Event.LONGITUDE_KEY)));
        event.setEventLink(eventDetails.get(Event.EVENT_LINK));
        event.setImageURL(eventDetails.get(Event.IMAGE_URL_KEY));
        return event;
    }
}
