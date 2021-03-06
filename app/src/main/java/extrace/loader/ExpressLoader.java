package extrace.loader;

import android.app.Activity;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import extrace.misc.model.ExpressSheet;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam.RETURN_STATUS;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class ExpressLoader extends HttpAsyncTask {

	String url;
	IDataAdapter<ExpressSheet> adapter;
	private Activity context;

	public ExpressLoader(IDataAdapter<ExpressSheet> adpt, Activity context) {
		super(context);
		this.context = context;
		adapter = adpt;
		url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
	}

	@Override
	public void onDataReceive(String class_name, String json_data) {
		if(class_name.equals("ExpressSheet"))
		{
			//转换为快件expresssheet对象
			ExpressSheet ci = JsonUtils.fromJson(json_data, new TypeToken<ExpressSheet>(){});
			adapter.setData(ci);
			adapter.notifyDataSetChanged();
		}
		else if(class_name.equals("E_ExpressSheet"))		//已经存在
		{
			Toast.makeText(context, json_data, Toast.LENGTH_SHORT).show();
		}
		else if(class_name.equals("R_ExpressSheet"))		//保存完成
		{
			ExpressSheet ci = JsonUtils.fromJson(json_data, new TypeToken<ExpressSheet>(){});
			adapter.getData().setID(ci.getID());
			adapter.getData().onSave();
			adapter.notifyDataSetChanged();
			Toast.makeText(context, "快件运单信息保存完成!", Toast.LENGTH_SHORT).show();
		}else if(class_name.equals("isArrived")){
			Boolean bool = JsonUtils.fromJson(json_data,new TypeToken<Boolean>(){});
			if(bool){
				ExpressSheet expressSheet = new ExpressSheet();
				expressSheet.setID("00000000");
				adapter.setData(expressSheet);
			}else{
				Toast.makeText(context,"包裹没有到达",Toast.LENGTH_SHORT).show();
			}
		}else if(class_name.equals("NR_ExpressSheet"))		//已经存在
		{
			Toast.makeText(context, json_data, Toast.LENGTH_SHORT).show();
		}
		else
		{
			Toast.makeText(context, json_data, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onStatusNotify(RETURN_STATUS status, String str_response) {
		// TODO Auto-generated method stub
		
	}

	public void Load(String id)
	{
		url += "getExpressSheet/"+ id + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//
	public void New(String id)
	{
		int uid = ((ExTraceApplication)context.getApplication()).getLoginUser().getUID();
		url += "newExpressSheet/id/"+ id + "/uid/"+ uid + "?_type=json";
			try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Edit(ExpressSheet es)
	{
		String jsonObj = JsonUtils.toJson(es, true);
		url += "addExpressSheetMessage";
		try {
			execute(url, "POST", jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void Receive(String id)
	{
		int uid = ((ExTraceApplication)context.getApplication()).getLoginUser().getUID();
		url += "receiveExpressSheetId/id/"+ id + "/uid/"+ uid + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void Delivery(String id)
	{
		int uid = ((ExTraceApplication)context.getApplication()).getLoginUser().getUID();
		url += "deliveryExpressSheetId/id/"+ id + "/uid/"+ uid + "?_type=json";
		try {
			execute(url, "GET");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeExpressStatus(String id, int status){
		url += "changeExpressStatus/"+id+"/"+status+"?_type=json";
		try{
			execute(url,"GET");
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void  saveOneExpressSheet(ExpressSheet expressSheet){
		String json_data = JsonUtils.toJson(expressSheet,true);
		url += "saveOneExpressSheet";
		try{
			execute(url,"POST",json_data);
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public void isArrived(String expressID, String dptID) {
		url += "isarrived/"+expressID+"/"+dptID+"?_type=json";
		try{
			execute(url,"GET");
		}catch (Exception e){
			e.printStackTrace();
		}
	}

	//lsy 修改快件状态为已移出
	public void changeStatusInTranspackageContentToOut(String expressId,String packageId){
		url += "changeStatusInTranspackageContentToOut/"+expressId+"/"+packageId+"?_type=json";
		try{
			execute(url,"GET");
		}catch (Exception e){
			e.printStackTrace();
		}
	}
}
