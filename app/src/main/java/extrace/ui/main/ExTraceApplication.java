package extrace.ui.main;


import cn.smssdk.SMSSDK;
import extrace.misc.model.UserInfo;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class ExTraceApplication extends Application {
	//private static final String PREFS_NAME = "ExTrace.cfg";
    SharedPreferences settings;// = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
//	String mServerUrl;
//	String mMiscService,mDomainService;
    UserInfo userInfo;
    private volatile Activity mCurrentActivity;//并且添加get set方法
    public synchronized Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public synchronized void setCurrentActivity(Activity activity) {
        mCurrentActivity = activity;
    }

    public String getServerUrl() {
        return settings.getString("ServerUrl", "");
    }  
    public String getMiscServiceUrl() {  
        return getServerUrl() + settings.getString("MiscService", ""); 
    }  
    public String getDomainServiceUrl() {  
        return getServerUrl() + settings.getString("DomainService", ""); 
    }  
  
    public UserInfo getLoginUser(){
    	return userInfo;
    }
    
    @Override  
    public void onCreate() {  
        super.onCreate();  
        settings = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        //SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        SMSSDK.initSDK(this, "2f147ac272b9c", "3d99ba99670eca2e85ede4256b441529");

        registerActivityLifecycleCallbacks(new CCActivityLifecycleCallbacks());

    }
    public void setUserInfo(UserInfo userInfo){
        this.userInfo = userInfo;
        System.out.println(userInfo.toString());
    }
    public void onTerminate() {  
        super.onTerminate();
        //save data of the map  
    }

    public class CCActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
        private boolean mIsInForeground;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            Log.d(TAG, "onActivityStarted :" + activity);
        }

        @Override
        public void onActivityResumed(Activity activity) {
            mIsInForeground = true;
            setCurrentActivity(activity);
        }

        @Override
        public void onActivityPaused(Activity activity) {
            mIsInForeground = false;
            setCurrentActivity(null);
        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {

        }
    }
}
