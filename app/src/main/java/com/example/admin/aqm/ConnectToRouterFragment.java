package com.example.admin.aqm;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectToRouterFragment extends Fragment {
    private static final String LOG_TAG = ConnectToRouterFragment.class.getSimpleName();

    @BindView(R.id.successfully_connected_text)
    TextView successfullyConnectedText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_connect_to_router, container, false);
        ButterKnife.bind(this, view);
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();

        Log.i(LOG_TAG, "connectivity, " + networkInfo.getType());

        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            //successfullyConnectedText
            Log.i(LOG_TAG, "connected");
        } else {
            Log.i(LOG_TAG, "not connected");
            successfullyConnectedText.setText("NOT CONNECTED TO WIFI");
        }

        return view;
    }
}
