package ua.pp.appdev.expense.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ua.pp.appdev.expense.R;

public class DBHelper extends SQLiteOpenHelper{

    final String LOG_TAG = "DBLogs";

    public static final String DB_NAME = "expensesDB";

    public static final String CATEGORIES_TABLE = "categories";
    public static final String CURRENCIES_TABLE = "currencies";
    public static final String EXPENSES_TABLE = "expenses";

    private Context context;

    public DBHelper(Context context){
        super(context, DB_NAME, null, 1);
        this.context = context;
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

    public void fillData(SQLiteDatabase db) {

        Log.d(LOG_TAG, "--- fill database ---");

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
                cv.put("name", categoryNames.getString(i));
                cv.put("color", categoryColors.getColor(i,0));
                db.insert(CATEGORIES_TABLE, null, cv);
            }
        }
    }
}
