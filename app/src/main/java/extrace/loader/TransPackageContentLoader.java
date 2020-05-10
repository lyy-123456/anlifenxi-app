package extrace.loader;

import android.app.Activity;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import extrace.misc.model.TransHistory;
import extrace.misc.model.TransPackageContent;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class TransPackageContentLoader extends HttpAsyncTask {
    String url;
    IDataAdapter<TransPackageContent> adapter;
    private Activity context;
    public TransPackageContentLoader(IDataAdapter<TransPackageContent> adapter,Activity context) {
        super(context);
        this.adapter = adapter;
        this.context = context;
        url = ((ExTraceApplication)context.getApplication()).getDomainServiceUrl();
    }

    @Override
    public void onDataReceive(String class_name, String json_data) {
        if(class_name.equals("TransPackageContent")){
            TransPackageContent transPackageContent = JsonUtils.fromJson(json_data, new TypeToken<TransPackageContent>(){});
            adapter.setData(transPackageContent);
            adapter.notifyDataSetChanged();
        }else{
            Toast.makeText(context,"不存在这个快件",Toast.LENGTH_SHORT).show();
        }
    }

    public void getTransPackageContent(String pkgID,String expressID){
        url += "getTransPackageContent/"+pkgID+"/"+expressID+"?_type=json";
        try {
            execute(url,"GET");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //lyy新增
    public void changeExpressStatusInPackage(String pkgID,String expressID,int status){
        url += "changeExpressStatusInPackage/"+pkgID+"/"+expressID+"/"+status+"?_type=json";
        try {
            execute(url,"GET");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    public void onStatusNotify(HttpResponseParam.RETURN_STATUS status, String str_response) {

    }
}
