package extrace.loader;

import android.app.Activity;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import extrace.misc.model.PackageRoute;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class PackageRouteListLoader extends HttpAsyncTask {
    String url;
    IDataAdapter<List<PackageRoute>>adapter;
    private Activity context;
    public PackageRouteListLoader(IDataAdapter<List<PackageRoute>>adapter, Activity context) {
        super(context);
        this.adapter = adapter;
        this.context = context;
        url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
    }
    @Override
    public void onDataReceive(String class_name, String json_data) {
        List<PackageRoute> packageRouteList = JsonUtils.fromJson(json_data,new TypeToken<List<PackageRoute>>(){});
        adapter.setData(packageRouteList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onStatusNotify(HttpResponseParam.RETURN_STATUS status, String str_response) {

    }
    //lyy 新增
    public void getPackageRouteListByExpressId(String expressID){
        url += "getPackageRouteListByExpressId/"+expressID+"?_type=json";
        try{
            execute(url,"GET");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
