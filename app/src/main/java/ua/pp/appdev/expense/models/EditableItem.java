package ua.pp.appdev.expense.models;

import android.content.Context;

import java.io.Serializable;

public interface EditableItem extends Serializable {

    public void save(Context context);
    public void remove(Context context);
    public Class getActivityClass();
}
