package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.adapters.ExpenseAdapter;
import ua.pp.appdev.expense.helpers.EditableItemListView;
import ua.pp.appdev.expense.models.Expense;

import static ua.pp.appdev.expense.helpers.EditableItemListView.EDIT;

public class ExpenseListFragment extends Fragment {

    private final String LOG_TAG = "ExpenseListFragment";

    private static final String CATEGORIES_BUNDLE = "categories";

    private ExpenseAdapter expenseAdapter;

    private OnExpenseItemSelectedListener mListener;

    private String[] categoriesIds = null;

    public ExpenseListFragment() {
        // Required empty public constructor
    }

    public static ExpenseListFragment newInstance(String[] categories){
        ExpenseListFragment fragment = new ExpenseListFragment();
        Bundle args = new Bundle();
        args.putStringArray(CATEGORIES_BUNDLE, categories);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Log.i(LOG_TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(LOG_TAG, "onCreateView");

        Bundle args = getArguments();
        if (args != null) {
            categoriesIds = args.getStringArray(CATEGORIES_BUNDLE);
        }

        final ListView expenseList = new EditableItemListView(getActivity());
        expenseList.setId(R.id.expenseList);

        // Get array of categories and set adapter
        List<Expense> expenses = Expense.getAll(getActivity(), categoriesIds);
        expenseAdapter = new ExpenseAdapter(getActivity(), R.layout.listview_expense_row, expenses);
        expenseList.setAdapter(expenseAdapter);

        expenseList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Expense expense = (Expense) adapterView.getAdapter().getItem(i);
                Intent intent = new Intent(getActivity(), expense.getActivityClass());
                intent.putExtra("item", expense);
                getActivity().startActivityForResult(intent, EDIT);
            }
        });

        return expenseList;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.action_add_expense, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        try {
//            mListener = (OnExpenseItemSelectedListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString()
//                    + " must implement OnExpenseItemSelectedListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnExpenseItemSelectedListener {
        public void onExpenseItemSelected(Expense e);
    }
}
