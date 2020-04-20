package extrace.ui.packageopenning;

import android.app.AppComponentFactory;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import extrace.loader.TransPackageLoader;
import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;
import zxing.util.CaptureActivity;

public class PackageOpenActivity extends AppCompatActivity implements IDataAdapter<TransPackage> {

    public static final int REQUEST_CAPTURE=100;
    private EditText packageIdView;//包裹编号
    private Button open_pkg_Btn;//确定
    private TransPackage transPackage;
    private TransPackageLoader open_Loader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_open);
        packageIdView=(EditText)findViewById(R.id.packageId);
        open_pkg_Btn=(Button)findViewById(R.id.open_pkg_Btn);
        findViewById(R.id.action_pk_exp_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("packageopen","点击扫描包裹按钮");
                OpenCapture();
            }
        });
        open_pkg_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("packageopen","点击拆包");
                //openPkg();
            }
        });
    }
    private void OpenCapture(){
        Intent intent=new Intent();
        intent.putExtra("Action","Capture");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent,REQUEST_CAPTURE);
    }

    //拆解一个包裹
    //private void openPkg(){
        //String pkgId= packageIdView.getText().toString();
        //try{
        //    String sql="select "
       // }
   //}
    @Override
    public TransPackage getData() {
        return null;
    }

    @Override
    public void setData(TransPackage data) {

    }

    @Override
    public void notifyDataSetChanged() {
        Log.d("PackageCreateActivity执行了这个：","notifyDataSetChanged");
    }
}
