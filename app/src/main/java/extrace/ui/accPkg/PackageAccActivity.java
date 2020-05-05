package extrace.ui.accPkg;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import extrace.loader.ExpressListLoader;
import extrace.loader.ExpressLoader;
import extrace.loader.TransHistoryLoader;
import extrace.loader.TransPackageLoader;
import extrace.loader.TransHistoryListLoader;
import extrace.loader.UserPackageLoader;
import extrace.misc.model.ExpressSheet;
import extrace.misc.model.PackageRoute;
import extrace.misc.model.TransHistory;
import extrace.misc.model.TransPackage;
import extrace.misc.model.UsersPackage;
import extrace.net.IDataAdapter;
import extrace.ui.domain.ExpressListAdapter;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;
import zxing.util.CaptureActivity;

public class PackageAccActivity extends AppCompatActivity implements IDataAdapter<TransPackage>{


    private Intent mIntent;
    private final int REQUEST_CAPTURE = 100;
    private  final int REQUEST_CAPTURE_SCAN_EXPRESS =101;
    private static final int REQUEST_EXPRESS = 102;
    private TransPackage transPackage;
    private TextView pkg_textView;
    private Button pkg_acc_button;
    private List<TransHistory> transHistoryList;
    private ListView express_list_in_pkg;
    private Button express_scan_btn;


    private  TransPackageLoader transPackageLoader;
    private  TransHistoryListAdapter transHistoryListAdapter;
    private TransHistoryListLoader transhistoryListLoader;
    private TransHistoryLoader transHistoryLoader;
    private ExTraceApplication app;
    private ExpressListAdapter expressListAdapter;
    private  InTransHistory inTransHistory;
    private ExpressLoader expressLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_acc);
        pkg_textView = (TextView)findViewById(R.id.pkg_ac_thing);
        pkg_acc_button = (Button)findViewById(R.id.pkg_acc_btn);
        express_list_in_pkg = (ListView)findViewById(R.id.express_list_in_pkg);
        express_scan_btn = (Button)findViewById(R.id.express_scan);
        pkg_acc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pkgAcc();  //包裹确认操作
            }
        });
        //设置适配器
        expressListAdapter  =new ExpressListAdapter(new ArrayList<ExpressSheet>(),this,"ExDLV");
        express_list_in_pkg.setAdapter(expressListAdapter);

        app = (ExTraceApplication)this.getApplication();
        Log.d("生成的用户获取：",app.getLoginUser().toString());
        express_scan_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartScanExpress();
            }
        });
        mIntent = getIntent();
        if(mIntent.hasExtra("Action")){
            if(mIntent.getStringExtra("Action").equals("Accept")){
                StartCapture();
            }
        }

        inTransHistory = new InTransHistory();
        //得到本站的信息
    }

    //扫描快件确认快件
    private void StartScanExpress() {
        Intent intent = new Intent();
        intent.putExtra("Action","Captrue");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CAPTURE_SCAN_EXPRESS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("PackageAccActivity执行了这个","onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
                switch (requestCode){
                    case REQUEST_CAPTURE:
                        if (data.hasExtra("BarCode")) {//如果扫描结果得到的单号不为空
                            String id = data.getStringExtra("BarCode");
                            transPackageLoader = new TransPackageLoader(this,this);
                            transPackageLoader.Load(id);
                            //得到包裹中快件列表
                            ExpressListLoader expressListLoader = new ExpressListLoader(expressListAdapter,this);
                            expressListLoader.getExpressListInPackage(id);
                        }
                        break;
                    case REQUEST_CAPTURE_SCAN_EXPRESS:
                        if(data.hasExtra("BarCode")){
                            String id = data.getStringExtra("BarCode");
                            Intent intent = new Intent();
                            intent.putExtra("ExpressID",id);
                            intent.setClass(this,ExpressAccActivity.class);
                            startActivityForResult(intent,REQUEST_EXPRESS);
                            //跳转到express详情页面
                        }
                }
        }

    }

    //包裹从某一营业网点转到下一网点，下一网点的扫描员扫描确认包裹，将包裹状态改为4（表示到达转运中心）
    //根据扫描员自身的营业网点信息写入包裹历史，需要写的表packageroute和transhistory
    private void pkgAcc() {
        Log.d("PackageAccActivity执行了：","包裹确认方法");

        if( transPackage.getStatus() == TransPackage.PKG_TRSNSIT ) {

            //1改变包裹状态
            Log.d("PackageAccActivity执行了：", "包裹状态运输中");
            //transPackage.setStatus(TransPackage.PKG_ACCED);
            transPackageLoader = new TransPackageLoader(this, this);
            transPackageLoader.changeTransPackageStatus(transPackage, TransPackage.PKG_ACCED); //改变包裹状态


            //2:往transhistory写入一条记录,包裹确认获取上一站的历史，然后将其setuid改为本站的
            TransHistory transHistory = new TransHistory();
            TransHistory recent = inTransHistory.getData();
            transHistory.setPkg(recent.getPkg());
            transHistory.setX(recent.getX());
            transHistory.setY(recent.getY());
            transHistory.setUIDFrom(recent.getUIDFrom());
            transHistory.setUIDTo(app.getLoginUser().getUID());

            Log.d("pkgAcc 生成的用户获取：",transHistory.toString());
            transHistoryLoader = new TransHistoryLoader(inTransHistory,this);
            transHistoryLoader.AddOneTransHistory(transHistory);

            //往userpackage里面写入一条数据
            InUserPackage inUserPackage = new InUserPackage();
            UsersPackage usersPackage = new UsersPackage();
            TransPackage transPackage1 = transPackage;
            transPackage1.setStatus(TransPackage.PKG_ACCED);
            usersPackage.setPkg(transPackage1);
            usersPackage.setUserU(app.getLoginUser());
            UserPackageLoader userPackageLoader = new UserPackageLoader(inUserPackage,this);
            userPackageLoader.Save(usersPackage);


        }
        else{
            Toast.makeText(this,"包裹状态为"+transPackage.getStatus()+" 不符合要求",Toast.LENGTH_SHORT).show();
        }
    }

    class InUserPackage implements IDataAdapter<UsersPackage>{

        @Override
        public UsersPackage getData() {
            return null;
        }

        @Override
        public void setData(UsersPackage data) {

        }

        @Override
        public void notifyDataSetChanged() {

        }
    }

    class  InExpress implements IDataAdapter<ExpressSheet>{

        @Override
        public ExpressSheet getData() {
            return null;
        }

        @Override
        public void setData(ExpressSheet data) {

        }

        @Override
        public void notifyDataSetChanged() {

        }
    }
    class InTransHistory implements IDataAdapter<TransHistory>{

        private   TransHistory recentTransHistory;
        @Override
        public TransHistory getData() {
            return recentTransHistory;
        }

        @Override
        public void setData(TransHistory data) {
            //Log.d("生成的用户获取：",app.getLoginUser().toString());
            recentTransHistory = data;
        }

        @Override
        public void notifyDataSetChanged() {

        }

    }
    private void StartCapture(){
        Intent intent = new Intent();
        intent.putExtra("Action","Captrue");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    @Override
    public TransPackage getData() {
        return transPackage;
    }


    @Override
    public void setData(TransPackage data) {
        Log.d("PackageAccActivity执行了：","setData");
        transPackage = data;
        //包裹历史最近的一条且终点是本站的记录0-》上一站找到是谁（司机）送过来的-》再把自己的id放进去放入形成一条记录
        //1找到包裹历史里最近的一条记录

        transHistoryLoader = new TransHistoryLoader(inTransHistory,this);
        transHistoryLoader.getRecentOneTranHistory(transPackage);


    }

    @Override
    public void notifyDataSetChanged() {
        Log.d("PackageAccActivity执行了这个","notifyDataSetChanged");
        pkg_textView.setText(transPackage.toString());
    }
}
