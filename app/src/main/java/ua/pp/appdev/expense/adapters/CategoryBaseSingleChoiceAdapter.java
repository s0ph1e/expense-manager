package ua.pp.appdev.expense.adapters;

import android.content.Context;

import java.util.List;

import ua.pp.appdev.expense.models.Category;

public class CategoryBaseSingleChoiceAdapter extends CategoryBaseAdapter {

    protected int selected = -1;

    public CategoryBaseSingleChoiceAdapter(Context context, int resource, List<Category> categories) {
        super(context, resource, categories);
    }

    public void setSelected(int position){
        int categoriesCount = getCount();
        if(position < categoriesCount){
            // Set category at position selected
            selected = position;
            getItem(position).checked = true;
            // Set other categories deselected
            for(int i = 0; i < categoriesCount; i++){
                if(i != position){
                    categories.get(i).checked = false;
                }
            }
        }
        notifyDataSetChanged();
    }

    public Category getSelectedItem(){
        if(selected >= 0 && selected < categories.size()){
            return categories.get(selected);
        } else {
            return null;
        }
    }
}
