package extrace.loader;

import android.app.Activity;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

import extrace.misc.model.ListTransHistory;
import extrace.misc.model.TransHistory;
import extrace.misc.model.TransPackage;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.accPkg.PackageAccActivity;
import extrace.ui.main.ExTraceApplication;

public class TransHistoryListLoader extends HttpAsyncTask {
    String url;
    IDataAdapter<List<TransHistory>> adapter;
    private Activity context;
    public TransHistoryListLoader(IDataAdapter<List<TransHistory>> adpt, Activity context) {
        super(context);
        this.adapter = adpt;
        this.context = context;
        url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
    }


    @Override
    public void onDataReceive(String class_name, String json_data) {
        if(class_name.equals("N_TransPackage")){
            Toast.makeText(context,"包裹信息不存在",Toast.LENGTH_SHORT).show();
        }
        else if(class_name.equals("successed")){
            Toast.makeText(context,"成功",Toast.LENGTH_SHORT).show();
        }
    }

    //lyy 新增一次性写一堆的transhistory
    public void saveTransHistoryList(ListTransHistory transHistoryList){
        String jsonObj = JsonUtils.toJson(transHistoryList,true);

        url += "saveTransHistoryList";
        Toast.makeText(context,url+jsonObj,Toast.LENGTH_SHORT).show();
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
