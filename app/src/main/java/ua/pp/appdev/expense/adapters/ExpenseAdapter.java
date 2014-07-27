package ua.pp.appdev.expense.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

import ua.pp.appdev.expense.models.EditableItem;
import ua.pp.appdev.expense.models.Expense;

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
    public void remove(EditableItem item) {
        super.remove((Expense) item);
    }
}
