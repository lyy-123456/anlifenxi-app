package extrace.loader;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

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
			TransPackage ci = JsonUtils.fromJson(json_data, new TypeToken<TransPackage>(){});
			adapter.setData(ci);
			Log.d("TransPackageLoader,onDataReceive",ci.toString());
			adapter.notifyDataSetChanged();
		}
		else if(class_name.equals("R_TransPackage"))		//保存完成
		{
			TransPackage ci = JsonUtils.fromJson(json_data, new TypeToken<TransPackage>(){});
			adapter.getData().setID(ci.getID());
			adapter.getData().onSave();
			adapter.notifyDataSetChanged();
			Toast.makeText(context, "包裹信息保存完成!", Toast.LENGTH_SHORT).show();
		}
		else if(class_name.equals("E_TransPackage"))
		{
			Toast.makeText(context, "包裹信息已存在", Toast.LENGTH_SHORT).show();
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
		url += "saveTransPackage";
		try {
			execute(url, "POST", jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void pkgAcc(TransPackage transPackage){
		//改变包裹状态
	}

}
