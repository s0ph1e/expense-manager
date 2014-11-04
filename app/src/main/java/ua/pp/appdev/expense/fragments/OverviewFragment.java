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
    private static final String SELECTED_CATEGORY_ID_BUNDLE = "selectedCategoryId";
    private static final String CATEGORIES_BUNDLE = "categories";
    private long selectedCategoryId;
    private ArrayList<Category> categories;
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
            selectedCategoryId = args.getLong(SELECTED_CATEGORY_ID_BUNDLE);
        } else if(savedInstanceState != null){
            selectedCategoryId = savedInstanceState.getLong(SELECTED_CATEGORY_ID_BUNDLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i();

        // http://stackoverflow.com/questions/8474104/android-fragment-lifecycle-over-orientation-changes
        if (savedInstanceState == null) {
            if (asyncGetCategories != null)
                asyncGetCategories.cancel(true);
            asyncGetCategories = new AsyncGetCategories();
            asyncGetCategories.execute();
        } else {
            selectedCategoryId = savedInstanceState.getLong(SELECTED_CATEGORY_ID_BUNDLE);
            categories = savedInstanceState.getParcelableArrayList(CATEGORIES_BUNDLE);
            reloadPieFragment();
            reloadCategoriesFragment();
            reloadExpensesFragment();
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
        FragmentManager manager = getChildFragmentManager();
        manager.beginTransaction().remove(manager.findFragmentByTag(EXPENSES_FRAGMENT_TAG)).commitAllowingStateLoss();
        manager.beginTransaction().remove(manager.findFragmentByTag(CATEGORIES_DETAILS_FRAGMENT_TAG)).commitAllowingStateLoss();
        manager.beginTransaction().remove(manager.findFragmentByTag(CATEGORIES_PIE_FRAGMENT_TAG)).commitAllowingStateLoss();
        super.onSaveInstanceState(outState);
        Log.i();
        outState.putLong(SELECTED_CATEGORY_ID_BUNDLE, selectedCategoryId);
        outState.putParcelableArrayList(CATEGORIES_BUNDLE, categories);
    }

    @Override
    public void onCategoryPieSelected(long categoryId) {
        CategoryOverviewFragment categoriesFragment = (CategoryOverviewFragment)
                getChildFragmentManager().findFragmentById(R.id.overviewCategoriesListContainer);
        categoriesFragment.setSelectedById(categoryId);
        selectedCategoryId = categoryId;
        reloadExpensesFragment();
    }


    @Override
    public void OnCategoryOverviewSelected(long categoryId) {
        CategoryPieFragment pieFragment = (CategoryPieFragment)
                getChildFragmentManager().findFragmentById(R.id.overviewCategoriesPieContainer);
        pieFragment.setSelectedById(categoryId);
        selectedCategoryId = categoryId;
        reloadExpensesFragment();
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

    private void reloadPieFragment(){
        FragmentManager fragmentManager = getChildFragmentManager();
        CategoryPieFragment pieCategories = CategoryPieFragment.newInstance(categories);
        fragmentManager.beginTransaction()
                .replace(R.id.overviewCategoriesPieContainer, pieCategories, CATEGORIES_PIE_FRAGMENT_TAG)
                .commit();
    }

    private void reloadCategoriesFragment(){
        FragmentManager fragmentManager = getChildFragmentManager();
        CategoryOverviewFragment detailsCategories = CategoryOverviewFragment.newInstance(categories, selectedCategoryId);
        fragmentManager.beginTransaction()
                .replace(R.id.overviewCategoriesListContainer, detailsCategories, CATEGORIES_DETAILS_FRAGMENT_TAG)
                .commit();
    }

    private void reloadExpensesFragment(){
        String[] categoriesFilter = selectedCategoryId > 0 ? new String[]{String.valueOf(selectedCategoryId)} : null;
        FragmentManager fragmentManager = getChildFragmentManager();
        ExpenseListFragment expenses = ExpenseListFragment.newInstance(categoriesFilter);
        fragmentManager.beginTransaction()
                .replace(R.id.overviewExpensesContainer, expenses, EXPENSES_FRAGMENT_TAG)
                .commit();
    }

    class AsyncGetCategories extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            categories = new ArrayList<Category>(Category.getAll(getActivity()));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            reloadPieFragment();
            reloadCategoriesFragment();
            reloadExpensesFragment();
        }
    }
}