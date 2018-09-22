package com.example.android.bugbox;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.android.bugbox.adapters.FragmentPagerAdapter;
import com.example.android.bugbox.contentProvider.ContentProviderUtils;
import com.example.android.bugbox.model.Bug3D;
import com.example.android.bugbox.network.GetDataService;
import com.example.android.bugbox.network.RetrofitClientInstance;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BugsActivity extends AppCompatActivity {

    private FragmentPagerAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public static final String[] tabTitles = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bugs);

        //get tabs titles
        tabTitles[0] = getResources().getString(R.string.map_tab_name);
        tabTitles[1] = getResources().getString(R.string.mybugs_tab_name);

        //set toolbar
        Toolbar toolbar = findViewById(R.id.bugs_toolbar);
        setSupportActionBar(toolbar);

        //tabs with help from https://www.truiton.com/2015/06/android-tabs-example-fragments-viewpager/
        //and https://medium.com/@droidbyme/android-material-design-tabs-tab-layout-with-swipe-884085ae80ff
        tabLayout = findViewById(R.id.bugs_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.map_tab_name));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.mybugs_tab_name));

        viewPager = findViewById(R.id.fragments_pager);
        adapter = new FragmentPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


    }

    public void testButton(View view){

        //Create handle for the RetrofitInstance interface
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Bug3D> call = service.getBug3D();
        call.enqueue(new Callback<Bug3D>() {
            @Override
            public void onResponse(Call<Bug3D> call, Response<Bug3D> response) {

                Bug3D bug = response.body();
                ContentProviderUtils.insertBug(bug, BugsActivity.this);

            }

            @Override
            public void onFailure(Call<Bug3D> call, Throwable t) {
                Toast.makeText(BugsActivity.this, "Connection error", Toast.LENGTH_LONG).show();
                Log.d("BUGS_ACTIVITY", "Connection error");
                t.printStackTrace ();
            }

        });
    }
}
