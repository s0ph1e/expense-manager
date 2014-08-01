package ua.pp.appdev.expense.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.models.EditableItem;

/**
 * Created by:
 *    Ilya Antipenko <ilya@antipenko.pp.ua>
 *    Sophia Nepochataya <sophia@nepochataya.pp.ua>
 */
public class CategoryAdapter extends ArrayAdapter<Category> implements EditableItemAdapter{
    private Context context;
    int resource;
    private List<Category> categories;
    private int selected = -1;

    public CategoryAdapter(Context context, int resource, List<Category> categories) {
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
            holder.name = (TextView)row.findViewById(R.id.txtCategoryName);
            holder.color = (TextView)row.findViewById(R.id.txtCategoryColor);
            holder.radio = (RadioButton)row.findViewById(R.id.categoryRadio);

            row.setTag(holder);
        } else {
            holder = (CategoryHolder)row.getTag();
        }

        Category category = categories.get(position);
        String firstLetter = String.valueOf(category.name.charAt(0)).toUpperCase();

        holder.color.setBackgroundColor(category.color);
        holder.color.setText(firstLetter.isEmpty() ? "" : firstLetter);
        holder.name.setText(category.name);
        holder.radio.setTag(position);
        holder.radio.setChecked((selected == -1) ? category.checked : position == selected);

        return row;
    }

    public void setSelected(int position){
        if(position < getCount()){
            selected = position;
            getItem(position).checked = true;
        }
        notifyDataSetChanged();
    }

    public int getPosition(Category category){
        for(int i = 0; i < categories.size(); i++){
            if(categories.get(i).id == category.id)
                return i;
        }
        return -1;
    }

    public List<Category> getCategories(){
        return categories;
    }

    @Override
    public void remove(EditableItem item) {
        super.remove((Category) item);
    }

    static class CategoryHolder {
        TextView color;
        RadioButton radio;
        TextView name;
    }
}