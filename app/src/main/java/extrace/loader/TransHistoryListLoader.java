package extrace.loader;

import android.app.Activity;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

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
    IDataAdapter<Set<TransHistory>> adapter;
    private Activity context;
    public TransHistoryListLoader(IDataAdapter<Set<TransHistory>> adpt, Activity context) {
        super(context);
        this.adapter = adpt;
        this.context = context;
        url+= ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
    }


    @Override
    public void onDataReceive(String class_name, String json_data) {
        if(class_name.equals("N_TransPackage")){
            Toast.makeText(context,"包裹信息不存在",Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(context,"成功",Toast.LENGTH_SHORT).show();
        }
    }

    //lyy 往历史里添加一个数据
    public  void AddOneTransHistory(TransHistory transHistory){

        String jsondata = JsonUtils.toJson(transHistory,true);
        Toast.makeText(context,"TranshistoryListLoader执行了AddOneTransHistory方法"+jsondata,Toast.LENGTH_LONG).show();
        url += "addOneTransHistory";
        try{
            execute(url,"POST",jsondata);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onStatusNotify(HttpResponseParam.RETURN_STATUS status, String str_response) {

    }


}
