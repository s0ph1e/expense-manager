package ua.pp.appdev.expense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.fragments.ExpenseListFragment;
import ua.pp.appdev.expense.fragments.HistoryFragment;
import ua.pp.appdev.expense.fragments.NavigationFragment;
import ua.pp.appdev.expense.models.Expense;

import static ua.pp.appdev.expense.helpers.EditableItemListView.ADD;


public class StartActivity extends FragmentActivity
        implements NavigationFragment.OnNavigationItemSelectedListener, ExpenseListFragment.OnExpenseItemSelectedListener {

    private static final String FRAGMENT_TAG = "currentFragment";
    private  NavigationFragment navigationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start);

        navigationFragment = (NavigationFragment) getFragmentManager().findFragmentById(R.id.navigation_drawer);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawerLayout != null) {
            navigationFragment.setUpDrawer(R.id.navigation_drawer, drawerLayout);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_add_expense:
                Intent i = new Intent(this, SaveExpenseActivity.class);
                startActivityForResult(i, ADD);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = null;

        switch (position){
            case 0:
                fragment = new ExpenseListFragment();
                break;
            case 1:
                fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
                if(fragment == null || !(fragment instanceof HistoryFragment)){
                    fragment = new HistoryFragment();
                }
                break;
        }

        if(fragment != null) {
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public void onExpenseItemSelected(Expense e) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if(fragment != null){
            /* Here we have to reload expenses fragment
                because great changes may happen in save-expense-activity
                for example - changing or removing categories which causes changing expenses list
             */
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.detach(fragment);
            ft.attach(fragment);
            ft.commit();
        }
    }
}
