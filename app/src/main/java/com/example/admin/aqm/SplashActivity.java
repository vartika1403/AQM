package com.example.admin.aqm;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SplashActivity extends AppCompatActivity {
    private static final String LOG_TAG = SplashActivity.class.getSimpleName();

    @BindView(R.id.view_pager)
    public ViewPager viewPager;

    @BindView(R.id.layoutDots)
    public LinearLayout dotsLayout;

    @BindView(R.id.skip_button)
    public Button skipButton;

    private ViewPagerAdapter viewPagerAdapter;
    private TextView[] dots;
    private int[] layouts;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

/*
        String userName = SharedPreferenceUtils.getInstance(this)
                .getStringValue("GoogleUserName", "");
        Log.i(LOG_TAG, "google user name, " + userName);
        if (userName != null && !userName.isEmpty()) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }

        String fbUserName = SharedPreferenceUtils.getInstance(this)
                .getStringValue("FacebookUserName", "");
        Log.i(LOG_TAG, "fb user name, " + fbUserName);
        if (fbUserName != null && !fbUserName.isEmpty()) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
        }
*/
        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3
        };

        // adding bottom dots
        addBottomDots(0);

        viewPagerAdapter = new ViewPagerAdapter(layouts, this);
        viewPager.setAdapter(viewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
           /* if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                skipButton.setVisibility(View.GONE);
            } else {
                // still pages are left
                skipButton.setVisibility(View.VISIBLE);
            }*/
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };


    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int colorsActive = ContextCompat.getColor(this, R.color.colorSkyBlue);
        int colorsInactive = ContextCompat.getColor(this, R.color.colorWhite);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive);
    }

    @OnClick(R.id.skip_button)
    public void skipSplashScreen() {
        String googleUserName = SharedPreferenceUtils.getInstance(this)
                .getStringValue("GoogleUserName", "");
        Log.i(LOG_TAG, "google user name, " + googleUserName);
        Boolean isConfigured = SharedPreferenceUtils.getInstance(this)
                .getBooleanValue("config", false);
        Log.i(LOG_TAG, "configured, " + isConfigured);
        Boolean isLoggedIn = SharedPreferenceUtils.getInstance(this)
                .getBooleanValue("isLoggedIn", false);
        Log.i(LOG_TAG, "isLoggedIn, " + isLoggedIn);
        if (googleUserName != null && !googleUserName.isEmpty() && isConfigured && isLoggedIn) {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            return;
        } else if (googleUserName != null && !googleUserName.isEmpty() && !isConfigured && isLoggedIn) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            return;
        }

        String fbUserName = SharedPreferenceUtils.getInstance(this)
                .getStringValue("FacebookUserName", "");
        Log.i(LOG_TAG, "fb user name, " + fbUserName);
        if (fbUserName != null && !fbUserName.isEmpty() && isConfigured && isLoggedIn) {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            return;
        } else if (fbUserName != null && !fbUserName.isEmpty() && !isConfigured && isLoggedIn) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            return;
        }
        String userName = SharedPreferenceUtils.getInstance(this)
                .getStringValue("UserName", "");
        if (userName != null && !userName.isEmpty() && isConfigured) {
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
            return;
        } else if (userName != null && !userName.isEmpty() && !isConfigured) {
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            return;
        }
        launchHomeScreen();
    }

    private void launchHomeScreen() {
        Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
        startActivity(intent);
      //  finish();
    }

    @Override()
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }
}
