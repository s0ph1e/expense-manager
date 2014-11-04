package ua.pp.appdev.expense.helpers;

import android.content.Context;
import android.content.SharedPreferences;

import ua.pp.appdev.expense.models.Currency;

public class SharedPreferencesHelper {

    /**
     * Name of file with currency preferences
     */
    private static final String CURRENCY_PREFERENCES = "currencyPreferences";

    /**
     * Key for save/load base currency id
     */
    private static final String BASE_CURRENCY_ID_KEY = "baseCurrencyId";

    /**
     * Default id for base currency. Used if base currency was not found in preferences
     */
    private static final long BASE_CURRENCY_DEFAULT_ID = 1;

    /**
     * Cashed current currency
     */
    private static Currency currentCurrency = null;


    /**
     * Saves base currency id to shared preferences
     * @param context
     * @param currency
     */
    public static void saveBaseCurrency(Context context, Currency currency){
        SharedPreferences currencyPrefs = context.getApplicationContext()
                .getSharedPreferences(CURRENCY_PREFERENCES, Context.MODE_PRIVATE);

        long currencyId = (currency == null) ? BASE_CURRENCY_DEFAULT_ID : currency.id;
        currentCurrency = Currency.getById(context, currencyId);

        currencyPrefs.edit()
                .putLong(BASE_CURRENCY_ID_KEY, currencyId)
                .apply();
    }

    /**
     * Receiving base currency from shared preferences
     * @param context
     * @return Currency
     */
    public static Currency getBaseCurrency(Context context){
        if(currentCurrency == null) {
            SharedPreferences currencyPrefs = context.getApplicationContext()
                    .getSharedPreferences(CURRENCY_PREFERENCES, Context.MODE_PRIVATE);

            long currencyId = currencyPrefs.getLong(BASE_CURRENCY_ID_KEY, BASE_CURRENCY_DEFAULT_ID);
            currentCurrency = Currency.getById(context, currencyId);
        }

        return currentCurrency;
    }
}
