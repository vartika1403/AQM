package com.example.admin.aqm;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
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
        String image2 = "CO2";
        AQMFeature aqmFeature2 = new AQMFeature(image2, "UNHEALTHY", co2Value);
        featureList.add(aqmFeature2);
        int image3 = R.drawable.ic_temperature;
        AQMFeature aqmFeature3 = new AQMFeature(image3, "GREAT", temp);
        featureList.add(aqmFeature3);
        String image4 = "PM";
        AQMFeature aqmFeature4 = new AQMFeature(image4, "MODERATE", pmValue);
        featureList.add(aqmFeature4);
        String image5 = "VOC";
        AQMFeature aqmFeature5 = new AQMFeature(image5, "GREAT", vocValue);
        featureList.add(aqmFeature5);
        AQMFeature aqmFeature6 = new AQMFeature(image1, "GREAT", humidity);
        featureList.add(aqmFeature6);
        String image6 = "CO2";
        AQMFeature aqmFeature7 = new AQMFeature(image6, "UNHEALTHY", co2Value);
        featureList.add(aqmFeature7);
        int image7 = R.drawable.ic_temperature;
        AQMFeature aqmFeature8 = new AQMFeature(image7, "GREAT", temp);
        featureList.add(aqmFeature8);
        String image8 = "PM";
        AQMFeature aqmFeature9 = new AQMFeature(image8, "MODERATE", pmValue);
        featureList.add(aqmFeature9);
        String image9 ="VOC";
        AQMFeature aqmFeature10 = new AQMFeature(image9, "GREAT", vocValue);
        featureList.add(aqmFeature10);

        LinearLayout aqmFeatureLayout = (LinearLayout) findViewById(R.id.aqm_features);
        for (AQMFeature aqmFeature : featureList) {
            View aqmChildFeatureLayout = getLayoutInflater().inflate(R.layout.aqm_child_features_layout, null);
            ImageView imageView = (ImageView) aqmChildFeatureLayout.findViewById(R.id.aqm_feature_image);
            TextView textImage = (TextView) aqmChildFeatureLayout.findViewById(R.id.aqm_feature_text);
            if (aqmFeature.getImage() instanceof String) {
                if (aqmFeature.getImage().equals("CO2")) {
                    String co2TextValue = getResources().getString(R.string.co2_value);
                    Log.i(LOG_TAG, "co2TextValue, " + co2TextValue);
                    textImage.setVisibility(View.VISIBLE);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                        textImage.setText(Html.fromHtml("CO<sub>2</sub>",Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        textImage.setText(Html.fromHtml("CO<sub>2</sub>"));
                    }

                } else {
                    textImage.setVisibility(View.VISIBLE);
                    textImage.setText(aqmFeature.getImage().toString());
                }
            } else {
                imageView.setImageResource((Integer) aqmFeature.getImage());
            }

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
