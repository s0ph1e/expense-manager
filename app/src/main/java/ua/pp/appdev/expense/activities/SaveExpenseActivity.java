package ua.pp.appdev.expense.activities;


import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.math.BigDecimal;
import java.util.Calendar;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.fragments.CategoryListFragment;
import ua.pp.appdev.expense.fragments.DatePickerDialogFragment;
import ua.pp.appdev.expense.helpers.CurrencyAdapter;
import ua.pp.appdev.expense.helpers.DecimalDigitsInputFilter;
import ua.pp.appdev.expense.helpers.Helpers;
import ua.pp.appdev.expense.models.Expense;

public class SaveExpenseActivity extends EditActivity implements CategoryListFragment.OnFragmentInteractionListener, DatePickerDialogFragment.OnDateTimeSelectedListener {

    private Expense expense;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("SAVE_EXP", "onCreate");

        // Get expense
        Intent callingIntent = getIntent();
        expense = (Expense) callingIntent.getSerializableExtra("expense");

        // If creating action - make new expense
        if(expense == null){
            expense = new Expense();
            //expense.expenseDate.set(2000, Calendar.APRIL, 2);
        }

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
        Fragment prev = getFragmentManager().findFragmentById(R.id.categoryListContainer);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.add(R.id.categoryListContainer, categoryListFragment);
        fragmentTransaction.commit();

        // Add datipicker dialog on btnPickDate click
        Button btnPickDate = (Button) findViewById(R.id.btnPickDate);
        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get date & time for dialog
                int year = expense.expenseDate.get(Calendar.YEAR);
                int month = expense.expenseDate.get(Calendar.MONTH);
                int day = expense.expenseDate.get(Calendar.DAY_OF_MONTH);
                int hour = expense.expenseDate.get(Calendar.HOUR_OF_DAY);
                int minute = expense.expenseDate.get(Calendar.MINUTE);

                // Create bundle
                Bundle bundle = new Bundle();
                bundle.putInt("year", year);
                bundle.putInt("month", month);
                bundle.putInt("day", day);
                bundle.putInt("hour", hour);
                bundle.putInt("minute", minute);

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
                newFragment.setArguments(bundle);
                newFragment.show(ft, "datePicker");
            }
        });

        updateView();
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

    @Override
    public void onDateTimeSelected(int year, int month, int day, int hour, int minute) {
        expense.expenseDate.set(year, month, day, hour, minute);
        updateView();
    }

    public void updateView(){

        // Set expense
        EditText etxtSum = (EditText) findViewById(R.id.etxtSum);
        if(expense.sum.compareTo(BigDecimal.ZERO) > 0) {
            etxtSum.setText(String.valueOf(expense.sum));
        }

        // TODO: Set currency
        // TODO: Set category

        // Set datetime
        Button btnDateTime = (Button)findViewById(R.id.btnPickDate);
        btnDateTime.setText(Helpers.datetimeToString(this, expense.expenseDate));

        // Set note
        EditText etxtNote = (EditText) findViewById(R.id.etxtNote);
        if(!expense.note.isEmpty()){
            etxtNote.setText(expense.note);
        }
    }
}
