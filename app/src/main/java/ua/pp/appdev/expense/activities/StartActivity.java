package ua.pp.appdev.expense.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.fragments.HistoryFragment;
import ua.pp.appdev.expense.fragments.NavigationFragment;
import ua.pp.appdev.expense.fragments.NoExpensesFragment;
import ua.pp.appdev.expense.fragments.OverviewFragment;
import ua.pp.appdev.expense.models.Expense;
import ua.pp.appdev.expense.utils.Log;


public class StartActivity extends FragmentActivity
        implements NavigationFragment.OnNavigationItemSelectedListener,
        OverviewFragment.OnOverviewFragmentChangedListener,
        HistoryFragment.OnHistoryFragmentChangedListener{

    private static final String FRAGMENT_TAG = "currentFragment";

    private static final int NO_EXPENSES_FRAGMENT_POSITION = -1;
    private static final int OVERVIEW_FRAGMENT_POSITION = 0;
    private static final int HISTORY_FRAGMENT_POSITION = 1;

    private  NavigationFragment navigationFragment;

    private int currentNavigationPosition = OVERVIEW_FRAGMENT_POSITION;

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
        Log.i();
        currentNavigationPosition = position;
        loadFragment(position, false);
    }

    public void loadFragment(int position, final boolean needRedrawOldFragment){
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final Fragment oldFragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG);
        Fragment newFragment = null;

        if((position == OVERVIEW_FRAGMENT_POSITION || position == HISTORY_FRAGMENT_POSITION)
                && Expense.getCount(this) == 0){
            position = NO_EXPENSES_FRAGMENT_POSITION;
        }
        switch (position){
            case NO_EXPENSES_FRAGMENT_POSITION:
                if(!(oldFragment instanceof NoExpensesFragment)){
                    newFragment = new NoExpensesFragment();
                }
                break;
            case OVERVIEW_FRAGMENT_POSITION:
                if(!(oldFragment instanceof OverviewFragment)){
                    newFragment = new OverviewFragment();
                }
                break;
            case HISTORY_FRAGMENT_POSITION:
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
                } else if(needRedrawOldFragment) {
                    fragmentManager.beginTransaction()
                            .detach(oldFragment)
                            .attach(oldFragment)
                            .commit();
                }
            }
        }, 150);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadFragment(currentNavigationPosition, true);
    }

    @Override
    public void onHistoryFragmentChanged() {
        loadFragment(currentNavigationPosition, true);
    }

    @Override
    public void onOverviewFragmentChanged() {
        loadFragment(currentNavigationPosition, true);
    }
}
