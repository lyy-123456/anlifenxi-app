package extrace.loader;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import extrace.misc.model.ExpressSheet;
import extrace.misc.model.TransPackage;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class TransPackageLoader extends HttpAsyncTask {

	String url;
	IDataAdapter<TransPackage> adapter;
	private Activity context;
	
	public TransPackageLoader(IDataAdapter<TransPackage> adpt, Activity context) {
		super(context);
		this.context = context;
		adapter = adpt;
		url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
	}

	@Override
	public void onDataReceive(String class_name, String json_data) {
		if(class_name.equals("TransPackage"))
		{
			//Toast.makeText(context,json_data,Toast.LENGTH_LONG).show();
			TransPackage ci = JsonUtils.fromJson(json_data, new TypeToken<TransPackage>(){});
			adapter.setData(ci);
			//Log.d("TransPackageLoader,onDataReceive",ci.toString());
			adapter.notifyDataSetChanged();
		}
		else if(class_name.equals("R_TransPackage"))		//保存完成
		{
			TransPackage ci = JsonUtils.fromJson(json_data, new TypeToken<TransPackage>(){});
			adapter.getData().setID(ci.getID());
			adapter.getData().onSave();
			adapter.notifyDataSetChanged();
//			Toast.makeText(context, "包裹信息保存完成!", Toast.LENGTH_SHORT).show();
		}
		else if(class_name.equals("E_TransPackage"))
		{
			Toast.makeText(context, "包裹信息已存在", Toast.LENGTH_SHORT).show();
		}
		else if(class_name.equals("N_TransPackage")){
			Toast.makeText(context, json_data, Toast.LENGTH_SHORT).show();
		}
		else {
			Toast.makeText(context, json_data, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		// TODO Auto-generated method stub
		
	}

	public void Load(String id)
	{
		url += "getTransPackage/"+ id + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//新建一个包裹
	public void New(TransPackage transPackage)
	{
		String jsonObj = JsonUtils.toJson(transPackage,true);
		url+= "newTransPackage";
		try{
			execute(url,"POST",jsonObj);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public void Save(TransPackage tp)
	{
		String jsonObj = JsonUtils.toJson(tp, true);
		url += "saveOneTransPackage";
		try {
			execute(url, "POST", jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//lyy 新增
	public void pkgAcc(String pkgId){
		//改变包裹状态
		//Toast.makeText(context,"执行了pkgAcc方法",Toast.LENGTH_LONG).show();
		url += "accPkgAndChangStatus/"+pkgId+"?_type=json";
		try{
			execute(url,"GET");
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	//lyy 新增改变一个包裹的状态
	public void changeTransPackageStatus(TransPackage transPackage, int status){
		String jsonObj = JsonUtils.toJson(transPackage,true);
		url += "changeTransPackageStatus/"+status+"?_type=json";
		try{
			execute(url,"POST",jsonObj);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	//lyy 改变一个包裹里的快件的状态
	public  void changeExpressListStatusInTransPackage(String pkgID,int status){
		url += "changeExpressStatusInTransPackage/"+pkgID+"/"+status+"?_type=json";
		try{
			execute(url,"GET");
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	//lsy 改变一个包裹的状态为已拆包
	public void changeTransPackageStatustoUnpackaged(String pkgId){
		url += "changeTransPackageStatustoUnpackaged/"+ pkgId+"?_type=json";
		try{
			execute(url,"GET");
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
