package ua.pp.appdev.expense.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.helpers.Helpers;
import ua.pp.appdev.expense.helpers.SharedPreferencesHelper;
import ua.pp.appdev.expense.models.Currency;
import ua.pp.appdev.expense.models.EditableItem;
import ua.pp.appdev.expense.models.Expense;
import ua.pp.appdev.expense.utils.Log;

public class ExpenseAdapter extends ArrayAdapter<Expense> implements EditableItemAdapter{

    private Context context;
    int resource;
    private List<Expense> expenses;
    private int selected = -1;

    public ExpenseAdapter(Context context, int resource, List<Expense> expenses) {
        super(context, resource, expenses);
        this.context = context;
        this.resource = resource;
        this.expenses = expenses;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ExpenseHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(this.resource, parent, false);

            holder = new ExpenseHolder();
            holder.color = row.findViewById(R.id.vExpenseCategoryColor);
            holder.category = (TextView)row.findViewById(R.id.txtExpenseCategoryName);
            holder.date = (TextView) row.findViewById(R.id.txtExpenseDate);
            holder.note = (TextView) row.findViewById(R.id.txtExpenseNote);
            holder.sumInOriginalCurrency = (TextView) row.findViewById(R.id.txtExpenseSumOriginal);
            holder.sumInBaseCurrency = (TextView) row.findViewById(R.id.txtExpenseSumBase);

            row.setTag(holder);
        } else {
            holder = (ExpenseHolder) row.getTag();
        }

        Expense expense = expenses.get(position);

        // Show category is it is not null (if null - black rectangle with question mark)
        if(expense.category != null) {
            holder.color.setBackgroundColor(expense.category.color);
            holder.category.setText(expense.category.name);
        } else {
            holder.color.setBackgroundColor(context.getResources().getColor(android.R.color.black));
            holder.category.setText("¿");
            Log.wtf("Null category!");
        }

        // Show date of expense
        holder.date.setText(Helpers.calendarToDateTimeString(context, expense.expenseDate));

        // Get base currency
        Currency base = SharedPreferencesHelper.getBaseCurrency(context);
        holder.sumInBaseCurrency.setText(expense.getConvertedSumString(base));

        // Show original sum if it is not in base currency
        if(!base.equals(expense.currency)) {
            holder.sumInOriginalCurrency.setVisibility(View.VISIBLE);
            holder.sumInOriginalCurrency.setText(expense.getOriginalSumString());
        } else {
            holder.sumInOriginalCurrency.setVisibility(View.GONE);
        }

        // Show note if it exists
        if (!expense.note.isEmpty() ) {
            holder.note.setText(expense.note);
            holder.note.setVisibility(View.VISIBLE);
        } else {
            holder.note.setVisibility(View.GONE);
        }

        return row;
    }

    @Override
    public int getRemoveDialogTitle() {
        return R.string.remove_expenses;
    }

    @Override
    public View getRemoveDialogView(SparseBooleanArray checked) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.view_dialog_remove_expenses, null, false);
    }

    @Override
    public void remove(EditableItem item) {
        super.remove((Expense) item);
        item.remove(context);
    }

    public int getPosition(Expense expense){
        for(int i = 0; i < expenses.size(); i++){
            if(expenses.get(i).id == expense.id)
                return i;
        }
        return -1;
    }

    public List<Expense> getExpenses(){
        return expenses;
    }

    static class ExpenseHolder{

        View color;
        TextView category;
        TextView date;
        TextView note;
        TextView sumInOriginalCurrency;
        TextView sumInBaseCurrency;

    }
}
