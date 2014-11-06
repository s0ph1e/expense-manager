package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.activities.SaveExpenseActivity;
import ua.pp.appdev.expense.adapters.CurrencyAdapter;
import ua.pp.appdev.expense.adapters.ExpenseAdapter;
import ua.pp.appdev.expense.helpers.EditableItemListView;
import ua.pp.appdev.expense.helpers.SharedPreferencesHelper;
import ua.pp.appdev.expense.models.Currency;
import ua.pp.appdev.expense.models.Expense;
import ua.pp.appdev.expense.utils.Log;

import static android.widget.AbsListView.CHOICE_MODE_NONE;
import static ua.pp.appdev.expense.helpers.EditableItemListView.ADD;
import static ua.pp.appdev.expense.helpers.EditableItemListView.EDIT;

public class ExpenseListFragment extends Fragment {

    private static final String CATEGORIES_BUNDLE = "categories";
    private ExpenseAdapter expenseAdapter;
    private long[] categoriesIds = null;
    private EditableItemListView expensesList;
    private Context context;
    private OnExpenseListChangedListener mListener;
    private AsyncGetExpenses asyncGetExpenses;

    public ExpenseListFragment() {
        // Required empty public constructor
    }

    public static ExpenseListFragment newInstance(long[] categories){
        ExpenseListFragment fragment = new ExpenseListFragment();
        Bundle args = new Bundle();
        args.putLongArray(CATEGORIES_BUNDLE, categories);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        setHasOptionsMenu(true);
        Log.i();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i();
        Bundle args = getArguments();
        if (args != null) {
            categoriesIds = args.getLongArray(CATEGORIES_BUNDLE);
        }

        expensesList = new EditableItemListView(getActivity());
        expensesList.setId(R.id.expenseList);
        expensesList.setVisibility(View.GONE);

        expenseAdapter = new ExpenseAdapter(context, R.layout.listview_expense_row, new ArrayList<Expense>());

        // Load expenses
        expensesList.setAdapter(expenseAdapter);
        asyncGetExpenses = new AsyncGetExpenses();
        asyncGetExpenses.execute();

        expensesList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Expense expense = (Expense) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(getActivity(), expense.getActivityClass());
                intent.putExtra("item", expense);
                getActivity().startActivityForResult(intent, EDIT);
            }
        });

        expensesList.setOnEditableListViewChangedListener(new EditableItemListView.OnEditableListViewChangedListener() {
            @Override
            public void onEditableListViewChanged() {
                mListener.onExpenseListChanged();
            }
        });
        return expensesList;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.expenses_fragment, menu);

        // Add base currency spinner
        MenuItem spinnerItem = menu.findItem(R.id.actionSelectCurrency);
        View spinnerView = spinnerItem.getActionView();
        if (spinnerView instanceof Spinner) {

            Spinner spinnerCurrency = (Spinner) spinnerView;
            final CurrencyAdapter adapter = new CurrencyAdapter(context, R.layout.spinner_currency_row_actionbar);
            spinnerCurrency.setAdapter(adapter);

            // Set base currency selected
            Currency baseCurrency = SharedPreferencesHelper.getBaseCurrency(context);
            int currencyPos = adapter.getPosition(baseCurrency);
            if (currencyPos > 0) {
                spinnerCurrency.setSelection(currencyPos);
            }

            spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    Currency selected = adapter.getItem(position);
                    SharedPreferencesHelper.saveBaseCurrency(context, selected);
                    expenseAdapter.notifyDataSetChanged();
                    mListener.onBaseCurrencySelected();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.actionAddExpense:
                Intent i = new Intent(getActivity(), SaveExpenseActivity.class);
                startActivityForResult(i, ADD);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnExpenseListChangedListener) getParentFragment();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnExpenseListChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        Log.i();
        asyncGetExpenses.cancel(true);
        expensesList.setChoiceMode(CHOICE_MODE_NONE);
        super.onDestroyView();
    }

    public int getItemsCount(){
        return expenseAdapter.getCount();
    }

    public interface OnExpenseListChangedListener {
        public void onBaseCurrencySelected();
        public void onExpenseListChanged();
    }

    class AsyncGetExpenses extends AsyncTask<Void, Void, List<Expense>>{

        @Override
        protected List<Expense> doInBackground(Void... voids) {
            return Expense.getAllInCategories(context, categoriesIds);
        }

        @Override
        protected void onPostExecute(List<Expense> expenses) {
            expenseAdapter.clear();
            expenseAdapter.addAll(expenses);
            Animation animFadeIn = AnimationUtils.loadAnimation(context.getApplicationContext(), android.R.anim.fade_in);
            expensesList.setAnimation(animFadeIn);
            expensesList.setVisibility(View.VISIBLE);
        }
    }
}

