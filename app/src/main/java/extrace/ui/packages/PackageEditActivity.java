package extrace.ui.packages;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

import extrace.loader.ExpressListLoader;
import extrace.loader.ExpressLoader;
import extrace.misc.model.ExpressSheet;
import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;
import extrace.ui.domain.ExpressListAdapter;
import extrace.ui.main.MainActivity;
import extrace.ui.main.R;
import zxing.util.CaptureActivity;

public class PackageEditActivity extends AppCompatActivity implements IDataAdapter<ExpressSheet> {

    private  final  int REQUEST_CAPTURE = 100;
    private final int EXPRESS_EDIT  = 110;

    private TransPackage transPackage;
    private ExpressInPacListFragment expressInPacListFragment;
    private List<ExpressSheet> listExpress;
    private ExpressSheet eItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_edit);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        transPackage = (TransPackage) bundle.getSerializable("transPackage");
        bundle.putString("Action","Init");
        //Toast.makeText(this,transPackage.toString(),Toast.LENGTH_SHORT).show();

        expressInPacListFragment= ExpressInPacListFragment.instance(bundle);//创建fragment对象
        FragmentTransaction tran= getSupportFragmentManager().beginTransaction();//创建提交事务对象
        tran.add(R.id.layout,expressInPacListFragment);//添加
        tran.commit();//提交
    }


    @Override
    protected void onStart() {
        super.onStart();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //菜单栏
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.package_edit, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("PackageEditActivity执行了这个：","onOptionsItemSelected");
        int id = item.getItemId();
        switch (id) {
            case R.id.action_ok:
                //Save();
                Log.d("package","选中了保存");
                ReservePackage();
                return true;
            case R.id.action_refresh:
//                if (mItem != null) {
//                    //Refresh(mItem.getID());
//                }
                Log.d("package","选中了刷新");
                Toast.makeText(this,"包裹信息已刷新！",Toast.LENGTH_SHORT).show();
                expressInPacListFragment.ReloadList();
                return true;
            case R.id.action_new:
                Log.d("package","选中了新建");
                StartCapture();
                //addExpressToPackage();
                return true;
            case (android.R.id.home):
                /*将选中的对象赋值给Intent*/
//	        Bundle bundle = new Bundle();
//	        bundle.putSerializable("CustomerInfo",mItem);
//			mIntent.putExtras(bundle);

                //mIntent.putExtra("CustomerInfo",mItem);
                //this.setResult(RESULT_OK, mIntent);
                this.finish();
//			Intent intent = new Intent(this, CustomerListActivity.class);
//			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ReservePackage() {
        Toast.makeText(this,"包裹信息已保存！",Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
//        Bundle bundle = new Bundle();
//        bundle.putString("Action","ReservePackage");
//        expressInPacListFragment.setArguments(bundle);
//        expressInPacListFragment.RefreshList();
    }
    //添加快件-》扫描快件条形码-》根据条形码获取快件信息-》加入listfragment里面-》刷新fragment

    private void StartCapture(){
        Log.d("PackageEditActivity执行了这个：","StartCapture");
        Intent intent = new Intent();
        intent.putExtra("Action","Capture");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }
    //像包裹TransPackge里面添加快件
    private void addExpressToPackage() {
        Log.d("PackageEditActivity执行了这个：","addExpressToPackage");

        //打开一个界面。搜索

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("PackageEditActivity执行了这个：","onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        ExpressLoader mLoader;
        switch (resultCode)
        {
            case RESULT_OK:
                switch (requestCode)
                {
                    case REQUEST_CAPTURE:
                        if (data.hasExtra("BarCode")) {//如果扫描结果得到的单号不为空
                            String id = data.getStringExtra("BarCode");
                            try {
                                mLoader = new ExpressLoader(this, this);  //加载一个快件
                                mLoader.Load(id);  //用id去查询这个快件是否存在

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case EXPRESS_EDIT:
                        Log.d("PackageEditActivity执行了这个：","编辑快件返回");
                        Bundle bundle = new Bundle();
                        bundle.putString("Action","RefreshList");
                        expressInPacListFragment.setArguments(bundle);
                        expressInPacListFragment.RefreshList();
                        break;
                    default:
                        Log.d("PackageEditActivity执行了这个：","其他返回");
                         break;
                }
                break;
            case RESULT_CANCELED:
                switch (requestCode){
                    case EXPRESS_EDIT:
                        Log.d("PackageEditActivity执行了这个：","编辑快件返回");
                        Bundle bundle = new Bundle();
                        bundle.putString("Action","RefreshList");
                        expressInPacListFragment.setArguments(bundle);
                        expressInPacListFragment.RefreshList();
                        break;
                }
            default:
                break;
        }

    }

    @Override
    public ExpressSheet getData() {
        Log.d("PackageEditActivity执行了这个：","getData");
        return eItem;
    }

    public void setData(List<ExpressSheet> data){
        Log.d("PackageEditActivity执行了这个：","setData1");
        expressInPacListFragment.setData(data);
    }


    @Override
    public void setData(ExpressSheet data) {
        Log.d("PackageEditActivity执行了这个：","setData2");
        eItem = data;

        //如果查询来的数据为空，说明不存在该快件
        if(eItem == null){
            Log.d("PackageEditActivity执行了这个：","不能存在该快件");
        }else{
            //将它加到包裹里的快件列表里
            Log.d("PackageEditActivity执行了这个：","存在该快件");
            Bundle bundle = new Bundle();
            bundle.putSerializable("ExpressSheet",eItem);
            bundle.putString("Action","AddExpressToPackage");

            expressInPacListFragment.setArguments(bundle);
            expressInPacListFragment.RefreshList();

        }
    }

    @Override
    public void notifyDataSetChanged() {
        Log.d("PackageEditActivity执行了这个：","notifyDataSetChanged");
        expressInPacListFragment.notifyDataSetChanged();
    }
}
