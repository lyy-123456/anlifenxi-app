package extrace.loader;

import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import extrace.misc.model.ExpressSheet;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class ExpressListLoader extends HttpAsyncTask {

	String url;
	IDataAdapter<List<ExpressSheet>> adapter;
	private Activity context;
	
	public ExpressListLoader(IDataAdapter<List<ExpressSheet>> adpt, Activity context) {
		super(context);
		this.context = context;
		adapter = adpt;
		url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
	}
	
	@Override
	//接收数据
	public void onDataReceive(String class_data, String json_data) {
		if(class_data.equals("MoveExpressIntoPackage")){
			if(json_data.equals("true")){
				Toast.makeText(context, "包裹信息已保存!", Toast.LENGTH_SHORT).show();
			}
		}else
		if(json_data.equals("Deleted")){
			//adapter.getData().remove(0);	//这个地方不好处理
			Toast.makeText(context, "快件信息已删除!", Toast.LENGTH_SHORT).show();
		}
		else{
			List<ExpressSheet> cstm = JsonUtils.fromJson(json_data, new TypeToken<List<ExpressSheet>>(){});
			adapter.setData(cstm);
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		Log.i("onStatusNotify", "onStatusNotify: " + str_response);
	}

	public void LoadExpressListInPackage(String pkgId)
	{
		url += "getExpressListInPackage/PackageId/"+pkgId+"?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	//像包裹中添加运单列表
//	public void MoveExpressListIntoPackage(List<ExpressSheet> esList,String pkgId)
//	{
//		//转换为json数据
//		String jsonObj = JsonUtils.toJson(esList, true);
//		url += "MoveExpressListIntoPackage";
//		try{
//			execute(url,"POST", jsonObj,pkgId);
//		}catch (Exception e){
//			e.printStackTrace();
//		}
//
//	}

	public  void MoveExpressFromPackage(String id, String pkgId){
		url += "MoveExpressFromPackage/"+id+"/"+pkgId+"?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//向包裹中添加运单
	public void MoveExpressIntoPackage(String id,String pkgId)
	{
		url += "MoveExpressIntoPackage/"+id+"/"+pkgId+"?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
