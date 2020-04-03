package extrace.ui.packages;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import extrace.loader.ExpressListLoader;
import extrace.misc.model.TransPackage;
import extrace.ui.domain.ExpressListAdapter;
import extrace.ui.main.R;

public class PackageEditActivity extends AppCompatActivity {

    String data[] = {"上海", "北京", "南京"};//定义数据源

    private TransPackage transPackage;
    private ExpressInPacListFragment expressInPacListFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_edit);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        transPackage = (TransPackage) bundle.getSerializable("transPackage");
        //Toast.makeText(this,transPackage.toString(),Toast.LENGTH_SHORT).show();

        expressInPacListFragment=ExpressInPacListFragment.instance(data);//创建fragment对象
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
                Log.d("package","选中了完成");
                return true;
            case R.id.action_refresh:
//                if (mItem != null) {
//                    //Refresh(mItem.getID());
//                }
                Log.d("package","选中了刷新");
                return true;
            case R.id.action_new:
                Log.d("package","选中了新建");
                addExpressToPackage();
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
    //添加快件-》扫描快件条形码-》根据条形码获取快件信息-》加入listfragment里面-》刷新fragment

    //像包裹TransPackge里面添加快件
    private void addExpressToPackage() {
        Log.d("PackageEditActivity执行了这个：","addExpressToPackage");

        //打开一个界面。搜索u
    }
}
