package extrace.loader;

import android.app.Activity;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import extrace.misc.model.TransHistory;
import extrace.misc.model.TransNode;
import extrace.misc.model.TransPackage;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class TransHistoryLoader extends HttpAsyncTask {
    String url;
    IDataAdapter<TransHistory> adapter;
    private Activity context;
    public TransHistoryLoader(IDataAdapter<TransHistory> adapter,Activity context) {
        super(context);
        this.adapter = adapter;
        this.context = context;
        url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
    }

    @Override
    public void onDataReceive(String class_name, String json_data) {
        if(class_name.equals("N_TransPackage")){
            Toast.makeText(context,"包裹信息不存在",Toast.LENGTH_SHORT).show();
        }else if(class_name.equals("TransHistory")){
            TransHistory transHistory = JsonUtils.fromJson(json_data,new TypeToken<TransHistory>(){});
            Toast.makeText(context,transHistory.toString(),Toast.LENGTH_SHORT).show();
            adapter.setData(transHistory);

            adapter.notifyDataSetChanged();
        }
        else {
            Toast.makeText(context,"成功",Toast.LENGTH_SHORT).show();
        }
    }

    //lyy 往历史里添加一个数据
    public  void AddOneTransHistory(TransHistory transHistory){

        String json_data = JsonUtils.toJson(transHistory,true);
        url += "addOneTransHistory";
        //Toast.makeText(context,"TranshistoryListLoader执行了AddOneTransHistory方法"+url,Toast.LENGTH_LONG).show();
        try{
            execute(url,"POST",json_data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //lyy 新增
    public void getRecentOneTranHistory(TransPackage transPackage){
        String jsonObj = JsonUtils.toJson(transPackage,true);
        url += "getRecentOneTranHistory";
        try{
            execute(url,"POST",jsonObj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onStatusNotify(HttpResponseParam.RETURN_STATUS status, String str_response) {

    }
}
