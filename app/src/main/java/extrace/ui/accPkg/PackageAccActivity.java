package extrace.ui.accPkg;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import extrace.loader.ExpressLoader;
import extrace.loader.TransPackageLoader;
import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;
import zxing.util.CaptureActivity;

public class PackageAccActivity extends AppCompatActivity implements IDataAdapter<TransPackage> {

    private Intent mIntent;
    private final int REQUEST_CAPTURE = 100;
    private TransPackage transPackage;
    private TextView pkg_textView;
    private Button pkg_acc_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_acc);
        pkg_textView = (TextView)findViewById(R.id.pkg_ac_thing);
        pkg_acc_button = (Button)findViewById(R.id.pkg_acc_btn);
        pkg_acc_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pkgAcc();  //包裹确认操作
            }


        });
        mIntent = getIntent();
        if(mIntent.hasExtra("Action")){
            if(mIntent.getStringExtra("Action").equals("Accept")){
                StartCapture();
            }
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
                            TransPackageLoader transPackageLoader = new TransPackageLoader(this,this);
                            transPackageLoader.Load(id);
                        }
                        break;
                }
        }

    }

    //包裹从某一营业网点转到下一网点，下一网点的扫描员扫描确认包裹，将包裹状态改为4（表示到达转运中心）
    //根据扫描员自身的营业网点信息写入包裹历史，需要写的表packageroute和transhistory
    private void pkgAcc() {
        //看一下他的状态是不是运输中
        if(transPackage.getStatus() == 4){
            TransPackageLoader transPackageLoader = new TransPackageLoader(this,this);
            transPackageLoader.pkgAcc(transPackage);  //改变包裹状态
            //包裹历史添加一条数据

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
        transPackage = data;
    }

    @Override
    public void notifyDataSetChanged() {
        Log.d("PackageAccActivity执行了这个","notifyDataSetChanged");
        pkg_textView.setText(transPackage.toString());
    }
}
