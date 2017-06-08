package com.leonardo.askforlocationpermission;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int LOCATION_INTERVAL_SECONDS = 30;
    private static final int LOCATION_INTERVAL_MILLISECONDS = MILLISECONDS_PER_SECOND * LOCATION_INTERVAL_SECONDS;
    private static final int REQUEST_CHECK_SETTINGS = 111;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequestHighAccuracy;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.location_request);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        createLocationRequest();
        setFabOnClickListener();
    }

    private void setFabOnClickListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequestHighAccuracy);
                builder.setAlwaysShow(true);
                PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
                result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                    @Override
                    public void onResult(LocationSettingsResult result) {
                        final Status status = result.getStatus();
                        switch (status.getStatusCode()) {

                            case LocationSettingsStatusCodes.SUCCESS:
                                doAction();
                                break;

                            default:
                                try {
                                    status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException e) {
                                }
                                break;
                        }
                    }
                });
            }
        });
    }

    private void createLocationRequest() {
        mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setInterval(LOCATION_INTERVAL_MILLISECONDS);
        mLocationRequestHighAccuracy.setFastestInterval(LOCATION_INTERVAL_MILLISECONDS / 10);
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        doAction();
                        // All required changes were successfully made
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(MainActivity.this, "RESULT_CANCELED", Toast.LENGTH_SHORT).show();
                        // The user was asked to change settings, but chose not to
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    private void doAction() {
        Toast.makeText(MainActivity.this, "EVERYTHING IS OK", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }
}
