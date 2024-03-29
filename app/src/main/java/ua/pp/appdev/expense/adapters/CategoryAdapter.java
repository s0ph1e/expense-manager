package ua.pp.appdev.expense.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.models.EditableItem;
import ua.pp.appdev.expense.models.Expense;
import ua.pp.appdev.expense.utils.Log;

/**
 * Created by:
 *    Ilya Antipenko <ilya@antipenko.pp.ua>
 *    Sophia Nepochataya <sophia@nepochataya.pp.ua>
 */
public class CategoryAdapter extends CategoryBaseSingleChoiceAdapter implements EditableItemAdapter{

    private boolean needToMoveExpensesOnRemove = false;
    private long categoryIdToMoveExpenses = 0;

    public CategoryAdapter(Context context, int resource, List<Category> categories) {
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
            holder.radio.setChecked(position == selected);
        } else {
            holder.color.setBackgroundColor(context.getResources().getColor(android.R.color.black));
            holder.color.setText("¿");
            holder.name.setText("NULL CATEGORY");
            holder.radio.setChecked(false);
            Log.wtf("Null category!");
        }

        return row;
    }

    @Override
    public int getRemoveDialogTitle() {
        return R.string.remove_categories;
    }

    @Override
    public View getRemoveDialogView(SparseBooleanArray checked) {
        // Inflate view
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.view_dialog_remove_categories, null, false);

        // Get all checked categories
        List<Category> selectedCategories = new ArrayList<Category>();
        for (int i = 0, len = getCount(); i < len; i++) {
            if (checked.get(i)) {
                Category cat = getItem(i);
                if(cat != null){
                    selectedCategories.add(cat);
                }
            }
        }

        // Check expenses count in selected categories
        long[] selectedCategoriesIds = new long[selectedCategories.size()];
        for(int i = 0; i < selectedCategoriesIds.length; i++){
            selectedCategoriesIds[i] = selectedCategories.get(i).id;
        }
        int expensesCount = Expense.getCountInCategories(context, selectedCategoriesIds);

        // If selected categories contain at least 1 expense - show actions for expenses
        if(expensesCount > 0){
            // Get animations for spinner fadein and fadeout
            final Animation animFadeIn = AnimationUtils.loadAnimation(context.getApplicationContext(), android.R.anim.fade_in);
            final Animation animFadeOut = AnimationUtils.loadAnimation(context.getApplicationContext(), android.R.anim.fade_out);

            // Set categories spinner
            final Spinner expenseActionSpinner = (Spinner) view.findViewById(R.id.spinnerRemoveCategoryExpenses);
            List<Category> categoriesToMoveExpenses = Category.getAllExcept(context, selectedCategoriesIds);
            CategorySpinnerAdapter adapter = new CategorySpinnerAdapter(context, R.layout.spinner_category_row, categoriesToMoveExpenses);
            expenseActionSpinner.setAdapter(adapter);
            expenseActionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    Category cat = (Category) adapterView.getAdapter().getItem(i);
                    categoryIdToMoveExpenses = cat.id;
                    Log.i("need to move = " + needToMoveExpensesOnRemove + " catId = " + categoryIdToMoveExpenses);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            // Set radiobuttons
            RadioGroup rgAction = (RadioGroup) view.findViewById(R.id.rgCategoryRemoveExpenseAction);
            // Hide move radiobutton if no available categories to move expenses
            if(categoriesToMoveExpenses == null || categoriesToMoveExpenses.size() == 0){
                rgAction.findViewById(R.id.rbMoveExpenses).setVisibility(View.GONE);
            }
            // Change expenses action listener
            rgAction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int i) {
                    switch (i){
                        case R.id.rbRemoveExpenses:
                            needToMoveExpensesOnRemove = false;
                            expenseActionSpinner.setAnimation(animFadeOut);
                            expenseActionSpinner.setVisibility(View.GONE);
                            break;
                        case R.id.rbMoveExpenses:
                            needToMoveExpensesOnRemove = true;
                            expenseActionSpinner.setAnimation(animFadeIn);
                            expenseActionSpinner.setVisibility(View.VISIBLE);
                            break;
                    }
                    Log.i("need to move = " + needToMoveExpensesOnRemove + " catId = " + categoryIdToMoveExpenses);
                }
            });
            // Set view with additional actions visible
            view.findViewById(R.id.removeCategoryIsNotEmpty).setVisibility(View.VISIBLE);
        }

        return view;
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
            // Remove item from adapter
            super.remove((Category) item);
            // Move or remove expenses from category
            if(needToMoveExpensesOnRemove){
                ((Category) item).moveExpenses(context, categoryIdToMoveExpenses);
            } else {
                ((Category) item).removeExpenses();
            }
            // Remove category from db
            item.remove(context);

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
        RadioButton radio;
        TextView name;
    }
}