package extrace.ui.main;


import cn.smssdk.SMSSDK;
import extrace.misc.model.UserInfo;
import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

public class ExTraceApplication extends Application {
	//private static final String PREFS_NAME = "ExTrace.cfg";
    SharedPreferences settings;// = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
//	String mServerUrl;
//	String mMiscService,mDomainService;
    UserInfo userInfo;

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

//		//临时造一个用户
//		userInfo = new UserInfo();
//		userInfo.setID(12);
//		userInfo.setDptID("11022800");
//		userInfo.setReceivePackageID("1111112222");
//		userInfo.setTransPackageID("1111115555");
//		userInfo.setDelivePackageID("1111113333");

    }
    public void setUserInfo(UserInfo userInfo){
        this.userInfo = userInfo;
        System.out.println(userInfo.toString());
    }
    public void onTerminate() {  
        super.onTerminate();
        //save data of the map  
    }  
}
