package com.example.admin.aqm;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
public class LogInFragment extends Fragment {
    private static final String LOG_TAG = LogInFragment.class.getSimpleName();

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

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
