package extrace.ui.paiSong;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import java.util.Timer;
import java.util.TimerTask;

import GPS.LocationService;
import extrace.loader.PackageRouteLoader;
import extrace.misc.model.PackageRoute;
import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;
import extrace.ui.main.ExTraceApplication;

public class BackLocationService extends Service {
    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private boolean isStop = false;
    private static int delay = 1000; // 1s
    private static int period = 1000; // 1s
    private static String strUrl = "http://192.168.1.113:9191/sf/";
    private TransPackage transPackage;
    private LocationService locationService;

    public BackLocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        locationService = new LocationService(getApplicationContext());
//        多个activity
//        locationService = ((App) getApplication()).locationService;
        LocationClientOption diyOption = locationService.getOption();
        locationService.registerListener( myListener);
        diyOption.setCoorType("bd09ll");
        diyOption.setScanSpan(60*1000);//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        diyOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        diyOption.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        diyOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
        diyOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        diyOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        diyOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        diyOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        diyOption.SetIgnoreCacheException(false) ;//可选，默认false，设置是否收集CRASH信息，默认收集
        diyOption.setOpenGps(true);//可选，默认false，设置是否开启Gps定位
        diyOption.setIsNeedAltitude(false);//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationService.setLocationOption(diyOption);

    }
    public BDAbstractLocationListener myListener = new BDAbstractLocationListener(){

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            getLocationInfo(bdLocation);
        }
    };

    //得到定位信息
    private void getLocationInfo(BDLocation bdLocation) {
        if(locationService != null){
            double lat = bdLocation.getLatitude();
            double lng = bdLocation.getLongitude();
            Log.d("获得的定位信息",lat+" || "+lng);
            PostData(lat,lng);  //向后台传递信息
        }else{
            Log.d("","locationService为空");
        }
    }

    private void PostData(double lat, double lng) {
        PackageRoute packageRoute = new PackageRoute();
        packageRoute.setX((float) lng);
        packageRoute.setY((float) lat);
        packageRoute.setPkg(transPackage);

        InPackageRoute inPackageRoute = new InPackageRoute();
        PackageRouteLoader packageRouteLoader = new PackageRouteLoader(inPackageRoute, ((ExTraceApplication)getApplication()).getCurrentActivity());
        packageRouteLoader.Save(packageRoute);
        Log.d("执行到这个方法","PostData()");
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
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("执行方法","onStartCommand()");
        if(transPackage == null && intent.getExtras() != null){
            transPackage = (TransPackage) intent.getExtras().getSerializable("TransPackage");
        }
        // 触发定时器
        if (!isStop) {
            //Log.i("K", "开始服务");
            locationService.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //Log.d("Res", "onDestroy");
        locationService.stop();
        super.onDestroy();
        // 停止定时器
        if (isStop) {
            Log.i("T", "服务停止");
            stopTimer();
        }
    }

    /*
     * 定时器 每1分钟执行一次
     */
    private void startTimer() {
        Log.i("T", "startTimer");
        if (mTimer == null) {
            mTimer = new Timer();
        }
        //Log.i(TAG, "count: " + String.valueOf(count++));
        isStop = true;
        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    //Log.i(TAG, "count: " + String.valueOf(count++));
                    do {
                        try {
                            locationService.start();
                            Thread.sleep(60*1000);//暂停1分钟
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (isStop);
                }
            };
        }

        if (mTimer != null && mTimerTask != null)
            mTimer.schedule(mTimerTask, delay, period);
    }
    private void stopTimer() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        isStop = false;
    }

}
