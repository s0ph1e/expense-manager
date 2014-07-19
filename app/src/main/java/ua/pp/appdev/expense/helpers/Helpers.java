package ua.pp.appdev.expense.helpers;

/**
 * Created by Sophia on 19.07.2014.
 */
public class Helpers {

    public static String colorToString(int color){
        return String.format("#%06X", (0xFFFFFF & color));
    }
}
