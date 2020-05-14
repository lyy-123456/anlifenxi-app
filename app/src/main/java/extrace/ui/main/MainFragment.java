package extrace.ui.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import extrace.misc.model.UserInfo;
import extrace.ui.accPkg.PackageAccActivity;
import extrace.ui.domain.ExpressEditActivity;
import extrace.ui.genZong.ExpressGenZongMainActivity;
import extrace.ui.misc.CustomerListActivity;
import extrace.ui.packages.PackageCreateActivity;
import extrace.ui.zhuanyun.ZhuanyunCreateActivity;

public class MainFragment  extends Fragment {
	
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MainFragment newInstance() {
    	MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        //args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MainFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
    	ExTraceApplication app = (ExTraceApplication) Objects.requireNonNull(this.getActivity()).getApplication();
    	UserInfo userInfo = app.getLoginUser();
		//显示主页
		View rootView = inflater.inflate(R.layout.fragment_main, container, false);
    	if(userInfo.getURull() == UserInfo.STATUS.SIJI){
    		//快件揽收
			rootView.findViewById(R.id.lanshou_root).setVisibility(View.GONE);
			//快件派送
			rootView.findViewById(R.id.paisong_root).setVisibility(View.GONE);
			//包裹打包
			rootView.findViewById(R.id.dopkg_root).setVisibility(View.GONE);
			//包裹拆包
			rootView.findViewById(R.id.caipkg_root).setVisibility(View.GONE);
			//客户管理
			rootView.findViewById(R.id.mng_root).setVisibility(View.GONE);
			//快件查询
			rootView.findViewById(R.id.query_root).setVisibility(View.GONE);

//			//包裹转运
//			rootView.findViewById(R.id.zhuanyun_root).setVisibility(View.GONE);
//
			//快件跟踪
			rootView.findViewById(R.id.genzong_root).setVisibility(View.GONE);

		}else if(userInfo.getURull() == UserInfo.STATUS.SAOMIAOYUAN){
			//快件揽收
			rootView.findViewById(R.id.lanshou_root).setVisibility(View.GONE);
			//快件派送
			rootView.findViewById(R.id.paisong_root).setVisibility(View.GONE);
//			//包裹打包
//			rootView.findViewById(R.id.dopkg_root).setVisibility(View.GONE);
//			//包裹拆包
//			rootView.findViewById(R.id.caipkg_root).setVisibility(View.GONE);
//			//客户管理
//			rootView.findViewById(R.id.mng_root).setVisibility(View.GONE);
//			//快件查询
//			rootView.findViewById(R.id.query_root).setVisibility(View.GONE);

			//包裹转运
			rootView.findViewById(R.id.zhuanyun_root).setVisibility(View.GONE);

//			//快件跟踪
//			rootView.findViewById(R.id.genzong_root).setVisibility(View.GONE);

		}else if(userInfo.getURull() == UserInfo.STATUS.KUAIDIYUAN) {
//			//快件揽收
//			rootView.findViewById(R.id.lanshou_root).setVisibility(View.GONE);
//			//快件派送
//			rootView.findViewById(R.id.paisong_root).setVisibility(View.GONE);
			//包裹打包
			rootView.findViewById(R.id.dopkg_root).setVisibility(View.GONE);
			//包裹拆包
			rootView.findViewById(R.id.caipkg_root).setVisibility(View.GONE);
			//客户管理
			rootView.findViewById(R.id.mng_root).setVisibility(View.GONE);
			//快件查询
			rootView.findViewById(R.id.query_root).setVisibility(View.GONE);
			//包裹转运
			rootView.findViewById(R.id.zhuanyun_root).setVisibility(View.GONE);
			//快件跟踪
			rootView.findViewById(R.id.genzong_root).setVisibility(View.GONE);
		}

        //fragment_main中的操作激发
		//快递揽收
        rootView.findViewById(R.id.action_ex_receive_icon).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						StartReceiveExpress();
					}
				});
        rootView.findViewById(R.id.action_ex_receive).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						StartReceiveExpress();
					}
				});
        //快递派送
        rootView.findViewById(R.id.action_ex_transfer_icon).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						StartQueryExpress();
					}
				});
        rootView.findViewById(R.id.action_ex_transfer).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						StartQueryExpress();
					}
				});


        //包裹拆包和打包在这里
        rootView.findViewById(R.id.action_pk_exp_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartOpenPackage();
				Log.d("adsa","包裹拆包");
            }


        });
        rootView.findViewById(R.id.action_pk_exp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				Log.d("adsa","包裹拆包");
                StartOpenPackage();
            }
        });
        //包裹打包
        rootView.findViewById(R.id.action_pk_pkg_icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartCreatePackage();
				Log.d("adsa","包裹打包");
            }
        });
        rootView.findViewById(R.id.action_pk_pkg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartCreatePackage();
				Log.d("adsa","包裹打包");
            }
        });

		//客户管理
        rootView.findViewById(R.id.action_cu_mng_icon).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						StartCustomerList();
					}
				});
        rootView.findViewById(R.id.action_cu_mng).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						StartCustomerList();
					}
				});

        //快件查询
        rootView.findViewById(R.id.action_ex_qur_icon).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						StartQueryExpress();
					}
				});
        rootView.findViewById(R.id.action_ex_qur).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						StartQueryExpress();
					}
				});


        //包裹转运
		rootView.findViewById(R.id.action_pkg_zhuanyun_icon).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StartZhuanyunPackage();
			}
		});
		rootView.findViewById(R.id.action_pkg_zhuanyun).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StartZhuanyunPackage();
			}
		});

		//包裹跟踪：
		rootView.findViewById(R.id.action_express_genzong).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StartGenZong();
			}
		});
		rootView.findViewById(R.id.action_express_genzong_icon).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				StartGenZong();
			}
		});
        return rootView;
    }

    //包裹拆包
    private void StartOpenPackage() {
        Intent intent = new Intent();
        intent.putExtra("Action","Open");
        intent.setClass(this.getActivity(), PackageAccActivity.class);
        startActivity(intent);
    }

    //包裹跟踪
	private void StartGenZong() {
        Intent intent = new Intent();
        intent.setClass(this.getActivity(), ExpressGenZongMainActivity.class);
        startActivityForResult(intent, 0);
	}

	//包裹转运
	private void StartZhuanyunPackage() {
    	Intent intent = new Intent();
    	intent.setClass(this.getActivity(), ZhuanyunCreateActivity.class);
    	startActivityForResult(intent,0);
	}

    //包裹打包
    private void StartCreatePackage() {
        Intent intent = new Intent();
        intent.putExtra("Action","Create");
        intent.setClass(this.getActivity(), PackageCreateActivity.class);
        startActivityForResult(intent, 0);
    }

    void StartReceiveExpress()
    {
		Intent intent = new Intent();
		intent.putExtra("Action","New");
		intent.setClass(this.getActivity(), ExpressEditActivity.class);
		startActivityForResult(intent, 0);  	
    }

    void StartQueryExpress()
    {
		Intent intent = new Intent();
		intent.putExtra("Action","Query");
		intent.setClass(this.getActivity(), ExpressEditActivity.class);
		startActivityForResult(intent, 0);  	
    }

    void StartCustomerList()
    {
		Intent intent = new Intent();
		intent.putExtra("Action","None");
		intent.setClass(this.getActivity(), CustomerListActivity.class);
		startActivityForResult(intent, 0);
    }

}
