package com.example.admin.aqm;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ConnectToRouterFragment extends Fragment {
    private static final String LOG_TAG = ConnectToRouterFragment.class.getSimpleName();

    @BindView(R.id.successfully_connected_text)
    TextView successfullyConnectedText;
    @BindView(R.id.tick_mark_image)
    ImageView tickMarkImage;

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
/*
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();

        Log.i(LOG_TAG, "connectivity, " + networkInfo.getType());

        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            //successfullyConnectedText
            Log.i(LOG_TAG, "connected");
*/
/*
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            ((HomeActivity)getActivity()).openScanWifiFrgament();
*//*

        } else {
            Log.i(LOG_TAG, "not connected");
            successfullyConnectedText.setText("NOT CONNECTED TO WIFI");
        }
*/

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();

        Log.i(LOG_TAG, "connectivity, " + networkInfo.getType());

        if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            successfullyConnectedText.setText("successfully connected");
            tickMarkImage.setVisibility(View.VISIBLE);
            Log.i(LOG_TAG, "connected");
            SharedPreferenceUtils.getInstance(getActivity()).setValue("config", true);
            Handler handler = new Handler();
            handler.postDelayed(r, 3000);
        } else {
            Log.i(LOG_TAG, "not connected");
            successfullyConnectedText.setText("not connected");
            tickMarkImage.setVisibility(View.INVISIBLE);
        }
    }

    final Runnable r = new Runnable() {
        public void run() {
            if (getActivity() != null) {
                ((HomeActivity) getActivity()).openDashBoardActivity();
            }
        }
    };
}
