package com.example.admin.aqm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.Settings;
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
import butterknife.OnClick;

public class AvailableWifiNetworkFragment extends Fragment {
    private static final String LOG_TAG = AvailableWifiNetworkFragment.class.getSimpleName();
    private WifiManager wifiManager;
    private ArrayAdapter listAdapter;
    private List<String> wifiSSIdList;
    private List<String> wifiConnectedList;
    private boolean isConnected = false;
    private boolean isNextButtonClickable = false;

    @BindView(R.id.scan_wifi_text)
    TextView scanWifiText;
    @BindView(R.id.available_aqm_devices_list)
    ListView wifiItemsList;
    @BindView(R.id.next_button_text)
    TextView nextButtonText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            // If wifi disabled then enable it
            Toast.makeText(getActivity(), R.string.wifi_error_message,
                    Toast.LENGTH_LONG).show();

            wifiManager.setWifiEnabled(true);
        }
        getActivity().registerReceiver(aqmScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(aqmScanReceiver,
                new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(aqmScanReceiver);
        super.onPause();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @OnClick(R.id.next_button_text)
    public void clickOnNext() {
      Log.i(LOG_TAG, "next is clicked, " + nextButtonText.isClickable());
        if(isNextButtonClickable) {
            ((HomeActivity)getActivity()).openWifiListFragment();
        }
    }

    private final BroadcastReceiver aqmScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION ) && !isNextButtonClickable) {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                Log.i(LOG_TAG, "scanResult, " + scanResults);
                scanWifiText.setText(R.string.available_networks);
                // add your logic here
                StringBuilder sb = new StringBuilder();
                sb.append("\n   Number Of Wifi connections :" + scanResults.size() + "\n\n");

                wifiSSIdList = new ArrayList<String>();
                for (int i = 0; i < scanResults.size(); i++) {
                    sb.append(new Integer(i + 1).toString() + ". ");
                    Log.i(LOG_TAG, "scan result get i," + scanResults.get(i).toString());
                    sb.append((scanResults.get(i)).toString());
                    sb.append("\n\n");
                    wifiSSIdList.add(scanResults.get(i).SSID);
                }
                listAdapter = new ArrayAdapter(getActivity(), R.layout.wifi_list_item, R.id.wifi_name_text, wifiSSIdList);
                wifiItemsList.setAdapter(listAdapter);
                wifiItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String wifiSSIDName = wifiSSIdList.get(position);
                       /* if (isNextButtonClickable) {
                            getWifiResults();
                            return;
                        }*/
                        openWifiSettings();

                        //((HomeActivity)getActivity()).openAQMConnectionFragment(wifiSSIDName);
                        /*AQMConnectionFragment aqmConnectionFragment = AQMConnectionFragment.newInstance(wifiSSidName);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container, aqmConnectionFragment).commit();*/
                    }
                });
            }
        }
    };

    private void openWifiSettings() {
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //check which request we are responding
        Log.i(LOG_TAG, "onActivityResult, " + requestCode);
        if (requestCode == 1) {
            //check if the request was successful
            Log.i(LOG_TAG, "resultCode, " + resultCode);
            WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext()
                    .getSystemService(Context.WIFI_SERVICE);
            if (!wifiManager.isWifiEnabled()) {
                Toast.makeText(getActivity(),
                        "something went wrong ", Toast.LENGTH_SHORT).show();
            } else {
                //write your code for any kind of network calling to fetch data
                if (wifiConnectedList != null && !wifiConnectedList.isEmpty()) {
                    ((HomeActivity)getActivity()).openConnectToRouterFragment();
                }
                isConnected = true;
                nextButtonText.setClickable(true);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                Log.i(LOG_TAG, "WifiInfo, " + wifiInfo);
                scanWifiText.setText("connected devices");
                wifiConnectedList = new ArrayList<String>();
                wifiConnectedList.add(wifiInfo.getSSID());
              //  wifiSSIdList = wifiConnectedList;
                Log.i(LOG_TAG, "new wifi ssid list, " + wifiSSIdList);
                wifiSSIdList.clear();
                wifiSSIdList.add(wifiInfo.getSSID());
                listAdapter.notifyDataSetChanged();
                nextButtonText.setTextColor(Color.parseColor("#000080"));
                isNextButtonClickable = true;

             //   openAQMConnectionFragment(wifiInfo);
                //scanWifiText.setText("connected devices");
            }
        }
    }

    private void openAQMConnectionFragment(WifiInfo wifiInfo) {
        ((HomeActivity) getActivity()).openAQMConnectionFragment(wifiInfo);
    }
}
