package ua.pp.appdev.expense;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import ua.pp.appdev.expense.helpers.CurrencyUpdate;
import ua.pp.appdev.expense.helpers.DBHelper;
import ua.pp.appdev.expense.helpers.DatabaseManager;

/**
 * Created by:
 * Ilya Antipenko <ilya@antipenko.pp.ua>
 * Sophia Nepochataya <sophia@nepochataya.pp.ua>
 */
public class ExpenseApplication extends Application {
    @Override
    public void onCreate() {
        DatabaseManager.initializeInstance(new DBHelper(this));
        // Update currencies rates
        CurrencyUpdate.startUpdate(this);
        Log.i("ExpenseApplication", "onCreate");
        super.onCreate();
    }
}
