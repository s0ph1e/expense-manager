package ua.pp.appdev.expense.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.math.BigDecimal;
import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.helpers.Helpers;
import ua.pp.appdev.expense.helpers.SharedPreferencesHelper;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.utils.Log;

public class CategoryOverviewAdapter extends CategoryBaseSingleChoiceAdapter{

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
            holder.name = (TextView)row.findViewById(R.id.txtOverviewCategoryName);
            holder.color = (TextView)row.findViewById(R.id.txtOverviewCategoryColor);
            holder.sum = (TextView)row.findViewById(R.id.txtOverviewCategorySum);
            holder.expensesCount = (TextView)row.findViewById(R.id.txtOverviewCategoryExpensesCount);

            row.setTag(holder);
        } else {
            holder = (CategoryHolder)row.getTag();
        }

        Category category = categories.get(position);

        if(category != null) {
            int expensesCount = category.getExpensesCount(context);
            BigDecimal expensesSum = category.getExpensesSum(context);

            String firstLetter = String.valueOf(category.name.charAt(0)).toUpperCase();
            String expensesSumString = Helpers.sumToString(expensesSum, SharedPreferencesHelper.getBaseCurrency(context));

            holder.color.setBackgroundColor(category.color);
            holder.color.setText(firstLetter.isEmpty() ? "" : firstLetter);
            holder.name.setText(category.name);
            holder.sum.setText(expensesSumString);
            holder.expensesCount.setText("amount of expenses: " + expensesCount);
        } else {
            holder.color.setBackgroundColor(context.getResources().getColor(android.R.color.black));
            holder.color.setText("¿");
            holder.name.setText("NULL CATEGORY");
            Log.wtf("Null category!");
        }

        return row;
    }

    static class CategoryHolder {
        TextView color;
        TextView name;
        TextView sum;
        TextView expensesCount;
    }
}