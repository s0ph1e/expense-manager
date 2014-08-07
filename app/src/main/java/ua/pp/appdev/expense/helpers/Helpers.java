package ua.pp.appdev.expense.helpers;

import android.content.Context;
import android.text.format.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Calendar;

import ua.pp.appdev.expense.models.Currency;

import static android.text.format.DateFormat.getTimeFormat;

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
        // This formats currency values as the user expects to read them (default locale).
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        // Note we don't supply a locale to this method - uses default locale to format the currency symbol.
        String symbol = currency.getSymbol();

        // We then tell our formatter to use this symbol.
        DecimalFormatSymbols decimalFormatSymbols = ((java.text.DecimalFormat) currencyFormat).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol(symbol);
        ((java.text.DecimalFormat) currencyFormat).setDecimalFormatSymbols(decimalFormatSymbols);

        return currencyFormat.format(sum);
    }

    public static String percentageString(BigDecimal part, BigDecimal whole){
        BigDecimal result = part.multiply(new BigDecimal(100)).divide(whole, 2, RoundingMode.HALF_UP);
        return result.toString() + "%";
    }

    public static String makePlaceholders(int len) {
        if (len < 1) {
            // It will lead to an invalid query anyway ..
            throw new RuntimeException("No placeholders");
        } else {
            StringBuilder sb = new StringBuilder(len * 2 - 1);
            sb.append("?");
            for (int i = 1; i < len; i++) {
                sb.append(",?");
            }
            return sb.toString();
        }
    }
}
