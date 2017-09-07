package com.example.raajesharunachalam.communityserviceconnector;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Tests for the Firebase Database functionality in AuthActivity which is to be able to add new
 * users to the database.
 * <p>
 * Created by raajesharunachalam on 4/18/17.
 *
 * @author arnchlm2
 */
@RunWith(AndroidJUnit4.class)
public class UsernameTest {
    final static String DEFAULT_EMAIL = "mike.smith@gmail.com";

    /**
     * Tests the addition of a user's email that to the Database schema
     *
     * @throws Exception An Exception from an Assertion or from an InterruptedException caused by
     *                   the CountDownLatch.
     */
    @Test
    public void addNewUserTest() throws Exception {
        DatabaseOperations.addUser(DEFAULT_EMAIL);
    }

    /**
     * Tests that the same user's email will not be added twice to the Database schema.
     *
     * @throws Exception An Exception from an Assertion or from an InterruptedException caused by
     *                   the CountDownLatch.
     */
    @Test
    public void addSameUserTest() throws Exception {
        DatabaseOperations.addUser(DEFAULT_EMAIL);
    }

    /**
     * Tests that a unique email different from the above two tests will be added to the
     * Database schema properly.
     *
     * @throws Exception An Exception from an Assertion or from an InterruptedException caused by
     *                   the CountDownLatch.
     */
    @Test
    public void addDifferentUserTest() throws Exception {
        String email = "mickey.mouse@disney.com";
        DatabaseOperations.addUser(email);
    }
}
