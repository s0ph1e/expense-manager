package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.utils.Log;

public class HistoryFragment extends Fragment implements CategoryMultiChoiceListFragment.OnCategorySelectedListener, ExpenseListFragment.OnExpenseListChangedListener {

    private static final String CATEGORIES_FRAGMENT_TAG = "categoriesFragment";
    private static final String EXPENSES_FRAGMENT_TAG = "expensesFragment";
    private static final String FILTER_BUNDLE = "filter";
    private OnHistoryFragmentChangedListener mListener;
    private long[] categoriesFilter = null;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i();
        Bundle args = getArguments();
        if(args != null){
            categoriesFilter = args.getLongArray(FILTER_BUNDLE);
        } else if(savedInstanceState != null){
            categoriesFilter = savedInstanceState.getLongArray(FILTER_BUNDLE);
        }

        View view =  inflater.inflate(R.layout.fragment_history, container, false);

        // Try to find container for categories list
        // If found - add fragment to container, if not - display categories in dialog
        View categoriesContainer = view.findViewById(R.id.historyCategoriesContainer);

        boolean categoriesContainerExists = (categoriesContainer != null);

        // Set action bar if no category container
        setHasOptionsMenu(!categoriesContainerExists);

        FragmentManager fragmentManager = getChildFragmentManager();

        if (categoriesContainerExists) {
            Fragment categories = CategoryMultiChoiceListFragment.newInstance(categoriesFilter);
            fragmentManager.beginTransaction()
                    .replace(R.id.historyCategoriesContainer, categories, CATEGORIES_FRAGMENT_TAG)
                    .commit();
        }

        // http://stackoverflow.com/questions/8474104/android-fragment-lifecycle-over-orientation-changes
        // TODO: Think about extract to onCreate
        if (savedInstanceState == null) {

            Fragment expenses = ExpenseListFragment.newInstance(categoriesFilter);
            fragmentManager.beginTransaction()
                    .replace(R.id.historyExpensesContainer, expenses, EXPENSES_FRAGMENT_TAG)
                    .commit();
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter_categories, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i();
        switch (id){
            case R.id.actionbar_filter:
                FragmentManager fragmentManager = getChildFragmentManager();
                Fragment prev = fragmentManager.findFragmentByTag(CATEGORIES_FRAGMENT_TAG);
                if(prev != null){
                    fragmentManager.beginTransaction()
                            .remove(prev)
                            .commit();
                }
                CategoryMultiChoiceListFragment categories = CategoryMultiChoiceListFragment.newInstance(categoriesFilter);
                categories.show(fragmentManager, CATEGORIES_FRAGMENT_TAG);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public interface OnHistoryFragmentChangedListener{
        public void onHistoryFragmentChanged();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnHistoryFragmentChangedListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHistoryFragmentChangedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i();
        if(categoriesFilter != null && categoriesFilter.length != 0){
            outState.putLongArray(FILTER_BUNDLE, categoriesFilter);
        }
    }

    @Override
    public void onCategorySelected(long[] ids) {
        categoriesFilter = ids;
        reloadExpensesFragment();
    }

    @Override
    public void onBaseCurrencySelected() {

    }

    @Override
    public void onExpenseListChanged() {
        mListener.onHistoryFragmentChanged();
    }

    private void reloadExpensesFragment(){
        Fragment expenses = ExpenseListFragment.newInstance(categoriesFilter);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.historyExpensesContainer, expenses)
                .commit();
    }
}
