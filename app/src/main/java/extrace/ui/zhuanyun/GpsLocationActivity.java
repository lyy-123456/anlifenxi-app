package extrace.ui.zhuanyun;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import GPS.util.GPSLocationListener;
import GPS.util.GPSLocationManager;
import GPS.util.GPSProviderStatus;
import extrace.ui.main.R;

public class GpsLocationActivity extends AppCompatActivity {

    private Button gpsBtn;
    private TextView locationText;
    private GPSLocationManager gpsLocationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_loaction);

        initData();
    }

    private void initData() {
        gpsLocationManager = GPSLocationManager.getInstances(GpsLocationActivity.this);

        gpsBtn = (Button)findViewById(R.id.gpsBtn);
        locationText=(TextView)findViewById(R.id.loaction);
        gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsLocationManager.start(new MyListener()); //开启GPS定位
            }
        });
    }

    class MyListener implements GPSLocationListener {


        @Override
        public void UpdateLocation(Location location) {
            if (location != null) {
                locationText.setText("经度：" + location.getLongitude() + "\n纬度：" + location.getLatitude());
            }
        }

        @Override
        public void UpdateStatus(String provider, int status, Bundle extras) {
            if ("gps".equals(provider)) {
                Toast.makeText(GpsLocationActivity.this, "定位类型：" + provider, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void UpdateGPSProviderStatus(int gpsStatus) {
            switch (gpsStatus) {
                case GPSProviderStatus.GPS_ENABLED:
                    Toast.makeText(GpsLocationActivity.this, "GPS开启", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_DISABLED:
                    Toast.makeText(GpsLocationActivity.this, "GPS关闭", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_OUT_OF_SERVICE:
                    Toast.makeText(GpsLocationActivity.this, "GPS不可用", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_TEMPORARILY_UNAVAILABLE:
                    Toast.makeText(GpsLocationActivity.this, "GPS暂时不可用", Toast.LENGTH_SHORT).show();
                    break;
                case GPSProviderStatus.GPS_AVAILABLE:
                    Toast.makeText(GpsLocationActivity.this, "GPS可用啦", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在onPause()方法终止定位  
        gpsLocationManager.stop();
    }
}
