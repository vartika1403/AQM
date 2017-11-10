package com.example.admin.aqm;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LogInFragment extends Fragment implements View.OnClickListener {
    private static final String LOG_TAG = LogInFragment.class.getSimpleName();

    @BindView(R.id.google_log_in_button)
    public ImageView googleLogIn;

    @BindView(R.id.facebook_log_in_button)
    public ImageView fbLogIn;

    @BindView(R.id.log_in_button)
    public Button logInButton;

    @BindView(R.id.edit_text_logIn_password)
    public EditText editTextLogInPassword;

    @BindView(R.id.edit_text_logIn_email)
    public EditText editTextLogInEmail;

    @BindView(R.id.forget_text)
    public TextView forgetText;

    private OnFragmentInteractionListener mListener;

    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_log_in, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume of LogInFragment");
        SignInActivity activity = (SignInActivity) getActivity();
        if (activity != null) {
            activity.hideButton();
        }
    }

    @Override
    public void onPause() {
        Log.i(LOG_TAG, "onPause of LogInFragment");
        SignInActivity activity = (SignInActivity) getActivity();
        if (activity != null) {
            activity.showButton();
        }
        super.onPause();
    }

    @OnClick(R.id.google_log_in_button)
    public void loginInWithGoogle() {
        String userName = SharedPreferenceUtils.getInstance(getActivity())
                .getStringValue("GoogleUserName", "");
        Log.i(LOG_TAG, "Log in google userName, " + userName);
        Boolean isConfigured = SharedPreferenceUtils.getInstance(getActivity())
                .getBooleanValue("config", false);
        Log.i(LOG_TAG, "isConfigured, " + isConfigured);
        if (userName != null && !userName.isEmpty() && !isConfigured) {
            Log.i(LOG_TAG, "GoogleUserName, " + userName);
            SharedPreferenceUtils.getInstance(getActivity()).setValue("isLoggedIn", true);
            openHomeActivity();
        } else if (userName != null && !userName.isEmpty() && isConfigured) {
            Log.i(LOG_TAG, "GoogleUserName, " + userName);
            SharedPreferenceUtils.getInstance(getActivity()).setValue("isLoggedIn", true);
            openDashBoardActivity();
        } else {
            Toast.makeText(getActivity(), "First Sign In as New User", Toast.LENGTH_SHORT).show();
        }
    }

    public void openHomeActivity() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        startActivity(intent);
    }

    public void openDashBoardActivity() {
        Intent intent = new Intent(getActivity(), DashboardActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.facebook_log_in_button)
    public void loginWithFb() {
        String userName = SharedPreferenceUtils.getInstance(getActivity())
                .getStringValue("FacebookUserName", "");
        Log.i(LOG_TAG, "Log in fb userName, " + userName);
        Boolean isConfigured = SharedPreferenceUtils.getInstance(getActivity())
                .getBooleanValue("config", false);
        Log.i(LOG_TAG, "isConfigured, " + isConfigured);
        if (userName != null && !userName.isEmpty() && !isConfigured) {
            Log.i(LOG_TAG, "FbUserName, " + userName);
            SharedPreferenceUtils.getInstance(getActivity()).setValue("isLoggedIn", true);
            openHomeActivity();
        } else if (userName != null && !userName.isEmpty() && isConfigured) {
            Log.i(LOG_TAG, "GoogleUserName, " + userName);
            SharedPreferenceUtils.getInstance(getActivity()).setValue("isLoggedIn", true);
            openDashBoardActivity();
        } else {
            Toast.makeText(getActivity(), "First Sign In as New User", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.log_in_button)
    public void openDashBoardScreen() {
        Boolean isConfigured = SharedPreferenceUtils.getInstance(getActivity())
                .getBooleanValue("config", false);
        Log.i(LOG_TAG, "isConfigured, " + isConfigured);

        if (!editTextLogInEmail.getText().toString().isEmpty() &&
                !editTextLogInPassword.getText().toString().isEmpty() && !isConfigured) {
            SharedPreferenceUtils.getInstance(getActivity()).setValue("isLoggedIn", true);
            openHomeActivity();
        } else if (!editTextLogInEmail.getText().toString().isEmpty() &&
                !editTextLogInPassword.getText().toString().isEmpty() && isConfigured) {
            SharedPreferenceUtils.getInstance(getActivity()).setValue("isLoggedIn", true);
            openDashBoardActivity();
        } else {
            Toast.makeText(getActivity(), "Please enter correct details", Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.forget_text)
    public void changePassword() {
        Log.i(LOG_TAG, "forget text is clicked");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
