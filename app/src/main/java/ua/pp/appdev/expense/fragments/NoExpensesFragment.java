package ua.pp.appdev.expense.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.activities.SaveExpenseActivity;

import static ua.pp.appdev.expense.helpers.EditableItemListView.ADD;

public class NoExpensesFragment extends Fragment {

    public NoExpensesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_no_expenses, container, false);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.add_expense, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.actionAddExpense:
                Intent i = new Intent(getActivity(), SaveExpenseActivity.class);
                startActivityForResult(i, ADD);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
