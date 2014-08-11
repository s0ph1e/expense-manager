package ua.pp.appdev.expense.helpers;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;

import ua.pp.appdev.expense.exceptions.CurrencyUpdateException;
import ua.pp.appdev.expense.models.Currency;
import ua.pp.appdev.expense.utils.Log;

/**
 * Created by:
 * Ilya Antipenko <ilya@antipenko.pp.ua>
 * Sophia Nepochataya <sophia@nepochataya.pp.ua>
 */

public class CurrencyUpdate {

    // TODO: Add support for few currency servers
    private final String CURRENCY_RATE_URL = "http://rates.expense.appdev.pp.ua/rates.json";
    private final String CURRENCY_NAME_URL = "http://openexchangerates.org/api/currencies.json";

    private Context context;

    public CurrencyUpdate(Context context) {
        this.context = context;
    }

    public void update() {
        try {
            // Get currency names (full name)
            JSONObject currenciesNames = JsonReader.readJsonFromUrl(CURRENCY_NAME_URL);

            // Get currency rates
            JSONObject latestRates = JsonReader.readJsonFromUrl(CURRENCY_RATE_URL);
            long updateTimestamp = latestRates.getLong("timestamp");
            JSONObject rateListObject = latestRates.getJSONObject("rates");
            Iterator<?> rateListKeys = rateListObject.keys();

            while (rateListKeys.hasNext()) {
                String key = (String)rateListKeys.next();

                float rate;
                try {
                    rate = Float.parseFloat(rateListObject.get(key).toString());
                } catch (NumberFormatException ex) {
                    Log.e(ex.toString());
                    continue;
                }

                Currency currency = Currency.getByIso(context, key);
                if (currency == null) {
                    currency = new Currency(key, key, currenciesNames.getString(key), rate, updateTimestamp);
                } else {
                    currency.name = key;
                    currency.fullName = currenciesNames.getString(key);
                    currency.rate = rate;
                    currency.updatedTime = updateTimestamp;
                }
                currency.save(context);
            }
        } catch (IOException e) {
            Log.e("Exception", e);
            throw new CurrencyUpdateException(e);
        } catch (JSONException e) {
            Log.e("Exception", e);
            throw new CurrencyUpdateException(e);
        }
    }

    public static void startUpdate(final Context context) {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                Log.i("startUpdate - start");
                try {
                    CurrencyUpdate updater = new CurrencyUpdate(context);
                    updater.update();
                } catch (Exception e) {
                    Log.e(e.toString());
                }
                Log.i("startUpdate - end");
            }
        });
        thread.start();
    }

}

class JsonReader {

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        } finally {
            is.close();
        }
    }
}