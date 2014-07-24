package ua.pp.appdev.expense.helpers;

import android.content.Context;

import java.text.DateFormat;
import java.util.Calendar;

public class Helpers {

    public static String colorToString(int color){
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public static String datetimeToString(Context context, Calendar calendar){
        return  dateToString(context, calendar) + " " + timeToString(context, calendar);
    }

    public static String dateToString(Context context, Calendar calendar){
        DateFormat df = android.text.format.DateFormat.getDateFormat(context);
        return  df.format(calendar.getTime());
    }

    public static String timeToString(Context context, Calendar calendar){
        DateFormat tf = android.text.format.DateFormat.getTimeFormat(context);
        return tf.format(calendar.getTime());
    }
}
