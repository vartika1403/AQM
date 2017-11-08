package com.example.admin.aqm;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.MetadataChangeSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


public class DashboardActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback {
    private static final String LOG_TAG = DashboardActivity.class.getSimpleName();
    private static final String URL = "https://awonmn7coi.execute-api.ap-southeast-1.amazonaws.com" +
            "/prod/getlivedatabyid?deviceId=AQMDevice03";
    private ArrayList<AQMFeature> featureList = new ArrayList<AQMFeature>();
    private static final int CODE_WRITE_SETTINGS_PERMISSION = 1;
    protected static final int REQUEST_CODE_RESOLUTION = 2;
    private static final int REQUEST_CODE_CAPTURE_IMAGE = 3;
    private static final int REQUEST_CODE_CREATOR = 4;
    private GoogleApiClient googleApiClient;
    private Bitmap bitmapToSave;
    private Bitmap image;
    private DriveId driveId;

    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.download_image)
    ImageView downloadImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        if (PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Log.i(LOG_TAG, "permission granted");
        } else {
            requestPermission(this);
        }
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
        String temp = (Integer) payloadObject.get("TMP") + " \u2103";
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
        String image9 = "VOC";
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
                        textImage.setText(Html.fromHtml("CO<sub>2</sub>", Html.FROM_HTML_MODE_LEGACY));
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

    @OnClick(R.id.download_image)
    public void downloadData() {
        openDialog();
    }

    private void openDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(DashboardActivity.this);
        alertBuilder.setTitle("Where do you want to download data")
                .setItems(R.array.dowload_option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            Log.i(LOG_TAG, "item google drive clicked, " + i);
                          //  downloadDataViaGoogleDrive();
                        } else if (i == 1) {
                            Log.i(LOG_TAG, "item local storage clicked, " + i);
                            downloadDataInFileManager();
                        }
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    private void downloadDataInFileManager() {
        String fileNameXls = "air_quality_data" + ".xls";
        File sdCard = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Log.i(LOG_TAG, "sdCard, " + sdCard);
        File directory = new File(sdCard.getAbsolutePath() + "/new");
        Log.i(LOG_TAG, "directory, " + directory);
        boolean isPresent = directory.mkdirs();
        if (!isPresent) {
            Log.i(LOG_TAG, "isPresent is false, " + directory);
            return;
        }
        // make a excel file
        File file = new File(directory, fileNameXls);
        Log.i(LOG_TAG, "the file, " + file);
        WorkbookSettings workbookSettings = new WorkbookSettings();
        workbookSettings.setLocale(new Locale("en", "EN"));
        WritableWorkbook workbook;
        try {
            workbook = Workbook.createWorkbook(file, workbookSettings);
            WritableSheet sheet = workbook.createSheet("First Sheet", 0);
            for (int i = 0; i < featureList.size(); i++) {
                String featureText = " ";
                Log.i(LOG_TAG, "feature list, " + featureList.get(i));
                if ((featureList.get(i).getImage() instanceof Integer
                        && (Integer) featureList.get(i).getImage() == R.drawable.ic_temperature)) {
                    featureText = "Temperature";
                } else if (featureList.get(i).getImage() instanceof Integer
                        && (Integer) featureList.get(i).getImage() == R.drawable.ic_humidity) {
                    featureText = "Humidity";
                } else {
                    featureText = (String) featureList.get(i).getImage();
                }

                Label label0 = new Label(0, i, featureText);
                Label label1 = new Label(1, i, featureList.get(i).getQuality());
                Label label2 = new Label(2, i, featureList.get(i).getPercentage());

                try {
                    sheet.addCell(label0);
                    sheet.addCell(label1);
                    sheet.addCell(label2);
                } catch (RowsExceededException e) {
                    e.printStackTrace();
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            }
            workbook.write();

            try {
                workbook.close();
            } catch (WriteException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadDataViaGoogleDrive() {
/*
        if (bitmapToSave == null) {
            // This activity has no UI of its own. Just start the camera.
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                    REQUEST_CODE_CAPTURE_IMAGE);
            return;
        }
*/
        //saveFileToDrive();
        saveExcelFileToDrive();
    }

    private void saveExcelFileToDrive() {
        }

    private void requestPermission(final Context context) {
        if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(context)
                    .setMessage("Write Permission")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context,
                                    new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    CODE_WRITE_SETTINGS_PERMISSION);
                        }
                    }).show();

        } else {
            // permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CODE_WRITE_SETTINGS_PERMISSION);
        }
    }

    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i(LOG_TAG, "resultCode, " + resultCode);
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION && Settings.System.canWrite(this)) {
            Log.d(LOG_TAG, "CODE_WRITE_SETTINGS_PERMISSION success");
            //do your code
        }
/*
        if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
            googleApiClient.connect();
            Log.d(LOG_TAG, "Request code resolution success");
        }
*/

        switch (requestCode) {
            case REQUEST_CODE_CAPTURE_IMAGE:
                // Called after a photo has been taken.
                if (resultCode == Activity.RESULT_OK) {
                    // Store the image data as a bitmap for writing later.
                    bitmapToSave = (Bitmap) data.getExtras().get("data");
                    Log.i(LOG_TAG, "capture image , bitmap image, " + bitmapToSave);
                }
                break;
            case REQUEST_CODE_CREATOR:
                // Called after a file is saved to Drive.
                if (resultCode == RESULT_OK) {
                    Log.i(LOG_TAG, "Image successfully saved.");
                    bitmapToSave = null;
                    // Just start the camera again for another photo.
                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                            REQUEST_CODE_CAPTURE_IMAGE);
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODE_WRITE_SETTINGS_PERMISSION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //do your code
            Log.i(LOG_TAG, "permission requested granted success");
        } else {
            Log.i(LOG_TAG, "permission failed");
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(LOG_TAG, "API client connected.");
        // create new contents resource
/*
        if (bitmapToSave == null) {
            // This activity has no UI of its own. Just start the camera.
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE),
                    REQUEST_CODE_CAPTURE_IMAGE);
            return;
        }
        saveFileToDrive();
*/
    }

    private void saveFileToDrive() {
        Log.i(LOG_TAG, "creating new contents");
        Drive.DriveApi.newDriveContents(getGoogleApiClient())
                .setResultCallback(driveContentsCallback);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "connection failed, " + connectionResult);
        // Called whenever the API client fails to connect.
        if (!connectionResult.hasResolution()) {
            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this,
                    connectionResult.getErrorCode(), 0).show();
            return;
        }
        try {
            connectionResult.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(LOG_TAG, "Exception while starting resolution activity", e);
        }
    }

    final private ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(@NonNull DriveApi.DriveContentsResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.i("Error", "fail");
                        return;
                    }

                    Log.i(LOG_TAG, "New contents created.");
                    // Get ano output stream for the contents.
                    image = bitmapToSave;
                    Log.i(LOG_TAG, "image value, " + image);
                    OutputStream outputStream = result.getDriveContents().getOutputStream();
                    // Write the bitmap data from it.
                    ByteArrayOutputStream bitmapStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, bitmapStream);
                    try {
                        outputStream.write(bitmapStream.toByteArray());
                    } catch (IOException e1) {
                        Log.i(LOG_TAG, "Unable to write file contents.");
                    }

                    MetadataChangeSet metadataChangeSet = new MetadataChangeSet.Builder()
                            .setMimeType("text/plain")
                            .setMimeType("image/jpeg").setTitle("Android Photo.png").build();
                    // Create an intent for the file chooser, and start it.
                    IntentSender intentSender = Drive.DriveApi
                            .newCreateFileActivityBuilder()
                            .setInitialMetadata(metadataChangeSet)
                            .setInitialDriveContents(result.getDriveContents())
                            .build(googleApiClient);
                    try {
                        startIntentSenderForResult(
                                intentSender, REQUEST_CODE_CREATOR, null, 0, 0, 0);
                    } catch (IntentSender.SendIntentException e) {
                        Log.i(LOG_TAG, "Failed to launch file chooser.");
                    }

/*
                    MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                            .setTitle("appconfig.txt")
                            .setMimeType("text/plain")
                            .setStarred(true)
                            .build();
*/
/*
                    Drive.DriveApi.getAppFolder(getGoogleApiClient())
                            .createFile(getGoogleApiClient(), changeSet, result.getDriveContents())
                            .setResultCallback(fileCallback);
*/
                }
            };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new
            ResultCallback<DriveFolder.DriveFileResult>() {
                @Override
                public void onResult(@NonNull DriveFolder.DriveFileResult result) {
                    if (!result.getStatus().isSuccess()) {
                        Log.i("Error", "fail");
                        return;
                    }
                    Log.i(LOG_TAG, "file in folder, " + result.getDriveFile().getDriveId());
                }
            };

    @Override
    public void onResult(@NonNull Result result) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API).addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    // required for App Folder sample
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            Log.i(LOG_TAG, "googleApiClient, " + googleApiClient);
        }
        googleApiClient.connect();
    }

    @Override
    protected void onPause() {
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        super.onPause();
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }
}
