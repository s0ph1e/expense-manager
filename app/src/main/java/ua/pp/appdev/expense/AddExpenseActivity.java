package ua.pp.appdev.expense;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.helpers.CategoryAdapter;

public class AddExpenseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        ListView categoryList = (ListView)this.findViewById(R.id.categoriesList);

        // Create button for new category and put it to the end of listview
        final Button btnAddNew = new Button(this);
        /*btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/
        btnAddNew.setText(R.string.add_new_category);

        // Note: When first introduced, this method could only be called before setting
        // the adapter with setAdapter(ListAdapter).
        // Starting with KITKAT, this method may be called at any time.
        categoryList.addFooterView(btnAddNew);

        // Get array of categories and set adapter
        Category categories[] = Category.getAll(this);
        categoryList.setAdapter(new CategoryAdapter(this, R.layout.listview_category_row, categories));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_expense, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
