package ua.pp.appdev.expense.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.models.Currency;
import ua.pp.appdev.expense.models.Expense;

public class DBHelper extends SQLiteOpenHelper{

    final String LOG_TAG = "DBLogs";

    public static final String DB_NAME = "expensesDB";

    private Context context;

    public DBHelper(Context context){
        super(context, DB_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(LOG_TAG, "--- onCreate database ---");

        // Create categories table
        db.execSQL("create table " + Category.TABLE + "( "
            + Category.ID_COLUMN + " integer primary key autoincrement, "
            + Category.NAME_COLUMN + " varchar(32) not null, "
            + Category.COLOR_COLUMN + " integer not null);"
        );

        // Create currencies table
        db.execSQL("create table " + Currency.TABLE + "( "
            + Currency.ID_COLUMN + " integer primary key autoincrement, "
            + Currency.ISO_CODE_COLUMN + " varchar(3) not null, "
            + Currency.SHORT_NAME_COLUMN + " varchar(8) not null, "
            + Currency.FULL_NAME_COLUMN + " varchar(32) not null, "
            + Currency.RATES_COLUMN + " text not null);"
        );

        // Create expenses table
        db.execSQL("create table " + Expense.TABLE + "( "
            + Expense.ID_COLUMN + " integer primary key autoincrement, "
            + Expense.CREATE_DATE_COLUMN + " timestamp default current_timestamp, "
            + Expense.EXPENSE_DATE_COLUMN + " timestamp default current_timestamp, "
            + Expense.CATEGORY_COLUMN + " integer not null, "
            + Expense.SUM_COLUMN + " double not null, "
            + Expense.CURRENCY_COLUMN + " integer not null, "
            + Expense.NOTE_COLUMN + " varchar(128));"
        );

        fillData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    private void fillData(SQLiteDatabase db) {

        Log.d(LOG_TAG, "--- fill database ---");
        fillCategoriesTable(db);
        fillCurrenciesTable(db);

    }

    private void fillCategoriesTable(SQLiteDatabase db){

        ContentValues cv = new ContentValues();
        Resources res = context.getResources();

        // Get categories init data
        TypedArray categoryNames = res.obtainTypedArray(R.array.category_names);
        TypedArray categoryColors = res.obtainTypedArray(R.array.category_colors);

        // Check categories xml, if it is good, add init data to db
        if(categoryNames.length() != categoryColors.length()){
            Log.wtf(LOG_TAG, "Invalid categories xml file");
        } else {

            Log.d(LOG_TAG, "--- fill categories table ---");

            for(int i = 0, len = categoryNames.length(); i < len; i++ ){
                cv.put(Category.NAME_COLUMN, categoryNames.getString(i));
                cv.put(Category.COLOR_COLUMN, categoryColors.getColor(i,0));
                db.insert(Category.TABLE, null, cv);
            }
        }
    }

    private void fillCurrenciesTable(SQLiteDatabase db){

        ContentValues cv = new ContentValues();
        Resources res = context.getResources();

        // Get currencies init data
        TypedArray currencyISOCodes = res.obtainTypedArray(R.array.currency_codes);
        TypedArray currencyNames = res.obtainTypedArray(R.array.currency_names);
        TypedArray currencyFullNames = res.obtainTypedArray(R.array.currency_full_names);
        TypedArray currencyRates = res.obtainTypedArray(R.array.currency_rates);

        int currenciesCount = currencyISOCodes.length();

        // Check categories xml, if it is good, add init data to db
        if(currenciesCount != currencyNames.length()
                || currenciesCount != currencyFullNames.length()
                || currenciesCount != currencyRates.length()){
            Log.wtf(LOG_TAG, "Invalid currencies xml file");
        } else {

            Log.d(LOG_TAG, "--- fill currencies table ---");

            for(int i = 0; i < currenciesCount; i++ ){
                cv.put(Currency.ISO_CODE_COLUMN, currencyISOCodes.getString(i));
                cv.put(Currency.SHORT_NAME_COLUMN, currencyNames.getString(i));
                cv.put(Currency.FULL_NAME_COLUMN, currencyFullNames.getString(i));
                cv.put(Currency.RATES_COLUMN, currencyRates.getString(i));
                db.insert(Currency.TABLE, null, cv);
            }
        }
    }
}
