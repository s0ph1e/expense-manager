package ua.pp.appdev.expense.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ua.pp.appdev.expense.activities.SaveExpenseActivity;
import ua.pp.appdev.expense.helpers.DBHelper;
import ua.pp.appdev.expense.helpers.Helpers;

public class Expense implements EditableItem{

    public static final String TABLE = "expenses";
    public static final String ID_COLUMN = "id";
    public static final String CREATE_DATE_COLUMN = "create_timestamp";
    public static final String EXPENSE_DATE_COLUMN = "expense_timestamp";
    public static final String CATEGORY_COLUMN = "category_id";
    public static final String SUM_COLUMN = "sum";
    public static final String CURRENCY_COLUMN = "currency_id";
    public static final String NOTE_COLUMN = "note";

    public long id;
    public Calendar createDate;
    public Calendar expenseDate;
    public Category category;
    public BigDecimal sum;
    public Currency currency;
    public String note;

    public Expense(){
        id = 0;
        createDate = GregorianCalendar.getInstance();
        expenseDate = GregorianCalendar.getInstance();
        category = null;
        sum = new BigDecimal(0);
        currency = null;
        note = "";
    }

    public Expense(long id, Calendar createDate, Calendar expenseDate, Category category, BigDecimal sum, Currency currency, String note) {
        this.id = id;
        this.createDate = createDate;
        this.expenseDate = expenseDate;
        this.category = category;
        this.sum = sum;
        this.currency = currency;
        this.note = note;
    }

    public static List<Expense> getAll(Context context, String[] categories) {

        List<Expense> expensesList = new ArrayList<Expense>();

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String where = "";

        if(categories != null){
            where = " where " + CATEGORY_COLUMN  + " in ( " + Helpers.makePlaceholders(categories.length) + " )";
        }

        Cursor c = db.rawQuery("select * from " + TABLE + where
                + " order by " + EXPENSE_DATE_COLUMN + " DESC",
                categories
        );

        if (c.moveToFirst()) {

            // Get column indexes
            int idColIndex = c.getColumnIndex(ID_COLUMN);
            int createDateColIndex = c.getColumnIndex(CREATE_DATE_COLUMN);
            int expenseDateColIndex = c.getColumnIndex(EXPENSE_DATE_COLUMN);
            int categoryColIndex = c.getColumnIndex(CATEGORY_COLUMN);
            int sumColIndex = c.getColumnIndex(SUM_COLUMN);
            int currencyColIndex = c.getColumnIndex(CURRENCY_COLUMN);
            int noteColIndex = c.getColumnIndex(NOTE_COLUMN);

            // Variables for creating expense
            long id;

            BigDecimal sum;
            Category category;
            Currency currency;
            String note;

            do {
                id = c.getLong(idColIndex);
                Calendar createDate = GregorianCalendar.getInstance(),
                        expenseDate = GregorianCalendar.getInstance();
                createDate.setTimeInMillis(c.getLong(createDateColIndex));
                expenseDate.setTimeInMillis(c.getLong(expenseDateColIndex));
                sum =  new BigDecimal(c.getString(sumColIndex));
                category = Category.getById(context, c.getLong(categoryColIndex));
                currency = Currency.getById(context, c.getLong(currencyColIndex));
                note = c.getString(noteColIndex);

                expensesList.add(new Expense(id, createDate, expenseDate, category, sum, currency, note));

            } while (c.moveToNext());
        }
        db.close();

        return expensesList;
    }

    public void save(Context context){

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(CREATE_DATE_COLUMN, createDate.getTimeInMillis());
        cv.put(EXPENSE_DATE_COLUMN, expenseDate.getTimeInMillis());
        cv.put(CATEGORY_COLUMN, category.id);
        cv.put(SUM_COLUMN, sum.doubleValue());
        cv.put(CURRENCY_COLUMN, currency.id);
        cv.put(NOTE_COLUMN, note);

        if(id == 0){    // new category
            id = db.insert(TABLE, null, cv);
        } else {        // existing category
            db.update(TABLE, cv, ID_COLUMN + " = ?", new String[] {String.valueOf(id)});
        }

        db.close();
    }

    public void remove(Context context){
        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(TABLE, ID_COLUMN + " = ?", new String[] { String.valueOf(id) });

        db.close();
    }

    @Override
    public Class getActivityClass() {
        return SaveExpenseActivity.class;
    }

    public String getSumString(){
        return Helpers.sumToString(sum, currency);
    }

    public String toString(Context context) {
        String str = "{id: " + id
                + ", date: " + Helpers.calendarToShortDateString(context, expenseDate)
                + ", category: " + category
                + ", sum: " + Helpers.sumToString(sum, currency)
                + ", note: " + note + "}";
        return str;
    }
}
