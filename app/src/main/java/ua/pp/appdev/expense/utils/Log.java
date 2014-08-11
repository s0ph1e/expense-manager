package ua.pp.appdev.expense.utils;

import ua.pp.appdev.expense.BuildConfig;

public class Log {
    static final boolean LOG = BuildConfig.DEBUG;
    static final int TRACE_ELEMENT_INDEX = 4;

    private static String getClassName(){
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        String className;
        if(trace.length > TRACE_ELEMENT_INDEX){
            className = trace[TRACE_ELEMENT_INDEX].getClassName();
            className = className.substring(className.lastIndexOf(".") + 1);
        } else {
            className =  "No class was found";
        }
        return className;
    }

    private static String getMethodName(){
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if(trace.length > TRACE_ELEMENT_INDEX){
            return trace[TRACE_ELEMENT_INDEX].getMethodName();
        } else {
            return "No method was found";
        }
    }
    public static void i() {
        if (LOG) android.util.Log.i(getClassName(), getMethodName());
    }
    public static void i(String string) {
        if (LOG) android.util.Log.i(getClassName(), string);
    }
    public static void i(String string, Throwable e) {
        if(LOG) android.util.Log.i(getClassName(), string, e);
    }
    public static void e() {
        if (LOG) android.util.Log.e(getClassName(), getMethodName());
    }
    public static void e(String string) {
        if (LOG) android.util.Log.e(getClassName(), string);
    }
    public static void e(String string, Throwable e) {
        if(LOG) android.util.Log.e(getClassName(), string, e);
    }
    public static void d() {
        if (LOG) android.util.Log.d(getClassName(), getMethodName());
    }
    public static void d(String string) {
        if (LOG) android.util.Log.d(getClassName(), string);
    }
    public static void d(String string, Throwable e) {
        if(LOG) android.util.Log.d(getClassName(), string, e);
    }
    public static void v() {
        if (LOG) android.util.Log.v(getClassName(), getMethodName());
    }
    public static void v(String string) {
        if (LOG) android.util.Log.v(getClassName(), string);
    }
    public static void v(String string, Throwable e) {
        if(LOG) android.util.Log.v(getClassName(), string, e);
    }
    public static void w() {
        if (LOG) android.util.Log.w(getClassName(), getMethodName());
    }
    public static void w(String string) {
        if (LOG) android.util.Log.w(getClassName(), string);
    }
    public static void w(String string, Throwable e) {
        if(LOG) android.util.Log.w(getClassName(), string, e);
    }
    public static void wtf(String string) {
        if (LOG) android.util.Log.wtf(getClassName(), string);
    }
}
