package ua.pp.appdev.expense.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.models.Currency;
import ua.pp.appdev.expense.models.Expense;
import ua.pp.appdev.expense.utils.Log;

public class DBHelper extends SQLiteOpenHelper{

    private static final String DB_NAME = "expensesDB";
    private static final int DB_VERSION = 3;

    private Context context;

    public DBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d();

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
            + Currency.RATE_COLUMN + " float not null, "
            + Currency.UPDATED_TIME + " timestamp default current_timestamp);"
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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Replace currency rate column
        if (oldVersion < 3) {
                db.execSQL("ALTER TABLE " + Currency.TABLE + " RENAME TO " + Currency.TABLE + "_old;");
                db.execSQL("create table " + Currency.TABLE + "( "
                                + Currency.ID_COLUMN + " integer primary key autoincrement, "
                                + Currency.ISO_CODE_COLUMN + " varchar(3) not null, "
                                + Currency.SHORT_NAME_COLUMN + " varchar(8) not null, "
                                + Currency.FULL_NAME_COLUMN + " varchar(32) not null, "
                                + Currency.RATE_COLUMN + " float not null);"
                );

                db.execSQL("insert into " + Currency.TABLE + " select * from " + Currency.TABLE + "_old;");
                db.execSQL("alter table " + Currency.TABLE + " add column " + Currency.UPDATED_TIME + " timestamp default 0;");
                db.execSQL("DROP TABLE IF EXISTS " +  Currency.TABLE + "_old;");
        }

    }

    private void fillData(SQLiteDatabase db) {

        Log.d();
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
            Log.wtf("Invalid categories xml file");
        } else {

            Log.d();

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
            Log.wtf("Invalid currencies xml file");
        } else {

            Log.d();

            for(int i = 0; i < currenciesCount; i++ ){
                cv.put(Currency.ISO_CODE_COLUMN, currencyISOCodes.getString(i));
                cv.put(Currency.SHORT_NAME_COLUMN, currencyNames.getString(i));
                cv.put(Currency.FULL_NAME_COLUMN, currencyFullNames.getString(i));
                cv.put(Currency.RATE_COLUMN, currencyRates.getFloat(i, 0));
                db.insert(Currency.TABLE, null, cv);
            }
        }
    }
}
