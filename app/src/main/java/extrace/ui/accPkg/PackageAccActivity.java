package extrace.ui.accPkg;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
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
    private Button pkg_acc_button;
    private List<TransHistory> transHistoryList;
    private ListView express_list_in_pkg;
    private Button express_scan_btn;

    private TextView pkg_id;
    private TextView pkg_sNode;
    private TextView pkg_eNode;
    private TextView pkg_time;
    private TextView pkg_stauts;

    private TransNode sNode;  //源站点
    private TransNode eNode; //目的站点
    private TransNode nowNode; //扫描员所在站点

    private  TransPackageLoader transPackageLoader;
    private  TransHistoryListAdapter transHistoryListAdapter;
    private TransHistoryListLoader transhistoryListLoader;
    private TransHistoryLoader transHistoryLoader;
    private ExTraceApplication app;
    private ExpressListAdapter expressListAdapter;
    private  InTransHistory inTransHistory;
    private ExpressLoader expressLoader;

    private AlertDialog alertDialog2;
    private boolean isInit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_acc);
        Init();

    }

    private void Init() {
        pkg_id=(TextView)findViewById(R.id.pkg_acc_Id);
        pkg_sNode=(TextView)findViewById(R.id.pkg_acc_sNode);
        pkg_eNode=(TextView)findViewById(R.id.pkg_acc_eNode);
        pkg_stauts=(TextView)findViewById(R.id.pkg_acc_status);
        pkg_time=(TextView)findViewById(R.id.pkg_acc_time);
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
        InTransNode inTransNode = new InTransNode("nowNode");
        TransNodeLoader transNodeLoader = new TransNodeLoader(inTransNode,this);
        transNodeLoader.Load(app.getLoginUser().getDptID());

        isInit = true;
    }

    //扫描快件确认快件
    private void StartScanExpress() {
        if (transPackage == null){
            Toast.makeText(this,"包裹不存在请重新扫描",Toast.LENGTH_SHORT).show();
            StartCapture();
            return;
        }
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
                        Bundle bundle = data.getExtras();
                        if( bundle != null){
                            Toast.makeText(this,"我回来了",Toast.LENGTH_SHORT).show();
                            ExpressSheet es = (ExpressSheet) bundle.getSerializable("ExpressSheet");
                            //System.out.println("ExpressAccActivity"+es.toString());
//                            List<ExpressSheet> da1 = expressListAdapter.getData();
//                            da1.remove(es);
//                            expressListAdapter.setData(da1);

                            //扫描回来的状态已经改变，所以无法删除导致list无法刷新
                            //es.setStatus(TransPackageContent.STATUS.STATUS_ACTIVE);
                            //expressListAdapter.getPosition(es)
//                            System.out.println("删除前");
//                            for(ExpressSheet expressSheet:expressListAdapter.getData()){
//                                System.out.println(expressSheet.toString());
//                            }
                            //expressListAdapter.remove(es);
                            //System.out.println("删除中");
                            expressListAdapter.delete(es);

//                            int i = 0;
//                            for(ExpressSheet expressSheet:expressListAdapter.getData()){
//                                if(expressSheet.toString().equals(es.toString())){
//                                    System.out.println("删除中");
//                                    expressListAdapter.remove(expressListAdapter.getItem(i));
//                                }
//                                i++;
//                            }
//                            System.out.println("删除后");
//                            for(ExpressSheet expressSheet:expressListAdapter.getData()){
//                                System.out.println(expressSheet.toString());
//                            }
                            expressListAdapter.notifyDataSetChanged();
                        }
                        break;
                }
        }

    }

    private boolean expressIsInPackage(String id) {
        boolean flag = false;
        if(expressListAdapter.getData().size() == 0) flag =false;
        for(ExpressSheet es:expressListAdapter.getData()){
            if(es.getID().equals(id)){
                flag = true;
                break;
            }
        }
        return  flag;
    }

    //包裹从某一营业网点转到下一网点，下一网点的扫描员扫描确认包裹，将包裹状态改为4（表示到达转运中心）
    //根据扫描员自身的营业网点信息写入包裹历史，需要写的表packageroute和transhistory
    private void pkgAcc() {
        Log.d("PackageAccActivity执行了：","包裹确认方法");

        if (transPackage == null){
            Toast.makeText(this,"包裹不存在请重新扫描",Toast.LENGTH_SHORT).show();
            StartCapture();
            return;
        }
        if( transPackage.getStatus() == TransPackage.PKG_TRSNSIT ) {

            if(expressListAdapter.getData().size() != 0){
                alertDialog2 = new AlertDialog.Builder(this)
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
                finish();
            }
        }
        else{
            Toast.makeText(this,"包裹状态为"+transPackage.getStatus()+" 不符合要求",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onDestroy() {
        if(alertDialog2 != null) {
            alertDialog2.dismiss();
        }
        super.onDestroy();
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
        private String e_type;
        InTransNode(String e_type){
            this.e_type =e_type;
        }
        @Override
        public TransNode getData() {
            return  getTranNode(e_type);
        }

        @Override
        public void setData(TransNode data) {

            setTranNode(e_type,data);
            RefreshUI();
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
        RefreshUI();
        //包裹历史最近的一条且终点是本站的记录0-》上一站找到是谁（司机）送过来的-》再把自己的id放进去放入形成一条记录

        if (isInit) {
            isInit = false;
            //1：得到包裹中快件列表
            ExpressListLoader expressListLoader = new ExpressListLoader(expressListAdapter,this);
            expressListLoader.getExpressListInPackage(transPackage.getID());

            //2：找到包裹历史里最近的一条记录
            transHistoryLoader = new TransHistoryLoader(inTransHistory,this);
            transHistoryLoader.getRecentOneTranHistory(transPackage);

            //3得到目的站点和原站点
            InTransNode inTransNode = new InTransNode("sNode");
            TransNodeLoader transNodeLoader = new TransNodeLoader(inTransNode,this);
            transNodeLoader.Load(data.getSourceNode());

            InTransNode inTransNode1 = new InTransNode("eNode");
            TransNodeLoader transNodeLoader1 = new TransNodeLoader(inTransNode1,this);
            transNodeLoader1.Load(data.getTargetNode());
        }

    }
    public void setTranNode(String e_type,TransNode transNode){
        switch (e_type){
            case "sNode":
                sNode = transNode;
                break;
            case "eNode":
                eNode =transNode;
                break;
            case "nowNode":
                nowNode = transNode;
        }
        return;
    }

    public TransNode getTranNode(String e_type){
        TransNode transNode=null;
        switch (e_type){
            case "sNode":
                transNode=sNode;
                break;
            case "eNode":
                transNode=eNode;
                break;
            case "nowNode":
                transNode=nowNode;
        }
        return transNode;
    }
    private void RefreshUI() {
        if(transPackage != null){
            pkg_id.setText(transPackage.getID());
            pkg_time.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", transPackage.getCreateTime()));
            pkg_stauts.setText(TransPackage.getPackageStatus(transPackage.getStatus()));
            if(sNode !=null){
                pkg_sNode.setText(sNode.getNodeName());
            }
            if(eNode != null){
                pkg_eNode.setText(eNode.getNodeName());
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        Log.d("PackageAccActivity执行了这个","notifyDataSetChanged");
    }
}
