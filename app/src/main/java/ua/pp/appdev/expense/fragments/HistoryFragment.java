package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.pp.appdev.expense.R;

public class HistoryFragment extends Fragment implements CategoryMultiChoiceListFragment.OnCategorySelectedListener {

    private static final String LOG_TAG = "HistoryFragment";

    private static final String FILTER_BUNDLE = "filter";

    private OnFragmentInteractionListener mListener;

    private String[] categoriesFilter = null;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle args = getArguments();
        if(args != null){
            categoriesFilter = args.getStringArray(FILTER_BUNDLE);
        }

        View view =  inflater.inflate(R.layout.fragment_history, container, false);
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment categories = CategoryMultiChoiceListFragment.newInstance(categoriesFilter);
        Fragment expenses = ExpenseListFragment.newInstance(categoriesFilter);
        fragmentManager.beginTransaction()
                .replace(R.id.historyCategoriesContainer, categories)
                .replace(R.id.historyExpensesContainer, expenses)
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

    @Override
    public void onAllCategoriesSelected() {
        categoriesFilter = null;
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

        if(categoriesFilter != null){
            outState.putStringArray(FILTER_BUNDLE, categoriesFilter);
        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
