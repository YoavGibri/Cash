package com.yoavgibri.cash;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Created by Yoav on 08/03/16.
 */
public class AnalyticsActivity extends AppCompatActivity implements YearViewFragment.OnFragment_YearView_InteractionListener , MonthViewFragment.OnFragment_MonthView_InteractionListener{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analytics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAnalytics);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (findViewById(R.id.frameLayoutAnalytics) !=null){
            if (savedInstanceState != null){
                return;
            }
            YearViewFragment yearViewFragment = new YearViewFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutAnalytics, yearViewFragment, "YearViewFragment").commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
//            case R.id.action_settings:
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onFragment_YearView_Interaction(String month, int monthTotal) {
        MonthViewFragment monthViewFragment = new MonthViewFragment();
        Bundle bundle = new Bundle();
        bundle.putString("month", month);
        bundle.putInt("monthTotal", monthTotal);
        monthViewFragment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.addToBackStack("YearViewFragment");
        ft.replace(R.id.frameLayoutAnalytics, monthViewFragment, "MonthViewFragment");
        ft.commit();
    }

    @Override
    public void onFragment_MonthView_Interaction(Uri uri) {

    }
}
