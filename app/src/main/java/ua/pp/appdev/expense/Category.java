package ua.pp.appdev.expense;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import ua.pp.appdev.expense.helpers.DBHelper;

/**
 * Created by:
 *    Ilya Antipenko <ilya@antipenko.pp.ua>
 *    Sophia Nepochataya <sophia@nepochataya.pp.ua>
 */
public class Category {
    public int id;
    public String name;
    public int color;
    public boolean checked;

    public Category(int id, String name, int color, boolean checked) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.checked = checked;
    }

    public Category(int id, String name, int color) {
        this(id, name, color, false);
    }

    public static Category[] getAll(Context context) {

        List<Category> catList = new ArrayList<Category>();

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor c = db.query(DBHelper.CATEGORIES_TABLE, null, null, null, null, null, null);

        if (c.moveToFirst()) {

            // Get column indexes
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int colorColIndex = c.getColumnIndex("color");

            do {
                catList.add(new Category(
                        c.getInt(idColIndex),
                        c.getString(nameColIndex),
                        c.getInt(colorColIndex)));

            } while (c.moveToNext());
        }

        Category[] catArray = catList.toArray(new Category[catList.size()]);
        return catArray;
    }
}
