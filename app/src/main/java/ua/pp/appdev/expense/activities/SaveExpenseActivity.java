package ua.pp.appdev.expense.activities;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.fragments.CategoryListFragment;
import ua.pp.appdev.expense.fragments.DatePickerDialogFragment;
import ua.pp.appdev.expense.helpers.CurrencyAdapter;
import ua.pp.appdev.expense.helpers.DecimalDigitsInputFilter;
import ua.pp.appdev.expense.helpers.Helpers;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.models.Expense;

public class SaveExpenseActivity extends EditActivity implements CategoryListFragment.OnFragmentInteractionListener, DatePickerDialogFragment.OnDateTimeSelectedListener {

    private final String LOG_TAG = "SaveExpenseActivity";
    private Expense expense;
    private CategoryListFragment categoryListFragment;

    // Views
    private EditText etSum;
    private EditText etNote;
    private Spinner spinnerCurrency;
    private Button btnPickDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");

        // Get expense
        Intent callingIntent = getIntent();
        expense = (Expense) callingIntent.getSerializableExtra("expense");

        // If creating action - make new expense
        if(expense == null){
            expense = new Expense();
            // TODO: TMP code
            /*
            List<Expense> expenses = Expense.getAll(this);
            if (expenses.size() > 0)
                expense = expenses.get(0);
            */
            //expense.expenseDate.set(2000, Calendar.APRIL, 2);
        }

        setContentView(R.layout.activity_save_expense);

        etSum = (EditText)findViewById(R.id.etxtSum);
        // Добавляем фильтр на количество цифр после запятой & remove focus after done
        etSum.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(2)});
        etSum.setOnEditorActionListener(lostFocusAfterDone);

        // Remove focus after actionDone from editText for Note
        etNote = (EditText) findViewById(R.id.etxtNote);
        etNote.setOnEditorActionListener(lostFocusAfterDone);

        // Add currency spinner
        spinnerCurrency = (Spinner)findViewById(R.id.spinnerCurrency);
        final CurrencyAdapter adapter = new CurrencyAdapter(this, R.layout.spinner_currency_row);
        spinnerCurrency.setAdapter(adapter);
        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                expense.currency = adapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        // Add category list
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if (categoryListFragment != null) {
            fragmentTransaction.remove(categoryListFragment);
        }
        categoryListFragment = new CategoryListFragment();
        fragmentTransaction.add(R.id.categoryListContainer, categoryListFragment);
        fragmentTransaction.commit();

        // Add datepicker dialog on btnPickDate click
        btnPickDate = (Button) findViewById(R.id.btnPickDate);
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
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateView();
    }

    @Override
    protected void onSave(View v) {

        String errorMessage = "";
        String sum = etSum.getText().toString();

        if(sum.isEmpty()){
            errorMessage += "Expense sum can't be zero.\n";
        } else {
            expense.sum = new BigDecimal(sum);
        }

        if(expense.currency == null){
            errorMessage += "Currency is not set.\r\n";
        }

        if(expense.category == null){
            errorMessage += "Category is not set.\r\n";
        }

        if(!errorMessage.isEmpty()){
            new AlertDialog.Builder(this)
                    .setTitle("Saving expense error!")
                    .setMessage(errorMessage)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
            return;
        }
        expense.note = etNote.getText().toString();
        expense.save(this);
        List<Expense> list = Expense.getAll(this);
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
        if(expense.sum.compareTo(BigDecimal.ZERO) > 0) {
            etSum.setText(String.valueOf(expense.sum));
        }

        // Set currency
        if (expense.currency != null) {
            final CurrencyAdapter adapter = new CurrencyAdapter(this, R.layout.spinner_currency_row);
            int currencyPos = adapter.getPosition(expense.currency);
            if (currencyPos > 0) {
                spinnerCurrency.setSelection(currencyPos);
            }
        }

        // Set category
        if (expense.category != null) {
            categoryListFragment.setCategory(expense.category);
        }

        // Set datetime
        btnPickDate.setText(Helpers.datetimeToString(this, expense.expenseDate));

        // Set note
        if(!expense.note.isEmpty()){
            etNote.setText(expense.note);
        }
    }

    @Override
    public void onCategorySelected(Category category) {
        expense.category = category;
    }
}