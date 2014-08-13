package ua.pp.appdev.expense.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.models.Expense;
import ua.pp.appdev.expense.utils.Log;

public class OverviewFragment extends Fragment implements CategoryPieFragment.OnCategoryPieSelectedListener, ExpenseListFragment.OnExpenseItemSelectedListener {

    private static final String CATEGORIES_FRAGMENT_TAG = "categoriesFragment";
    private static final String EXPENSES_FRAGMENT_TAG = "expensesFragment";
    private static final String FILTER_BUNDLE = "filter";
    private String[] categoriesFilter = null;

    public OverviewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i();

        Bundle args = getArguments();
        if(args != null){
            categoriesFilter = args.getStringArray(FILTER_BUNDLE);
        } else if(savedInstanceState != null){
            categoriesFilter = savedInstanceState.getStringArray(FILTER_BUNDLE);
        }

        // http://stackoverflow.com/questions/8474104/android-fragment-lifecycle-over-orientation-changes
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getChildFragmentManager();
            Fragment pieCategories = CategoryPieFragment.newInstance();
            Fragment expenses = ExpenseListFragment.newInstance(categoriesFilter);
            fragmentManager.beginTransaction()
                    .replace(R.id.overviewCategoriesContainer, pieCategories, CATEGORIES_FRAGMENT_TAG)
                    .replace(R.id.overviewExpensesContainer, expenses, EXPENSES_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i();
        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    private void reloadExpensesFragment(){
        Fragment expenses = ExpenseListFragment.newInstance(categoriesFilter);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.overviewExpensesContainer, expenses)
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i();
        if(categoriesFilter != null){
            outState.putStringArray(FILTER_BUNDLE, categoriesFilter);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter_categories, menu);
    }

    @Override
    public void onCategoryPieSelected(long categoryId) {
        categoriesFilter = categoryId > 0 ? new String[]{String.valueOf(categoryId)} : null;
        reloadExpensesFragment();
    }

    @Override
    public void onExpenseItemSelected(Expense e) {

    }

    @Override
    public void onBaseCurrencySelected() {
        CategoryPieFragment categoriesFragment = (CategoryPieFragment)
                getChildFragmentManager().findFragmentById(R.id.overviewCategoriesContainer);
        categoriesFragment.updateText();
    }
}