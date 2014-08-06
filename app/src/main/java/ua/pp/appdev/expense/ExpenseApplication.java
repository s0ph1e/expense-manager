package ua.pp.appdev.expense;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

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
        super.onCreate();
    }
}
