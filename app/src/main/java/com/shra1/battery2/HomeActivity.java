package com.shra1.battery2;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.shra1.battery2.adapters.MyViewPagerAdapter;
import com.shra1.battery2.fragments.DetailsFragment;
import com.shra1.battery2.fragments.HomeFragment;
import com.shra1.battery2.fragments.SettingsFragment;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {


    private FragmentManager fragmentManager;
    private ViewPager viewPager;
    private BottomNavigationView bottomNavigationView;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_dashboard:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_notifications:
                    viewPager.setCurrentItem(2);
                    return true;
            }
            return false;
        }
    };

    @Deprecated
    private void changeFragment(Fragment fragment, boolean isFirst) {
        if (isFirst) {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack("OK")
                    .commit();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentManager = getSupportFragmentManager();

        //changeFragment(HomeFragment.getInstance(), true);

        viewPager = findViewById(R.id.viewPager);

        List<Fragment> fragments = new ArrayList<>();

        fragments.add(HomeFragment.getInstance());
        fragments.add(DetailsFragment.getInstance());
        fragments.add(SettingsFragment.getInstance());

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(fragmentManager, fragments);

        viewPager.setAdapter(myViewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


    }


}
