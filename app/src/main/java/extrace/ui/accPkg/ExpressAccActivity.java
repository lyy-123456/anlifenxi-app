package extrace.ui.accPkg;

import androidx.annotation.IntRange;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import extrace.loader.ExpressLoader;
import extrace.loader.TransPackageContentLoader;
import extrace.misc.model.ExpressSheet;
import extrace.misc.model.TransPackageContent;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;

public class ExpressAccActivity extends AppCompatActivity implements IDataAdapter<ExpressSheet> {

    private static final String[] status={"正常","已丢失","已损坏"};
    private Button accBtn;
    private Spinner spinner;
    private ArrayAdapter<String> adapter;
    private String selectItem;
    private ExpressSheet expressSheet;
    private int selectPosition;
    private TransPackageContent transPackageContent;
    
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
    private String packageID;
    private String expressID;

    private boolean isCommit;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_acc);
        accBtn = (Button)findViewById(R.id.btnCommit);
        spinner = (Spinner) findViewById(R.id.expressInPkgStatusSpinner);
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

        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,status);

        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        spinner.setAdapter(adapter);
        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new SpinnerSelectedListener());
        //设置默认值
        selectPosition = 0;
        spinner.setSelection(selectPosition);
        spinner.setVisibility(View.VISIBLE);


        accBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartAccExpress();

            }
        });

        //初始化状态
        isCommit = false;

        //加载快件的信息
        expressID = getIntent().getStringExtra("ExpressID");
        ExpressLoader expressLoader = new ExpressLoader(this,this);
        expressLoader.Load(expressID);
        packageID = getIntent().getStringExtra("PackageID");
        InTransPackageContent inTransPackageContent = new InTransPackageContent();
        TransPackageContentLoader transPackageContentLoader = new TransPackageContentLoader(inTransPackageContent,this);
        transPackageContentLoader.getTransPackageContent(packageID,expressID);

    }

    //确认包裹
    private void StartAccExpress() {
        isCommit = true;
        //改变状态
        InTransPackageContent inTransPackageContent = new InTransPackageContent();
        TransPackageContentLoader transPackageContentLoader = new TransPackageContentLoader(inTransPackageContent,this);
        transPackageContentLoader.changeExpressStatusInPackage(packageID,expressID,selectPosition+1);
    }

    class InTransPackageContent implements IDataAdapter<TransPackageContent>{

        @Override
        public TransPackageContent getData() {
            return transPackageContent;
        }

        @Override
        public void setData(TransPackageContent data) {
            transPackageContent = data;
            if(isCommit){
                isCommit = false;
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("ExpressSheet",expressSheet);
                System.out.println("ExpressAccActivity"+expressSheet.toString());
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        }

        @Override
        public void notifyDataSetChanged() {

        }
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
    @Override
    public ExpressSheet getData() {
        return expressSheet;
    }

    @Override
    public void setData(ExpressSheet data) {
        expressSheet = data;
        RefreshUI();
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
            case ExpressSheet.STATUS.STATUS_DAIZHUAYUN:
                stText="等待转运";
                break;
        }
        mStatusView.setText(stText);
    }

    @Override
    public void notifyDataSetChanged() {

    }

    //使用数组形式操作
    class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

        public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                                   long arg3) {
            selectPosition = arg2;
            selectItem=status[arg2];
        }

        public void onNothingSelected(AdapterView<?> arg0) {
        }
    }
}
