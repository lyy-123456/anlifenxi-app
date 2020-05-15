package extrace.ui.paiSong;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import extrace.loader.ExpressLoader;
import extrace.misc.model.ExpressSheet;
import extrace.net.IDataAdapter;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;
import zxing.util.CaptureActivity;

public class ExpressPaiSongActivity extends AppCompatActivity implements IDataAdapter<ExpressSheet> {

    private static final int REQUEST_CAPTURE = 100;
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

    private  Button paiSongBtn;

    private ExpressSheet expressSheet;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qianshou_activity);
        initView();
        initData();
        StartCapture();
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
    private void initData() {
        paiSongBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartPaiSong();
            }
        });
    }

    //开始派送
    private void StartPaiSong() {
        if(expressSheet ==null){
            Toast.makeText(this,"没有快件无法派送",Toast.LENGTH_SHORT).show();
            return;
        }
        if(expressSheet.getStatus() != ExpressSheet.STATUS.STATUS_DAIPAISONG){
            Toast.makeText(this,"快件状态不正确，无法派送",Toast.LENGTH_SHORT).show();
            return;
        }
        //1获取登陆者的信息往ExpressSheet里面写
        ExpressLoader expressLoader =new ExpressLoader(this,this);
        expressSheet.setStatus(ExpressSheet.STATUS.STATUS_PAISONG);
        expressSheet.setDeliver(String.valueOf( ((ExTraceApplication)getApplication()).getLoginUser().getUID() ) );
        RefreshUI();
        expressLoader.saveOneExpressSheet(expressSheet);
    }

    //新建View
    private void initView() {
        findViewById(R.id.paisong_lay).setVisibility(View.VISIBLE);
        paiSongBtn = (Button) findViewById(R.id.btn_paisong);

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


    @Override
    public ExpressSheet getData() {
        return expressSheet;
    }

    @Override
    public void setData(ExpressSheet data) {

        expressSheet = data;
        RefreshUI();
    }

    @Override
    public void notifyDataSetChanged() {

    }
}
