package com.example.admin.aqm;

import android.app.Fragment;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiInfo;
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

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.WIFI_SERVICE;

public class AQMConnectionFragment extends Fragment {
    private static final String LOG_TAG = AQMConnectionFragment.class.getSimpleName();
    private static final int SERVER_PORT = 5000;
    @BindView(R.id.aqm_wifi_ssid_name)
    TextView aqmWifiSSidName;
    @BindView(R.id.password_text_box)
    EditText passwordText;
    @BindView(R.id.connect_button)
    Button connectButton;
    @BindView(R.id.send_input_text)
    EditText sendInputText;
    @BindView(R.id.send_button)
    Button sendButtton;
    private String wifiSSidName;
    private Socket socket;
    private String ipAddressServer;

    public static AQMConnectionFragment newInstance(String param) {
        Bundle bundle = new Bundle();
        bundle.putString("wifiName", param.getSSID());
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

             connectToWifi(networkSSID, networkPass);
            //connnectToWifiOnLollipop(networkSSID, networkPass);
            WifiManager wm = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
            List<WifiConfiguration> networks = wm.getConfiguredNetworks();
            Log.i(LOG_TAG, "all networks, " + networks);
            if (networks == null) {
                //git remote set-url origin git@github.com:vartika1403/VehicleTracking.git  wm.setWifiEnabled(true);
                return;
            }
            Iterator<WifiConfiguration> iterator = networks.iterator();
            Log.i(LOG_TAG, "iterator, " + iterator.hasNext());
            while (iterator.hasNext()) {
                WifiConfiguration wifiConfig = iterator.next();
                Log.i(LOG_TAG, "wifiConfig ssid, " + wifiConfig.SSID);
                Log.i(LOG_TAG, "networkSSID, " + "\"" + networkSSID + "\"");
                String netSSID = "\"" + networkSSID + "\"";
                if (wifiConfig.SSID.equals(netSSID)) {
                     connnectToWifiOnLollipop(networkSSID, networkPass);
                    /*state = wm.enableNetwork(wifiConfig.networkId, true);
                    Log.i(LOG_TAG, "state, " + state);
                    Toast.makeText(getActivity(), "The connection is succesfull", Toast.LENGTH_SHORT).show();
                    ipAddressServer = getIpAddressForServer();
                    Log.i(LOG_TAG, "ipAddress of server, " + ipAddressServer);
*/
                } else
                    connectToWifi(networkSSID, networkPass);
                    wm.disableNetwork(wifiConfig.networkId);
            }
            wm.reconnect();
             connectToWifi(networkSSID, networkPass);
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
        WifiManager wm = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wm.isWifiEnabled()) {
            // If wifi disabled then enable it
            Toast.makeText(getActivity(), R.string.wifi_error_message,
                    Toast.LENGTH_LONG).show();

            wm.setWifiEnabled(true);
        }
        List<WifiConfiguration> networks = wm.getConfiguredNetworks();
        Log.i(LOG_TAG, "all networks, " + networks);
        if (networks == null) {
            //  wm.setWifiEnabled(true);
            return;
        }
        WifiConfiguration wifiConfiguration = new WifiConfiguration();
        wifiConfiguration.SSID = String.format("\"%s\"", networkSSID);
        wifiConfiguration.preSharedKey = String.format("\"%s\"", networkPass);
        Log.i(LOG_TAG, "wifiConfig ssid, " + wifiConfiguration.SSID);
        Log.i(LOG_TAG, "networkSSID, " + "\"" + networkSSID + "\"");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            //wifiConfiguration.enterpriseConfig.setIdentity("name");
            //wifiConfiguration.enterpriseConfig.setPassword("testpassword");
            wifiConfiguration.enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
            wifiConfiguration.enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.MSCHAPV2);
        }
        networks.add(wifiConfiguration);

        /*int netId = wm.addNetwork(wifiConfiguration);
           Log.i(LOG_TAG, "netID, " + netId);
        Log.i(LOG_TAG, "wifinetworkID, " + wifiConfiguration.networkId);
        wm.disconnect();
        state = wm.enableNetwork(netId, true);
        Log.i(LOG_TAG, "state, " + state);
        if (state) {
            Toast.makeText(getActivity(), "The connection is successful", Toast.LENGTH_SHORT).show();
        }

        wm.reconnect();*/
    //}
       Iterator<WifiConfiguration> iterator = networks.iterator();
        Log.i(LOG_TAG, "iterator, " + iterator.next());
        while (iterator.hasNext()) {
            WifiConfiguration wifiConfig = iterator.next();
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
            if (wifiConfig.SSID.equals(netSSID)) {
                state = wm.enableNetwork(wifiConfig.networkId, true);
                Log.i(LOG_TAG, "state, " + state);
                Toast.makeText(getActivity(), "The connection is succesfull", Toast.LENGTH_SHORT).show();
                ipAddressServer = getIpAddressForServer();
                Log.i(LOG_TAG, "ipAddress of server, " + ipAddressServer);

            } else
                wm.disableNetwork(wifiConfig.networkId);
        }
        wm.reconnect();
    }

    private String getIpAddressForServer() {
        try {
            //Loop through all the network interface devices
            for (Enumeration<NetworkInterface> enumeration = NetworkInterface
                    .getNetworkInterfaces(); enumeration.hasMoreElements(); ) {
                NetworkInterface networkInterface = enumeration.nextElement();
                //Loop through all the ip addresses of the network interface devices
                for (Enumeration<InetAddress> enumerationIpAddr = networkInterface.getInetAddresses();
                     enumerationIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumerationIpAddr.nextElement();
                    //Filter out loopback address and other irrelevant ip addresses
                    Log.i(LOG_TAG, "inet Address, " + inetAddress);
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getAddress().length == 4) {
                        //Print the device ip address in to the text view
                        //  tvServerIP.setText(inetAddress.getHostAddress());
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            Log.e("ERROR:", e.toString());
        }
        return " ";
    }
    //   }
    //  }

    private void connectToWifi(String networkSSID, String networkPass) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.status = WifiConfiguration.Status.ENABLED;
        wifiConfig.SSID = String.format("\"%s\"", networkSSID);
        wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        int networkId = wifiManager.getConnectionInfo().getNetworkId();
        Log.i(LOG_TAG, "networkId," + networkId);
       // wifiManager.removeNetwork(networkId);
        wifiManager.saveConfiguration();
        //remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        Log.i(LOG_TAG, "netId, " + netId);
        wifiManager.disconnect();
        boolean x = wifiManager.enableNetwork(networkId, true);
        Log.i(LOG_TAG, "x, " + x);
        wifiManager.reconnect();
    }

    @OnClick(R.id.send_button)
    public void sendDataToServer() {
        if (!sendInputText.getText().toString().isEmpty()) {
            new Thread(new ClientThread()).start();
            String data = sendInputText.getText().toString();
            //    Socket socket = null;
            if (socket != null) {
                try {
                    Log.i(LOG_TAG, "socket , " + socket);
                    PrintWriter out = new PrintWriter(new BufferedWriter(new
                            OutputStreamWriter(socket.getOutputStream())));
                    out.println(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public class ClientThread implements Runnable {

        @Override
        public void run() {
            try {
                InetAddress serverAddress = InetAddress.getByName(ipAddressServer);
                socket = new Socket(serverAddress, SERVER_PORT);
            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
