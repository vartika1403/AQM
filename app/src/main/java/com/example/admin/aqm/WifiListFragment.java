package com.example.admin.aqm;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.WIFI_SERVICE;

public class WifiListFragment extends Fragment {
 private static final String LOG_TAG = WifiListFragment.class.getSimpleName();
    private WifiManager wifiManager;
    private ArrayAdapter wifiListAdapter;
    private List<String> availableWifiSSIdList;

    @BindView(R.id.wifi_list_view)
    ListView wifiListView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wifi_list, container, false);
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
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION )) {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                Log.i(LOG_TAG, "scanResult of wifi, " + scanResults);
                // add your logic here
                StringBuilder sb = new StringBuilder();
                sb.append("\n   Number Of Wifi connections :" + scanResults.size() + "\n\n");

                availableWifiSSIdList = new ArrayList<String>();
                for (int i = 0; i < scanResults.size(); i++) {
                    sb.append(new Integer(i + 1).toString() + ". ");
                    Log.i(LOG_TAG, "scan result get i," + scanResults.get(i).toString());
                    sb.append((scanResults.get(i)).toString());
                    sb.append("\n\n");
                    availableWifiSSIdList.add(scanResults.get(i).SSID);
                }
                wifiListAdapter = new ArrayAdapter(getActivity(), R.layout.wifi_list_item,
                        R.id.wifi_name_text, availableWifiSSIdList);
                wifiListView.setAdapter(wifiListAdapter);
                wifiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String wifiSSIDName = availableWifiSSIdList.get(position);
                        Log.i(LOG_TAG, "wifiSSIDName, " + wifiSSIDName);
                        openDialog(wifiSSIDName);}
                });
            }
        }
    };

    private void openDialog(final String wifiSSIDName) {
        final Dialog dialog1 = new Dialog(getActivity());
        dialog1.setContentView(R.layout.dialog_view);
        TextView wifiName = (TextView)dialog1.findViewById(R.id.wifi_ssid_name);
        wifiName.setText(wifiSSIDName);
        final EditText passwordEditText = (EditText)dialog1.findViewById(R.id.input_password_text);
        Button submitButton = (Button)dialog1.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!passwordEditText.getText().toString().isEmpty()) {
                    String wifiPassword = passwordEditText.getText().toString();
                    WifiManager wm = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    List<WifiConfiguration> networks = wm.getConfiguredNetworks();
                    Log.i(LOG_TAG, "all networks, " + networks);
                    if (networks == null) {
                        return;
                    }
                    Iterator<WifiConfiguration> iterator = networks.iterator();
                    Log.i(LOG_TAG, "iterator, " + iterator.hasNext());
                    while (iterator.hasNext()) {
                        WifiConfiguration wifiConfig = iterator.next();
                        Log.i(LOG_TAG, "wifiConfig ssid, " + wifiConfig.SSID);
                        Log.i(LOG_TAG, "networkSSID, " + "\"" + wifiSSIDName + "\"");
                        String netSSID = "\"" + wifiSSIDName + "\"";
                        if (wifiConfig.SSID.equals(netSSID)) {
                            Log.i(LOG_TAG, "already configured");
                            connnectToWifiOnLollipop(wifiSSIDName, wifiPassword, wifiConfig);
                        } else {
                            Log.i(LOG_TAG, "not already conf");
                            connectToWifi(wifiSSIDName, wifiPassword);
                        }
                        dialog1.dismiss();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_SHORT).show();
                }
             }
        });
        dialog1.show();
    }

    private void connnectToWifiOnLollipop(String networkSSID, String networkPass, WifiConfiguration wifiConfig) {
     //   WifiConfiguration wifiConfig = iterator.next();
        WifiManager wm = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //wifiConfiguration.enterpriseConfig.setIdentity("name");
            //wifiConfiguration.enterpriseConfig.setPassword("testpassword");
            wifiConfig.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
            wifiConfig.enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.MSCHAPV2);
        }
        Log.i(LOG_TAG, "wifiConfig ssid, " + wifiConfig.SSID);
        Log.i(LOG_TAG, "networkSSID, " + "\"" + networkSSID + "\"");
        wifiConfig.hiddenSSID = true;
        wifiConfig.allowedAuthAlgorithms
                .set(WifiConfiguration.AuthAlgorithm.OPEN);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        String netSSID = "\"" + networkSSID + "\"";
        Boolean state = false;
        if (wifiConfig.SSID.equals(netSSID)) {
            state = wm.enableNetwork(wifiConfig.networkId, true);
            Log.i(LOG_TAG, "state, " + state);
            Toast.makeText(getActivity(), "The connection is succesfull", Toast.LENGTH_SHORT).show();
           // ipAddressServer = getIpAddressForServer();
            //Log.i(LOG_TAG, "ipAddress of server, " + ipAddressServer);

        } else {
            wm.disableNetwork(wifiConfig.networkId);
        }

       wm.reconnect();
}

    private void connectToWifi(String wifiSSIDName, String wifiPassword) {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiConfiguration wifiConfig = new WifiConfiguration();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //wifiConfiguration.enterpriseConfig.setIdentity("name");
            //wifiConfiguration.enterpriseConfig.setPassword("testpassword");
            wifiConfig.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
            wifiConfig.enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.MSCHAPV2);
        }

        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        wifiConfig.SSID = String.format("\"%s\"", wifiSSIDName);
        wifiConfig.preSharedKey = String.format("\"%s\"", wifiPassword);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.hiddenSSID = true;
        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        int networkId = wifiManager.getConnectionInfo().getNetworkId();
        Log.i(LOG_TAG, "networkId," + networkId);
      //   wifiManager.removeNetwork(networkId);
        wifiManager.saveConfiguration();
        //remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.i(LOG_TAG, "netId, " + netId);
        Log.i(LOG_TAG, "wifi network, " + wifiConfig.networkId);
        wifiManager.disconnect();
        boolean x = wifiManager.enableNetwork(netId, true);
        Log.i(LOG_TAG, "x, " + x);
        Log.i(LOG_TAG, "connected network, " + wifiInfo.getSSID());
        wifiManager.reconnect();

    }
}
