package com.example.raajesharunachalam.communityserviceconnector;

import android.location.Location;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

/**
 * Tests a method from Calculations.java which calculates the time in milliseconds since the epoch
 * of a specific date
 * <p>
 * Created by raajesharunachalam on 5/2/17.
 *
 * @author arnchlm2
 */
public class CalculationsTest {

    /**
     * Checks that the time in milliseconds since the epoch for a date close to the current date
     * returned by Calculations.calculate TimeInMillis() is accurate by comparing to a standard
     * online calculator.
     *
     * @throws Exception An Exception caused by an error with the Asserts in the test.
     */
    @Test
    public void calculateTimeInMillisCloseDate() throws Exception {
        String date = "5/08/2017";
        String time = "9:30 PM";

        /**
         * Expected value calculated from epochconverter.com which returns time in milliseconds
         * since the epoch of a specific date.
         */
        long expectedTimeInMillis = 1494297000000L;

        long actualTimeInMillis = Calculations.calculateTimeInMillis(date, time);

        assertEquals(expectedTimeInMillis, actualTimeInMillis);
    }

    /**
     * Checks that the time in milliseconds since the epoch for a date 1.5 years from the current
     * date returned by Calculations.calculate TimeInMillis() is accurate by comparing to a standard
     * online calculator.
     *
     * @throws Exception An Exception caused by an error with the Asserts in the test.
     */
    @Test
    public void calculateTimeInMillisLongDate() throws Exception {
        String date = "11/25/2018";
        String time = "2:12 PM";

        /**
         * Expected value calculated from epochconverter.com which returns time in milliseconds
         * since the epoch of a specific date.
         */
        long expectedTimeInMillis = 1543176720000L;

        long actualTimeInMillis = Calculations.calculateTimeInMillis(date, time);

        assertEquals(expectedTimeInMillis, actualTimeInMillis);
    }

    /**
     * Tests that if an improperly formatted date or time String, such as one with letters, is
     * inputted into Calculations.calculateTimeInMillis(), a NumberFormatException is thrown.
     *
     * @throws Exception A NumberFormatException caused by the improper input
     */
    @Test(expected = NumberFormatException.class)
    public void calculateTimeInMillisIncorrectInput() throws Exception {
        String incorrectDateFormat = "1a/df/2sdl";
        String incorrectTimeFormat = "s:1f PM";

        long actualTimeInMillis = Calculations.calculateTimeInMillis(incorrectDateFormat, incorrectTimeFormat);
    }

}