package com.example.admin.aqm;

import android.app.Fragment;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.WIFI_SERVICE;

public class AQMConnectionFragment extends Fragment {
    private static final String LOG_TAG = AQMConnectionFragment.class.getSimpleName();
    private String wifiSSidName;

    @BindView(R.id.aqm_wifi_ssid_name)
    TextView aqmWifiSSidName;
    @BindView(R.id.password_text_box)
    EditText passwordText;
    @BindView(R.id.connect_button)
    Button connectButton;

    public static AQMConnectionFragment newInstance(String param) {
        Bundle bundle = new Bundle();
        bundle.putString("wifiName", param);
        AQMConnectionFragment fragment = new AQMConnectionFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            wifiSSidName = getArguments().getString("wifiName");
            Log.i(LOG_TAG, "wifiSSid name, " + wifiSSidName);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_aqmconnection, container, false);
        ButterKnife.bind(this, view);
        aqmWifiSSidName.setText(wifiSSidName);
        return view;
    }

    @OnClick(R.id.connect_button)
    public void connectWithWifi() {
        if (!passwordText.getText().toString().isEmpty()) {
            String networkPass = passwordText.getText().toString();
            String networkSSID = aqmWifiSSidName.getText().toString();
            Log.i(LOG_TAG, "network ssis string, " + networkSSID);

           // connectToWifi(networkSSID, networkPass);
            connnectToWifiOnLollipop(networkSSID, networkPass);

         /*   WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = "\"" + networkSSID + "\"";
            wifiConfiguration.preSharedKey = "\""+ networkPass +"\"";
            Log.i(LOG_TAG, "networkPass, " + wifiConfiguration.preSharedKey);
          //  wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wifiConfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wifiConfiguration.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            WifiManager wifiManager = (WifiManager)getActivity().getSystemService(WIFI_SERVICE);
            int netId = wifiManager.addNetwork(wifiConfiguration);
            Log.i(LOG_TAG, "netId, " + netId);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netId, true);
            wifiManager.reconnect();*/
        } else {
            Toast.makeText(getActivity(), "Please Enter Password", Toast.LENGTH_SHORT).show();
        }
    }

    private void connnectToWifiOnLollipop(String networkSSID, String networkPass) {
        Log.i(LOG_TAG, "networkssid, " + networkSSID);
        boolean state = false;
       /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {*/
            WifiManager wm = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
           /* if (wm.setWifiEnabled(true)) {*/
                List<WifiConfiguration> networks = wm.getConfiguredNetworks();
               Log.i(LOG_TAG, "all networks, " + networks);
                Iterator<WifiConfiguration> iterator = networks.iterator();
                Log.i(LOG_TAG, "iterator, " + iterator.hasNext());
                while (iterator.hasNext()) {
                    WifiConfiguration wifiConfig = iterator.next();
                    Log.i(LOG_TAG, "wifiConfig ssid, " + wifiConfig.SSID);
                    Log.i(LOG_TAG, "networkSSID, " +  "\""+networkSSID+"\"");
                    String netSSID = "\""+networkSSID+"\"";
                    if (wifiConfig.SSID.equals(netSSID)){
                        state = wm.enableNetwork(wifiConfig.networkId, true);
                    Log.i(LOG_TAG, "state, " + state);
                        Toast.makeText(getActivity(), "The connection is succesfull", Toast.LENGTH_SHORT).show();
                } else
                        wm.disableNetwork(wifiConfig.networkId);
                }
                wm.reconnect();
            }
     //   }
  //  }

    private void connectToWifi(String networkSSID, String networkPass) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(WIFI_SERVICE);
        int networkId = wifiManager.getConnectionInfo().getNetworkId();
        Log.i(LOG_TAG, "networkId," + networkId);
         wifiManager.removeNetwork(networkId);
        wifiManager.saveConfiguration();
        //remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        Log.i(LOG_TAG, "netId, " + netId);
        wifiManager.disconnect();
        boolean x = wifiManager.enableNetwork(netId, true);
        Log.i(LOG_TAG, "x, " + x);
        wifiManager.reconnect();
    }
}
