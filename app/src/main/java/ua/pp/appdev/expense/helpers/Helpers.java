package ua.pp.appdev.expense.helpers;

import android.content.Context;
import android.text.format.DateUtils;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.models.Currency;

import static android.text.format.DateFormat.*;

public class Helpers {

    private static boolean showTime = false;

    public static String colorToString(int color){
        return String.format("#%06X", (0xFFFFFF & color));
    }

    public static String calendarToDateTimeString(Context context, Calendar calendar){
        String dateStr = calendarToDateString(context, calendar);
        if(showTime){
            dateStr += " " + calendarToTimeString(context, calendar);
        }
        return dateStr;
    }

    public static String calendarToDateString(Context context, Calendar calendar){

        if(DateUtils.isToday(calendar.getTimeInMillis())) {
            return "Today";
        }

        Calendar tmp = (Calendar) calendar.clone();
        tmp.add(Calendar.DAY_OF_MONTH, 1);
        if(DateUtils.isToday(tmp.getTimeInMillis())){
            return "Yesterday";
        }

        int flags = DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_SHOW_WEEKDAY
                | DateUtils.FORMAT_ABBREV_WEEKDAY;

        if(Calendar.getInstance().get(Calendar.YEAR) == calendar.get(Calendar.YEAR)){
            flags |= DateUtils.FORMAT_NO_YEAR;
        }

        return DateUtils.formatDateTime(context, calendar.getTimeInMillis(), flags);
    }

    public static String calendarToShortDateString(Context context, Calendar calendar){
        int flags = DateUtils.FORMAT_NUMERIC_DATE;
        return DateUtils.formatDateTime(context, calendar.getTimeInMillis(), flags);
    }

    public static String calendarToTimeString(Context context, Calendar calendar){
        DateFormat tf = getTimeFormat(context);
        return tf.format(calendar.getTime());
    }


    public static String sumToString(BigDecimal sum, Currency currency){
        return sum.setScale(2, BigDecimal.ROUND_UP) + " " + currency.name;
    }
}
