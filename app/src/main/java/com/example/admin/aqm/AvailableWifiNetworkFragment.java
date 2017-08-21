package com.example.admin.aqm;

import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AvailableWifiNetworkFragment extends Fragment {
    private static final String LOG_TAG = AvailableWifiNetworkFragment.class.getSimpleName();
    private WifiManager wifiManager;
    private AvailableWifiNetworkFragment context;

    @BindView(R.id.scan_wifi_text)
    TextView scanWifiText;
    @BindView(R.id.wifi_list)
    ListView wifiItemsList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        context = this;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_available_network, container, false);
        ButterKnife.bind(this, view);
        getWifiResults();
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getWifiResults() {
        wifiManager = (WifiManager)getActivity().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager.isWifiEnabled() == false) {
            // If wifi disabled then enable it
            Toast.makeText(getActivity(), R.string.wifi_error_message,
                    Toast.LENGTH_LONG).show();

            wifiManager.setWifiEnabled(true);
        }
        getActivity().registerReceiver(wifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(wifiScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    @Override
    public void onPause() {
       getActivity().unregisterReceiver(wifiScanReceiver);
        super.onPause();
    }

    private final BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                Log.i(LOG_TAG, "scanResult, " + scanResults);
                scanWifiText.setText(R.string.available_networks);
                // add your logic here
                StringBuilder sb = new StringBuilder();
                sb.append("\n        Number Of Wifi connections :"+scanResults.size()+"\n\n");

                final List<String> wifiSSIdList = new ArrayList<String>();
                for(int i = 0; i < scanResults.size(); i++){

                    sb.append(new Integer(i+1).toString() + ". ");
                    Log.i(LOG_TAG, "scan result get i," + scanResults.get(i).toString());
                    sb.append((scanResults.get(i)).toString());
                    sb.append("\n\n");
                    wifiSSIdList.add(scanResults.get(i).SSID);
                }
                ArrayAdapter listAdapter = new ArrayAdapter(getActivity(), R.layout.wifi_list_item, R.id.wifi_name_text, wifiSSIdList);
                wifiItemsList.setAdapter(listAdapter);
                wifiItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String wifiSSIDName = wifiSSIdList.get(position);
                        ((HomeActivity)getActivity()).openAQMConnectionFragment(wifiSSIDName);
                        /*AQMConnectionFragment aqmConnectionFragment = AQMConnectionFragment.newInstance(wifiSSidName);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, aqmConnectionFragment).commit();*/
                    }
                });
            }
        }
    };
}
