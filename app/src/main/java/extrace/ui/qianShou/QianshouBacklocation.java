package extrace.ui.qianShou;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import extrace.loader.ExpressListLoader;
import extrace.loader.TransPackageContentLoader;
import extrace.loader.TransPackageListLoader;
import extrace.loader.TransPackageLoader;
import extrace.misc.model.ExpressSheet;
import extrace.misc.model.TransPackage;
import extrace.misc.model.TransPackageContent;
import extrace.net.IDataAdapter;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.R;
import extrace.ui.paiSong.BackLocationService;

public class QianshouBacklocation extends AppCompatActivity implements IDataAdapter<List<ExpressSheet>> {

    private String PackageID;
    private Button over_qianshou_btn;
    private ExpressListLoader eloader;
    private ExpressListLoader Eloader;
    private TransPackageLoader tloader;
    private Activity activity=this;
    private List<ExpressSheet> itemlist1;//用于存放签收前的快件列表
    private List<ExpressSheet> itemlist2;//用于存放签收完成后的快件列表，用于判断签收完成时快件是否为空
    private int packagecontent=0;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expressqianshou);
        InitView();
        InitData1();
        InitData2();
    }
    private void InitView(){
        over_qianshou_btn=(Button)findViewById(R.id.overqianshou_btn);
    }
    private void InitData1(){
        itemlist1=new ArrayList<ExpressSheet>();
        itemlist2=new ArrayList<ExpressSheet>();
        PackageID=((ExTraceApplication)this.getApplication()).getLoginUser().getDelivePackageID();
        reFreshdata();
    }
    private void InitData2(){
        findViewById(R.id.action_pk_exp_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setClass(activity,ExpressQianShouActivity.class);
                startActivity(intent);
            }
        });
        over_qianshou_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if(isPackageContentNull)
                over_qianshou();
                //else {
                    //Toast.makeText(this,"包裹内还有快件未签收！",Toast.LENGTH_SHORT).show();
                //}
            }
        });
    }

    private void over_qianshou(){
        isPackageNull(PackageID);
        if (packagecontent==1) {
            InTransPackage inTransPackage = new InTransPackage();
            tloader = new TransPackageLoader(inTransPackage, this);
            tloader.changeTransPackageStatustoUnpackaged(PackageID);
            Intent stopIntent = new Intent(this, BackLocationService.class);
            stopService(stopIntent);
        }
        else {
            Toast.makeText(this,"包裹内还有快件未签收",Toast.LENGTH_SHORT).show();
        }
    }

    private void isPackageNull(String pkgId){
        Packageisnull packageisnull=new Packageisnull();
        Eloader=new ExpressListLoader(packageisnull,this);
        Eloader.getExpressListInPackage(pkgId);
    }

    @Override
    public List<ExpressSheet> getData() {
        return null;
    }

    public void reFreshdata(){
        eloader=new ExpressListLoader(this,this);
        eloader.getExpressListInPackage(PackageID);
    }

    @Override
    public void setData(List<ExpressSheet> data) {
        itemlist1.addAll(data);
        //System.out.println(data);
        //System.out.println(itemlist1);
        ispckNull(itemlist1);
    }

    private void ispckNull(List<ExpressSheet> itemlist){
        if(itemlist.size()==0) {
            Toast.makeText(this, "包裹为空", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    class InTransPackage implements  IDataAdapter<TransPackage>{

        @Override
        public TransPackage getData() {
            return null;
        }

        @Override
        public void setData(TransPackage data) {

        }

        @Override
        public void notifyDataSetChanged() {

        }
    }

    class Packageisnull implements IDataAdapter<List<ExpressSheet>>{

        @Override
        public List<ExpressSheet> getData() {
            return null;
        }

        @Override
        public void setData(List<ExpressSheet> data) {
            itemlist2.addAll(data);
            if(itemlist2.size()!=0)
                packagecontent=1;
            else
                packagecontent=0;
        }

        @Override
        public void notifyDataSetChanged() {

        }
    }

    @Override
    public void notifyDataSetChanged() {

    }
}
