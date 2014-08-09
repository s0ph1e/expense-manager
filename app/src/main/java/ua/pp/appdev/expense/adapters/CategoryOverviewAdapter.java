package ua.pp.appdev.expense.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.models.Category;

public class CategoryOverviewAdapter extends CategoryBaseSingleChoiceAdapter{

    private static final String LOG_TAG = "CategoryOverviewAdapter";

    public CategoryOverviewAdapter(Context context, int resource, List<Category> categories) {
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
            holder.color = (TextView)row.findViewById(R.id.txtCategoryColor);
            holder.radio = (RadioButton)row.findViewById(R.id.categoryRadio);

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
            holder.radio.setTag(position);
            holder.radio.setChecked((selected == -1) ? category.checked : position == selected);
        } else {
            holder.color.setBackgroundColor(context.getResources().getColor(android.R.color.black));
            holder.color.setText("¿");
            holder.name.setText("NULL CATEGORY");
            holder.radio.setChecked(false);
            Log.wtf(LOG_TAG, "Null category!");
        }

        return row;
    }

    static class CategoryHolder {
        TextView color;
        RadioButton radio;
        TextView name;
    }
}