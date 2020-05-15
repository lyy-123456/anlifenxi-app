package extrace.loader;

import android.app.Activity;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import extrace.misc.model.TransHistoryDetail;
import extrace.misc.model.TransPackage;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class TransHistoryDetailListLoader extends HttpAsyncTask {
    String url;
    IDataAdapter<List<TransHistoryDetail>> adapter;
    private Activity context;
    public TransHistoryDetailListLoader(IDataAdapter<List<TransHistoryDetail>> adapter, Activity context) {
        super(context);
        this.adapter = adapter;
        this.context =context;
        url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
    }

    @Override
    public void onDataReceive(String class_name, String json_data) {
        List<TransHistoryDetail> transHistoryDetails = JsonUtils.fromJson(json_data,new TypeToken<List<TransHistoryDetail>>(){});
        if(transHistoryDetails == null || transHistoryDetails.size() == 0){
            Toast.makeText(context,"包裹历史为空或不存在",Toast.LENGTH_SHORT).show();
            return;
        }
        adapter.setData(transHistoryDetails);
        adapter.notifyDataSetChanged();
    }

    public  void getTransHistoryDetailList(String expressID){
        url += "getTransHistoryDetailList/"+expressID+"?_type=json";
        try{
            execute(url,"GET");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onStatusNotify(HttpResponseParam.RETURN_STATUS status, String str_response) {

    }
}
