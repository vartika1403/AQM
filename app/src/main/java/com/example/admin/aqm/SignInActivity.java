package com.example.admin.aqm;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookSdk;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.facebook.FacebookSdk.getApplicationContext;

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

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume of signInActivity");
    }

    @Override
    protected void onPause() {
        Log.i(LOG_TAG, "onPause of signInActivity");
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
        fragmentTransaction.add(R.id.frame_container_sign_in, new SignInFragment())
                .addToBackStack(null).commit();
    }

/*
    @OnClick(R.id.log_in_button)
    public void openLogInFragment() {
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.frame_container_sign_in, new LogInFragment())
                .addToBackStack(null).commit();
    }
*/

    public void showButton() {
        logInButton.setVisibility(View.VISIBLE);
        signInButton.setVisibility(View.VISIBLE);
    }

    public void hideButton() {
        logInButton.setVisibility(View.INVISIBLE);
        signInButton.setVisibility(View.INVISIBLE);
    }
}
