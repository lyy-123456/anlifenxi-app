package extrace.ui.packages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.MessageQueue;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import extrace.loader.PackageRouteLoader;
import extrace.loader.TransNodeLoader;
import extrace.loader.TransPackageLoader;
import extrace.loader.UserInfoLoader;
import extrace.loader.UserPackageLoader;
import extrace.misc.model.PackageRoute;
import extrace.misc.model.TransNode;
import extrace.misc.model.TransPackage;
import extrace.misc.model.UsersPackage;
import extrace.net.IDataAdapter;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;
import extrace.ui.misc.RegionListActivity;
import extrace.ui.misc.TransNodeListActivity;
import extrace.ui.zhuanyun.ZhuanyunCreateActivity;
import zxing.util.CaptureActivity;


public class PackageCreateActivity extends AppCompatActivity implements IDataAdapter<TransPackage> {

    public static final int REQUEST_CAPTURE = 100;
    public static final int REQUEST_SPOSTCODE = 101;
    public static final int REQUEST_EPOSTCODE = 102;
    public static final int REQUEST_CREATE_PKG = 103;

    private EditText  packageIdView;  //包裹编号
    private  EditText sourcePostCodeView;  //打包地网点编号
    private EditText endPostCodeView;
    private ImageButton sourcePostCodeBtnView;  //终点站网点编号
    private ImageButton endPostCodeBtnView;   //获取地址的按钮
    private Button create_pkg_Btn; //提交按钮
    private EditText sourceName;   //本站信息
    private  EditText endName;     //终点站信息

    private TransNode stransNode;
    private TransNode etransNode;
    private TransPackage transPackage;

    private TransPackageLoader tLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_create);

        sourceName =(EditText)findViewById(R.id.sourceName);
        endName=(EditText)findViewById(R.id.endName);
        packageIdView = (EditText) findViewById(R.id.packageId);
        sourcePostCodeView = (EditText)findViewById(R.id.sourcePostCode);
        sourcePostCodeBtnView = (ImageButton) findViewById(R.id.sourcePostCodeBtn);
        endPostCodeView = (EditText)findViewById(R.id.endPostCode);
        endPostCodeBtnView = (ImageButton)findViewById(R.id.endPostCodeBtn);
        create_pkg_Btn = (Button)findViewById(R.id.create_pkg_Btn);

        findViewById(R.id.action_pk_exp_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("packagecreate","点击扫描包裹按钮");
                StartCapture();
            }
        });
        sourcePostCodeBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("packagecreate","点击选择始发站按钮");
                getSourceRegion();
            }
        });
        endPostCodeBtnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("packagecreate","点击选择终点站按钮");
                getEndRegion();

            }
        });
        create_pkg_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("packagecreate","点击创建包裹按钮");
                createPkg();
            }
        });

    }

    //创建一个包裹，并跳转到包裹内部页面
    private  void createPkg(){
        System.out.println(transPackage.toString());
        ExTraceApplication app = (ExTraceApplication)this.getApplication();
        String pkgId = packageIdView.getText().toString();
        String sourcePostCode = sourcePostCodeView.getText().toString();
        String endPostCode = endPostCodeView.getText().toString();
        if("".equals(pkgId)) {
            Toast.makeText(this,"包裹ID不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }

        if( "".equals(sourcePostCode)){
            Toast.makeText(this,"发送地邮编不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }

        if("".equals(endPostCode)){
            Toast.makeText(this,"终点站邮编不能为空！",Toast.LENGTH_SHORT).show();
            return ;
        }
        if(transPackage == null){
            Toast.makeText(this,"包裹不存在，新建包裹",Toast.LENGTH_SHORT).show();
            //如果包裹不存在 新建包裹
            tLoader = new TransPackageLoader(this,this);
            transPackage = new TransPackage();
            transPackage.setID(pkgId);
            transPackage.setSourceNode(sourcePostCode);
            transPackage.setTargetNode(endPostCode);
            transPackage.setStatus(TransPackage.PKG_NEW);
            transPackage.setCreateTime(new Date());
            tLoader.New(transPackage);
        }

        //往userspackage中心写入一条数据，代表该用户打的包裹
        InUsersPackage inUsersPackage= new InUsersPackage();
        UserPackageLoader userPackageLoader = new UserPackageLoader(inUsersPackage,this);
        UsersPackage usersPackage = new UsersPackage();
        usersPackage.setPkg(transPackage);
        usersPackage.setUserU(app.getLoginUser());
        userPackageLoader.Save(usersPackage);
        Log.d("创建一个新的包裹",transPackage.toString());

        Bundle bundle = new Bundle();
        bundle.putSerializable("transPackage",transPackage);
        bundle.putSerializable("sTransNode",stransNode);
        Intent intent = new Intent();
        intent.putExtras(bundle);

        //先用this
        intent.setClass(this, PackageEditActivity.class);
        startActivity(intent);
        finish();
    }


    class InUsersPackage implements IDataAdapter<UsersPackage>{
        UsersPackage usersPackage;
        @Override
        public UsersPackage getData() {
            return usersPackage;
        }

        @Override
        public void setData(UsersPackage data) {
            usersPackage = data;
        }

        @Override
        public void notifyDataSetChanged() {

        }
    }
    //扫描条形码
    private void StartCapture(){
        Intent intent = new Intent();
        intent.putExtra("Action","Capture");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    //得到源站的网点编号
    private void getSourceRegion() {
        Intent intent = new Intent();
        intent.setClass(this, TransNodeListActivity.class);
        startActivityForResult(intent, REQUEST_SPOSTCODE);
    }

    //得到终点站的邮编
    private void getEndRegion() {
        Intent intent = new Intent();
        intent.setClass(this, TransNodeListActivity.class);
        startActivityForResult(intent, REQUEST_EPOSTCODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                switch(requestCode){
                    case REQUEST_CAPTURE:
                        if (data.hasExtra("BarCode")) {//如果扫描结果得到的单号不为空
                            String id = data.getStringExtra("BarCode");
                            Log.d("包裹编号：",id);
                            //得到id 判断包裹是否存在如果存在那么
                            tLoader = new TransPackageLoader(this,this);
                            tLoader.Load(id);
                            packageIdView.setText(id);
                        }
                        break;
                        //源点邮编
                    case REQUEST_SPOSTCODE:
                        Log.d("PackageCreateActivity执行了这个：onActivityResult返回了：","sd");
                        Bundle bundle = data.getExtras();
                        stransNode = (TransNode) bundle.getSerializable("TransNode");
                        sourcePostCodeView.setText(stransNode.getID());
                        sourceName.setText(stransNode.getNodeName());
                        break;
                        //终点邮编
                    case REQUEST_EPOSTCODE:
                        Log.d("PackageCreateActivity执行了这个：onActivityResult返回了：","sd");
                        Bundle bundle1 = data.getExtras();
                        etransNode = (TransNode) bundle1.getSerializable("TransNode");
                        endPostCodeView.setText(etransNode.getID());
                        endName.setText(etransNode.getNodeName());
                        break;

                }
                break;
            default:
                break;
        }
    }


    @Override
    public TransPackage getData()
    {
        Log.d("PackageCreateActivity执行了这个：","getData");
        return transPackage;
    }

    class InTransNode implements IDataAdapter<TransNode>{

        private  String type;
        InTransNode(String etype){
            type = etype;
        }
        @Override
        public TransNode getData() {
            if(type.equals("sTransNode")) return stransNode;
            else return etransNode;
        }

        @Override
        public void setData(TransNode data) {
            if(type.equals("sTransNode"))  stransNode= data;
            else if(type.equals("eTransNode")) etransNode = data;
            RefreshUI();
        }

        @Override
        public void notifyDataSetChanged() {

        }
    }
    @Override
    public void setData(TransPackage data) {
        Log.d("PackageCreateActivity执行了这个：","setData");
        if(data.getStatus() == TransPackage.PKG_NEW){
            transPackage = data;
            packageIdView.setText(data.getID());

            //获取整个包裹的源站和目的站点的信息

            TransNodeLoader transNodeLoader = new TransNodeLoader(new InTransNode("sTransNode"),this);
            transNodeLoader.Load(transPackage.getSourceNode());

            TransNodeLoader transNodeLoader1 = new TransNodeLoader(new InTransNode("eTransNode"),this);
            transNodeLoader1.Load(transPackage.getTargetNode());
            RefreshUI();
        }
        else{
            packageIdView.setText("");
            Toast.makeText(this,"包裹状态已存在且该状态不可以打包"+data.getStatus(),Toast.LENGTH_SHORT).show();
        }
    }

    private void RefreshUI() {
        if(transPackage != null){
            packageIdView.setText(transPackage.getID());
            sourcePostCodeView.setText(transPackage.getSourceNode());
            endPostCodeView.setText(transPackage.getTargetNode());
        }

        if(stransNode != null){
            sourceName.setText(stransNode.getNodeName());
        }
        if(etransNode != null){
            endName.setText(etransNode.getNodeName());
        }
    }

    @Override
    public void notifyDataSetChanged() {
        //刷新界面
        Log.d("PackageCreateActivity执行了这个：","notifyDataSetChanged");
    }
}
