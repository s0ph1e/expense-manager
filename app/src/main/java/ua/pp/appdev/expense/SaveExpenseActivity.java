package ua.pp.appdev.expense;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import ua.pp.appdev.expense.helpers.DecimalDigitsInputFilter;

public class SaveExpenseActivity extends EditActivity implements CategoryListFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_expense);

        EditText etSum = (EditText)findViewById(R.id.etxtSum);
        // Добавляем фильтр на количество цифр после запятой
        etSum.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});

        Spinner spinnerCurrency = (Spinner)findViewById(R.id.spinnerCurrency);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>((Activity)this, android.R.layout.simple_spinner_dropdown_item, new String[]{"test", "1232134234"});
        spinnerCurrency.setAdapter(adapter);

        CategoryListFragment categoryListFragment = new CategoryListFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.categoryListContainer, categoryListFragment);
        fragmentTransaction.commit();
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
