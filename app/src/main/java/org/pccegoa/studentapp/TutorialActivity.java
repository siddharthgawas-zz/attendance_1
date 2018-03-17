package org.pccegoa.studentapp;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.pccegoa.studentapp.fragment.TutorialFragment;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ViewPager pager = findViewById(R.id.pager);
        TabLayout tabLayout = (TabLayout)  findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(pager, true);
        pager.setAdapter(new ScreenSlidePagerAdapter(getSupportFragmentManager()));
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            TutorialFragment f = new TutorialFragment();
            Bundle b = new Bundle();
            if (position == 1)
                b.putInt(TutorialFragment.ARG_LAYOUT,R.layout.developer_page);
            f.setArguments(b);
            return f;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
