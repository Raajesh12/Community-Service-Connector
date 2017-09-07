package com.example.raajesharunachalam.communityserviceconnector;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests methods in the Format.java class that format descriptions of events and times entered by
 * the user
 * Created by raajesharunachalam on 5/2/17.
 *
 * @author arnchlm2
 */
public class FormatTest {

    /**
     * Takes in a description that is of length greater than 40 and is expected to truncate the
     * description and add ellipsis appropriately.
     *
     * @throws Exception An Exception caused by an error with the Asserts in the test.
     */
    @Test
    public void formatDescriptionLong() throws Exception {
        String descriptionLong = "This is a great event that will help promote a sense of community" +
                " within the local CU area. Please bring friends and family!";

        String descriptionFormattedExpected = "This is a great event that will help pro...";

        assertEquals(descriptionFormattedExpected, Format.formatDescription(descriptionLong));
    }

    /**
     * Takes in a description that is of length less than 40, so is expected to not truncate it at
     * all and just return the full description.
     *
     * @throws Exception An Exception caused by an error with the Asserts in the test.
     */
    @Test
    public void formatDescriptionShort() throws Exception {
        String descriptionShort = "A great event that will feed families.";

        String descriptionFormattedExpected = "A great event that will feed families.";

        assertEquals(descriptionFormattedExpected, Format.formatDescription(descriptionShort));
    }

    /**
     * Tests that the method will convert a military hour greater than 12 to an hour within 1-12
     * and add PM to it.
     *
     * @throws Exception An Exception caused by an error with the Asserts in the test.
     */
    @Test
    public void formatTimePMTest() throws Exception {
        int hour = 23;
        int minute = 49;

        String expectedTime = "11:49 PM";
        assertEquals(expectedTime, Format.formatTime(hour, minute));
    }

    /**
     * Tests that the method will keep a military hour less than 12 the same and add AM to it.
     *
     * @throws Exception An Exception caused by an error with the Asserts in the test.
     */
    @Test
    public void formatTimeAMTest() throws Exception {
        int hour = 11;
        int minute = 32;

        String expectedTime = "11:32 AM";
        assertEquals(expectedTime, Format.formatTime(hour, minute));
    }

    /**
     * Tests that the method will keep a military hour of 12 the same, and add PM to it.
     *
     * @throws Exception An Exception caused by an error with the Asserts in the test.
     */
    @Test
    public void formatTimeMidDay() throws Exception {
        int hour = 12;
        int minute = 16;

        String expectedTime = "12:16 PM";
        assertEquals(expectedTime, Format.formatTime(hour, minute));
    }

    /**
     * Tests that the method will convert a military hour greater of 0 to 12 and add AM to it.
     *
     * @throws Exception An Exception caused by an error with the Asserts in the test.
     */
    @Test
    public void formatTimeNewDay() throws Exception {
        int hour = 0;
        int minute = 19;

        String expectedTime = "12:19 AM";
        assertEquals(expectedTime, Format.formatTime(hour, minute));
    }

    /**
     * Tests that the method will convert minutes of single digits to be a String of "0" plus the minute.
     *
     * @throws Exception An Exception caused by an error with the Asserts in the test.
     */
    @Test
    public void formatTimeSingleDigitMinutes() throws Exception {
        int hour = 14;
        int minute = 8;

        String expectedTime = "2:08 PM";
        assertEquals(expectedTime, Format.formatTime(hour, minute));
    }

    /**
     * Tests that the method will convert minutes of 0 to a String of "00"
     *
     * @throws Exception An Exception caused by an error with the Asserts in the test.
     */
    @Test
    public void formatTimeZeroMinutesTest() throws Exception {
        int hour = 18;
        int minute = 0;

        String expectedTime = "6:00 PM";
        assertEquals(expectedTime, Format.formatTime(hour, minute));
    }

}