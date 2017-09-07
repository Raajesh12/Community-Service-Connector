package com.example.raajesharunachalam.communityserviceconnector;

import java.util.Calendar;

/**
 * Class that contains methods which help perform calculations for distance and time that are used
 * within certain activities of the app.
 * Created by raajesharunachalam on 5/2/17.
 *
 * @author arnchlm2
 */
public class Calculations {
    private static final int NEW_DAY = 12;
    private static final int MID_DAY = 12;
    private static final int CONVERSION_TO_PM = 12;

    /**
     * Uses a date in mm/dd/yyyy format and a time in hh:mm AM/PM format to calculate the exact time
     * in milliseconds
     *
     * @param date A date in the mm/dd/yyyy format
     * @param time A time in mm:hh AM/PM format
     * @return The exact number of milliseconds of the date/time specified
     */
    public static long calculateTimeInMillis(String date, String time) {
        //Breaks up the date and time into its individual components
        String[] dateComponents = date.split("/");
        int year = Integer.parseInt(dateComponents[2]);
        int month = Integer.parseInt(dateComponents[0]) - 1;
        int day = Integer.parseInt(dateComponents[1]);

        String[] timeComponents = time.split(":");
        int hour = Integer.parseInt(timeComponents[0]);

        //The millisecond conversion uses military time, so add 12 if the time is any time in pm after 12 pm
        if (timeComponents[1].contains("PM") && hour != MID_DAY) {
            hour += CONVERSION_TO_PM;
        } else if (timeComponents[1].contains("AM") && hour == NEW_DAY) {
            //If it is within the hour of 12 AM, the military time by hour is 0, so subtract 12
            hour -= 12;
        }
        int minute = Integer.parseInt(timeComponents[1].substring(0, 2));

        //Uses the date/time components above to calculate the exact end time in milliseconds
        Calendar exactTime = Calendar.getInstance();
        exactTime.clear();
        exactTime.set(year, month, day, hour, minute);
        return exactTime.getTimeInMillis();
    }
}
