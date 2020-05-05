package extrace.ui.zhuanyun;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.Poi;

import GPS.LocationService;
import extrace.loader.PackageRouteLoader;
import extrace.misc.model.ListTransPackage;
import extrace.misc.model.PackageRoute;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;

public class MyLocationActivity extends AppCompatActivity {
    private LocationService locationService;
    private TextView mTextView;
    private Button button;
    private ListTransPackage listTransPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        Log.d("myLocation中的listTransPackage","onCreate");
        setTitle("转运定位");
        Intent mIntent = this.getIntent();
        Bundle bundle = mIntent.getExtras();
        if(bundle!= null ){
            Log.d("myLocation中的listTransPackage","BUNDLE！=null");
            listTransPackage = (ListTransPackage) bundle.getSerializable("ListTransPackage");
            if (listTransPackage != null){
                Log.d("myLocation中的listTransPackage",listTransPackage.toString());
                Toast.makeText(this,"listTransPackage",Toast.LENGTH_SHORT).show();
            }
        }

        mTextView = findViewById(R.id.textView01);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (button.getText().toString().equals("打开定位")) {
                    locationService.start();
                } else {
                    locationService.stop();
                    button.setText("打开定位");
                    mTextView.setText("");
                }
            }
        });

        locationService = new LocationService(this);
//        多个activity
//        locationService = ((App) getApplication()).locationService;
        locationService.registerListener(mListener);
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                button.setText("停止定位");

                //得到经纬度：写入packageRoute
                writePackageRoute(location);


                StringBuilder sb = new StringBuilder(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocTypeDescription());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\nradius : ");// 半径
                sb.append(location.getRadius());
                sb.append("\nCountryCode : ");// 国家码
                sb.append(location.getCountryCode());
                sb.append("\nCountry : ");// 国家名称
                sb.append(location.getCountry());
                sb.append("\ncitycode : ");// 城市编码
                sb.append(location.getCityCode());
                sb.append("\ncity : ");// 城市
                sb.append(location.getCity());
                sb.append("\nDistrict : ");// 区
                sb.append(location.getDistrict());
                sb.append("\nStreet : ");// 街道
                sb.append(location.getStreet());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                sb.append(location.getUserIndoorState());
                sb.append("\nDirection(not all devices have value): ");
                sb.append(location.getDirection());// 方向
                sb.append("\nlocationdescribe: ");
                sb.append(location.getLocationDescribe());// 位置语义化信息
                sb.append("\nPoi: ");// POI信息
                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {
                    for (int i = 0; i < location.getPoiList().size(); i++) {
                        Poi poi = (Poi) location.getPoiList().get(i);
                        sb.append(poi.getName() + ";");
                    }
                }
                if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                    sb.append("\nspeed : ");
                    sb.append(location.getSpeed());// 速度 单位：km/h
                    sb.append("\nsatellite : ");
                    sb.append(location.getSatelliteNumber());// 卫星数目
                    sb.append("\nheight : ");
                    sb.append(location.getAltitude());// 海拔高度 单位：米
                    sb.append("\ngps status : ");
                    sb.append(location.getGpsAccuracyStatus());// *****gps质量判断*****
                    sb.append("\ndescribe : ");
                    sb.append("gps定位成功");
                } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                    // 运营商信息
                    if (location.hasAltitude()) {// *****如果有海拔高度*****
                        sb.append("\nheight : ");
                        sb.append(location.getAltitude());// 单位：米
                    }
                    sb.append("\noperationers : ");// 运营商信息
                    sb.append(location.getOperators());
                    sb.append("\ndescribe : ");
                    sb.append("网络定位成功");
                } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                    sb.append("\ndescribe : ");
                    sb.append("离线定位成功，离线定位结果也是有效的");
                } else if (location.getLocType() == BDLocation.TypeServerError) {
                    sb.append("\ndescribe : ");
                    sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
                } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                    sb.append("\ndescribe : ");
                    sb.append("网络不同导致定位失败，请检查网络是否通畅");
                } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                    sb.append("\ndescribe : ");
                    sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
                }
                logMsg(sb.toString());
            }
        }

    };

    //操作pckageroute表，写入数据
    private void writePackageRoute(BDLocation location) {

        if(listTransPackage == null || listTransPackage.getTransPackageList().size() == 0){
            Toast.makeText(this,"包裹为空！",Toast.LENGTH_SHORT).show();
            return;
        }
        InPackageRoute inPackageRoute = new InPackageRoute();
        PackageRouteLoader packageRouteLoader = new PackageRouteLoader(inPackageRoute,this);
        packageRouteLoader.SaveListPackageRoute(listTransPackage,(float) location.getLongitude(),(float) location.getLatitude());
    }

    class InPackageRoute implements IDataAdapter<PackageRoute>{

        @Override
        public PackageRoute getData() {
            return null;
        }

        @Override
        public void setData(PackageRoute data) {

        }

        @Override
        public void notifyDataSetChanged() {

        }
    }
    /**
     * 显示请求字符串
     */
    public void logMsg(final String str) {
        //System.out.println("输出这个"+str);
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    mTextView.post(new Runnable() {
                        @Override
                        public void run() {
                            mTextView.setText(str);
                        }
                    });
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
