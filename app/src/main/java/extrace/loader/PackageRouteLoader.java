package extrace.loader;

import android.app.Activity;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import java.util.List;

import extrace.misc.model.ExpressSheet;
import extrace.misc.model.ListTransPackage;
import extrace.misc.model.PackageRoute;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class PackageRouteLoader extends HttpAsyncTask {
    String url;
    IDataAdapter<PackageRoute> adapter;
    private Activity context;
    public PackageRouteLoader(IDataAdapter<PackageRoute> adapter,Activity context) {
        super(context);
        this.adapter = adapter;
        this.context = context;
        url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
    }

    @Override
    public void onDataReceive(String class_name, String json_data) {
        if(class_name.equals("ListPackageRoute")){
            Toast.makeText(context,json_data,Toast.LENGTH_SHORT).show();
        }else  if(class_name.equals("PackageRoute")){
            PackageRoute packageRoute = JsonUtils.fromJson(json_data,new TypeToken<PackageRoute>(){});
            adapter.setData(packageRoute);
            adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(context,json_data,Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusNotify(HttpResponseParam.RETURN_STATUS status, String str_response) {

    }

    public void Save(PackageRoute packageRoute) {
        String jsonObj = JsonUtils.toJson(packageRoute,true);
        url += "saveOnePackageRoute";
        try{
            execute(url,"POST",jsonObj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //lyy 新增
    public void SaveListPackageRoute(ListTransPackage listTransPackage,float x,float y) {
        String jsonObj = JsonUtils.toJson(listTransPackage,true);
        url += "saveListPackageRoute/"+x+"/"+y+"?_type=json";
        try{
            execute(url,"POST",jsonObj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
