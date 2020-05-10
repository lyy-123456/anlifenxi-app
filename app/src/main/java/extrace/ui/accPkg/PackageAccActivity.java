package extrace.ui.accPkg;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import extrace.loader.TransNodeLoader;
import extrace.loader.TransPackageLoader;
import extrace.loader.TransHistoryListLoader;
import extrace.loader.UserPackageLoader;
import extrace.misc.model.ExpressSheet;
import extrace.misc.model.PackageRoute;
import extrace.misc.model.TransHistory;
import extrace.misc.model.TransNode;
import extrace.misc.model.TransPackage;
import extrace.misc.model.TransPackageContent;
import extrace.misc.model.UsersPackage;
import extrace.net.IDataAdapter;
import extrace.ui.domain.ExpressListAdapter;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.MainActivity;
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

    private TransNode nowNode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_acc);
        Init();

    }

    private void Init() {
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
            if(mIntent.getStringExtra("Action").equals("Open")){
                StartCapture();
            }
        }

        inTransHistory = new InTransHistory();

        //得到本站的信息
        InTransNode inTransNode = new InTransNode();
        TransNodeLoader transNodeLoader = new TransNodeLoader(inTransNode,this);
        transNodeLoader.Load(app.getLoginUser().getDptID());
    }

    //扫描快件确认快件
    private void StartScanExpress() {
        if( transPackage.getStatus() == TransPackage.PKG_TRSNSIT ){
            Intent intent = new Intent();
            intent.putExtra("Action","Capture");
            intent.setClass(this, CaptureActivity.class);
            startActivityForResult(intent, REQUEST_CAPTURE_SCAN_EXPRESS);
        }
        else{
            Toast.makeText(this,"包裹状态不是运输中",Toast.LENGTH_SHORT).show();
        }

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
                        }
                        break;
                    case REQUEST_CAPTURE_SCAN_EXPRESS:
                        if(data.hasExtra("BarCode")){
                            String id = data.getStringExtra("BarCode");
                            if(expressIsInPackage(id)){ //如果扫描得到的id在包裹里面
                                Intent intent = new Intent();
                                intent.putExtra("ExpressID",id);
                                intent.putExtra("PackageID",transPackage.getID());
                                intent.setClass(this,ExpressAccActivity.class);
                                startActivityForResult(intent,REQUEST_EXPRESS);
                                //跳转到express详情页面
                            }

                        }
                        break;
                    case REQUEST_EXPRESS:
                        if((this.getIntent().getExtras()) != null){
                            ExpressSheet es = (ExpressSheet) (this.getIntent().getExtras()).getSerializable("ExpressSheet");
                            expressListAdapter.getData().remove(es);
                        }


                        break;
                }
        }

    }

    private boolean expressIsInPackage(String id) {
        boolean flag = true;
        for(ExpressSheet es:expressListAdapter.getData()){
            if(!es.getID().equals(id)){
                flag = false;
                break;
            }
        }
        return  flag;
    }

    //包裹从某一营业网点转到下一网点，下一网点的扫描员扫描确认包裹，将包裹状态改为4（表示到达转运中心）
    //根据扫描员自身的营业网点信息写入包裹历史，需要写的表packageroute和transhistory
    private void pkgAcc() {
        Log.d("PackageAccActivity执行了：","包裹确认方法");

        if( transPackage.getStatus() == TransPackage.PKG_TRSNSIT ) {

            if(expressListAdapter.getData().size() != 0){
                AlertDialog alertDialog2 = new AlertDialog.Builder(this)
                        .setTitle("包裹内有未扫描的快件存在！")
                        .setMessage("点击确定后，将把所有未确认的快件状态更改为已丢失，是否确定？")
                        .setIcon(R.mipmap.ic_launcher)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {//添加"Yes"按钮
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(PackageAccActivity.this, "这是确定按钮", Toast.LENGTH_SHORT).show();
                                TransPackageLoader transPackageLoader = new TransPackageLoader(PackageAccActivity.this,PackageAccActivity.this);
                                transPackageLoader.changeExpressListStatusInTransPackage(transPackage.getID(), TransPackageContent.STATUS.STATUS_LOST);
                                databaseDao();//将所有状态改为已丢失
                                finish();
                            }
                        })

                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {//添加取消
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(PackageAccActivity.this, "这是取消按钮", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create();
                alertDialog2.show();
            }else{
                databaseDao();
            }
        }
        else{
            Toast.makeText(this,"包裹状态为"+transPackage.getStatus()+" 不符合要求",Toast.LENGTH_SHORT).show();
        }
    }

    private void databaseDao(){
        //1改变包裹状态
        Log.d("PackageAccActivity执行了：", "包裹状态运输中");
        //transPackage.setStatus(TransPackage.PKG_ACCED);
        transPackageLoader = new TransPackageLoader(this, this);
        transPackageLoader.changeTransPackageStatus(transPackage, TransPackage.PKG_UNPACKED); //改变包裹状态


        //2:往transhistory写入一条记录,包裹确认获取上一站的历史，然后将其uidto改为本站的
        TransHistory transHistory = new TransHistory();
        TransHistory recent = inTransHistory.getData();
        transHistory.setPkg(recent.getPkg());
        transHistory.setX(nowNode.getX());
        transHistory.setY(nowNode.getY());
        transHistory.setUIDFrom(recent.getUIDFrom());
        transHistory.setUIDTo(app.getLoginUser().getUID());

        Log.d("pkgAcc 生成的用户获取：",transHistory.toString());
        transHistoryLoader = new TransHistoryLoader(inTransHistory,this);
        transHistoryLoader.AddOneTransHistory(transHistory);

        //3往userpackage里面写入一条数据
        InUserPackage inUserPackage = new InUserPackage();
        UsersPackage usersPackage = new UsersPackage();
        TransPackage transPackage1 = transPackage;
        transPackage1.setStatus(TransPackage.PKG_UNPACKED);
        usersPackage.setPkg(transPackage1);
        usersPackage.setUserU(app.getLoginUser());
        UserPackageLoader userPackageLoader = new UserPackageLoader(inUserPackage,this);
        userPackageLoader.Save(usersPackage);
    }
    class InTransNode implements IDataAdapter<TransNode>{

        @Override
        public TransNode getData() {
            return nowNode;
        }

        @Override
        public void setData(TransNode data) {
            nowNode = data;
        }

        @Override
        public void notifyDataSetChanged() {

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
        intent.putExtra("Action","Capture");
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
//        if(data.getTargetNode().equals(app.getLoginUser().getDptID())){
//            Toast.makeText(this,"该包裹不是发往本站点的",Toast.LENGTH_SHORT).show();
//            return;
//        }
        transPackage = data;
        //包裹历史最近的一条且终点是本站的记录0-》上一站找到是谁（司机）送过来的-》再把自己的id放进去放入形成一条记录

        //1：得到包裹中快件列表
        ExpressListLoader expressListLoader = new ExpressListLoader(expressListAdapter,this);
        expressListLoader.getExpressListInPackage(transPackage.getID());

        //2：找到包裹历史里最近的一条记录
        transHistoryLoader = new TransHistoryLoader(inTransHistory,this);
        transHistoryLoader.getRecentOneTranHistory(transPackage);

    }

    @Override
    public void notifyDataSetChanged() {
        Log.d("PackageAccActivity执行了这个","notifyDataSetChanged");
        pkg_textView.setText(transPackage.toString());
    }
}
