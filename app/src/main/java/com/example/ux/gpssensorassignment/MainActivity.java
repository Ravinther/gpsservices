package com.example.ux.gpssensorassignment;

import android.app.Activity;
import android.app.PendingIntent;
import android.location.Location;
import android.location.LocationListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    private static final String TAG="LocationActivity";
    private static final long Interval=1000*10;
    private static final long FASTEST_INTERVAL=1000*5;

    Button btn;
    TextView txtlocation;

    GoogleApiClient googleApiClient;
    Location mycurrentlocation;
    LocationRequest locationRequest;
    String lastupdate;

    protected  void createlocationrequest()
    {
        locationRequest=new LocationRequest();
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
        locationRequest.setInterval(Interval);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    private  boolean isGoogleplayavailable()
    {
        int status= GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(ConnectionResult.SUCCESS==status)
        {
            return true;
        }
        else
        {
            GooglePlayServicesUtil.getErrorDialog(status,this,0).show();
            return false;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!isGoogleplayavailable())
        {
            finish();
        }
        createlocationrequest();
        googleApiClient=new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        setContentView(R.layout.activity_main);
        txtlocation=(TextView)findViewById(R.id.tvlocation);
        btn=(Button)findViewById(R.id.showbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
    protected void startLocationUpdates()
    {
        PendingResult<Status> pendingResult=LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();;
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mycurrentlocation=location;
        lastupdate = DateFormat.getTimeInstance().format(new Date());
    }


    public void updateUI()
    {
        if (null!=mycurrentlocation)
        {
            String lat=String.valueOf(mycurrentlocation.getLatitude());
            String lon=String.valueOf(mycurrentlocation.getLongitude());
            txtlocation.setText("At Time:"+lastupdate+"\n"
            +"Longitude:"+lon+"\n"
            +"Latitude"+lat+"\n"
            +"Accrecy:"+mycurrentlocation.getAccuracy()+"\n"
            +"Provider"+mycurrentlocation.getProvider());
        }
    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }
    protected void stopLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient,this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (googleApiClient.isConnected())
        {
            startLocationUpdates();
        }
    }
}
