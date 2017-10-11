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
    private WifiListAdapter wifiAdapter;
    private List<String> availableWifiSSIdList;
    private Dialog dialog1;

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
        if (getActivity() == null) {
            return;
        }
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
        if (getActivity() != null) {
            getActivity().registerReceiver(wifiScanReceiver,
                    new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            wifiManager.startScan();
        }
    }

    @Override
    public void onPause() {
        if (getActivity() != null) {
            getActivity().unregisterReceiver(wifiScanReceiver);
        }
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
                wifiAdapter = new WifiListAdapter(getActivity(), R.layout.wifi_list_item, availableWifiSSIdList);
               /* wifiListAdapter = new ArrayAdapter(getActivity(), R.layout.wifi_list_item,
                        R.id.wifi_name_text, availableWifiSSIdList);*/
                wifiListView.setAdapter(wifiAdapter);
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
        dialog1 = new Dialog(getActivity());
        dialog1.setContentView(R.layout.wifi_connection_dialog);
        final TextView wifiName = (TextView)dialog1.findViewById(R.id.wifi_ssid_name);
        wifiName.setText(wifiSSIDName);
        final EditText passwordEditText = (EditText)dialog1.findViewById(R.id.input_password_text);
        Button submitButton = (Button)dialog1.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!passwordEditText.getText().toString().isEmpty()) {
                    String wifiPassword = passwordEditText.getText().toString();
                    Log.i(LOG_TAG, "wifiPassword, " + wifiPassword);
                    if (getActivity() == null) {
                        dialog1.dismiss();
                        return;
                    }
                    WifiManager wm = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    List<WifiConfiguration> networks = wm.getConfiguredNetworks();
                    Log.i(LOG_TAG, "all networks, " + networks);
                    if (networks == null) {
                        dialog1.dismiss();
                        return;
                    }
                    Iterator<WifiConfiguration> iterator = networks.iterator();
                    Log.i(LOG_TAG, "iterator, " + iterator.hasNext());
                    dialog1.dismiss();
                    if (getActivity() != null) {
                        ((HomeActivity) getActivity()).openConnectToRouterFragment();
                    }
                } else  {
                    if (getActivity() != null) {
                        Toast.makeText(getActivity(), "Please enter password", Toast.LENGTH_SHORT).show();
                    }
                }
             }
        });
        dialog1.show();
    }

    private void connectToConfiguredWifi(String wifiSSIDName, String wifiPassword) {
        String networkSSID = wifiSSIDName;
        String networkPass = wifiPassword;

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";
        conf.preSharedKey = "\""+ networkPass +"\"";
        if (getActivity() == null) {
            return;
        }
        WifiManager wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);
        Log.i(LOG_TAG, "conf netId, " + wifiManager.addNetwork(conf));
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.saveConfiguration();
                wifiManager.removeNetwork(i.networkId);
                wifiManager.disconnect();
                Log.i(LOG_TAG, "conf net id," + i.networkId);
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }
    }


    private void connectToWifiOnMarshMallow(String wifiSSIDName, String wifiPassword) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = wifiSSIDName;
        wifiConfig.preSharedKey = String.format("\"%s\"", wifiPassword);

        WifiManager wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
//remember id
     //   wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiConfig.status = WifiConfiguration.Status.ENABLED;

        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
      //  wifiConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.);
       // wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.);
        int networkId = wifiManager.getConnectionInfo().getNetworkId();
        Log.i(LOG_TAG, "networkId," + networkId);
       // wifiManager.removeNetwork(networkId);
     //   wifiManager.disconnect();
      //  wifiManager.saveConfiguration();
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        if (netId == -1) {
          netId = getExistingNetworkId(wifiSSIDName);
            Log.i(LOG_TAG, "NetID, " + netId);
        }
        if (netId == -1) {
            Log.i(LOG_TAG, "netId on Android 7, " + networkId);
            wifiManager.enableNetwork(networkId, true);
            wifiManager.reconnect();
        } else {
            Log.i(LOG_TAG, "netId on Android 7, " + netId);
            // wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();
        }
    }

    private void connnectToWifiOnLollipop(String networkSSID, String networkPass,
                                          WifiConfiguration wifiConfig) {     //   WifiConfiguration wifiConfig = iterator.next();
        WifiManager wm = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            wifiConfig.enterpriseConfig.setIdentity(networkSSID);
            wifiConfig.enterpriseConfig.setPassword(networkPass);
            wifiConfig.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
            wifiConfig.enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.MSCHAPV2);
        }
        Log.i(LOG_TAG, "wifiConfig ssid, " + wifiConfig.SSID);
        Log.i(LOG_TAG, "networkSSID, " + "\"" + networkSSID + "\"");
        wifiConfig.hiddenSSID = true;
        wifiConfig.allowedAuthAlgorithms
                .set(WifiConfiguration.AuthAlgorithm.OPEN);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        String netSSID = String.format("\"%s\"", networkSSID);
        Boolean state = false;
        if (wifiConfig.SSID.equals(netSSID)) {
           // wm.saveConfiguration();
            //wm.removeNetwork(wifiConfig.networkId);
             wm.disconnect();
            int netId = wm.addNetwork(wifiConfig);
            WifiInfo wifiInfo = wm.getConnectionInfo();
            Log.i(LOG_TAG, "netId already, " + netId);
            Log.i(LOG_TAG, "wifi network already, " + wifiInfo.getNetworkId());

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
      //  wifiConfig.SSID = wifiSSIDName;
        wifiConfig.preSharedKey = String.format("\"%s\"", wifiPassword);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wifiConfig.hiddenSSID = false;
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
        wifiManager.removeNetwork(networkId);
        wifiManager.disconnect();
        wifiManager.saveConfiguration();
        //remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        Log.i(LOG_TAG, "netId, " + netId);
        Log.i(LOG_TAG, "wifi network, " + wifiConfig.networkId);
     //   wifiManager.disconnect();
        boolean x = wifiManager.enableNetwork(netId, true);
        Log.i(LOG_TAG, "x, " + x);
        Log.i(LOG_TAG, "connected network, " + wifiInfo.getSSID());
        wifiManager.reconnect();
        dialog1.dismiss();
    }

    private int getExistingNetworkId(String SSID) {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration existingConfig : configuredNetworks) {
                if (existingConfig.SSID.equals(SSID)) {
                    return existingConfig.networkId;
                }
            }
        }
        return -1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog1 != null) {
            dialog1.dismiss();
            dialog1 = null;
        }
    }
}
