package extrace.loader;

import android.app.Activity;

import java.util.List;

import extrace.misc.model.TransNode;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam;
import extrace.net.IDataAdapter;
import extrace.ui.main.ExTraceApplication;

public class TransNodeListLoader extends HttpAsyncTask {
    String url;// = "http://192.168.7.100:8080/TestCxfHibernate/REST/Misc/";
    IDataAdapter<List<TransNode>> adapter;
    private Activity context;
    public TransNodeListLoader(IDataAdapter<List<TransNode> > listIDataAdapter, Activity context) {
        super(context);
        this.adapter = listIDataAdapter;
        this.context = context;
        url = ((ExTraceApplication)context.getApplication()).getMiscServiceUrl();
    }

    @Override
    public void onDataReceive(String class_name, String json_data) {

    }

    //lyy 通过区域码获取TransNode
    public void getTransNodeByRegion(String region){
        url += "/getTransNodeByRegionCode/"+region+"?_type=json";
        try{
            execute(url,"GET");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //lyy 通过id号查询TranNode
    public void getTransNodeById(String id){
        url += "/getTransNodeById/"+id+"?_type=json";
        try{
            execute(url,"GET");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //lyy 通过网点名称查询TranNode
    public void getTransNodeByNodeName(String nodeName){
        url += "/getTransNodeByNodeName/"+nodeName+"?_type=json";
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
