package ua.pp.appdev.expense.adapters;

import android.util.SparseBooleanArray;
import android.view.View;

import ua.pp.appdev.expense.models.EditableItem;

public interface EditableItemAdapter {
    public EditableItem getItem(int position);
    public int getRemoveDialogTitle();
    public View getRemoveDialogView(SparseBooleanArray checked);
    public void remove(EditableItem item);
}
