package com.example.admin.aqm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LogInFragment extends Fragment implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
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

    private GoogleApiClient googleApiClient;
    private static final int RC_SIGN_IN = 007;
    private CallbackManager callbackManager;
    private ProgressDialog progressDialog;
    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            Log.i(LOG_TAG, "loginResult is, " + loginResult.getAccessToken().getToken());
            if (Profile.getCurrentProfile() == null) {
                profileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                        Log.i("facebook - profile", currentProfile.getFirstName());
                        //       Log.i("facebook - profile email", currentProfile.)
                        SharedPreferenceUtils.getInstance(getActivity()).setValue("FacebookUserName",
                                currentProfile.getFirstName());
                        profileTracker.stopTracking();
                    }
                };
            } else {
                Profile profile = Profile.getCurrentProfile();
                Log.i("facebook - profile", profile.getFirstName());
                SharedPreferenceUtils.getInstance(getActivity()).setValue("FacebookUserName",
                        profile.getFirstName());
            }
            // AccessTokenccessToken = loginResult.getAccessToken().getToken();
            //Log.i(LOG_TAG, "accessToken, " + accessToken);
            Profile profile = Profile.getCurrentProfile();
            Log.i(LOG_TAG, "profile on success, " + profile);
            // nextActivity(profile);
            Toast.makeText(getApplicationContext(), "Logging in...", Toast.LENGTH_SHORT).show();
            signInSuccessfull();
        }

        @Override
        public void onCancel() {
            Log.i(LOG_TAG, "event cancelled");
        }

        @Override
        public void onError(FacebookException e) {
            Log.i(LOG_TAG, "error, " + e);
        }
    };
    private ProfileTracker profileTracker;
    private AccessTokenTracker tracker;
    String FILENAME = "AndroidSSO_data";

    public LogInFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate of LogInFragment");
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken,
                                                       AccessToken currentAccessToken) {
                Log.i(LOG_TAG, "oldAccessToken, " + oldAccessToken);
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.i(LOG_TAG, "oldProfile, " + oldProfile + " " + currentProfile);
                Log.i(LOG_TAG, "newProfile, " + currentProfile);
            }
        };

        tracker.startTracking();
        profileTracker.startTracking();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_log_in, container, false);
        ButterKnife.bind(this, fragmentView);
        Log.i(LOG_TAG, "onCreateView of LogInFragment");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                 .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                 .requestEmail()
                 .build();
        googleApiClient = new GoogleApiClient.Builder(getActivity())
                //.enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        return fragmentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.i(LOG_TAG, "onAttach of LogInFragment");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(LOG_TAG, "onActivityCreated of LogInFragment");
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
    public void onStart() {
        super.onStart();
        Log.i(LOG_TAG, "onStart of LogInFragment");
        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            Log.d(LOG_TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            //   handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            //showProgressDialog();
            Log.i(LOG_TAG, "googleSignInResult, " + googleLogIn);
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                     hideProgressDialog();
                    //  handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setIndeterminate(true);
        }

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }


    @Override
    public void onPause() {
        Log.i(LOG_TAG, "onPause of LogInFragment");
        SignInActivity activity = (SignInActivity) getActivity();
        if (activity != null) {
            activity.hideButton();
        }
        super.onPause();
    }

    @OnClick(R.id.google_log_in_button)
    public void loginInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
/*
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
*/
    }

    public void signInSuccessfull() {
        SharedPreferenceUtils.getInstance(getActivity()).setValue("isLoggedIn", true);
        Boolean isConfigured = SharedPreferenceUtils.getInstance(getActivity())
                .getBooleanValue("config", false);
        Log.i(LOG_TAG, "isConfigured, " + isConfigured);
        Log.i(LOG_TAG, "onResume of LogInFragment");
        SignInActivity activity = (SignInActivity) getActivity();
        if (activity != null) {
            activity.hideButton();
        }
        if (isConfigured) {
            openDashBoardActivity();
        } else if (!isConfigured) {
            openHomeActivity();
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
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        //  LoginManager.getInstance().
        LoginManager.getInstance().registerCallback(callbackManager, callback);
/*
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
*/
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

    private void handleSignInResult(GoogleSignInResult result) {
        if (result == null) {
            return;
        }
        Log.d(LOG_TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            if (acct == null) {
                return;
            }
            Log.d(LOG_TAG, "display name: " + acct.getDisplayName());
            SharedPreferenceUtils.getInstance(getActivity()).setValue("GoogleUserName", acct.getDisplayName());

            String personName = acct.getDisplayName();
            String personPhotoUrl = " ";
            if (acct.getPhotoUrl() != null) {
                personPhotoUrl = acct.getPhotoUrl().toString();
            }
            String email = acct.getEmail();

            Log.d(LOG_TAG, "Name: " + personName + ", email: " + email
                    + ", Image: " + personPhotoUrl);
            signInSuccessfull();
        }
    }


    @OnClick(R.id.forget_text)
    public void changePassword() {
        Log.i(LOG_TAG, "forget text is clicked");
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onStop() {
        Log.i(LOG_TAG, "onStop of LogInFragment");
        tracker.stopTracking();
        profileTracker.stopTracking();
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i(LOG_TAG, "onDestroyView of LogInFragment");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "onDestroy of LogInFragment");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i(LOG_TAG, "onDetach of SignInFragment");
        SignInActivity activity = (SignInActivity) getActivity();
        if (activity != null) {
            activity.showButton();
        }
        super.onDetach();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "connection failed, " + connectionResult);
        Toast.makeText(getActivity(), "Sorry connection failed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(LOG_TAG, "requestCode, " + requestCode);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.i(LOG_TAG, "result from google signin, " + result);
            handleSignInResult(result);
        }
    }
}
