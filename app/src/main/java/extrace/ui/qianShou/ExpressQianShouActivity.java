package extrace.ui.qianShou;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import extrace.loader.ExpressLoader;
import extrace.misc.model.ExpressSheet;
import extrace.misc.model.TransHistory;
import extrace.net.IDataAdapter;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;
import zxing.util.CaptureActivity;

public class ExpressQianShouActivity extends AppCompatActivity implements IDataAdapter<ExpressSheet> {
    private static final int REQUEST_CAPTURE = 100;
    private ExpressSheet expressSheet;
    private Button qianshouBtn;
    private boolean isQianshou; //是否签收

    private TextView mIDView;
    private TextView mRcvNameView;
    private TextView mRcvTelCodeView;
    private TextView mRcvDptView;
    private TextView mRcvAddrView;
    private TextView mRcvRegionView;

    private TextView mSndNameView;
    private TextView mSndTelCodeView;
    private TextView mSndDptView;
    private TextView mSndAddrView;
    private TextView mSndRegionView;

    private TextView mRcverView;
    private TextView mRcvTimeView;

    private TextView mSnderView;
    private TextView mSndTimeView;

    private TextView mStatusView;
    private int Userinfo;
    private String Userinfoch;
    private String PackageId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qianshou_activity);
        initView();
        StartCapture();
        initData();
    }

    private void initData() {
        isQianshou = false;
        qianshouBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QianShouExpress();
            }
        });
    }

    //新建View
    private void initView() {
        findViewById(R.id.qianshou_lay).setVisibility(View.VISIBLE);
        qianshouBtn = (Button)findViewById(R.id.btn_qianshou);
        
        mIDView = (TextView) findViewById(R.id.express_acc_Id);
        mRcvNameView = (TextView) findViewById(R.id.express_acc_RcvName);
        mRcvTelCodeView = (TextView) findViewById(R.id.express_acc_RcvTel);
        mRcvAddrView = (TextView)findViewById(R.id.express_acc_RcvAddr);
        mRcvDptView = (TextView)findViewById(R.id.express_acc_RcvDpt);
        mRcvRegionView = (TextView)findViewById(R.id.express_acc_RcvRegion);

        mSndNameView = (TextView)findViewById(R.id.express_acc_SndName);
        mSndTelCodeView = (TextView)findViewById(R.id.express_acc_SndTel);
        mSndAddrView = (TextView)findViewById(R.id.express_acc_SndAddr);
        mSndDptView = (TextView)findViewById(R.id.express_acc_SndDpt);
        mSndRegionView = (TextView)findViewById(R.id.express_acc_SndRegion);

        mRcvTimeView = (TextView)findViewById(R.id.express_acc_AccTime);
        mSndTimeView = (TextView)findViewById(R.id.express_acc_DlvTime);

        mStatusView =  (TextView)findViewById(R.id.express_acc_Status);
    }

    private void displayRcv(ExpressSheet es){
        if(es.getRecever() != null){
            mRcvNameView.setText(es.getRecever().getName());
            mRcvTelCodeView.setText(es.getRecever().getTelCode());
            mRcvNameView.setTag(es.getRecever());
            mRcvAddrView.setText(es.getRecever().getAddress());
            mRcvDptView.setText(es.getRecever().getDepartment());
            mRcvRegionView.setText(es.getRecever().getRegionString());
        }
        else{
            mRcvNameView.setText(null);
            mRcvTelCodeView.setText(null);
            mRcvNameView.setTag(null);
            mRcvAddrView.setText(null);
            mRcvDptView.setText(null);
            mRcvRegionView.setText(null);
        }
    }

    private  void displaySnd(ExpressSheet es){
        if(es.getSender() != null){
            mSndNameView.setText(es.getSender().getName());
            mSndTelCodeView.setText(es.getSender().getTelCode());
            mSndNameView.setTag(es.getSender());
            mSndAddrView.setText(es.getSender().getAddress());
            mSndDptView.setText(es.getSender().getDepartment());
            mSndRegionView.setText(es.getSender().getRegionString());
        }
        else{
            mSndNameView.setText(null);
            mSndTelCodeView.setText(null);
            mSndNameView.setTag(null);
            mSndAddrView.setText(null);
            mSndDptView.setText(null);
            mSndRegionView.setText(null);
        }
    }
    private void RefreshUI() {
        mIDView.setText(expressSheet.getID());
        displayRcv(expressSheet);
        displaySnd(expressSheet);
        if(expressSheet.getAccepteTime() != null)
            mRcvTimeView.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", expressSheet.getAccepteTime()));
        else
            mRcvTimeView.setText(null);
        if(expressSheet.getDeliveTime() != null)
            mSndTimeView.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", expressSheet.getDeliveTime()));
        else
            mSndTimeView.setText(null);

        String stText = "";
        switch(expressSheet.getStatus()){
            case ExpressSheet.STATUS.STATUS_CREATED:
                stText = "待揽收";
                break;
            case ExpressSheet.STATUS.STATUS_TRANSPORT:
                stText = "运输中";
                break;
            case ExpressSheet.STATUS.STATUS_DELIVERIED:
                stText = "已交付";
                break;
            case ExpressSheet.STATUS.STATUS_PAISONG:
                stText = "派送中";
                break;
            case ExpressSheet.STATUS.STATUS_DAIPAISONG:
                stText="等待派送";
                break;
            case ExpressSheet.STATUS.STATUS_DAIZHUAYUN:
                stText="等待转运";
                break;
        }
        mStatusView.setText(stText);
    }
    private void StartCapture(){
        Intent intent = new Intent();
        intent.putExtra("Action","Capture");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
                switch (requestCode){
                    case REQUEST_CAPTURE:
                        if (data.hasExtra("BarCode")) {//如果扫描结果得到的单号不为空
                            String id = data.getStringExtra("BarCode");
                            ExpressLoader mLoader = new ExpressLoader(this, this);  //加载一个快件
                            mLoader.Load(id);
                        }
                        break;
                }
        }
    }

    @Override
    public ExpressSheet getData() {
        return expressSheet;
    }

    @Override
    public void setData(ExpressSheet data) {

        if(data == null) {
            Toast.makeText(this,"快件不存在！",Toast.LENGTH_SHORT).show();
            finish();
        }else{
            expressSheet = data;
            RefreshUI();
//            if(!isQianshou){
//                if(expressSheet.getStatus() != ExpressSheet.STATUS.STATUS_PAISONG){  //如果快件状态不是派送中
//                    Toast.makeText(this,"快件状态不可签收",Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//
//            }
//            else{
//                Toast.makeText(this,"快件签收成功",Toast.LENGTH_SHORT).show();
//                finish();
//            }
        }
    }

    private void QianShouExpress() {

        Userinfo=((ExTraceApplication)this.getApplication()).getLoginUser().getUID();
        Userinfoch= (String) (Userinfo+ "");
        PackageId=((ExTraceApplication)this.getApplication()).getLoginUser().getDelivePackageID();

        if(expressSheet == null){
            Toast.makeText(this,"快件不存在！请重新扫描",Toast.LENGTH_SHORT).show();
            StartCapture();
            return;
        }
        if(expressSheet.getStatus() != ExpressSheet.STATUS.STATUS_PAISONG){  //如果快件状态不是派送中
            Toast.makeText(this,"快件状态不可签收",Toast.LENGTH_SHORT).show();
            return;
        }
        if(isQianshou){
            Toast.makeText(this,"快件已签收",Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if(!expressSheet.getDeliver().equals(Userinfoch)){
            Toast.makeText(this,"快件不在包裹内!请重新扫描",Toast.LENGTH_SHORT).show();
            StartCapture();
        }
        isQianshou = true;
        ExpressLoader expressLoader = new ExpressLoader(this,this);
        ExpressLoader expressLoader1=new ExpressLoader(this,this);
        expressLoader.changeExpressStatus(expressSheet.getID(),ExpressSheet.STATUS.STATUS_DELIVERIED);
        expressSheet.setStatus(ExpressSheet.STATUS.STATUS_DELIVERIED);  //把快件状态改为已到达
        expressLoader1.changeStatusInTranspackageContentToOut(expressSheet.getID(),PackageId);
        RefreshUI();

        //expressLoader.Receive();
    }

    @Override
    public void notifyDataSetChanged() {

    }

}
