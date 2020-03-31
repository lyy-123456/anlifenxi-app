package extrace.ui.packages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import extrace.ui.main.R;

public class PackageEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_edit);

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
}
