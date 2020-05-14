package extrace.ui.main;

import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import extrace.misc.model.UserInfo;
import extrace.ui.domain.ExpressListFragment;
import extrace.ui.domain.ExpressListFragment.OnFragmentInteractionListener;
import extrace.ui.login_register_reset.Login_Activity;
import extrace.ui.zhuanyun.GpsLocationActivity;
import extrace.ui.zhuanyun.MyLocationActivity;

public class MainActivity extends AppCompatActivity implements ActionBar.TabListener,OnFragmentInteractionListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    SectionsPagerAdapter mSectionsPagerAdapter;
    ViewPager mViewPager;
    private  ExTraceApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        app = (ExTraceApplication)this.getApplication();
        Toast.makeText(this,app.getLoginUser().toString(),Toast.LENGTH_SHORT).show();
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        // Set up the action bar.
        //设置菜单栏
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager =  (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }


   }

    @Override
    //菜单
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if(app.getLoginUser().getURull() == UserInfo.STATUS.SIJI){

        }
        switch (app.getLoginUser().getURull()){
            case UserInfo.STATUS.SIJI:
                setTitle("卓越快递（司机）");;
                break;
            case UserInfo.STATUS.SAOMIAOYUAN:
                setTitle("卓越快递（业务员）");
                break;
            case UserInfo.STATUS.FUZEREN:
                setTitle("卓越快递（负责人）");
                break;
            case UserInfo.STATUS.KUAIDIYUAN:
                setTitle("卓越快递（快递员）");
                break;
             default:
                setTitle("卓越快递");
                break;
        }
        return true;
    }

    @Override
    //只写了设置，其他的都没有实现
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch(id){
        case R.id.action_my_location:
            Intent intent2 = new Intent();
            intent2.setClass(this, GpsLocationActivity.class);
            startActivity(intent2);
            return true;
        case R.id.action_login:
            startActivity(new Intent(MainActivity.this, Login_Activity.class));
            return true;
        case R.id.action_logout:
            //清空
            editor.clear();
            editor.commit();
            startActivity(new Intent(MainActivity.this,Login_Activity.class));
            finish();
            return true;
        case R.id.action_settings:
    		Intent intent = new Intent();
    		intent.setClass(this, SettingsActivity.class);
    		startActivityForResult(intent, 0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    //tab被选择的时候
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            if(app.getLoginUser().getURull() == UserInfo.STATUS.FUZEREN){
                switch(position){
                    case 0:
                        return MainFragment.newInstance();
                    case 1:
                        return ExpressListFragment.newInstance("ExDLV");	//派送快件
                    case 2:
                        return ExpressListFragment.newInstance("ExRCV");	//揽收快件
                    case 3:
                        return ExpressListFragment.newInstance("ExTAN");	//转运快件
                }
            }else if(app.getLoginUser().getURull() == UserInfo.STATUS.KUAIDIYUAN){
                switch(position){
                    case 0:
                        return MainFragment.newInstance();
                    case 1:
                        return ExpressListFragment.newInstance("ExDLV");	//派送快件
                    case 2:
                        return ExpressListFragment.newInstance("ExRCV");	//揽收快件
                }
            }
            else if(app.getLoginUser().getURull() == UserInfo.STATUS.SAOMIAOYUAN){
                switch(position){
                    case 0:
                        return MainFragment.newInstance();
                }
            }if(app.getLoginUser().getURull() == UserInfo.STATUS.SIJI){
                switch(position){
                    case 0:
                        return MainFragment.newInstance();
                    case 1:
                        return ExpressListFragment.newInstance("ExTAN");	//转运快件
                }
            }

        	return null;
        }

        @Override
    public int getCount() {
        // 总共4页.
            int i = 0;
        if(app.getLoginUser().getURull() == UserInfo.STATUS.FUZEREN){
            i = 4;
        }else if(app.getLoginUser().getURull() == UserInfo.STATUS.SIJI){
            i = 2;
        }else if(app.getLoginUser().getURull() == UserInfo.STATUS.SAOMIAOYUAN){
            i = 1;
        }else  if(app.getLoginUser().getURull() == UserInfo.STATUS.KUAIDIYUAN){
            i = 3;
        }
        return i;
    }

    @Override
    //fragment的标题
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        switch (position) {
            case 0:
                return getString(R.string.title_section1).toUpperCase(l);
            case 1:
                if(app.getLoginUser().getURull() == UserInfo.STATUS.SIJI) return getString(R.string.title_section4).toUpperCase(l);
                return getString(R.string.title_section2).toUpperCase(l);
            case 2:
                return getString(R.string.title_section3).toUpperCase(l);
            case 3:
                return getString(R.string.title_section4).toUpperCase(l);
        }
        return null;
    }
}

    //没有实现？？
	@Override
	public void onFragmentInteraction(String id) {
		// TODO Auto-generated method stub
		
	}

}
