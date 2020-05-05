package extrace.ui.zhuanyun;

import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import extrace.loader.ExpressLoader;
import extrace.loader.TransHistoryListLoader;
import extrace.loader.TransHistoryLoader;
import extrace.loader.TransNodeLoader;
import extrace.loader.TransPackageListLoader;
import extrace.loader.TransPackageLoader;
import extrace.loader.UserInfoLoader;
import extrace.misc.model.ExpressSheet;
import extrace.misc.model.ListTransHistory;
import extrace.misc.model.ListTransPackage;
import extrace.misc.model.TransHistory;
import extrace.misc.model.TransNode;
import extrace.misc.model.TransPackage;
import extrace.misc.model.UserInfo;
import extrace.net.IDataAdapter;
import extrace.ui.accPkg.TransHistoryListAdapter;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;
import extrace.ui.misc.TransNodeListActivity;
import zxing.util.CaptureActivity;

public class ZhuanyunCreateActivity extends AppCompatActivity implements IDataAdapter<TransPackage>{

    private static final int REQUEST_CAPTURE = 100;
    private static final int REQUEST_GET_NODE = 101;
    private Button zhuanyun_start_btn;
    private ImageButton zhuanyun_nextNode_btn;
    private EditText zhuanyun_nextNode_edt;
    private Button zhuanyun_addpkg_btn;
    private ListView zhuanyun_pkg_list;
    private PackageListAdapter packageListAdapter;
    private List<TransPackage> itemList;
    private UserInfo nextManger; //下一站的负责人
    private TransNode nextTransNode;
    private TransNode nowTranNode;
    private TransPackageLoader transPackageLoader;
    private TransHistoryLoader transHistoryLoader;
    private UserInfoLoader userInfoLoader;
    private List<TransHistory> transHistoryList = new ArrayList<TransHistory>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuanyun_create);
        itemList = new ArrayList<TransPackage>();
        zhuanyun_addpkg_btn = (Button)findViewById(R.id.zhuanyun_addpkg_btn);
        zhuanyun_start_btn=(Button)findViewById(R.id.zhuanyun_start_btn);
        zhuanyun_pkg_list =(ListView)findViewById(R.id.zhuanyun_pkg_list);
        zhuanyun_nextNode_btn = (ImageButton) findViewById(R.id.zhuanyun_nextNode_btn);
        zhuanyun_nextNode_edt = (EditText)findViewById(R.id.zhuanyun_nextNode_edt);
        //添加包裹
        zhuanyun_addpkg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartCapture();
            }
        });

        //开始转运
        zhuanyun_start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartZhuanYun();
            }
        });
        //网点信息获取：
        zhuanyun_nextNode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetNextNode();
            }
        });
        //适配器初始化
        packageListAdapter = new PackageListAdapter(itemList,this);
        zhuanyun_pkg_list.setAdapter(packageListAdapter);

        //刚加载时获取司机的站点
        String nodeID = ((ExTraceApplication)this.getApplication()).getLoginUser().getDptID();
        InZhuanyunActivityTranNode inZhuanyunActivityTranNode = new InZhuanyunActivityTranNode();
        TransNodeLoader transNodeLoader = new TransNodeLoader(inZhuanyunActivityTranNode,this);
        transNodeLoader.Load(nodeID);
    }
    //得到网点信息
    private void GetNextNode() {
        Intent intent  = new Intent();
        intent.setClass(this, TransNodeListActivity.class);
        startActivityForResult(intent,REQUEST_GET_NODE);
    }
    //开始转运
    private void StartZhuanYun(){
        if(itemList.size() == 0 || nextManger == null){
            Toast.makeText(this,"包裹列表或下一站为空,请确认信息是否完整后重试",Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("PackageEditActivity执行了这个：","StartZhuanYun开始转运");

        transHistoryList.clear();
        //1,开始转运写transhistory历史
        //①得到司机的id，然后写入。加上下一站的负责人的uid写入
        int uidfrom = ((ExTraceApplication)this.getApplication()).getLoginUser().getUID();
        if(nextManger == null) return;
        int uidto = nextManger.getUID();


        for(TransPackage item:itemList){
            TransHistory transHistory = new TransHistory();
            transHistory.setUIDFrom(uidfrom);
            transHistory.setUIDTo(uidto);
            transHistory.setPkg(item);
            transHistory.setX(nowTranNode.getX());
            transHistory.setY(nowTranNode.getY());

            transHistoryList.add(transHistory);

        }


        //2打包成一个列表一次性写入,并改变包裹状态
        if(transHistoryList.size() != 0) {

            //2.1往Transhistory里面写入数据
            ListTransHistory listTransHistory = new ListTransHistory();
            listTransHistory.setTransHistoryList(transHistoryList);
            TransHistoryListAdapter transHistoryListAdapter = new TransHistoryListAdapter(new ArrayList<TransHistory>(), this);
            TransHistoryListLoader transHistoryListLoader = new TransHistoryListLoader(transHistoryListAdapter, this);
            transHistoryListLoader.saveTransHistoryList(listTransHistory);

            //2.2改变包裹状态
            ListTransPackage listTransPackage = new ListTransPackage();
            listTransPackage.setTransPackageList(itemList);
            //改变包裹状态为运输中
            PackageListAdapter packageListAdapter = new PackageListAdapter(new ArrayList<TransPackage>(),this);
            TransPackageListLoader transPackageListLoader = new TransPackageListLoader(packageListAdapter,this);
            transPackageListLoader.changeTransPackageListStatus(listTransPackage,TransPackage.PKG_TRSNSIT);

            //2.3往改变包裹里的快件的状态为运输中
            PackageListAdapter packageListAdapter1 =  new PackageListAdapter(new ArrayList<TransPackage>(),this);
            TransPackageListLoader transPackageListLoader1 = new TransPackageListLoader(packageListAdapter,this);
            transPackageListLoader1.changeExpressStatusInTransPackageList(listTransPackage,ExpressSheet.STATUS.STATUS_DAIZHUAYUN, ExpressSheet.STATUS.STATUS_TRANSPORT);
            //2.4开启GPS定位给 每隔n分钟获取位置信息并记录到packageroute中

        }
    }
    private void StartCapture(){
        Log.d("PackageEditActivity执行了这个：","StartCapture");
        Intent intent = new Intent();
        intent.putExtra("Action",  "Capture");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("PackageEditActivity执行了这个：","onActivityResult"+requestCode+"|"+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
                switch (requestCode){
                    case REQUEST_CAPTURE:
                        if (data.hasExtra("BarCode")) {//如果扫描结果得到的单号不为空
                            String id = data.getStringExtra("BarCode");
                            try {
                                transPackageLoader  =new TransPackageLoader( this,this);
                                if(!isRepeat(id)){
                                    transPackageLoader.Load(id); //得到这个包裹id
                                }
                                else{
                                    Toast.makeText(this,"该包裹已添加，请勿重复添加",Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case REQUEST_GET_NODE:
                        Log.d("PackageEditActivity执行了这个：onActivityResult返回了：","sd");
                        Bundle bundle = data.getExtras();
                        TransNode transNode = (TransNode) bundle.getSerializable("TransNode");
                        if(transNode != null){
                            nextTransNode = transNode;
                            InZhuanyunActivityUserInfo in = new  InZhuanyunActivityUserInfo();
                            userInfoLoader = new UserInfoLoader(in,this);
                            userInfoLoader.getUserManagerById(transNode.getID());
                            //Toast.makeText(this,transNode.toString(),Toast.LENGTH_SHORT).show();
                            Log.d("PackageEditActivity执行了这个：onActivityResult返回了：",transNode.toString());
                            RefreshUI();
                        }
                        break;
                }
        }
    }

    //内部类
    class  InZhuanyunActivityUserInfo implements IDataAdapter<UserInfo>{

        @Override
        public UserInfo getData() {
            return nextManger;
        }

        @Override
        public void setData(UserInfo data) {
            Log.d("PackageEditActivity执行了这个InZhuanyunActivityUserInfo","setData"+data.toString());
            nextManger = data;
        }

        @Override
        public void notifyDataSetChanged() {

        }
    }
    private boolean isRepeat(String id) {
        for(TransPackage transPackage : itemList){
            if(transPackage.getID().equals(id)){
                return true;
            }
        }
        return false;
    }

    private void RefreshUI() {
        if (nextTransNode != null){
            zhuanyun_nextNode_edt .setText(nextTransNode.getNodeName());
        }

    }

    //第二个内部类
    class InZhuanyunActivityTranNode implements IDataAdapter<TransNode>{

        @Override
        public TransNode getData() {
            return  nowTranNode;
        }

        @Override
        public void setData(TransNode data) {
            nowTranNode = data;
        }

        @Override
        public void notifyDataSetChanged() {

        }
    }

    @Override
    public TransPackage getData() {

        return null;
    }

    @Override
    public void setData(TransPackage data) {
        itemList.add(data);
        packageListAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {

    }
}
