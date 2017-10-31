package com.example.admin.aqm;

import android.app.DownloadManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DashboardActivity extends AppCompatActivity {
    private static final String LOG_TAG = DashboardActivity.class.getSimpleName();
    private static final String URL = "https://awonmn7coi.execute-api.ap-southeast-1.amazonaws.com/prod/getlivedatabyid?deviceId=AQMDevice03";
    private ArrayList<AQMFeature> featureList = new ArrayList<AQMFeature>();

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        getDataFromServer();
    }

    private void getDataFromServer() {
        final Request request = new Request.Builder()
                .url(URL)
                .build();

        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                 Log.e("the failure ", e.getMessage());
            }

            @Override
            public void onResponse(Call call, @NonNull final Response response) throws IOException {
              Log.i(LOG_TAG, "the response, " + response);
                if (response.isSuccessful()) {
                    final String responseData = response.body().string();
                    Log.i(LOG_TAG, "response data, " + responseData);
                    //show output on main thread
                    DashboardActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                            try {
                                JSONObject jsonObject = new JSONObject(responseData);
                                Log.i(LOG_TAG, "jsonObject, " + jsonObject);
                                JSONObject payloadObject = jsonObject.getJSONObject("Item")
                                        .getJSONObject("payload");
                                Log.i(LOG_TAG, "payloadObject, " + payloadObject);
                                addData(payloadObject);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Log.i(LOG_TAG, "data on ui thread, " + responseData);
                        }
                    });
                }
            }
        });

    }

    private void addData(JSONObject payloadObject) throws JSONException {
        int image1 = R.drawable.ic_humidity;
        String humidity = (Integer) payloadObject.get("HUM") + "%";
        Log.i(LOG_TAG, "humidity, " + humidity);
        String co2Value = (Integer) payloadObject.get("CO2") + " ";
        String temp = (Integer) payloadObject.get("TMP") + " \u2103" ;
        Log.i(LOG_TAG, "temp, " + temp);
        String pmValue = (Integer) payloadObject.get("PM") + "%";
        String vocValue = (Integer) payloadObject.get("VOC") + " ";
        AQMFeature aqmFeature1 = new AQMFeature(image1, "GREAT", humidity);
        featureList.add(aqmFeature1);
        int image2 = R.drawable.ic_humidity;
        AQMFeature aqmFeature2 = new AQMFeature(image2, "UNHEALTHY", co2Value);
        featureList.add(aqmFeature2);
        int image3 = R.drawable.ic_temperature;
        AQMFeature aqmFeature3 = new AQMFeature(image3, "GREAT", temp);
        featureList.add(aqmFeature3);
        int image4 = Integer.parseInt("PM");
        AQMFeature aqmFeature4 = new AQMFeature(image4, "MODERATE", pmValue);
        featureList.add(aqmFeature4);
        int image5 = Integer.parseInt("VOC");
        AQMFeature aqmFeature5 = new AQMFeature(image5, "GREAT", vocValue);
        featureList.add(aqmFeature5);

        LinearLayout aqmFeatureLayout = (LinearLayout) findViewById(R.id.aqm_features);
        for (AQMFeature aqmFeature : featureList) {
            View aqmChildFeatureLayout = getLayoutInflater().inflate(R.layout.aqm_child_features_layout, null);
            ImageView imageView = (ImageView) aqmChildFeatureLayout.findViewById(R.id.aqm_feature_image);
            imageView.setImageResource(aqmFeature.getImage());
            TextView qualityText = (TextView) aqmChildFeatureLayout.findViewById(R.id.quality_text);
            qualityText.setText(aqmFeature.getQuality());
            if (aqmFeature.getQuality().equals("GREAT")) {
                qualityText.setTextColor(Color.parseColor("#83c326"));
            }

            if (aqmFeature.getQuality().equals("UNHEALTHY")) {
                qualityText.setTextColor(Color.parseColor("#f80000"));
            }

            if (aqmFeature.getQuality().equals("MODERATE")) {
                qualityText.setTextColor(Color.parseColor("#fff500"));
            }
            TextView percentageText = (TextView) aqmChildFeatureLayout.findViewById(R.id.percentage_text);
            percentageText.setText(aqmFeature.getPercentage());
            aqmFeatureLayout.addView(aqmChildFeatureLayout);
        }
    }
}
