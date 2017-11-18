package com.example.admin.aqm;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.WIFI_SERVICE;

public class AvailableWifiNetworkFragment extends Fragment {
    private static final String LOG_TAG = AvailableWifiNetworkFragment.class.getSimpleName();
    private static final int PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION = 1;
    @BindView(R.id.scan_wifi_text)
    TextView scanWifiText;
    @BindView(R.id.available_aqm_devices_list)
    ListView wifiItemsList;
    @BindView(R.id.list_line)
    View listLine;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    private WifiManager wifiManager;
    private Dialog aqmDialog;
    private List<String> wifiSSIdList;
    private final BroadcastReceiver aqmScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> scanResults = wifiManager.getScanResults();
                Log.i(LOG_TAG, "scanResult, " + scanResults);
               // scanWifiText.setText(R.string.available_networks);
                scanWifiText.setVisibility(View.INVISIBLE);
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
                ArrayAdapter listAdapter = new ArrayAdapter(getActivity(), R.layout.wifi_list_item,
                        R.id.wifi_name_text, wifiSSIdList);
                wifiItemsList.setAdapter(listAdapter);
                wifiItemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String wifiSSIDName = wifiSSIdList.get(position);
                        openAQMConnectionInstructionDialog(wifiSSIDName);
                    }
                });
            }
        }
    };

    private void openAQMConnectionInstructionDialog(final String wifiSSIDName) {
        final Dialog dialog = new Dialog(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.aqm_connection_dialog, null);
        Button okButton = (Button)view.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                openWifiSettings();
                //openAQMConnectionDialog(wifiSSIDName);
            }
        });
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    private void openAQMConnectionDialog(final String wifiSSIDName) {
        aqmDialog = new Dialog(getActivity());
        aqmDialog.setContentView(R.layout.wifi_connection_dialog);
        final TextView wifiName = (TextView)aqmDialog.findViewById(R.id.wifi_ssid_name);
        wifiName.setText(wifiSSIDName);
        final EditText passwordEditText = (EditText)aqmDialog.findViewById(R.id.input_password_text);
        Button submitButton = (Button)aqmDialog.findViewById(R.id.submit_button);
        submitButton.setText("connect");
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!passwordEditText.getText().toString().isEmpty()) {
                    String wifiPassword = passwordEditText.getText().toString();
                    if (getActivity() == null) {
                        return;
                    }
                        openWifiSettings();
                      //  connectToWifiOnMarshMallow(wifiSSIDName, wifiPassword);
                }
        }
    });
        aqmDialog.show();
    }

    private void connectToWifiOnMarshMallow(String wifiSSIDName, String wifiPassword) throws InterruptedException {
        final WifiConfiguration wifiConfig = new WifiConfiguration();
      //  wifiConfig.SSID = wifiSSIDName;
        wifiConfig.SSID = "\"" + wifiSSIDName + "\"";
        wifiConfig.preSharedKey = String.format("\"%s\"", wifiPassword);

        final WifiManager wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
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
            Log.i(LOG_TAG, "netId on Android 7 conf, " + networkId);
            wifiManager.enableNetwork(networkId, true);
            wifiManager.reconnect();
        } else {
            Log.i(LOG_TAG, "netId on Android 7 not conf, " + netId);
             wifiManager.disconnect();
            final Handler handler = new Handler();
            final int finalNetId = netId;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                   // wifiManager.disconnect();
                    Boolean state = wifiManager.enableNetwork(finalNetId, true);
                    Log.i(LOG_TAG, "state, " + state);
                    wifiManager.reconnect();
                    Log.i(LOG_TAG, "wifiSSID," + wifiConfig.SSID);
                }
            }, 3000);
        }
        aqmDialog.dismiss();
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_available_network, container, false);
        ButterKnife.bind(this, view);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle("AQM List");
        Handler hand = new Handler();
        hand.postDelayed(new Runnable() {

            @Override
            public void run() {
                progressBar.setVisibility(View.INVISIBLE);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity() != null &&
                        getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                            PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method

                }else{
                    getWifiResults();
                    //do something, permission was previously granted; or legacy device
                }
               // getWifiResults();
            }
        }, 2000);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void getWifiResults() {
        if (getActivity() != null) {
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
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, "onResume of available");
        getWifiResults();
    }

    @Override
    public void onPause() {
        Log.i(LOG_TAG, "onPause is called");
        if (getActivity() != null) {
            getActivity().unregisterReceiver(aqmScanReceiver);
        }
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.i(LOG_TAG, "onStop is called");
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Log.i(LOG_TAG, "onDestroyView is called");
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        Log.i(LOG_TAG, "onDestroy is called");
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        Log.i(LOG_TAG, "onDetach is called");
       super.onDetach();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


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
            if (getActivity() != null) {
                WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE);
                if (!wifiManager.isWifiEnabled()) {
                    Toast.makeText(getActivity(),
                            "something went wrong ", Toast.LENGTH_SHORT).show();
                } else {
                    ((HomeActivity) getActivity()).openWifiListFragment();
                }
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE_ACCESS_COARSE_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Do something with granted permission
            getWifiResults();
        }
    }
}
