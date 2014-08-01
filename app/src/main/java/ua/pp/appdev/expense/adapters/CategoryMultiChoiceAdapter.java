package ua.pp.appdev.expense.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.models.Category;

public class CategoryMultiChoiceAdapter extends ArrayAdapter<Category> {
    private Context context;
    int resource;
    private List<Category> categories;

    public CategoryMultiChoiceAdapter(Context context, int resource, List<Category> categories) {
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
            holder.name = (TextView)row.findViewById(R.id.txtHistoryCategoryName);
            holder.color = (TextView)row.findViewById(R.id.txtHistoryCategoryColor);
            holder.expensesCount = (TextView)row.findViewById(R.id.txtHistoryCategoryExpensesCount);

            row.setTag(holder);
        } else {
            holder = (CategoryHolder)row.getTag();
        }

        Category category = categories.get(position);
        String firstLetter = String.valueOf(category.name.charAt(0)).toUpperCase();

        holder.color.setBackgroundColor(category.color);
        holder.color.setText(firstLetter.isEmpty() ? "" : firstLetter);
        holder.name.setText(category.name);
        holder.expensesCount.setText("(" + category.getExpensesCount(context) + ")");
        row.setActivated(category.checked);

        return row;
    }

    public int getPosition(Category category){
        if(category != null) {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).id == category.id)
                    return i + 1;
            }
        }
        return -1;
    }

    public List<Category> getCategories(){
        return categories;
    }

    public String[] getCheckedCategoriesIds(){

        List<String> list = new ArrayList<String>();
        for (Category category : categories) {
            if (category.checked) {
                list.add(String.valueOf(category.id));
            }
        }
        return list.toArray(new String[list.size()]);
    }

    static class CategoryHolder {
        TextView color;
        TextView name;
        TextView expensesCount;
    }
}