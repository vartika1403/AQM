package com.example.admin.aqm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.os.Build;
import android.os.Bundle;
import android.app.FragmentTransaction;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String LOG_TAG = HomeActivity.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 3;
    private static final int CODE_WRITE_SETTINGS_PERMISSION = 1;

    @BindView(R.id.frame_container)
    FrameLayout frameContainer;
    @BindView(R.id.config_now_button)
    Button configNowButton;
    @BindView(R.id.home_image)
    ImageView homeImage;
    @BindView(R.id.text_good_air)
    TextView goodAirText;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.homeToolbar)
    AppBarLayout homeToolbar;

/*
    @BindView(R.id.scan_aqm_wifi_button)
    Button scanAqmWifiButton;
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
        }

        boolean permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permission = Settings.System.canWrite(this);
        } else {
            permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_SETTINGS) == PackageManager.PERMISSION_GRANTED;
        }
        if (!permission) {
            //do your code
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                startActivityForResult(intent, CODE_WRITE_SETTINGS_PERMISSION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_SETTINGS}, CODE_WRITE_SETTINGS_PERMISSION);
            }
        }
    //    getSupportActionBar().hide();

         //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       //  setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


       // toolbar.setVisibility(View.INVISIBLE);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        int colorBlue = Color.parseColor("#5fb6d9");
        String textGoodAir = getString(R.string.good_is_air_text);
        SpannableString spannableString = new SpannableString(textGoodAir);
        spannableString.setSpan(new ForegroundColorSpan(colorBlue), 4, 8, 0);
        goodAirText.setText(spannableString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.config_now_button)
    public void openAQMConnectionList() {
        configNowButton.setVisibility(View.INVISIBLE);
        homeImage.setVisibility(View.INVISIBLE);
        goodAirText.setVisibility(View.INVISIBLE);
        homeToolbar.setVisibility(View.VISIBLE);
        openAvailableWifiNetworkFragment();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_wifi_config) {
            openAvailableWifiNetworkFragment();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openAvailableWifiNetworkFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, new AvailableWifiNetworkFragment())
                .addToBackStack(null).commit();
    }

    public void openAQMConnectionFragment(WifiInfo wifiInfo) {
        AQMConnectionFragment aqmConnectionFragment = AQMConnectionFragment.newInstance(wifiInfo.getSSID());
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, aqmConnectionFragment)
                .addToBackStack(null).commit();
    }

    public void openConnectToRouterFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, new ConnectToRouterFragment())
                .addToBackStack(null).commit();
    }

    public void openWifiListFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, new WifiListFragment())
                .addToBackStack(null).commit();
    }

    public void openScanWifiFrgament() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container,new ScanWifiFragment()).addToBackStack(null).commit();
    }

    public void openDashBoardActivity() {
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
    }

  /*  @OnClick(R.id.scan_aqm_wifi_button)
    public void showAvailableWifiNetworks() {
        openAvailableWifiNetworkFragment();
       // scanAqmWifiButton.setVisibility(View.INVISIBLE);
*//*
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, new AvailableWifiNetworkFragment()).commit();
*//*
    }*/

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        int count = getFragmentManager().getBackStackEntryCount();

        if (count >  1) {
            Log.i(LOG_TAG, "count, " + count);
            getFragmentManager().popBackStack();
            //additional code
        } else {
            if (count == 0) {
                configNowButton.setVisibility(View.INVISIBLE);
                homeImage.setVisibility(View.INVISIBLE);
                goodAirText.setVisibility(View.INVISIBLE);
                Log.i(LOG_TAG, "count 0, " + count);
                Intent intent = new Intent(this, SplashActivity.class);
                startActivity(intent);
                finish();
            } else {
                configNowButton.setVisibility(View.VISIBLE);
                homeImage.setVisibility(View.VISIBLE);
                goodAirText.setVisibility(View.VISIBLE);
                Log.i(LOG_TAG, "count, " + count);
            }
        }
        super.onBackPressed();
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION && Settings.System.canWrite(this)){
            Log.d(LOG_TAG, "CODE_WRITE_SETTINGS_PERMISSION success");
            //do your code
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //do your code
        }
    }
}
