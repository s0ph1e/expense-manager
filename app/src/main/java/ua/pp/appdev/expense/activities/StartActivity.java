package ua.pp.appdev.expense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.fragments.ExpenseListFragment;
import ua.pp.appdev.expense.fragments.HistoryFragment;
import ua.pp.appdev.expense.fragments.NavigationFragment;
import ua.pp.appdev.expense.fragments.OverviewFragment;
import ua.pp.appdev.expense.models.Expense;


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
    public void onNavigationItemSelected(int position) {
        Log.i("StartActivity", "onNavigationItemSelected");
        final FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment oldFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        Fragment newFragment = null;

        switch (position){
            case 0:
                if(!(oldFragment instanceof OverviewFragment)){
                    newFragment = new OverviewFragment();
                }
                break;
            case 1:
                if(!(oldFragment instanceof HistoryFragment)){
                    newFragment = new HistoryFragment();
                }
                break;
        }

        /*
        Delayed fragment loading to prevent drawer's stuttering
        http://stackoverflow.com/questions/18871725/how-to-create-smooth-navigation-drawer
        */
        final Fragment finalNewFragment = newFragment;
        Handler h = new Handler();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(finalNewFragment != null) {
                    fragmentManager.beginTransaction()
                            .replace(R.id.container, finalNewFragment, FRAGMENT_TAG)
                            .commit();
                }
            }
        }, 150);

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
