package ua.pp.appdev.expense.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import ua.pp.appdev.expense.activities.SaveExpenseActivity;
import ua.pp.appdev.expense.helpers.DatabaseManager;
import ua.pp.appdev.expense.helpers.Helpers;
import ua.pp.appdev.expense.helpers.SharedPreferencesHelper;

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

    private static List<Expense> expenses = null;

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

    private static List<Expense> fetchAll(Context context) {
        List<Expense> expensesList = new ArrayList<Expense>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        Cursor c = db.rawQuery("select * from " + TABLE
                        + " order by " + EXPENSE_DATE_COLUMN + " DESC", new String[]{}
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
        c.close();
        DatabaseManager.getInstance().closeDatabase();

        return expensesList;
    }

    public static List<Expense> getAll(Context context) {
        if (expenses == null) {
            expenses = fetchAll(context);
        }
        return  new ArrayList<Expense>(expenses);
    }

    public static List<Expense> getAllInCategories(Context context, long[] categories) {

        if (categories == null || categories.length == 0) {
            return getAll(context);
        }

        List<Expense> allExpenses = getAll(context);
        List<Expense> filtered = new ArrayList<Expense>();

        for(Expense exp : allExpenses) {
            boolean inResult = false;
            for (long categoryId : categories) {
                if (exp.category != null && exp.category.id == categoryId) {
                    inResult = true;
                    break;
                }
            }

            if (inResult) {
                filtered.add(exp);
            }
        }

        return filtered;
    }

    public static int getCount(Context context){
        return getAll(context).size();
    }

    public static int getCountInCategory(Context context, long categoryId){
        return getCountInCategories(context, new long[]{categoryId});
    }

    public static int getCountInCategories(Context context, long[] categoryIds){

        if (categoryIds == null || categoryIds.length == 0) {
            return getCount(context);
        }

        List<Expense> allExpenses = getAll(context);
        int count = 0;

        for(Expense exp : allExpenses) {
            for(long categoryId : categoryIds) {
                if(exp.category.id == categoryId) {
                    count++;
                    break;
                }
            }
        }

        return count;
    }

    public static boolean allInSameCurrency(Context context, List<Expense> expenses, Currency baseCurrency) {
        // Check if all expenses are in base currency
        boolean allExpensesInBaseCurrency = true;
        for (Expense expense : expenses){
            if (!expense.currency.equals(baseCurrency)){
                allExpensesInBaseCurrency = false;
                break;
            }
        }

        return allExpensesInBaseCurrency;
    }

    public static BigDecimal getSum(Context context){
        return getSumInCategories(context, null);
    }

    public static BigDecimal getSumInCategories(Context context, long[] categoryIds){

        BigDecimal sum = new BigDecimal(BigInteger.ZERO);

        List<Expense> expenses = getAllInCategories(context, categoryIds);

        Currency baseCurrency = SharedPreferencesHelper.getBaseCurrency(context);
        boolean allExpensesInBaseCurrency = allInSameCurrency(context, expenses, baseCurrency);

        // Get total sum
        for (Expense expense : expenses){
            sum = allExpensesInBaseCurrency
                    ? sum.add(expense.sum)
                    : sum.add(expense.convertSumToCurrency(baseCurrency));
        }

        return sum;
    }

    public void save(Context context) {
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues cv = new ContentValues();

        cv.put(CREATE_DATE_COLUMN, createDate.getTimeInMillis());
        cv.put(EXPENSE_DATE_COLUMN, expenseDate.getTimeInMillis());
        cv.put(CATEGORY_COLUMN, category.id);
        cv.put(SUM_COLUMN, sum.doubleValue());
        cv.put(CURRENCY_COLUMN, currency.id);
        cv.put(NOTE_COLUMN, note);

        if(id == 0){    // new category
            id = db.insert(TABLE, null, cv);

            // Update cached data
            // TODO: sort by date
            expenses.add(0, this);
        } else {        // existing category
            db.update(TABLE, cv, ID_COLUMN + " = ?", new String[] {String.valueOf(id)});

            // Update cached data
            if(expenses.contains(this)) {
                int currentPosition = expenses.indexOf(this);
                expenses.remove(currentPosition);
                expenses.add(currentPosition, this);
            }
        }

        DatabaseManager.getInstance().closeDatabase();
    }

    public void remove(Context context){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(TABLE, ID_COLUMN + " = ?", new String[] { String.valueOf(id) });
        DatabaseManager.getInstance().closeDatabase();

        // Update cached data
        expenses.remove(this);
    }

    @Override
    public Class getActivityClass() {
        return SaveExpenseActivity.class;
    }

    public String getOriginalSumString(){
        return Helpers.sumToString(sum, currency);
    }

    public BigDecimal convertSumToCurrency(Currency other){
        if(currency.equals(other)){
            return sum;
        } else {
            return sum
                    .divide(new BigDecimal(currency.rate), 2, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(other.rate));
        }
    }

    public String getConvertedSumString(Currency other){
        return Helpers.sumToString(convertSumToCurrency(other), other);
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
