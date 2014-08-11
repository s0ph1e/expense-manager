package ua.pp.appdev.expense.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ua.pp.appdev.expense.helpers.DatabaseManager;
import ua.pp.appdev.expense.utils.Log;

public class Currency implements Serializable {

    public static final String TABLE = "currencies";
    public static final String ID_COLUMN = "id";
    public static final String ISO_CODE_COLUMN = "iso_code";
    public static final String SHORT_NAME_COLUMN = "short_name";
    public static final String FULL_NAME_COLUMN = "full_name";
    public static final String RATE_COLUMN = "rate";
    public static final String UPDATED_TIME = "updatedTime";

    public long id;
    public String isoCode;
    public String name;
    public String fullName;
    public float rate; // TODO: consider storing rate
    public long updatedTime;

    public Currency(String isoCode, String name, String fullName, float rate, long updatedTime) {
        this(0, isoCode, name, fullName, rate, updatedTime);
    }

    private Currency(long id, String isoCode, String name, String fullName, float rate, long updatedTime) {
        this.id = id;
        this.isoCode = isoCode;
        this.name = name;
        this.fullName = fullName;
        this.rate = rate;
        this.updatedTime = updatedTime;
    }

    public static List<Currency> getAll(Context context){

        List<Currency> currenciesList = new ArrayList<Currency>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        Cursor c = db.query(TABLE, null, null, null, null, null, null);

        if (c.moveToFirst()) {

            // Get column indexes
            int idColIndex = c.getColumnIndex(ID_COLUMN);
            int isoCodeIndex = c.getColumnIndex(ISO_CODE_COLUMN);
            int nameColIndex = c.getColumnIndex(SHORT_NAME_COLUMN);
            int fullNameColIndex = c.getColumnIndex(FULL_NAME_COLUMN);
            int rateColIndex = c.getColumnIndex(RATE_COLUMN);
            int updatedTimeColIndex = c.getColumnIndex(UPDATED_TIME);

            long id, updatedTime;
            String isoCode, name, fullName;
            float rate;

            do {
                id = c.getLong(idColIndex);
                isoCode = c.getString(isoCodeIndex);
                name = c.getString(nameColIndex);
                fullName = c.getString(fullNameColIndex);
                rate = c.getFloat(rateColIndex);
                updatedTime = c.getLong(updatedTimeColIndex);

                currenciesList.add(new Currency(id, isoCode, name, fullName, rate, updatedTime));
            } while (c.moveToNext());
        }
        DatabaseManager.getInstance().closeDatabase();

        return currenciesList;
    }

    public long save(Context context) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues cv = new ContentValues();
        cv.put(ISO_CODE_COLUMN, isoCode);
        cv.put(FULL_NAME_COLUMN, fullName);
        cv.put(SHORT_NAME_COLUMN, name);
        cv.put(RATE_COLUMN, rate);
        cv.put(UPDATED_TIME, updatedTime);

        long response;

        if (id == 0) {
            response = db.insert(TABLE, null, cv);
        } else {
            response = db.update(TABLE, cv, "id = ?", new String[] {Long.toString(id)});
        }
        DatabaseManager.getInstance().closeDatabase();
        return response;
    }

    public static Currency getById(Context context, long id){
        Currency currency = null;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        Cursor c = db.query(TABLE, null, ID_COLUMN + " = ?", new String[] {String.valueOf(id)}, null, null, null);

        if (c.moveToFirst()) {

            // Get column indexes
            int idColIndex = c.getColumnIndex(ID_COLUMN);
            int isoCodeIndex = c.getColumnIndex(ISO_CODE_COLUMN);
            int nameColIndex = c.getColumnIndex(SHORT_NAME_COLUMN);
            int fullNameColIndex = c.getColumnIndex(FULL_NAME_COLUMN);
            int rateColIndex = c.getColumnIndex(RATE_COLUMN);
            int updatedTimeColIndex = c.getColumnIndex(UPDATED_TIME);

            long updatedTime;
            String isoCode, name, fullName;
            float rate;

            isoCode = c.getString(isoCodeIndex);
            name = c.getString(nameColIndex);
            fullName = c.getString(fullNameColIndex);
            rate = c.getFloat(rateColIndex);
            updatedTime = c.getLong(updatedTimeColIndex);

            currency = new Currency(id, isoCode, name, fullName, rate, updatedTime);
        }
        c.close();
        DatabaseManager.getInstance().closeDatabase();

        return currency;
    }

    public static Currency getByIso(Context context, String isoCode){
        Currency currency = null;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        Cursor c = db.query(TABLE, null, ISO_CODE_COLUMN + " = ?", new String[] {isoCode}, null, null, null);

        if (c.moveToFirst()) {

            // Get column indexes
            int idColIndex = c.getColumnIndex(ID_COLUMN);
            int isoCodeIndex = c.getColumnIndex(ISO_CODE_COLUMN);
            int nameColIndex = c.getColumnIndex(SHORT_NAME_COLUMN);
            int fullNameColIndex = c.getColumnIndex(FULL_NAME_COLUMN);
            int rateColIndex = c.getColumnIndex(RATE_COLUMN);
            int updatedTimeColIndex = c.getColumnIndex(UPDATED_TIME);

            long id, updatedTime;
            String name, fullName;
            float rate;

            id = c.getLong(idColIndex);
            name = c.getString(nameColIndex);
            fullName = c.getString(fullNameColIndex);
            rate = c.getFloat(rateColIndex);
            updatedTime = c.getLong(updatedTimeColIndex);

            currency = new Currency(id, isoCode, name, fullName, rate, updatedTime);
        }
        c.close();
        DatabaseManager.getInstance().closeDatabase();

        return currency;
    }

    @Override
    public boolean equals(Object o) {
        // Check class of object
        if (o instanceof Currency) {
            Currency other = (Currency) o;
            if (other.id == this.id
                    && other.isoCode.equals(this.isoCode)) {
                return true;
            }
        }

        return false;
    }

    public String getSymbol() {
        String currencySymbol = name;
        try {
            currencySymbol = java.util.Currency.getInstance(isoCode).getSymbol();
        } catch (IllegalArgumentException e){
            Log.e(e.getMessage());
        }
        return currencySymbol;

    }
}
