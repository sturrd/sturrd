package com.sturrd.sturrd;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.sturrd.sturrd.Fragments.DatesFragment;
import com.sturrd.sturrd.Fragments.ExploreFragment;
import com.sturrd.sturrd.Fragments.MessagesFragment;

import com.sturrd.sturrd.Fragments.RequestsFragment;
import com.sturrd.sturrd.Fragments.UserFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private BottomNavigationViewEx bottomNavigationView;
    private String currentUId;
    ViewPager viewPager;
    FirebaseAuth mAuth;
    private DatabaseReference usersDb, instanceDb;
    private FusedLocationProviderClient client;
    MessagesFragment messagesFragment;
    DatesFragment datesFragment;
    RequestsFragment requestsFragment;
    ExploreFragment exploreFragment;
    UserFragment userFragment;

    MenuItem prevMenuItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }
        //save the notificationID to the database

        viewPager = findViewById(R.id.viewpager);

        mAuth = FirebaseAuth.getInstance();
        currentUId = mAuth.getCurrentUser().getUid();

        usersDb = FirebaseDatabase.getInstance().getReference().child("Users");

        client = LocationServices.getFusedLocationProviderClient(this);
        bottomNavigationView = findViewById(R.id.navigation);

        bottomNavigationView.enableItemShiftingMode(false);
        bottomNavigationView.enableAnimation(false);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                else
                {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: "+position);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        locationUpdate();
        requestPermissions();
        setupViewPager(viewPager);

    }



    public void locationUpdate() {
        if (ActivityCompat.checkSelfPermission(MainActivity.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        client.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {

                if (location != null) {
                    final double longitude = location.getLongitude();
                    final double latitude = location.getLatitude();

                    String latitudeString = String.valueOf(latitude);
                    String longitudeString = String.valueOf(longitude);

                    //usersDb.child("latitude").child(latitudeString).setValue(true);
                    //usersDb.child("longitude").child(longitudeString).setValue(true);

                    Map userLatLng = new HashMap();
                    userLatLng.put("latitude", latitudeString);
                    userLatLng.put("longitude", longitudeString);


                    usersDb.child(currentUId).updateChildren(userLatLng);
                }

            }
        });
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);

    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        datesFragment = new DatesFragment();
        requestsFragment = new RequestsFragment();
        exploreFragment = new ExploreFragment();
        userFragment = new UserFragment();
        messagesFragment = new MessagesFragment();
        adapter.addFragment(exploreFragment, "Explore");
        adapter.addFragment(requestsFragment, "Requests");
        adapter.addFragment(datesFragment, "Dates");
        adapter.addFragment(messagesFragment,"Messages");
        adapter.addFragment(userFragment, "Profile");
        viewPager.setAdapter(adapter);
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

        @Override
        public CharSequence getPageTitle(int position) {
            //return mFragmentTitleList.get(position);
            return null;
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.sturrd_explore:
                viewPager.setCurrentItem(0);
                break;

            case R.id.sturrd_dates:
                viewPager.setCurrentItem(2);
                break;

            case R.id.sturrd_profile:
                viewPager.setCurrentItem(4);
                break;

            case R.id.sturrd_messages:
                viewPager.setCurrentItem(3);
                break;
            case R.id.sturrd_requests:
                viewPager.setCurrentItem(1);
                break;

        }

        return false;
    }
}
