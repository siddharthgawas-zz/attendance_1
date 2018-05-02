package org.pccegoa.studentapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import org.pccegoa.studentapp.fragment.OVERALL;
import org.pccegoa.studentapp.fragment.SUBJECT;
import org.pccegoa.studentapp.fragment.WEEKLY;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private NavigationView mNavigationView = null;
    private ActionBarDrawerToggle actionBarDrawerToggle = null;
    DrawerLayout drawer = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setUpFilters();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case android.R.id.home:
                drawer.openDrawer(Gravity.START);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpFilters()
    {
        Spinner yearSpinner = (Spinner)
                mNavigationView.getMenu().findItem(R.id.year).getActionView();
        final Spinner semesterSpinner = (Spinner)
                mNavigationView.getMenu().findItem(R.id.semester).getActionView();
        Spinner courseSpinner = (Spinner)
                mNavigationView.getMenu().findItem(R.id.course).getActionView();

        ArrayList<Integer> years = new ArrayList<>();
        Calendar calendar = GregorianCalendar.getInstance();
        int currentYear = calendar.get(Calendar.YEAR);
        for(int i = currentYear; i >= currentYear - 10;i--)
            years.add(i);
        ArrayAdapter<Integer> yearArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,years);
        yearSpinner.setAdapter(yearArrayAdapter);

        SharedPreferences preferences = getSharedPreferences(getString(R.string.shared_preference_filters),
                MODE_PRIVATE);

        String[] courseString = getResources().getStringArray(R.array.Course);

        int year = preferences.getInt(getString(R.string.year_key),currentYear);
        int semester = preferences.getInt(getString(R.string.semester_key),1);
        String course = preferences.getString(getString(R.string.course_key),courseString[0]);


        yearSpinner.setSelection(yearArrayAdapter.getPosition(year));
        semesterSpinner.setSelection(((ArrayAdapter<String>)semesterSpinner.getAdapter()).
                getPosition(""+semester));
        courseSpinner.setSelection(((ArrayAdapter<String>)courseSpinner.getAdapter()).
                getPosition(course));

        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int year = (int)parent.getAdapter().getItem(position);
                SharedPreferences preferences = getSharedPreferences(getString(R.string.shared_preference_filters),
                        MODE_PRIVATE);
                preferences.edit().putInt(getString(R.string.year_key),year).apply();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        semesterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int semester  = Integer.parseInt((String)parent.getAdapter().getItem(position));
                SharedPreferences preferences = getSharedPreferences(getString(R.string.shared_preference_filters),
                        MODE_PRIVATE);
                preferences.edit().putInt(getString(R.string.semester_key),semester).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        courseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String course = (String) parent.getAdapter().getItem(position);
                SharedPreferences preferences = getSharedPreferences(getString(R.string.shared_preference_filters),
                        MODE_PRIVATE);
                preferences.edit().putString(getString(R.string.course_key),course).apply();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new OVERALL(), "OVERALL");
        //adapter.addFragment(new WEEKLY(), "WEEKLY");
        adapter.addFragment(new SUBJECT(), "SUBJECT");
        viewPager.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.Logout) {
            //on logout clear api_token and user id from shared preferences
            //goto login
            SharedPreferences preferences = getSharedPreferences(getString(R.string.shared_preference),
                    MODE_PRIVATE);
            preferences.edit().remove(getString(R.string.user_api_token_key)).
                    remove(getString(R.string.user_id_key)).remove(getString(R.string.user_roll_no)).apply();

            preferences = getSharedPreferences(getString(R.string.shared_preference_filters),MODE_PRIVATE);
            preferences.edit().remove(getString(R.string.semester_key)).remove(getString(R.string.year_key))
                    .remove(getString(R.string.course_key)).apply();
            Intent i = new Intent(this,LoginActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.changePassword) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }


        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
        }
    }
