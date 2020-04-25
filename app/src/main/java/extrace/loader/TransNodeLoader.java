package extrace.loader;

import android.app.Activity;


import extrace.misc.model.TransNode;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam;
import extrace.net.IDataAdapter;
import extrace.ui.main.ExTraceApplication;

public class TransNodeLoader extends HttpAsyncTask {

    String url;
    IDataAdapter<TransNode> adapter;
    private Activity context;
    public TransNodeLoader(IDataAdapter<TransNode> adpt,Activity context) {
        super(context);
        this.adapter = adpt;
        this.context = context;
        url = ((ExTraceApplication)context.getApplication()).getMiscServiceUrl();
    }

    @Override
    public void onDataReceive(String class_name, String json_data) {

    }

    //lyy 加载一个网点信息
    public void Load(String NodeCode){
        url += "/getNode/"+NodeCode +"?_type=json";
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
