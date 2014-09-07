package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.models.Category;
import ua.pp.appdev.expense.utils.Log;

public class OverviewFragment extends Fragment
        implements CategoryPieFragment.OnCategoryPieSelectedListener,
        CategoryOverviewFragment.OnCategoryOverviewSelectedListener,
        ExpenseListFragment.OnExpenseListChangedListener {

    private static final String CATEGORIES_PIE_FRAGMENT_TAG = "categoriesPieFragment";
    private static final String CATEGORIES_DETAILS_FRAGMENT_TAG = "categoriesDetailsFragment";
    private static final String EXPENSES_FRAGMENT_TAG = "expensesFragment";
    private static final String FILTER_BUNDLE = "filter";
    private String[] categoriesFilter = null;
    private OnOverviewFragmentChangedListener mListener;
    private AsyncGetCategories asyncGetCategories;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i();

        // http://stackoverflow.com/questions/8474104/android-fragment-lifecycle-over-orientation-changes
        if (savedInstanceState == null) {
            asyncGetCategories = new AsyncGetCategories();
            asyncGetCategories.execute();
        }

        return inflater.inflate(R.layout.fragment_overview, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter_categories, menu);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnOverviewFragmentChangedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnOverviewFragmentChangedListener");
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
        if (asyncGetCategories != null)
            asyncGetCategories.cancel(true);
        super.onDestroyView();
    }

    public interface OnOverviewFragmentChangedListener{
        public void onOverviewFragmentChanged();
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
    public void onCategoryPieSelected(long categoryId) {
        CategoryOverviewFragment categoriesFragment = (CategoryOverviewFragment)
                getChildFragmentManager().findFragmentById(R.id.overviewCategoriesListContainer);
        categoriesFragment.setSelectedById(categoryId);
        reloadExpensesFragment(categoryId);
    }


    @Override
    public void OnCategoryOverviewSelected(long categoryId) {
        CategoryPieFragment pieFragment = (CategoryPieFragment)
                getChildFragmentManager().findFragmentById(R.id.overviewCategoriesPieContainer);
        pieFragment.setSelectedById(categoryId);
        reloadExpensesFragment(categoryId);
    }

    @Override
    public void onBaseCurrencySelected() {
        CategoryOverviewFragment categoriesFragment = (CategoryOverviewFragment)
                getChildFragmentManager().findFragmentById(R.id.overviewCategoriesListContainer);
        categoriesFragment.updateText();
    }

    @Override
    public void onExpenseListChanged() {
        mListener.onOverviewFragmentChanged();
    }

    private void reloadExpensesFragment(long categoryId){
        categoriesFilter = categoryId > 0 ? new String[]{String.valueOf(categoryId)} : null;
        Fragment expenses = ExpenseListFragment.newInstance(categoriesFilter);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.overviewExpensesContainer, expenses)
                .commit();
    }

    class AsyncGetCategories extends AsyncTask<Void, Void, ArrayList<Category>> {

        @Override
        protected ArrayList<Category> doInBackground(Void... voids) {
            return new ArrayList<Category>(Category.getAll(getActivity()));
        }

        @Override
        protected void onPostExecute(ArrayList<Category> categories) {
            FragmentManager fragmentManager = getChildFragmentManager();
            Fragment pieCategories = CategoryPieFragment.newInstance(categories);
            Fragment detailsCategories = CategoryOverviewFragment.newInstance(categories);
            Fragment expenses = ExpenseListFragment.newInstance(categoriesFilter);
            fragmentManager.beginTransaction()
                    .replace(R.id.overviewCategoriesPieContainer, pieCategories, CATEGORIES_PIE_FRAGMENT_TAG)
                    .replace(R.id.overviewCategoriesListContainer, detailsCategories, CATEGORIES_DETAILS_FRAGMENT_TAG)
                    .replace(R.id.overviewExpensesContainer, expenses, EXPENSES_FRAGMENT_TAG)
                    .commit();
        }
    }
}