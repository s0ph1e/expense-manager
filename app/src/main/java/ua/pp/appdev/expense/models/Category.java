package ua.pp.appdev.expense.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ua.pp.appdev.expense.activities.SaveCategoryActivity;
import ua.pp.appdev.expense.helpers.DatabaseManager;
import ua.pp.appdev.expense.helpers.Helpers;

/**
 * Created by:
 *    Ilya Antipenko <ilya@antipenko.pp.ua>
 *    Sophia Nepochataya <sophia@nepochataya.pp.ua>
 */
public class Category implements EditableItem {

    public static final String TABLE = "categories";
    public static final String ID_COLUMN = "id";
    public static final String NAME_COLUMN = "name";
    public static final String COLOR_COLUMN = "color";

    public long id = 0;
    public String name = "";
    public int color = 0;
    public boolean checked = false;

    public Category(long id, String name, int color, boolean checked) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.checked = checked;
    }

    public Category(long id, String name, int color) {
        this(id, name, color, false);
    }

    public Category(){}

    public static List<Category> getAll(Context context) {

        List<Category> catList = new ArrayList<Category>();

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        Cursor c = db.query(TABLE, null, null, null, null, null, null);

        if (c.moveToFirst()) {

            // Get column indexes
            int idColIndex = c.getColumnIndex(ID_COLUMN);
            int nameColIndex = c.getColumnIndex(NAME_COLUMN);
            int colorColIndex = c.getColumnIndex(COLOR_COLUMN);

            do {
                catList.add(new Category(
                        c.getLong(idColIndex),
                        c.getString(nameColIndex),
                        c.getInt(colorColIndex)));

            } while (c.moveToNext());
        }
        c.close();
        DatabaseManager.getInstance().closeDatabase();

        return catList;
    }

    public void save(Context context){

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues cv = new ContentValues();

        cv.put(NAME_COLUMN, name);
        cv.put(COLOR_COLUMN, color);

        if(id == 0){    // new category
            id = db.insert(TABLE, null, cv);
        } else {        // existing category
            db.update(TABLE, cv, ID_COLUMN + " = ?", new String[] {String.valueOf(id)});
        }

        DatabaseManager.getInstance().closeDatabase();
    }

    // TODO: suggest to move expenses to other category
    public void remove(Context context){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        // Remove expenses in category
        db.delete(Expense.TABLE, Expense.CATEGORY_COLUMN + " = ?", new String[] { String.valueOf(id) });
        // Remove category
        db.delete(TABLE, ID_COLUMN + " = ?", new String[] { String.valueOf(id) });

        DatabaseManager.getInstance().closeDatabase();
    }

    @Override
    public Class getActivityClass() {
        return SaveCategoryActivity.class;
    }

    public static Category getById(Context context, long id){

        Category cat = null;

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        Cursor c = db.query(TABLE, null, ID_COLUMN + " = ?", new String[] {String.valueOf(id)}, null, null, null);

        if (c.moveToFirst()) {

            // Get column indexes
            int idColIndex = c.getColumnIndex(ID_COLUMN);
            int nameColIndex = c.getColumnIndex(NAME_COLUMN);
            int colorColIndex = c.getColumnIndex(COLOR_COLUMN);

            cat =  new Category(
                    c.getLong(idColIndex),
                    c.getString(nameColIndex),
                    c.getInt(colorColIndex)
            );

        }
        c.close();
        DatabaseManager.getInstance().closeDatabase();

        return cat;
    }

    public int getExpensesCount(Context context){
        return Expense.getCountInCategory(context, id);
    }

    /**
     * Get sum of expenses in category converted to base currency
     * @param context
     * @return
     */
    public BigDecimal getExpensesSum(Context context){
        return Expense.getSumInCategories(context, new String[]{String.valueOf(id)});
    }

    @Override
    public boolean equals(Object o) {
        // Check class of object
        if (o instanceof Category) {
            Category other = (Category) o;
            if (other.id == this.id
                    && other.color == this.color
                    && other.name.equals(this.name)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "{id: " + id + ", name: " + name + ", color: " + Helpers.colorToString(color) + "}";
    }
}
