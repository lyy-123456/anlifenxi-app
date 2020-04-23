package extrace.ui.accPkg;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashSet;
import java.util.Set;

import extrace.loader.TransHistoryLoader;
import extrace.loader.TransPackageLoader;
import extrace.loader.TransHistoryListLoader;
import extrace.misc.model.TransHistory;
import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;
import zxing.util.CaptureActivity;

public class PackageAccActivity extends AppCompatActivity implements IDataAdapter<TransPackage>{

        private Intent mIntent;
        private final int REQUEST_CAPTURE = 100;
        private TransPackage transPackage;
        private TextView pkg_textView;
        private Button pkg_acc_button;
        private Set<TransHistory> transHistorySet;
        public static final int PKG_NEW = 0;  //新建
        public static final int PKG_PACKED = 1; //已打包
        public static final int PKG_TRSNSIT = 2; //运输中
        public static final int PKG_ACCED = 3; //转运中心（已扫描）
        public static final int PKG_ACHIEVED = 4; //以达到achieved
        public static final int PKG_UNPACKED = 5;

        TransPackageLoader transPackageLoader;
        TransHistoryListAdapter transHistoryListAdapter;
        TransHistoryListLoader transhistoryListLoader;
        ExTraceApplication app;

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
            Log.d("PackageAccActivity执行了：","包裹确认方法");
            //看一下他的状态是不是运输中
            InTransHistory test = new InTransHistory();
            test.addOnePkgHistory();

            if( transPackage.getStatus() == PKG_TRSNSIT ){
                Log.d("PackageAccActivity执行了：","包裹状态运输中");
                transPackageLoader = new TransPackageLoader(this,this);
                transPackageLoader.pkgAcc(transPackage.getID());  //改变包裹状态为



//                transHistorySet = new HashSet<TransHistory>();
//                transHistoryListAdapter = new TransHistoryListAdapter(transHistorySet, this);
//                transhistoryListLoader = new TransHistoryListLoader(transHistoryListAdapter,this);
//                transhistoryListLoader.AddOneTransHistory(transHistory);
            }
        }
        class InTransHistory implements IDataAdapter<TransHistory>{

            @Override
            public TransHistory getData() {

                return null;
            }

            @Override
            public void setData(TransHistory data) {
                Log.d("内部类InTransHistory执行：","setData");
            }

            @Override
            public void notifyDataSetChanged() {

            }

            public void addOnePkgHistory() {
                Log.d("内部类InTransHistory执行：","添加包裹历史");
                //包裹历史添加一条数据,transhistory里面添加一条，并且改变package的状态为3转运中心（已确认），
                TransHistory transHistory = new TransHistory();
                transPackage.setStatus(PKG_ACHIEVED);
                transHistory.setPkg(transPackage);
                app = (ExTraceApplication)getApplicationContext();
                transHistory.setUIDFrom(app.getLoginUser().getUID());  //得到登陆者的UID


                Log.d("PackageAccActivity执行了：", String.valueOf(app.getLoginUser().getUID()));
                transHistory.setUIDTo(PKG_ACCED);  //状态设为转运中心已确认

                TransHistoryLoader transHistoryLoader =new TransHistoryLoader(this, PackageAccActivity.this);
                transHistoryLoader.AddOneTransHistory(transHistory);
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

        }

    @Override
    public void notifyDataSetChanged() {
        Log.d("PackageAccActivity执行了这个","notifyDataSetChanged");
        pkg_textView.setText(transPackage.toString());
    }
}
