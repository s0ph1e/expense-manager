package ua.pp.appdev.expense.models;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ua.pp.appdev.expense.helpers.DBHelper;

public class Currency {

    public static final String TABLE = "currencies";
    public static final String ID_COLUMN = "id";
    public static final String ISO_CODE_COLUMN = "iso_code";
    public static final String SHORT_NAME_COLUMN = "short_name";
    public static final String FULL_NAME_COLUMN = "full_name";
    public static final String RATES_COLUMN = "rates";

    public long id;
    public String isoCode;
    public String name;
    public String fullName;
    public JSONObject rates;

    private Currency(long id, String isoCode, String name, String fullName, JSONObject rates) {
        this.id = id;
        this.isoCode = isoCode;
        this.name = name;
        this.fullName = fullName;
        this.rates = rates;
    }

    public static List<Currency> getAll(Context context){

        List<Currency> currenciesList = new ArrayList<Currency>();

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(TABLE, null, null, null, null, null, null);

        if (c.moveToFirst()) {

            // Get column indexes
            int idColIndex = c.getColumnIndex(ID_COLUMN);
            int isoCodeIndex = c.getColumnIndex(ISO_CODE_COLUMN);
            int nameColIndex = c.getColumnIndex(SHORT_NAME_COLUMN);
            int fullNameColIndex = c.getColumnIndex(FULL_NAME_COLUMN);
            int ratesColIndex = c.getColumnIndex(RATES_COLUMN);

            long id;
            String isoCode, name, fullName;
            JSONObject rates;

            do {
                id = c.getLong(idColIndex);
                isoCode = c.getString(isoCodeIndex);
                name = c.getString(nameColIndex);
                fullName = c.getString(fullNameColIndex);
                try {
                    rates = new JSONObject(c.getString(ratesColIndex));
                } catch (JSONException e) {
                    e.printStackTrace();
                    rates = null;
                }

                currenciesList.add(new Currency(id, isoCode, name, fullName, rates));
            } while (c.moveToNext());
        }
        db.close();

        return currenciesList;
    }

    public static Currency getById(Context context, long id){
        Currency currency = null;

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(TABLE, null, ID_COLUMN + " = ?", new String[] {String.valueOf(id)}, null, null, null);

        if (c.moveToFirst()) {

            // Get column indexes
            int idColIndex = c.getColumnIndex(ID_COLUMN);
            int isoCodeIndex = c.getColumnIndex(ISO_CODE_COLUMN);
            int nameColIndex = c.getColumnIndex(SHORT_NAME_COLUMN);
            int fullNameColIndex = c.getColumnIndex(FULL_NAME_COLUMN);
            int ratesColIndex = c.getColumnIndex(RATES_COLUMN);


            String isoCode, name, fullName;
            JSONObject rates;

            isoCode = c.getString(isoCodeIndex);
            name = c.getString(nameColIndex);
            fullName = c.getString(fullNameColIndex);
            try {
                rates = new JSONObject(c.getString(ratesColIndex));
            } catch (JSONException e) {
                e.printStackTrace();
                rates = null;
            }

            currency = new Currency(id, isoCode, name, fullName, rates);

        }
        db.close();

        return currency;
    }
}
