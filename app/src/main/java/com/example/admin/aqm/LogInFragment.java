package com.example.admin.aqm;

import android.content.Context;
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

import butterknife.BindView;
import butterknife.OnClick;

public class LogInFragment extends Fragment {
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

    private OnFragmentInteractionListener mListener;

    public LogInFragment() {
        // Required empty public constructor
    }

    public static LogInFragment newInstance(String param1, String param2) {
        LogInFragment fragment = new LogInFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);
        return view;
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

    @OnClick(R.id.log_in_button)
    public void openDashBoardScreen() {
        if (!editTextLogInEmail.getText().toString().isEmpty() &&
                !editTextLogInPassword.getText().toString().isEmpty()) {
            
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
