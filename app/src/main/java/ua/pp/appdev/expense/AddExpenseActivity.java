package ua.pp.appdev.expense;


import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import ua.pp.appdev.expense.helpers.CategoryAdapter;

public class AddExpenseActivity extends EditActivity {
    private CategoryAdapter categoryListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_expense);

        final ListView categoryList = (ListView)this.findViewById(R.id.categoriesList);

        categoryList.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        categoryList.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
               // View row = categoryList.getChildAt(i);
                //row.setBackgroundColor((b) ? getResources().getColor(android.R.color.holo_blue_light)
                 //       : getResources().getColor(android.R.color.transparent));
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                // Respond to clicks on the actions in the CAB
                switch (item.getItemId()) {
                    case R.id.actionbar_edit:
                        mode.finish(); // Action picked, so close the CAB
                        return true;
                    case R.id.actionbar_remove:
                        mode.finish(); // Action picked, so close the CAB
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
                // Here you can perform updates to the CAB due to
                // an invalidate() request
                return false;
            }
        });

        // Create button for new category and put it to the end of listview
        final Button btnAddNew = new Button(this);
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), AddCategoryActivity.class);
                startActivityForResult(i, 0);
            }
        });
        btnAddNew.setText(R.string.add_category);

        // Note: When first introduced, this method could only be called before setting
        // the adapter with setAdapter(ListAdapter).
        // Starting with KITKAT, this method may be called at any time.
        categoryList.addFooterView(btnAddNew);

        // Get array of categories and set adapter
        List<Category> categories = Category.getAll(this);
        categoryListAdapter = new CategoryAdapter(this, R.layout.listview_category_row, categories);
        categoryList.setAdapter(categoryListAdapter);

        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                categoryListAdapter.setSelected(i);
            }
        });

    }

    @Override
    protected void onSave(View v) {
        finish();
    }

    @Override
    protected void onCancel(View v) {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null || resultCode != RESULT_OK) {return;}
        // Get added category
        Category newCategory = (Category) data.getSerializableExtra("new");

        // Add it to ArrayAdapter and set selected
        categoryListAdapter.add(newCategory);
        categoryListAdapter.setSelected(categoryListAdapter.getCount() - 1);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_category, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
