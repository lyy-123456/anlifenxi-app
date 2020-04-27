package extrace.loader;

import java.util.List;

import android.app.Activity;

import extrace.misc.model.ListTransHistory;
import extrace.misc.model.ListTransPackage;
import extrace.misc.model.TransPackage;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class TransPackageListLoader extends HttpAsyncTask{

	String url;
	IDataAdapter<List<TransPackage>> adapter;
	private Activity context;
	
	public TransPackageListLoader(IDataAdapter<List<TransPackage>> adpt, Activity context) {
		super(context);
		this.context = context;
		adapter = adpt;
		url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
	}

	@Override
	public void onDataReceive(String class_name, String json_data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		// TODO Auto-generated method stub
		
	}

	//lyy 新建改变所有包裹的状态
	public  void changeTransPackageListStatus(ListTransPackage transPackageList, int status){
		String jsonObj = JsonUtils.toJson(transPackageList,true);
		url += "changeTransPackageListStatus/"+status+"?_type=json";
		try{
			execute(url,"POST",jsonObj);
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
