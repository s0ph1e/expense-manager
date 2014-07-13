package ua.pp.appdev.expense.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper{

    final String LOG_TAG = "DBLogs";

    public static final String DB_NAME = "expensesDB";

    public final String CATEGORIES_TABLE = "categories";
    public final String CURRENCIES_TABLE = "currencies";
    public final String EXPENSES_TABLE = "expenses";

    public DBHelper(Context context){
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d(LOG_TAG, "--- onCreate database ---");

        // Create categories table
        db.execSQL("create table " + CATEGORIES_TABLE + "( "
            + "id integer primary key autoincrement, "
            + "name varchar(32) not null, "
            + "color integer not null);"
        );

        // Create currencies table
        db.execSQL("create table " + CURRENCIES_TABLE + "( "
            + "id integer primary key autoincrement, "
            + "iso_code varchar(3) not null, "
            + "full_name varchar(32) not null, "
            + "rates text not null);"
        );

        // Create expenses table
        db.execSQL("create table " + EXPENSES_TABLE + "( "
            + "id integer primary key autoincrement, "
            + "create_timestamp timestamp default current_timestamp, "
            + "expense_timestamp timestamp default current_timestamp, "
            + "category_id integer not null, "
            + "sum integer not null, "
            + "currency_id integer not null, "
            + "description varchar(128));"
        );

        fillData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }

    // TODO: read data from config
    public void fillData(SQLiteDatabase db){

        Log.d(LOG_TAG, "--- fill database ---");

        ContentValues cv = new ContentValues();

        // Fill categories
        cv.put("name", "Food");
        cv.put("color", 10079232);
        db.insert(CATEGORIES_TABLE, null, cv);

        cv.put("name", "Transport");
        cv.put("color", 16759603);
        db.insert(CATEGORIES_TABLE, null, cv);

        cv.put("name", "Housing");
        cv.put("color", 3388901);
        db.insert(CATEGORIES_TABLE, null, cv);

        cv.put("name", "Health");
        cv.put("color", 16729156);
        db.insert(CATEGORIES_TABLE, null, cv);

        cv.put("name", "Leisure");
        cv.put("color", 11167436);
        db.insert(CATEGORIES_TABLE, null, cv);
    }
}
