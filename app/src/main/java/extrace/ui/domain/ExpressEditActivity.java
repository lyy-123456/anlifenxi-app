package extrace.ui.domain;

import java.text.SimpleDateFormat;
import java.util.Locale;

import extrace.ui.login_register_reset.Register_Activity;
import extrace.ui.main.ExTraceApplication;
import zxing.util.CaptureActivity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import extrace.loader.ExpressLoader;
import extrace.misc.model.CustomerInfo;
import extrace.misc.model.ExpressSheet;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;
import extrace.ui.misc.CustomerListActivity;
import zxing.util.ValidateUtil;

public class ExpressEditActivity extends AppCompatActivity implements ActionBar.TabListener,IDataAdapter<ExpressSheet> {

//	public static final int INTENT_NEW = 1; 
//	public static final int INTENT_EDIT = 2; 
	
	public static final int REQUEST_CAPTURE = 100; 
	public static final int REQUEST_RCV = 101; 
	public static final int REQUEST_SND = 102; 

	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	private ExpressSheet mItem;
	private static float weight;
	private static float tranFee;
	private static float packageFee;
	private static float insuFee;
	private ExpressLoader mLoader;
	private Intent mIntent;
	private ExpressEditFragment1 baseFragment; 
	private ExpressEditFragment2 externFragment; 
	private MenuItem action_menu_item;
	private static boolean new_es = false;	//新建
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("ExpressEditActivity执行了这个：","onCreate");
		setContentView(R.layout.activity_express_edit);

		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
				
		mIntent = getIntent();
		if (mIntent.hasExtra("Action")) {
			if(mIntent.getStringExtra("Action").equals("New")){
				new_es = true;
				StartCapture();
			}
			else if(mIntent.getStringExtra("Action").equals("Query")){
				StartCapture();
			}
			else if(mIntent.getStringExtra("Action").equals("Edit")){
				ExpressSheet es;

				if (mIntent.hasExtra("ExpressSheet")) {
					es = (ExpressSheet) mIntent.getSerializableExtra("ExpressSheet");
					Log.d("ExpressEditActivity执行了这个：",es.toString());
					Refresh(es.getID());
				} else {
					this.setResult(RESULT_CANCELED, mIntent);
					this.finish();
				}
			}
			else{
				this.setResult(RESULT_CANCELED, mIntent);
				this.finish();
			}
		}
		else{
			this.setResult(RESULT_CANCELED, mIntent);
			this.finish();
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.express_edit, menu);
		action_menu_item = menu.findItem(R.id.action_action);
		action_menu_item.setVisible(false);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
		case R.id.action_action:
			if(item.getTitle().equals("收件")){
				Receive(mItem.getID());
			}
			else if(item.getTitle().equals("交付")){
				Delivery(mItem.getID());
			}
			else if(item.getTitle().equals("追踪")){
				Tracert(mItem.getID());
			}
			return true;
		case R.id.action_ok:
			Save();
			return true;
		case R.id.action_refresh:
			if (mItem != null) {
				Refresh(mItem.getID());
			}
			return true;
		case (android.R.id.home):
			Log.d("ExpressEditActivity","点击了返回键");
	        mIntent.putExtra("ExpressSheet",mItem);  
			this.setResult(RESULT_OK, mIntent);
			this.finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Log.d("ExpressEditActivity","点击了返回键1");
		this.setResult(RESULT_OK);
		this.finish();
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	@Override
	public ExpressSheet getData() {
		return mItem;
	}

	@Override
	public void setData(ExpressSheet data) {
		mItem = data;
	}

	@Override
	public void notifyDataSetChanged() {
		if(baseFragment != null ){
			baseFragment.RefreshUI(mItem);

		}
		if(externFragment != null){
			externFragment.RefreshUI(mItem);
		}
		MenuDisplay(mItem.getStatus());
	}

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		CustomerInfo customer;

		switch (resultCode) {
			case RESULT_OK:
				switch (requestCode) {
					case REQUEST_CAPTURE:
						if (data.hasExtra("BarCode")) {//如果扫描结果得到的单号不为空
							String id = data.getStringExtra("BarCode");
							try {
								mLoader = new ExpressLoader(this, this);  //加载一个快件
								if (new_es) {
									new_es = false;
									mLoader.New(id);
								} else {
									mLoader.Load(id);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						break;
					case REQUEST_RCV:
						if (data.hasExtra("CustomerInfo")) {
							customer = (CustomerInfo) data.getSerializableExtra("CustomerInfo");
							mItem.setRecever(customer);
							baseFragment.displayRcv(mItem);
						}
						break;
					case REQUEST_SND:
						if (data.hasExtra("CustomerInfo")) {
							customer = (CustomerInfo) data.getSerializableExtra("CustomerInfo");
							mItem.setSender(customer);
							baseFragment.displaySnd(mItem);
						}
						break;
					default:
						break;
				}
				break;
			default:
				break;
		}
	}

	void MenuDisplay(int status){
		action_menu_item.setVisible(true);
		action_menu_item.setEnabled(true);
		switch(status){
		case ExpressSheet.STATUS.STATUS_CREATED:
			action_menu_item.setTitle("收件");
			break;
		case ExpressSheet.STATUS.STATUS_TRANSPORT:
			action_menu_item.setTitle("交付");
			break;
		case ExpressSheet.STATUS.STATUS_DELIVERIED:
			action_menu_item.setTitle("追踪");
			break;
		default:
			action_menu_item.setVisible(false);
			break;
		}
	}
	
	void Refresh(String id){
		try {
			mLoader = new ExpressLoader(this, this);
			mLoader.Load(id);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	//快件揽收
	void Receive(String id){
		try {
			mLoader = new ExpressLoader(this, this);
			mLoader.Receive(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	void Delivery(String id){
		try {
			mLoader = new ExpressLoader(this, this);
			mLoader.Delivery(id);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	void Tracert(String id){
		//快件追踪
	}
	
	void Save(){
		mItem.setWeight(weight);
		mItem.setTranFee(tranFee);
		mItem.setPackageFee(packageFee);
		mItem.setInsuFee(insuFee);
		mLoader = new ExpressLoader(this, this);
		mLoader.Edit(mItem);
	}
	
	private void StartCapture(){
		Intent intent = new Intent();
		intent.putExtra("Action","Capture");
		intent.setClass(this, CaptureActivity.class);
		startActivityForResult(intent, REQUEST_CAPTURE);  	
	}
	
	private void GetCustomer(int intent_code) {
		Intent intent = new Intent();
		intent.setClass(this, CustomerListActivity.class);
		if(intent_code == REQUEST_RCV){
			if(baseFragment.mRcvNameView.getTag() == null){
				intent.putExtra("Action","New");
			}
			else{
				intent.putExtra("Action","New");
				intent.putExtra("CustomerInfo",(CustomerInfo)baseFragment.mRcvNameView.getTag());
			}
		}
		else if(intent_code == REQUEST_SND){
			if(baseFragment.mSndNameView.getTag() == null){
				intent.putExtra("Action","New");
			}
			else{
				intent.putExtra("Action","New");
				intent.putExtra("CustomerInfo",(CustomerInfo)baseFragment.mSndNameView.getTag());
			}
		}
		startActivityForResult(intent, intent_code);
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
			switch(position){
			case 0:
				baseFragment = ExpressEditFragment1.newInstance();
				return baseFragment;
			case 1:
				externFragment = ExpressEditFragment2.newInstance();
				return externFragment;
			}
			return ExpressEditFragment1.newInstance();
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_ex_edit1).toUpperCase(l);
			case 1:
				return getString(R.string.title_ex_edit2).toUpperCase(l);
			}
			return null;
		}
	}

	public static class ExpressEditFragment1 extends Fragment {
		
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

		private ImageView mbtnCapture;
		private ImageView mbtnRcv;
		private ImageView mbtnSnd;

		public static ExpressEditFragment1 newInstance() {
			ExpressEditFragment1 fragment = new ExpressEditFragment1();
			return fragment;
		}

		public ExpressEditFragment1() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_express_edit1,
					container, false);
			mIDView = (TextView) rootView.findViewById(R.id.expressId);
			mRcvNameView = (TextView) rootView.findViewById(R.id.expressRcvName);
			mRcvTelCodeView = (TextView) rootView.findViewById(R.id.expressRcvTel);
			mRcvAddrView = (TextView) rootView.findViewById(R.id.expressRcvAddr);
			mRcvDptView = (TextView) rootView.findViewById(R.id.expressRcvDpt);
			mRcvRegionView = (TextView) rootView.findViewById(R.id.expressRcvRegion);	

			mSndNameView = (TextView) rootView.findViewById(R.id.expressSndName);
			mSndTelCodeView = (TextView) rootView.findViewById(R.id.expressSndTel);
			mSndAddrView = (TextView) rootView.findViewById(R.id.expressSndAddr);
			mSndDptView = (TextView) rootView.findViewById(R.id.expressSndDpt);
			mSndRegionView = (TextView) rootView.findViewById(R.id.expressSndRegion);	

			mRcvTimeView = (TextView) rootView.findViewById(R.id.expressAccTime);
			mSndTimeView = (TextView) rootView.findViewById(R.id.expressDlvTime);

			mStatusView =  (TextView) rootView.findViewById(R.id.expressStatus);
			
			mbtnCapture = (ImageView) rootView.findViewById(R.id.action_ex_capture_icon);
			mbtnCapture.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							((ExpressEditActivity) getActivity()).StartCapture();
						}
					});
			mbtnRcv = (ImageView) rootView.findViewById(R.id.action_ex_rcv_icon);
			mbtnRcv.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							((ExpressEditActivity) getActivity()).GetCustomer(REQUEST_RCV);
						}
					});
			mbtnSnd = (ImageView) rootView.findViewById(R.id.action_ex_snd_icon);
			mbtnSnd.setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							((ExpressEditActivity) getActivity()).GetCustomer(REQUEST_SND);
						}
					});
			return rootView;
		}
		
		void RefreshUI(ExpressSheet es){
			mIDView.setText(es.getID());
			displayRcv(es);
			displaySnd(es);
			SimpleDateFormat myFmt=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(es.getAccepteTime() != null)
				mRcvTimeView.setText(myFmt.format(es.getAccepteTime()));

//				mRcvTimeView.setText(DateFormat.format("yyyy-MM-dd HH:mm:ss", es.getAccepteTime()));
			else
				mRcvTimeView.setText(null);
			if(es.getDeliveTime() != null)
				mSndTimeView.setText(DateFormat.format("yyyy-MM-dd HH:mm:ss", es.getDeliveTime()));
			else
				mSndTimeView.setText(null);

			String stText = "";
			switch(es.getStatus()){
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
			case ExpressSheet.STATUS.STATUS_DAIPAISONG:
				stText="待派送";
				break;
			}
			mStatusView.setText(stText);
			displayBtn(es);
		}
		
		void displayBtn(ExpressSheet es){	//按钮状态控制
			if(es.getStatus() == ExpressSheet.STATUS.STATUS_CREATED){
				mbtnRcv.setVisibility(View.VISIBLE);
				mbtnSnd.setVisibility(View.VISIBLE);
			}
			else{
				mbtnRcv.setVisibility(View.INVISIBLE);
				mbtnSnd.setVisibility(View.INVISIBLE);
			}
		}

		void displayRcv(ExpressSheet es){
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
		
		void displaySnd(ExpressSheet es){
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
	}

	public static class ExpressEditFragment2 extends Fragment {

		private TextView mWeightView;
		private TextView mTranFeeView;
		private TextView mPackageFeeView;
		private TextView mInsuFeeView;


		public static ExpressEditFragment2 newInstance() {
			ExpressEditFragment2 fragment = new ExpressEditFragment2();
			return fragment;
		}

		public ExpressEditFragment2() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_express_edit2,
					container, false);
			mWeightView = (TextView) rootView.findViewById(R.id.expressWeight);
			mTranFeeView = (TextView) rootView.findViewById(R.id.expressTranFee);
			mPackageFeeView = (TextView) rootView.findViewById(R.id.expressPackageFee);
			mInsuFeeView = (TextView) rootView.findViewById(R.id.expressInsuFee);
			mWeightView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_VARIATION_NORMAL);
			mTranFeeView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_VARIATION_NORMAL);
			mPackageFeeView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_VARIATION_NORMAL);
			mInsuFeeView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_VARIATION_NORMAL);
			return rootView;
		}
		void RefreshUI(final ExpressSheet es){

			if(es.getWeight()!=null && es.getPackageFee()!=null && es.getTranFee()!=null && es.getInsuFee()!=null) {
				mWeightView.setText(String.valueOf(es.getWeight()));
				mTranFeeView.setText(String.valueOf(es.getTranFee()));
				mPackageFeeView.setText(String.valueOf(es.getPackageFee()));
				mInsuFeeView.setText(String.valueOf(es.getInsuFee()));
				weight=es.getWeight();
				tranFee=es.getTranFee();
				packageFee=es.getPackageFee();
				insuFee=es.getInsuFee();
				mWeightView.setFocusable(false);
				mTranFeeView.setFocusable(false);
				mPackageFeeView.setFocusable(false);
				mInsuFeeView.setFocusable(false);
			}
			mWeightView.addTextChangedListener(new TextWatcher() {
				private CharSequence word;
				private int selectionStart;
				private int selectionEnd;
				@Override
				public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
					word = charSequence;
				}

				@Override
				public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

				}

				@Override
				public void afterTextChanged(Editable s) {
					if(word.length()==0) {
						Toast.makeText(getActivity(), "快件重量不能为空", Toast.LENGTH_SHORT).show();
					}
					selectionStart = mWeightView.getSelectionStart();
					selectionEnd = mWeightView.getSelectionEnd();
					if (!ValidateUtil.isOnlyPointNumber(mWeightView.getText().toString()) && s.length() > 0) {
						//删除多余输入的字（不会显示出来）
						Toast.makeText(getActivity(), "只能保留两位小数", Toast.LENGTH_SHORT).show();
						s.delete(selectionStart - 1, selectionEnd);
						mWeightView.setText(s);
						System.out.println(word.toString()+"weight");
					}
					weight = Float.parseFloat(word.toString());
				}
			});

			mTranFeeView.addTextChangedListener(new TextWatcher() {
				private CharSequence word;
				private int selectionStart;
				private int selectionEnd;

				@Override
				public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
					word = charSequence;
				}

				@Override
				public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

				}

				@Override
				public void afterTextChanged(Editable s) {
					if(word.length()==0) {
						Toast.makeText(getActivity(), "运费不能为空", Toast.LENGTH_SHORT).show();
					}
					selectionStart = mTranFeeView.getSelectionStart();
					selectionEnd = mTranFeeView.getSelectionEnd();
					if (!ValidateUtil.isOnlyPointNumber(mTranFeeView.getText().toString()) && s.length() > 0) {
						//删除多余输入的字（不会显示出来）
						Toast.makeText(getActivity(), "只能保留两位小数", Toast.LENGTH_SHORT).show();
						s.delete(selectionStart - 1, selectionEnd);
						mTranFeeView.setText(s);
						System.out.println(word.toString()+"tranFee");
					}
					tranFee = Float.parseFloat(word.toString());
				}
			});
			mPackageFeeView.addTextChangedListener(new TextWatcher() {
				private CharSequence word;
				private int selectionStart;
				private int selectionEnd;

				@Override
				public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
					word = charSequence;
				}

				@Override
				public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

				}

				@Override
				public void afterTextChanged(Editable s) {
					if(word.length()==0) {
						Toast.makeText(getActivity(), "包裹费不能为空", Toast.LENGTH_SHORT).show();
					}
					selectionStart = mPackageFeeView.getSelectionStart();
					selectionEnd = mPackageFeeView.getSelectionEnd();
					if (!ValidateUtil.isOnlyPointNumber(mPackageFeeView.getText().toString()) && s.length() > 0) {
						//删除多余输入的字（不会显示出来）
						Toast.makeText(getActivity(), "只能保留两位小数", Toast.LENGTH_SHORT).show();
						s.delete(selectionStart - 1, selectionEnd);
						mPackageFeeView.setText(s);
						System.out.println(word.toString()+"packageFee");
					}
					packageFee = Float.parseFloat(word.toString());
				}
			});
			mInsuFeeView.addTextChangedListener(new TextWatcher() {
				private CharSequence word;
				private int selectionStart;
				private int selectionEnd;

				@Override
				public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
					word = charSequence;
				}

				@Override
				public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

				}

				@Override
				public void afterTextChanged(Editable s) {
					if(word.length()==0) {
						Toast.makeText(getActivity(), "运费险不能为空", Toast.LENGTH_SHORT).show();
					}
					selectionStart = mInsuFeeView.getSelectionStart();
					selectionEnd = mInsuFeeView.getSelectionEnd();
					if (!ValidateUtil.isOnlyPointNumber(mInsuFeeView.getText().toString()) && s.length() > 0) {
						//删除多余输入的字（不会显示出来）
						Toast.makeText(getActivity(), "只能保留两位小数", Toast.LENGTH_SHORT).show();
						s.delete(selectionStart - 1, selectionEnd);
						System.out.println(word.toString()+"insuFee");
					}
					insuFee= Float.parseFloat(word.toString());
				}
			});
		}
	}


}
