package com.example.admin.aqm;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.facebook.FacebookSdk;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.facebook.FacebookSdk.getApplicationContext;

public class SignInFragment extends Fragment implements View.OnClickListener,
        GoogleApiClient.OnConnectionFailedListener {
    private static final String LOG_TAG = SignInFragment.class.getSimpleName();
    private final String PERMISSIONS[] = new String[]{"public_profile", "user_birthday", "email"};
    // Your Facebook APP ID
    private static String APP_ID = "118602105499714";
    private static final int RC_SIGN_IN = 007;
    private GoogleApiClient googleApiClient;
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
    private SharedPreferences prefs;

    @BindView(R.id.google_sign_in_button)
    public ImageView googleSignIn;
    @BindView(R.id.facebook_sign_in_button)
    public ImageView facebookSignIn;
    @BindView(R.id.edit_text_signIn_password)
    public EditText editTextSignInPassword;
    @BindView(R.id.edit_text_signIn_email)
    public EditText editTextSignInEmail;
    @BindView(R.id.edit_text_name)
    public EditText editTextSignInName;

    public static SignInFragment newInstance(String param1, String param2) {
        SignInFragment fragment = new SignInFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        tracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
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
        // LoginManager.getInstance().logInWithReadPermissions(this.getActivity(), Arrays.asList(PERMISSIONS));
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ButterKnife.bind(this, view);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(getActivity())
                //.enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        // LoginManager.getInstance().setReadPermissions("user_friends");
        //   LoginManager.getInstance().registerCallback(callbackManager, callback);

/*
        facebookSignIn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
              Log.i(LOG_TAG, "loginResult, " + loginResult.getAccessToken().getUserId());
            }

            @Override
            public void onCancel() {
              Log.i(LOG_TAG, "on cancelled called");
            }


            @Override
            public void onError(FacebookException e) {
                Log.e(LOG_TAG, e.getMessage());
            }
        });
*/
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);

/*
        loginButton.setReadPermissions("user_friends");
      //  loginButton.setFragment(this.getTargetFragment());Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends")
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("email");
        loginButton.setReadPermissions("user_birthday");
*/
        //      loginButton.setFragment(this);
        //    loginButton.setReadPermissions(Arrays.asList(
        //     "public_profile", "email", "user_birthday", "user_friends"));
        //  loginButton.registerCallback(callbackManager,callback);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume of SignInFragment");
        Profile profile = Profile.getCurrentProfile();
        Log.i(LOG_TAG, "profile, " + profile);
        SignInActivity activity = (SignInActivity) getActivity();
        if (activity != null) {
            activity.hideButton();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
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
            Log.i(LOG_TAG, "googleSignInResult, " + googleSignIn);
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    // hideProgressDialog();
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
            showProgressDialog();
            signInSuccessfull();
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

    public void signInSuccessfull() {
        SharedPreferenceUtils.getInstance(getActivity()).setValue("isLoggedIn", true);
        Boolean isConfigured = SharedPreferenceUtils.getInstance(getActivity()).getBooleanValue("config", false);
        Log.i(LOG_TAG, "isConfigured, " + isConfigured);
        hideProgressDialog();
        if (isConfigured) {
            openDashBoardActivity();
        } else if (!isConfigured) {
            openHomeActivity();
        }
    }

    @OnClick(R.id.google_sign_in_button)
    public void signInWithGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @OnClick(R.id.facebook_sign_in_button)
    public void signInWithFacebook() {
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
        //  LoginManager.getInstance().
        LoginManager.getInstance().registerCallback(callbackManager, callback);
    }

    @OnClick(R.id.sign_in_button)
    public void signIn() {
        if (!editTextSignInEmail.getText().toString().isEmpty() &&
                !editTextSignInPassword.getText().toString().isEmpty() &&
                !editTextSignInName.getText().toString().isEmpty()) {
            String userName = editTextSignInName.getText().toString();
            String password = editTextSignInPassword.getText().toString();
            String email = editTextSignInEmail.getText().toString();
            Log.i(LOG_TAG, "sign in user name, " + userName);
            Log.i(LOG_TAG, "sign in password, " + password);
            Log.i(LOG_TAG, "sign in email, " + email);
            SharedPreferenceUtils.getInstance(getActivity()).setValue("UserName", userName);
            SharedPreferenceUtils.getInstance(getActivity()).setValue("Password", password);
            SharedPreferenceUtils.getInstance(getActivity()).setValue("Email", email);
            signInSuccessfull();
        } else {
            Toast.makeText(getActivity(), "Please enter correct details", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        Log.i(LOG_TAG, "onPause of SignInFragment");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(LOG_TAG, "onStop of SignInFragmnet");
        tracker.stopTracking();
        profileTracker.stopTracking();
        super.onStop();
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
    public void onClick(View view) {

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
