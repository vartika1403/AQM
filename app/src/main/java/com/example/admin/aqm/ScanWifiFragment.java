package com.example.admin.aqm;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ScanWifiFragment extends Fragment {
    private static final String LOG_TAG = ScanWifiFragment.class.getSimpleName();

    @BindView(R.id.scan_aqm_wifi_button)
    Button scanAqmWifiButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_scan_wifi, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.scan_aqm_wifi_button)
    public void showAvailableWifiNetworks() {
        ((HomeActivity)getActivity()).openAvailableWifiNetworkFragment();
/*
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_container, new AvailableWifiNetworkFragment()).commit();
*/
    }
}
