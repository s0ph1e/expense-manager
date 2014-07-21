package ua.pp.appdev.expense;


import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import ua.pp.appdev.expense.helpers.CurrencyAdapter;
import ua.pp.appdev.expense.helpers.DecimalDigitsInputFilter;

public class SaveExpenseActivity extends EditActivity implements CategoryListFragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_save_expense);

        EditText etSum = (EditText)findViewById(R.id.etxtSum);
        // Добавляем фильтр на количество цифр после запятой & remove focus after done
        etSum.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
        etSum.setOnEditorActionListener(lostFocusAfterDone);

        // Remove focus after actionDone from editText for Note
        EditText etNote = (EditText) findViewById(R.id.etxtNote);
        etNote.setOnEditorActionListener(lostFocusAfterDone);

        // Add currency spinner
        Spinner spinnerCurrency = (Spinner)findViewById(R.id.spinnerCurrency);
        CurrencyAdapter adapter = new CurrencyAdapter(this, R.layout.spinner_currency_row);
        spinnerCurrency.setAdapter(adapter);

        // Add category list
        CategoryListFragment categoryListFragment = new CategoryListFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        //((FrameLayout)findViewById(R.id.categoryListContainer)).removeAllViews();
        //fragmentTransaction.remove(categoryListFragment);
        Fragment prev = getFragmentManager().findFragmentById(R.id.categoryListContainer);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.add(R.id.categoryListContainer, categoryListFragment);
        fragmentTransaction.commit();

        // Add datipicker dialog on btnPickDate click
        Button btnPickDate = (Button) findViewById(R.id.btnPickDate);
        int mYear, mMonth, mDay;
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // DialogFragment.show() will take care of adding the fragment
                // in a transaction.  We also want to remove any currently showing
                // dialog, so make our own transaction and take care of that here.
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                Fragment prev = getFragmentManager().findFragmentByTag("datePicker");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DatePickerDialogFragment newFragment = new DatePickerDialogFragment();
                newFragment.show(ft, "datePicker");
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
