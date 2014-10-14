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
import ua.pp.appdev.expense.models.EditableItem;
import ua.pp.appdev.expense.utils.Log;

/**
 * Created by:
 *    Ilya Antipenko <ilya@antipenko.pp.ua>
 *    Sophia Nepochataya <sophia@nepochataya.pp.ua>
 */
public class CategorySettingsAdapter extends CategoryBaseSingleChoiceAdapter implements EditableItemAdapter{

    public CategorySettingsAdapter(Context context, int resource, List<Category> categories) {
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
        } else {
            holder.color.setBackgroundColor(context.getResources().getColor(android.R.color.black));
            holder.color.setText("¿");
            holder.name.setText("NULL CATEGORY");
            Log.wtf("Null category!");
        }

        return row;
    }

    /**
     * When removing item we have to take care about selected item. 3 events may happen:
     * 1) removed item is above selected (selectedPos > removedPos): move selected top by 1 pos
     * 2) removed is selected (selectedPos == removedPos): set selected = -1 (nothing selected)
     * 3) removed item is below selected (selectedPos < removedPos): selected doesn't change
     *
     * @param item - EditableItem for removing
     */
    @Override
    public void remove(EditableItem item) {
        Category cat = (Category) item;
        int removePosition = getPosition(cat);
        if(removePosition >= 0) {
            super.remove((Category) item);
            if(selected > removePosition){
                --selected;
            } else if(selected == removePosition){
                selected = -1;
            }
        }
    }

    @Override
    public void add(Category cat) {
        if(getPosition(cat) < 0){
            super.add(cat);
        }
    }

    static class CategoryHolder {
        TextView color;
        TextView name;
    }
}