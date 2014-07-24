package ua.pp.appdev.expense.helpers;

import android.content.Context;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Sophia on 19.07.2014.
 */
public class Helpers {

    public static String colorToString(int color){
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public static String CalendarToString(Context context, Calendar calendar){

        Date date = calendar.getTime();

        DateFormat df = android.text.format.DateFormat.getDateFormat(context);
        DateFormat tf = android.text.format.DateFormat.getTimeFormat(context);

        return  df.format(date) + " " + tf.format(date);
    }
}
