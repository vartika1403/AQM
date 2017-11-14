package com.example.admin.aqm;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LayoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        LinearLayout aqmFeatureLayout = (LinearLayout) findViewById(R.id.aqm_features);
        for (int i = 0; i < 10; i++) {
            View view  = getLayoutInflater().inflate(R.layout.aqm_feature, null);
            TextView aqmText = (TextView)view.findViewById(R.id.aqm_text);
            TextView aqmPercentage = (TextView)view.findViewById(R.id.aqm_percentage);
            if (i == 1) {
                aqmText.setText("Healthy");
                aqmPercentage.setText("1295");
            }
            aqmFeatureLayout.addView(view);
        }
    }
}
