package ua.pp.appdev.expense.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.utils.Log;

public class CategoryMultiChoiceAdapter extends CategoryBaseAdapter {

    public boolean[] checked;

    public CategoryMultiChoiceAdapter(Context context, int resource, List<Category> categories) {
        super(context, resource, categories);
        checked = new boolean[categories.size()];
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CategoryHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(this.resource, parent, false);

            holder = new CategoryHolder();
            holder.name = (TextView)row.findViewById(R.id.txtHistoryCategoryName);
            holder.color = (TextView)row.findViewById(R.id.txtHistoryCategoryColor);
            holder.expensesCount = (TextView)row.findViewById(R.id.txtHistoryCategoryExpensesCount);
            row.setTag(holder);
        } else {
            holder = (CategoryHolder)row.getTag();
        }

        Category category = categories.get(position);
        if(category != null) {
            String firstLetter = String.valueOf(category.name.charAt(0)).toUpperCase();

            holder.color.setBackgroundColor(category.color);
            holder.color.setText(firstLetter.isEmpty() ? "" : firstLetter);
            holder.name.setText(category.name);
            holder.expensesCount.setText("(" + category.getExpensesCount(context) + ")");
            row.setActivated(checked[position]);
        } else {
            holder.color.setBackgroundColor(context.getResources().getColor(android.R.color.black));
            holder.color.setText("¿");
            holder.name.setText("NULL CATEGORY");
            holder.expensesCount.setText("(?)");
            Log.wtf("Null category!");
        }

        return row;
    }

    @Override
    public int getPosition(Category category) {
        // +1 because we have header 'all categories'
        return super.getPosition(category) + 1;
    }

    public long[] getCheckedCategoriesIds(){

        List<Long> list = new ArrayList<Long>();
        for (int i = 0; i < categories.size(); i++) {
            if (checked[i]) {
                list.add(categories.get(i).id);
            }
        }

        long[] checkedArray = new long[list.size()];
        for (int i = 0; i < checkedArray.length; i++) {
            checkedArray[i] = list.get(i);
        }

        return checkedArray;
    }

    static class CategoryHolder {
        TextView color;
        TextView name;
        TextView expensesCount;
    }
}