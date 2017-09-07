package com.example.raajesharunachalam.communityserviceconnector;

import android.os.Build;
import android.widget.TimePicker;

/**
 * Class that contains methods to format times and text into a standard way.
 * <p>
 * Created by raajesharunachalam on 5/2/17.
 *
 * @author arnchlm2
 */

public class Format {

    private static final int DESCRIPTION_MAXIMUM_LENGTH = 40;
    private static final int MID_DAY = 12;
    private static final int NEW_DAY = 0;
    private static final int MAXIMUM_SINGLE_DIGIT_MINUTE = 9;
    private static final int TIME_PICKER_MINIMUM_SDK = 23;

    /**
     * With a given description, if the length is greater than 40 characters, shortens it and adds
     * ellipses to the end. Otherwise returns the original description.
     *
     * @param description A description for a given event.
     * @return A String that represents the shortened description of an event.
     */
    public static String formatDescription(String description) {
        String descriptionShortened;
        if (description.length() > DESCRIPTION_MAXIMUM_LENGTH) {
            descriptionShortened = description.substring(0, DESCRIPTION_MAXIMUM_LENGTH) + "...";
        } else {
            descriptionShortened = description;
        }
        return descriptionShortened;
    }

    /**
     * Returns a string that formats the time received by a user's input in a TimePicker widget which
     * is in 24 military time format to an AM/PM format.
     *
     * @param picker A TimePicker that the user has entered a time in
     * @return A formatted String that represents the time int AM/PM format
     */
    public static String formatTime(TimePicker picker) {
        int hour;
        int minute;

        //23 is the minimum SDK level in which getHour() and getMinute() can be used
        if (Build.VERSION.SDK_INT >= 23) {
            hour = picker.getHour();
            minute = picker.getMinute();
        } else {
            hour = picker.getCurrentHour();
            minute = picker.getCurrentMinute();
        }

        return formatTime(hour, minute);
    }

    /**
     * Returns a string that formats given hour (military time format) and minute values into an
     * AM/PM format.
     *
     * @param hour   An hour that is in military time (0-23)
     * @param minute A value that represents the minutes of the specific time (0-59)
     * @return A formatted String that represents the time in AM/PM format
     */
    public static String formatTime(int hour, int minute) {
        String periodOfDay;
        if (hour > MID_DAY) {
            hour -= 12;
            periodOfDay = "PM";
        } else if (hour == MID_DAY) {
            periodOfDay = "PM";
        } else if (hour == NEW_DAY) {
            hour += 12;
            periodOfDay = "AM";
        } else {
            periodOfDay = "AM";
        }

        //If the minute is 0, display 00 for standard clock format
        String minuteString;
        if (minute == 0) {
            minuteString = "00";
        } else if (minute <= MAXIMUM_SINGLE_DIGIT_MINUTE) {
            //If the minute is single digits, add a 0 before it
            minuteString = "0" + String.valueOf(minute);
        } else {
            minuteString = String.valueOf(minute);

        }

        String formattedTime = hour + ":" + minuteString + " " + periodOfDay;
        return formattedTime;
    }
}
