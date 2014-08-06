package ua.pp.appdev.expense.activities;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
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

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.adapters.CurrencyAdapter;
import ua.pp.appdev.expense.fragments.CategoryListFragment;
import ua.pp.appdev.expense.fragments.DatePickerDialogFragment;
import ua.pp.appdev.expense.helpers.DecimalDigitsInputFilter;
import ua.pp.appdev.expense.helpers.Helpers;
import ua.pp.appdev.expense.helpers.SharedPreferencesHelper;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.models.Currency;
import ua.pp.appdev.expense.models.Expense;

public class SaveExpenseActivity extends EditActivity implements CategoryListFragment.OnCategorySelectedListener, DatePickerDialogFragment.OnDateTimeSelectedListener {

    private final String LOG_TAG = "SaveExpenseActivity";

    private static final String EXPENSE_BUNDLE = "currentExpense";

    private final String CATEGORY_FRAGMENT_TAG = "categoriesFragment";
    private final String DATEPICKER_FRAGMENT_TAG = "datePickerFragment";

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

        // Trying to get expense from bundle
        if(savedInstanceState != null){
            expense = (Expense) savedInstanceState.getSerializable(EXPENSE_BUNDLE);
        } else {
            // If bundle is null, get in from intent
            Intent callingIntent = getIntent();
            expense = (Expense) callingIntent.getSerializableExtra("item");
        }

        // If creating action - make new expense
        if(expense == null){
            expense = new Expense();
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
        Fragment prev = getFragmentManager().findFragmentByTag(CATEGORY_FRAGMENT_TAG);
//        if (prev != null) {
//            fragmentTransaction.remove(prev);
//        }
//        categoryListFragment = new CategoryListFragment();
//        fragmentTransaction.add(R.id.categoryListContainer, categoryListFragment, "categories");
//        fragmentTransaction.commit();
        if(prev == null || !(prev instanceof CategoryListFragment)){
            categoryListFragment = new CategoryListFragment();
            fragmentTransaction.add(R.id.categoryListContainer, categoryListFragment, CATEGORY_FRAGMENT_TAG);
            fragmentTransaction.commit();
        } else {
            categoryListFragment = (CategoryListFragment) prev;
        }

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
                Fragment prev = getFragmentManager().findFragmentByTag(DATEPICKER_FRAGMENT_TAG);
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                // Create and show the dialog.
                DatePickerDialogFragment newFragment = new DatePickerDialogFragment();
                newFragment.setArguments(bundle);
                newFragment.show(ft, DATEPICKER_FRAGMENT_TAG);
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
            errorMessage += "Expense sum can't be empty.\r\n";
        } else {
            expense.sum = new BigDecimal(sum);
            if(expense.sum.compareTo(BigDecimal.ZERO) <= 0){
                errorMessage += "Expense sum can't negative.\r\n";
            }
        }

        if(expense.currency == null){
            errorMessage += "Currency is not set.\r\n";
        }

        if(expense.category == null || categoryListFragment.getSelectedCategory() == null){
            errorMessage += "Category is not set.\r\n";
        }

        // Cut last 2 chars ("\r\n")
        if (errorMessage.length() > 2) {
            errorMessage = errorMessage.substring(0, errorMessage.length()-2);
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

        // Send saved expense back
        Intent intent = new Intent();
        intent.putExtra("item", expense);
        setResult(RESULT_OK, intent);

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

        // Get currency: if expense has currency - use it, else - get it from shared prefs
        Currency currentCurrency = (expense.currency == null)
                ? SharedPreferencesHelper.getBaseCurrency(this)
                : expense.currency;
        // Set currency
        final CurrencyAdapter adapter = new CurrencyAdapter(this, R.layout.spinner_currency_row);
        int currencyPos = adapter.getPosition(currentCurrency);
        if (currencyPos > 0) {
            spinnerCurrency.setSelection(currencyPos);
        }

        // Set category
        if (expense.category != null) {
            categoryListFragment.setSelectedCategory(expense.category);
        }

        // Set datetime
        btnPickDate.setText(Helpers.calendarToDateTimeString(this, expense.expenseDate));

        // Set note
        if(!expense.note.isEmpty()){
            etNote.setText(expense.note);
        }
    }

    @Override
    public void onCategorySelected(Category category) {
        expense.category = category;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(EXPENSE_BUNDLE, expense);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment categoriesFragment = getFragmentManager().findFragmentById(R.id.categoryListContainer);
        if(categoriesFragment != null){
            categoriesFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
