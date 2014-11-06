package ua.pp.appdev.expense.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Parcel;
import android.os.Parcelable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import ua.pp.appdev.expense.activities.SaveCategoryActivity;
import ua.pp.appdev.expense.helpers.DatabaseManager;
import ua.pp.appdev.expense.helpers.Helpers;
import ua.pp.appdev.expense.utils.Log;

/**
 * Created by:
 *    Ilya Antipenko <ilya@antipenko.pp.ua>
 *    Sophia Nepochataya <sophia@nepochataya.pp.ua>
 */
public class Category implements EditableItem, Parcelable {

    public static final String TABLE = "categories";
    public static final String ID_COLUMN = "id";
    public static final String NAME_COLUMN = "name";
    public static final String COLOR_COLUMN = "color";

    public long id = 0;
    public String name = "";
    public int color = 0;
    public boolean checked = false;

    private static List<Category> categories = null;

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

    private static List<Category> fetchAll() {
        List<Category> catList = new ArrayList<Category>();
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        Cursor c = db.rawQuery("select *" + " from " + TABLE, new String[]{});
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

    public static List<Category> getAll(Context context) {
        if(categories == null) {
            categories = fetchAll();
        }
        return new ArrayList<Category>(categories);
    }

    // TODO: do the same with Predicates
    public static List<Category> getAllExcept(Context context, long[] exceptCategoriesIds){

        if (exceptCategoriesIds == null || exceptCategoriesIds.length == 0) {
            return getAll(context);
        }

        List<Category> allCategories = getAll(context);
        List<Category> filtered = new ArrayList<Category>();

        for(Category cat : allCategories) {
            boolean inResult = true;
            for (long exceptId : exceptCategoriesIds) {
                if (cat.id == exceptId) {
                    inResult = false;
                    break;
                }
            }

            if (inResult) {
                filtered.add(cat);
            }
        }

        return filtered;
    }

    public void save(Context context){

        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues cv = new ContentValues();

        cv.put(NAME_COLUMN, name);
        cv.put(COLOR_COLUMN, color);

        if(id == 0){    // new category
            id = db.insert(TABLE, null, cv);

            // Update cached data
            categories.add(this);
        } else {        // existing category
            db.update(TABLE, cv, ID_COLUMN + " = ?", new String[] {String.valueOf(id)});

            // Update cached data
            if(categories.contains(this)) {
                int currentPosition = categories.indexOf(this);
                categories.remove(currentPosition);
                categories.add(currentPosition, this);
            }

        }

        DatabaseManager.getInstance().closeDatabase();
    }

    public void remove(Context context){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(TABLE, ID_COLUMN + " = ?", new String[] { String.valueOf(id) });
        DatabaseManager.getInstance().closeDatabase();

        // Update cached data
        categories.remove(this);
    }

    public void removeExpenses(){
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();
        db.delete(Expense.TABLE, Expense.CATEGORY_COLUMN + " = ?", new String[] { String.valueOf(id) });
        DatabaseManager.getInstance().closeDatabase();
    }

    public void moveExpensesTo(Context context, long otherCategoryId){
        // Check if specified category exists
        if(Category.getById(context, otherCategoryId) == null){
            Log.e("Category " + otherCategoryId + " doesn't exist!");
            return;
        }
        SQLiteDatabase db = DatabaseManager.getInstance().openDatabase();

        ContentValues cv = new ContentValues();
        cv.put(Expense.CATEGORY_COLUMN, otherCategoryId);

        db.update(Expense.TABLE, cv, Expense.CATEGORY_COLUMN + " = ?", new String[] { String.valueOf(id) });
        DatabaseManager.getInstance().closeDatabase();
    }

    @Override
    public Class getActivityClass() {
        return SaveCategoryActivity.class;
    }

    public static Category getById(Context context, long id){

        List<Category> allCategories = getAll(context);
        Category cat = null;

        for (Category currentCat : allCategories) {
            if (currentCat.id == id) {
                cat = currentCat;
                break;
            }
        }

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
        return Expense.getSumInCategories(context, new long[]{id});
    }

    @Override
    public boolean equals(Object o) {
        // Check class of object
        if (o instanceof Category) {
            Category other = (Category) o;
            if (other.id == this.id) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return "{id: " + id + ", name: " + name + ", color: " + Helpers.colorToString(color) + "}";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeInt(color);
        parcel.writeByte((byte) (checked ? 1 : 0));
    }

    public static final Parcelable.Creator<Category> CREATOR
            = new Parcelable.Creator<Category>() {
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    private Category(Parcel in) {
        id = in.readLong();
        name = in.readString();
        color = in.readInt();
        checked = in.readByte() != 0;
    }
}
