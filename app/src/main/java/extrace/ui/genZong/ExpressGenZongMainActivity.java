package extrace.ui.genZong;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.yinglan.scrolllayout.ScrollLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import GPS.LocationService;
import extrace.loader.PackageRouteListLoader;
import extrace.loader.TransHistoryDetailListLoader;
import extrace.misc.model.PackageRoute;
import extrace.misc.model.TransHistoryDetail;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;

public class ExpressGenZongMainActivity extends AppCompatActivity implements IDataAdapter<List<TransHistoryDetail>> {

    private LocationService locationService;
    private ScrollLayout mScrollLayout;
    private TextView text_foot;
    private ListViewAdapter listViewAdapter;
    private List<TransHistoryDetail> transHistoryDetails;
    private List<PackageRoute> packageRouteList;
    private String expressID;
    private MapView mapView = null;
    private BaiduMap mBaiduMap = null;
    private List<Double> latitudeList = new ArrayList<Double>();
    private List<Double> longitudeList = new ArrayList<Double>();
    private double maxLatitude;
    private double minLatitude;
    private double maxLongitude;
    private double minLongitude;
    private double distance;
    private float level;
    private LatLng center;

    private List<LatLng> points;
    private InfoWindow infoWindow;
    private ScrollLayout.OnScrollChangedListener mOnScrollChangedListener = new ScrollLayout.OnScrollChangedListener() {
        @Override
        public void onScrollProgressChanged(float currentProgress) {
            if (currentProgress >= 0) {
                float precent = 255 * currentProgress;
                if (precent > 255) {
                    precent = 255;
                } else if (precent < 0) {
                    precent = 0;
                }
                mScrollLayout.getBackground().setAlpha(255 - (int) precent);
            }
            if (text_foot.getVisibility() == View.VISIBLE)
                text_foot.setVisibility(View.GONE);
        }

        @Override
        public void onScrollFinished(ScrollLayout.Status currentStatus) {
            if (currentStatus.equals(ScrollLayout.Status.EXIT)) {
                text_foot.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onChildScroll(int top) {
        }
    };

    private void getTransHistory(){
        TransHistoryDetailListLoader transHistoryDetailListLoader = new TransHistoryDetailListLoader(this,this);
        transHistoryDetailListLoader.getTransHistoryDetailList(expressID);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_gen_zong_main);
        initView();
        initMap();
    }

    private void initMap() {
        //获取地图控件引用

        mBaiduMap = mapView.getMap();

//        //设置指南针位置
//        mBaiduMap.setCompassPosition(new android.graphics.Point(70, 380));

        //普通地图 ,mBaiduMap是地图控制器对象
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
//        //开启定位图层
//        mBaiduMap.setMyLocationEnabled(true);

        //定位到本地
        locationService = new LocationService(this);
        locationService.registerListener(mListener);
        LocationClientOption diyOption = locationService.getOption();
        diyOption.setCoorType("bd09ll");
        diyOption.setScanSpan(0);//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
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
        locationService.start();
    }
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener(){

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            LatLng latLng = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
            //让地图以被点击的覆盖物为中心
            MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(latLng);
//                mBaiduMap.setMapStatus(status);
            //以动画方式更新地图状态，动画耗时 500 ms
            mBaiduMap.animateMapStatus(status, 500);
            //构建marker图标
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);
            //构建MarkerOption，用于在地图上添加Marker
            MarkerOptions option = new MarkerOptions().icon(bitmap).position(latLng);
            //生长动画
            option.animateType(MarkerOptions.MarkerAnimateType.grow);
            //在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);

        }
    };
    
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mapView.onDestroy();
    }
    private void initView() {
        FrameLayout relativeLayout = (FrameLayout) findViewById(R.id.root);
        mScrollLayout = (ScrollLayout) findViewById(R.id.scroll_down_layout);
        text_foot = (TextView) findViewById(R.id.text_foot);
        mapView =(MapView)findViewById(R.id.genzong_map);
        ListView listView = (ListView) findViewById(R.id.list_view);
        SearchView searchView = (SearchView)findViewById(R.id.search);
        transHistoryDetails = new ArrayList<TransHistoryDetail>();
        listViewAdapter = new  ListViewAdapter(transHistoryDetails,this);
        listView.setAdapter(listViewAdapter);

        /**设置 setting*/
        mScrollLayout.setMinOffset(0);
        mScrollLayout.setMaxOffset((int) (ScreenUtil.getScreenHeight(this) * 0.5));
        mScrollLayout.setExitOffset(ScreenUtil.dip2px(this, 50));
        mScrollLayout.setIsSupportExit(true);
        mScrollLayout.setAllowHorizontalScroll(true);
        mScrollLayout.setOnScrollChangedListener(mOnScrollChangedListener);
        mScrollLayout.setToExit();

        mScrollLayout.getBackground().setAlpha(0);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScrollLayout.scrollToExit();
            }
        });

        text_foot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScrollLayout.setToOpen();
            }
        });

        //设置输入框的监听器
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                expressID = query;
                getTransHistory();
                getExpressRoute();
                Toast.makeText(getBaseContext(), query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    //得到快件的所有点
    private void getExpressRoute() {
        InPackageRouteList inPackageRouteList = new InPackageRouteList();
        PackageRouteListLoader packageRouteListLoader =new PackageRouteListLoader(inPackageRouteList,this);
        packageRouteListLoader.getPackageRouteListByExpressId(expressID);
    }

    class InPackageRouteList implements IDataAdapter<List<PackageRoute>>{

        @Override
        public List<PackageRoute> getData() {
            return packageRouteList;
        }

        @Override
        public void setData(List<PackageRoute> data) {
            packageRouteList = data;
            Collections.sort(packageRouteList);
            DrawMapView();
        }

        @Override
        public void notifyDataSetChanged() {

        }
    }

    //绘制地图
    private void DrawMapView() {
        mBaiduMap.clear();
        Toast.makeText(this,"绘制路线中",Toast.LENGTH_SHORT).show();
        points = new ArrayList<LatLng>();
        for(PackageRoute packageRoute :packageRouteList){
            System.out.println(packageRoute.toString());
            LatLng latLng = new LatLng(packageRoute.getY(),packageRoute.getX());
            points.add(latLng);
        }
        //设置折线的属性
        OverlayOptions mOverlayOptions = new PolylineOptions()
                .width(10)
                .color(0xAAFF0000)
                .points(points);
        //在地图上绘制折线
        // mPloyline 折线对象
        Overlay mPolyline = mBaiduMap.addOverlay(mOverlayOptions);

        showLineMarker();
        //比较选出集合中最大经纬度
        getMax();
        //计算两个Marker之间的距离
        calculateDistance();
        //根据距离判断地图级别
        getLevel();
        //计算中心点经纬度，将其设为启动时地图中心
        setCenter();
        setMarkerClick();
    }

    /**
     * 根据坐标点绘制Marker
     */
    private void showLineMarker() {
        //构建marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_launcher);

        for (int i = 0; i < points.size(); i++) {
            //构建MarkerOption，用于在地图上添加Marker
            MarkerOptions option = new MarkerOptions().icon(bitmap).position(points.get(i));
            //生长动画
            option.animateType(MarkerOptions.MarkerAnimateType.grow);
            //在地图上添加Marker，并显示
            mBaiduMap.addOverlay(option);
            //设置Marker覆盖物的ZIndex
            option.zIndex(i);
        }
    }

    /**
     * 比较选出集合中最大经纬度
     */
    private void getMax() {
        for (int i = 0; i < points.size(); i++) {
            double latitude = points.get(i).latitude;
            double longitude = points.get(i).longitude;
            latitudeList.add(latitude);
            longitudeList.add(longitude);
        }
        maxLatitude = Collections.max(latitudeList);
        minLatitude = Collections.min(latitudeList);
        maxLongitude = Collections.max(longitudeList);
        minLongitude = Collections.min(longitudeList);
    }

    /**
     * 计算两个Marker之间的距离
     */
    private void calculateDistance() {
        distance = GeoHasher.GetDistance(maxLatitude, maxLongitude, minLatitude, minLongitude);
    }

    /**
     *根据距离判断地图级别
     */
    private void getLevel() {
        int zoom[] = {10, 20, 50, 100, 200, 500, 1000, 2000, 5000, 1000, 2000, 25000, 50000, 100000, 200000, 500000, 1000000, 2000000};
        Log.i("info", "maxLatitude==" + maxLatitude + ";minLatitude==" + minLatitude + ";maxLongitude==" + maxLongitude + ";minLongitude==" + minLongitude);
        Log.i("info", "distance==" + distance);
        for (int i = 0; i < zoom.length; i++) {
            int zoomNow = zoom[i];
            if (zoomNow - distance * 1000 > 0) {
                level = 18 - i + 6;
                //设置地图显示级别为计算所得level
                mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(new MapStatus.Builder().zoom(level).build()));
                break;
            }
        }
    }

    /**
     * 计算中心点经纬度，将其设为启动时地图中心
     */
    private void setCenter() {

        center = new LatLng((maxLatitude + minLatitude) / 2, (maxLongitude + minLongitude) / 2);
        Log.i("info", "center==" + center);
        MapStatusUpdate status1 = MapStatusUpdateFactory.newLatLng(center);
        mBaiduMap.animateMapStatus(status1, 500);
    }


    /**
     * 设置Marker点击事件
     */
    private void setMarkerClick() {
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                final LatLng ll = marker.getPosition();

                GeoCoder mSearch = GeoCoder.newInstance();
                final InfoView infoView = new InfoView(getApplicationContext());
                mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
                    @Override
                    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

                    }

                    @Override
                    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
                        Log.d("打印转换后的地址" ,reverseGeoCodeResult.getAddress());
                        DecimalFormat df = new DecimalFormat("#.000");
                        String lot = df.format(ll.longitude);
                        String lat = df.format(ll.latitude);
                        infoView.setBackgroundResource(R.drawable.button);
                        infoView.setTv1(reverseGeoCodeResult.getAddress(), 14, Color.GREEN);
                        infoView.setTv2("经度:"+lot+" 纬度"+lat, 10, Color.RED);
                        infoView.setTv3("城市代码:"+reverseGeoCodeResult.getCityCode(), 10, Color.BLACK);
                        //初始化infoWindow，最后那个参数表示显示的位置相对于覆盖物的竖直偏移量，这里也可以传入一个监听器
                        infoWindow = new InfoWindow(infoView, ll, -100);
                        mBaiduMap.showInfoWindow(infoWindow);//显示此infoWindow

                    }
                });
                //下面是传入对应的经纬度
                mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(new LatLng(ll.latitude, ll.longitude)));
                //动态生成一个view对象，用户在地图中显示InfoWindow

                infoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int j = 0; j < points.size(); j++) {
                            LatLng point = points.get(j);
                            if (ll.equals(point)) {
                                Toast.makeText(ExpressGenZongMainActivity.this, "point=" + point, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });


                //让地图以被点击的覆盖物为中心
                MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(ll);
//                mBaiduMap.setMapStatus(status);
                //以动画方式更新地图状态，动画耗时 500 ms
                mBaiduMap.animateMapStatus(status, 500);
                return true;
            }
        });
    }
    @Override
    public List<TransHistoryDetail> getData() {
        return listViewAdapter.getData();
    }

    @Override
    public void setData(List<TransHistoryDetail> data) {
        Collections.sort(data);

        listViewAdapter.setData(data);

    }

    @Override
    public void notifyDataSetChanged() {
        listViewAdapter.notifyDataSetChanged();
    }
}
