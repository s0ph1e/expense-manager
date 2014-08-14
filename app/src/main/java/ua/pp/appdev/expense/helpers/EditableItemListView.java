package ua.pp.appdev.expense.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.adapters.EditableItemAdapter;
import ua.pp.appdev.expense.models.EditableItem;
import ua.pp.appdev.expense.utils.Log;

/**
 * Magic for preventing listviews' resize when keyboard appears
 */
public class EditableItemListView extends ListView {

    public static final int ADD = 0;
    public static final int EDIT = 1;
    private int minRowsCount = 2;
    private OnEditableListViewChangedListener mListener;
    protected EditableItemAdapter adapter;

    public EditableItemListView(final Context context) {
        super(context);

        // Set scrollbar always shown
        setScrollbarFadingEnabled(false);

        // Create contextual action mode (edit-remove categories)
        setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                actionMode.invalidate();
            }

            @Override
            public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.actionbar_edit:
                        editSelected();
                        mode.finish();
                        return true;
                    case R.id.actionbar_remove:
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.remove_item)
                                .setMessage(R.string.remove_item_message)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        removeSelected();
                                        mode.finish();
                                    }
                                })
                                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .show();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                // Inflate the menu for the CAB
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(R.menu.context, menu);
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                // Here you can make any necessary updates to the activity when
                // the CAB is removed. By default, selected items are deselected/unchecked.
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                final int checked = getCheckedItemCount();
                MenuItem editBtn = menu.findItem(R.id.actionbar_edit);
                boolean singleItemChecked = (checked == 1);
                editBtn.setVisible(singleItemChecked).setEnabled(singleItemChecked);
                return true;
            }

            public void removeSelected() {
                SparseBooleanArray checked = getCheckedItemPositions();
                int len = getCount();
                // Needed DESC order, otherwise unchecked items may be deleted instead of checked
                for (int i = len - 1; i >= 0; i--) {
                    if (checked.get(i)) {
                        EditableItemAdapter adapter = getListViewAdapter();
                        EditableItem item = adapter.getItem(i);
                        item.remove(context);
                        adapter.remove(item);
                    }
                }
                if(mListener != null) {
                    mListener.onEditableListViewChanged();
                }
            }

            public void editSelected() {
                SparseBooleanArray checked = getCheckedItemPositions();
                int checkedCount = getCheckedItemCount();
                if (checkedCount != 1) {
                    Log.wtf("Got multiple items, but only one can be edited");
                } else {
                    int key = checked.keyAt(0);
                    EditableItemAdapter adapter = getListViewAdapter();
                    EditableItem item = adapter.getItem(key);
                    Intent i = new Intent(context, item.getActivityClass());
                    i.putExtra("item", item);
                    ((Activity) context).startActivityForResult(i, EDIT);
                }
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        int minHeight = minRowsCount * getRowHeight();
        if(getHeight() < minHeight){
            Log.e(getHeight() + " < " + minHeight);
            getLayoutParams().height = minHeight;
            requestLayout();
        } else {
            getLayoutParams().height = getHeight();
            requestLayout();
        }
    }

    public int getRowHeight(){
        ListAdapter mAdapter = getAdapter();

        if (mAdapter != null && mAdapter.getCount() > 0) {
            View mView = mAdapter.getView(0, null, this);
            mView.measure(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            return mView.getMeasuredHeight();
        } else
            return 0;
    }

    public int getTotalHeight() {
        ListAdapter mAdapter = getAdapter();
        int totalHeight = 0;

        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, this);
            mView.measure(
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                    MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            totalHeight += mView.getMeasuredHeight();
            Log.w(String.valueOf(totalHeight));
        }
        return  totalHeight;
    }

    /**
     * handle both listview adapters (simple listview - example: expenses list)
     * and headerview list adapters (if listview has header or footer - example: categories list)
     * @return EditableItemAdapter
     */
    public EditableItemAdapter getListViewAdapter(){
        ListAdapter adapter;
        if(getAdapter() instanceof EditableItemAdapter){
            adapter = getAdapter();
        } else {
            adapter = ((HeaderViewListAdapter) getAdapter()).getWrappedAdapter();
        }
        return (EditableItemAdapter) adapter;
    }

    public interface OnEditableListViewChangedListener{
        public void onEditableListViewChanged();
    }

    public void setOnEditableListViewChangedListener(OnEditableListViewChangedListener eventListener) {
        mListener = eventListener;
    }
}
