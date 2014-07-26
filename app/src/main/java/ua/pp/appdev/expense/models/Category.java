package ua.pp.appdev.expense.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import ua.pp.appdev.expense.helpers.DBHelper;

/**
 * Created by:
 *    Ilya Antipenko <ilya@antipenko.pp.ua>
 *    Sophia Nepochataya <sophia@nepochataya.pp.ua>
 */
public class Category implements Serializable {

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

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

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
        db.close();

        return catList;
    }

    public void save(Context context){

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(NAME_COLUMN, name);
        cv.put(COLOR_COLUMN, color);

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

    public static Category getById(Context context, long id){

        Category cat = null;

        DBHelper dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

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
        db.close();

        return cat;
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
}
