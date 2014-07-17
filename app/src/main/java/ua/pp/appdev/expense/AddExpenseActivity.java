package ua.pp.appdev.expense;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import ua.pp.appdev.expense.helpers.CategoryAdapter;

public class AddExpenseActivity extends EditActivity {

    private ArrayAdapter categoryListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_expense);

        ListView categoryList = (ListView)this.findViewById(R.id.categoriesList);

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
        if (data == null) {return;}
        // Get added category
        Category newCategory = (Category) data.getSerializableExtra("new");

        // Add it to ArrayAdapter and set selected
        categoryListAdapter.add(newCategory);
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
