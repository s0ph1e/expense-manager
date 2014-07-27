package ua.pp.appdev.expense.adapters;

import ua.pp.appdev.expense.models.EditableItem;

public interface EditableItemAdapter {
    public EditableItem getItem(int position);
    public void remove(EditableItem item);
}
