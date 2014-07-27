package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.adapters.ExpenseAdapter;
import ua.pp.appdev.expense.helpers.EditableItemListView;
import ua.pp.appdev.expense.models.Expense;

public class ExpenseListFragment extends Fragment {

    private ExpenseAdapter expenseAdapter;

    private OnFragmentInteractionListener mListener;

    public ExpenseListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ListView expenseList = new EditableItemListView(getActivity());
        expenseList.setId(R.id.expense_list);

        // Get array of categories and set adapter
        List<Expense> expenses = Expense.getAll(getActivity());
        expenseAdapter = new ExpenseAdapter(getActivity(), R.layout.listview_expense_row, expenses);
        expenseList.setAdapter(expenseAdapter);

/*        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                categoryListAdapter.setSelected(i);
                mListener.onCategorySelected(categoryListAdapter.getItem(i));
            }
        });*/

        return expenseList;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
