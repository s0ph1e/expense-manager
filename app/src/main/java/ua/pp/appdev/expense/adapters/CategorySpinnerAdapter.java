package ua.pp.appdev.expense.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.utils.Log;

public class CategorySpinnerAdapter extends CategoryBaseSingleChoiceAdapter {

    public CategorySpinnerAdapter(Context context, int resource, List<Category> categories) {
        super(context, resource, categories);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CategoryHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(this.resource, parent, false);

            holder = new CategoryHolder();
            holder.name = (TextView)row.findViewById(R.id.txtCategoryName);

            row.setTag(holder);
        } else {
            holder = (CategoryHolder)row.getTag();
        }

        Category category = categories.get(position);
        if(category != null) {
            holder.name.setBackgroundColor(category.color);
            holder.name.setText(category.name);
        } else {
            holder.name.setBackgroundColor(context.getResources().getColor(android.R.color.black));
            holder.name.setText("NULL CATEGORY");
            Log.wtf("Null category!");
        }
        return row;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View row = getView(position, convertView, parent);
        return row;
    }

    static class CategoryHolder {
        TextView name;
    }
}