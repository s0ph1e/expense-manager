package ua.pp.appdev.expense.helpers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import ua.pp.appdev.expense.Category;
import ua.pp.appdev.expense.R;

/**
 * Created by:
 *    Ilya Antipenko <ilya@antipenko.pp.ua>
 *    Sophia Nepochataya <sophia@nepochataya.pp.ua>
 */
public class CategoryAdapter extends ArrayAdapter<Category> {
    private Context context;
    int resource;
    private Category[] categories;
    private RadioButton mSelectedRB;
    private int mSelectedPosition = -1;

    public CategoryAdapter(Context context, int resource, Category[] categories) {
        super(context, resource, categories);
        this.context = context;
        this.resource = resource;
        this.categories = categories;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        CategoryHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(this.resource, parent, false);

            holder = new CategoryHolder();
            holder.name = (TextView)row.findViewById(R.id.categoryName);
            holder.color = (ImageView)row.findViewById(R.id.categoryColor);
            holder.radio = (RadioButton)row.findViewById(R.id.categoryRadio);

            row.setTag(holder);
        } else {
            holder = (CategoryHolder)row.getTag();
        }

        holder.radio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position != mSelectedPosition && mSelectedRB != null) {
                    mSelectedRB.setChecked(false);
                }

                mSelectedPosition = position;
                mSelectedRB = (RadioButton) view;
            }
        });

        if(mSelectedPosition != position){
            holder.radio.setChecked(false);
        }else{
            holder.radio.setChecked(true);
            if(mSelectedRB != null && holder.radio!= mSelectedRB){
                mSelectedRB = holder.radio;
            }
        }

        Category category = categories[position];
        holder.color.setBackgroundColor(category.color);
        holder.name.setText(category.name);
        holder.radio.setChecked(category.checked);

        return row;
    }

    static class CategoryHolder {
        ImageView color;
        RadioButton radio;
        TextView name;
    }
}