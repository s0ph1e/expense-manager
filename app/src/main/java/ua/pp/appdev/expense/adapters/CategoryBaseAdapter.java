package ua.pp.appdev.expense.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import ua.pp.appdev.expense.models.Category;

public class CategoryBaseAdapter extends ArrayAdapter<Category> {

    protected Context context;

    protected int resource;

    protected List<Category> categories;

    public CategoryBaseAdapter(Context context, int resource, List<Category> categories) {
        super(context, resource, categories);
        this.context = context;
        this.resource = resource;
        this.categories = categories;
    }

    public int getPosition(Category category){
        if(category != null) {
            for (int i = 0; i < categories.size(); i++) {
                if (categories.get(i).id == category.id)
                    return i;
            }
        }
        return -1;
    }

    public List<Category> getCategories(){
        return categories;
    }

}
