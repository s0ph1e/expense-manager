package ua.pp.appdev.expense;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ua.pp.appdev.expense.helpers.DBHelper;

public class Currency {
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

        Cursor c = db.query(DBHelper.CURRENCIES_TABLE, null, null, null, null, null, null);

        if (c.moveToFirst()) {

            // Get column indexes
            int idColIndex = c.getColumnIndex("id");
            int isoCodeIndex = c.getColumnIndex("iso_code");
            int nameColIndex = c.getColumnIndex("short_name");
            int fullNameColIndex = c.getColumnIndex("full_name");
            int ratesColIndex = c.getColumnIndex("rates");

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
}
