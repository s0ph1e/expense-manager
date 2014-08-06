package ua.pp.appdev.expense.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.adapters.ExpenseAdapter;
import ua.pp.appdev.expense.helpers.EditableItemListView;
import ua.pp.appdev.expense.models.Expense;

import static android.widget.AbsListView.CHOICE_MODE_NONE;
import static ua.pp.appdev.expense.helpers.EditableItemListView.EDIT;

public class ExpenseListFragment extends Fragment {

    private final String LOG_TAG = "ExpenseListFragment";

    private static final String CATEGORIES_BUNDLE = "categories";

    private ExpenseAdapter expenseAdapter;

    private OnExpenseItemSelectedListener mListener;

    private String[] categoriesIds = null;

    private ListView expensesList;

    private Context context;

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

        context = getActivity();

        Bundle args = getArguments();
        if (args != null) {
            categoriesIds = args.getStringArray(CATEGORIES_BUNDLE);
        }

        expensesList = new EditableItemListView(getActivity());
        expensesList.setId(R.id.expenseList);

        expensesList.setVisibility(View.GONE);

        expenseAdapter = new ExpenseAdapter(context, R.layout.listview_expense_row, new ArrayList<Expense>());

        // Load expenses
        expensesList.setAdapter(expenseAdapter);
        new AsyncGetExpenses().execute();

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

        return expensesList;
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
    public void onDestroy() {
        super.onDestroy();
        // TODO: think of moving this to other 'onMethod'
        // This is here to remove CAB when fragment is removed from startActivity after navigation item click
        // May be it can be placed somewhere else, but I leave it here because it works
        expensesList.setChoiceMode(CHOICE_MODE_NONE);
        Log.i(LOG_TAG, "onDestroy");
    }

    public interface OnExpenseItemSelectedListener {
        public void onExpenseItemSelected(Expense e);
    }

    class AsyncGetExpenses extends AsyncTask<Void, Void, List<Expense>>{

        @Override
        protected List<Expense> doInBackground(Void... voids) {
            return Expense.getAll(context, categoriesIds);
        }

        @Override
        protected void onPostExecute(List<Expense> expenses) {
            super.onPostExecute(expenses);
            expenseAdapter.clear();
            expenseAdapter.addAll(expenses);
            Animation animFadeIn = AnimationUtils.loadAnimation(context.getApplicationContext(), android.R.anim.fade_in);
            expensesList.setAnimation(animFadeIn);
            expensesList.setVisibility(View.VISIBLE);
        }
    }
}

