package com.ctyon.socketclient.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ctyon.socketclient.R;
import com.ctyon.socketclient.project.util.GpsTool;

public class LocationTestActivity extends Activity {

    private final LocationListener locationlistener = new LocationListener() {
        public void onLocationChanged(Location location) {
            LocationTestActivity.this.updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            LocationTestActivity.this.updateWithNewLocation(null);
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    TextView textview_jd;
    TextView textview_wd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jwd);
        getWindow().addFlags(128);
        this.textview_jd = (TextView) findViewById(R.id.textview_jd);
        this.textview_wd = (TextView) findViewById(R.id.textview_wd);

        GpsTool.toggleGps(getApplicationContext(), true);

        LocationManager locationmanager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(1);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(true);
        criteria.setPowerRequirement(1);
        String provider = locationmanager.getBestProvider(criteria, true);
        updateWithNewLocation(locationmanager.getLastKnownLocation(provider));
        locationmanager.requestLocationUpdates(provider, 1000, 0.0f, this.locationlistener);
    }

    private void updateWithNewLocation(Location location) {
        if (location != null) {
            double jingdu = location.getLongitude();
            double weidu = location.getLatitude();
            this.textview_jd.setText("经度：" + jingdu);
            this.textview_wd.setText("纬度：" + weidu);
            return;
        }
        this.textview_jd.setText("解析中。。。");
        this.textview_wd.setText("请打开GPS");
    }

}
