package ua.pp.appdev.expense.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import ua.pp.appdev.expense.R;
import ua.pp.appdev.expense.fragments.NavigationFragment;


public class StartActivity extends Activity
        implements NavigationFragment.OnNavigationItemSelectedListener {

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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.start, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.action_add_expense:
                Intent i = new Intent(this, SaveExpenseActivity.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNavigationItemSelected(int position) {
//        FragmentManager fragmentManager = getFragmentManager();
//        fragmentManager.beginTransaction()
//                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
//                .commit();
    }

}
