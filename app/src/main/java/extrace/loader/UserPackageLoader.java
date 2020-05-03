package extrace.loader;

import android.app.Activity;

import extrace.misc.model.UserInfo;
import extrace.misc.model.UsersPackage;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

public class UserPackageLoader extends HttpAsyncTask {
    String url;
    IDataAdapter<UsersPackage> adapter;
    private Activity context;

    public UserPackageLoader(IDataAdapter<UsersPackage> adpt, Activity context) {
        super(context);
        this.adapter = adpt;
        this.context = context;
        url = ((ExTraceApplication)context.getApplication()).getMiscServiceUrl();
    }

    @Override
    public void onDataReceive(String class_name, String json_data) {

    }

    //lyy 新增
    public  void Save(UsersPackage usersPackage){
        url += "SaveUsersPackage";
        String jsonObj = JsonUtils.toJson(usersPackage,true);
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
