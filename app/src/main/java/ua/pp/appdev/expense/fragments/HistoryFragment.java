package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ua.pp.appdev.expense.R;

public class HistoryFragment extends Fragment implements CategoryMultiChoiceListFragment.OnCategorySelectedListener {

    private static final String LOG_TAG = "HistoryFragment";

    private static final String CATEGORIES_FRAGMENT_TAG = "categoriesFragment";

    private static final String EXPENSES_FRAGMENT_TAG = "expensesFragment";

    private static final String FILTER_BUNDLE = "filter";

    private OnFragmentInteractionListener mListener;

    private String[] categoriesFilter = null;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(LOG_TAG, "onCreateView");
        Bundle args = getArguments();
        if(args != null){
            categoriesFilter = args.getStringArray(FILTER_BUNDLE);
        } else if(savedInstanceState != null){
            categoriesFilter = savedInstanceState.getStringArray(FILTER_BUNDLE);
        }

        View view =  inflater.inflate(R.layout.fragment_history, container, false);


        FragmentManager fragmentManager = getChildFragmentManager();

        // Try to find container for categories list
        // If found - add fragment to container, if not - display categories in dialog
        View categoriesContainer = view.findViewById(R.id.historyCategoriesContainer);

        // Set action bar if no category container
        setHasOptionsMenu((categoriesContainer == null));

        // Remove old categories fragment
        Fragment prev = fragmentManager.findFragmentByTag(CATEGORIES_FRAGMENT_TAG);
        if(prev != null){
            fragmentManager.beginTransaction()
                    .remove(prev)
                    .commit();
        }

        if(categoriesContainer != null) {
            CategoryMultiChoiceListFragment categories = CategoryMultiChoiceListFragment.newInstance(categoriesFilter);
            fragmentManager.beginTransaction()
                    .add(R.id.historyCategoriesContainer, categories, CATEGORIES_FRAGMENT_TAG)
                    .commit();
        }

        Fragment expenses = ExpenseListFragment.newInstance(categoriesFilter);
        fragmentManager.beginTransaction()
                .replace(R.id.historyExpensesContainer, expenses, EXPENSES_FRAGMENT_TAG)
                .commit();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnFragmentInteractionListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCategorySelected(String[] ids) {
        categoriesFilter = ids;
        reloadExpensesFragment();
    }

    private void reloadExpensesFragment(){
        Fragment expenses = ExpenseListFragment.newInstance(categoriesFilter);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.historyExpensesContainer, expenses)
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i(LOG_TAG, "onSaveInstanceState");
        if(categoriesFilter != null){
            outState.putStringArray(FILTER_BUNDLE, categoriesFilter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i(LOG_TAG, "onOptionsItemSelected");
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.filter_categories, menu);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
