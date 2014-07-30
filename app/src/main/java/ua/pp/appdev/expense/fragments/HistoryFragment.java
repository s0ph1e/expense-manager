package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ua.pp.appdev.expense.R;

public class HistoryFragment extends Fragment implements CategoryMultiChoiceListFragment.OnCategorySelectedListener {

    private OnFragmentInteractionListener mListener;

    public HistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_history, container, false);
        FragmentManager fragmentManager = getChildFragmentManager();
        Fragment categories = new CategoryMultiChoiceListFragment();
        Fragment expenses = new ExpenseListFragment();
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
        Fragment expenses = ExpenseListFragment.newInstance(ids);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.historyExpensesContainer, expenses)
                .commit();
    }

    @Override
    public void onAllCategoriesSelected() {
        Fragment expenses = new ExpenseListFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.historyExpensesContainer, expenses)
                .commit();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
