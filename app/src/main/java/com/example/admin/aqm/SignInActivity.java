package com.example.admin.aqm;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInActivity extends AppCompatActivity {
    private static final String LOG_TAG = SignInActivity.class.getSimpleName();

    @BindView(R.id.log_in_button)
    Button logInButton;

    @BindView(R.id.sign_in_button)
    Button signInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);
        Log.i(LOG_TAG, "onCreate of signInActivity");
    }

    public void openActivity() {
        String googleUserName = SharedPreferenceUtils.getInstance(this)
                .getStringValue("GoogleUserName", "");
        Log.i(LOG_TAG, "google user name, " + googleUserName);
        if (googleUserName != null && !googleUserName.isEmpty()) {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
        }

        String fbUserName = SharedPreferenceUtils.getInstance(this)
                .getStringValue("FacebookUserName", "");
        Log.i(LOG_TAG, "fb user name, " + fbUserName);
        if (fbUserName != null && !fbUserName.isEmpty()) {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
        }

        String userName = SharedPreferenceUtils.getInstance(this)
                .getStringValue("UserName", "");
        if (userName != null && !userName.isEmpty()) {
            Intent intent = new Intent(this, SplashActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume of signInActivity");
        logInButton.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "onPause of signInActivity");
        logInButton.setVisibility(View.INVISIBLE);
        signInButton.setVisibility(View.INVISIBLE);
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(LOG_TAG, "onStop of signInActivity");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(LOG_TAG, "onDestroy of signInActivity");
        super.onDestroy();
    }

    @OnClick(R.id.sign_in_button)
    public void openSignInFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container_sign_in, new SignInFragment())
                .addToBackStack(null).commit();
    }

    @OnClick(R.id.log_in_button)
    public void openLogInFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container_sign_in, new LogInFragment())
                .addToBackStack(null).commit();
    }

    public void showButton() {
        logInButton.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.VISIBLE);
    }

    public void hideButton() {
        logInButton.setVisibility(View.INVISIBLE);
        signInButton.setVisibility(View.INVISIBLE);
    }
}
