package extrace.ui.packages;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import extrace.loader.TransPackageLoader;
import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;
import extrace.ui.misc.RegionListActivity;
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

        String pkgId = packageIdView.getText().toString();
        String sourcePostCode = sourcePostCodeView.getText().toString();
        String endPostCode = endPostCodeView.getText().toString();
//        Log.d("crearePkg",pkgId);
//        Log.d("crearePkg",sourcePostCode);
//        Log.d("crearePkg",endPostCode);
        if(pkgId == null || "".equals(pkgId)) {
            Toast.makeText(this,"包裹ID不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }

        if(sourcePostCode==null || "".equals(sourcePostCode)){
            Toast.makeText(this,"发送地邮编不能为空！",Toast.LENGTH_SHORT).show();
            return;
        }

        if(endPostCode ==null || "".equals(endPostCode)){
            Toast.makeText(this,"终点站邮编不能为空！",Toast.LENGTH_SHORT).show();
            return ;
        }
        //满足要求创建一个包裹,检查包裹是否存在？
        tLoader = new TransPackageLoader(this,this);
        transPackage = new TransPackage();
        transPackage.setID(pkgId);
        transPackage.setSourceNode(sourcePostCode);
        transPackage.setTargetNode(endPostCode);
        transPackage.setCreateTime(new Date());
        transPackage.setStatus(TransPackage.PKG_NEW);
        tLoader.New(transPackage);


        Log.d("创建一个新的包裹",transPackage.toString());
        //创建一个包裹，然后跳转到包裹新建页面，将包裹信息传到包裹信息编辑页面
    }

    //扫描条形码
    private void StartCapture(){
        Intent intent = new Intent();
        intent.putExtra("Action","Captrue");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    //得到源站的网点编号
    private void getSourceRegion() {
        Intent intent = new Intent();
        intent.setClass(this, RegionListActivity.class);
        try{
            String rCode = sourcePostCodeView.getText().toString();
            //String rString = mRegionView.getText().toString();
            intent.putExtra("RegionId", rCode);
            //intent.putExtra("RegionString", rString);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        startActivityForResult(intent, REQUEST_SPOSTCODE);
    }

    //得到终点站的邮编
    private void getEndRegion() {
        Intent intent = new Intent();
        intent.setClass(this, RegionListActivity.class);
        try{
            String rCode = endPostCodeView.getText().toString();
            //String rString = mRegionView.getText().toString();
            intent.putExtra("RegionId", rCode);
            //intent.putExtra("RegionString", rString);
        }
        catch(Exception e){
            e.printStackTrace();
        }

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
                            packageIdView.setText(id);
                        }
                        break;
                        //源点邮编
                    case REQUEST_SPOSTCODE:
                        String sregionId;
                        if (data.hasExtra("RegionId")) {
                            sregionId = data.getStringExtra("RegionId");
                            //regionString = data.getStringExtra("RegionString");
                        } else {
                            sregionId = "";
                            //regionString = "";
                        }
                        sourcePostCodeView.setText(sregionId);
                        searchSource(sregionId); //查询源站信息
                        break;
                        //终点邮编
                    case REQUEST_EPOSTCODE:
                        String eregionId;
                        if (data.hasExtra("RegionId")) {
                            eregionId = data.getStringExtra("RegionId");
                            //regionString = data.getStringExtra("RegionString");
                        } else {
                            eregionId = "";
                            //regionString = "";
                        }
                        endPostCodeView.setText(eregionId);
                        break;

                }
                break;
            default:
                break;
        }
    }

    //查询源站信息
    private void searchSource(String regionCode) {

    }

    @Override
    public TransPackage getData()
    {
        Log.d("PackageCreateActivity执行了这个：","getData");
        return transPackage;
    }

    @Override
    public void setData(TransPackage data) {
        Log.d("PackageCreateActivity执行了这个：","setData");
        transPackage = data;


        //Log.d("创建一个新的包裹",transPackage.toString());
        Bundle bundle = new Bundle();
        bundle.putSerializable("transPackage",transPackage);
        Intent intent = new Intent();
        intent.putExtras(bundle);

        //先用this
        intent.setClass(this, PackageEditActivity.class);
        startActivityForResult(intent, REQUEST_CREATE_PKG);
    }

    @Override
    public void notifyDataSetChanged() {
        //刷新界面
        Log.d("PackageCreateActivity执行了这个：","notifyDataSetChanged");
    }
}
