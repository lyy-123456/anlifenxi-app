package extrace.loader;

import android.app.Activity;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import extrace.misc.model.ExpressSheet;
import extrace.misc.model.UserInfo;
import extrace.net.HttpAsyncTask;
import extrace.net.HttpResponseParam;
import extrace.net.IDataAdapter;
import extrace.net.JsonUtils;
import extrace.ui.main.ExTraceApplication;

//lyy 新增
public class UserInfoLoader extends HttpAsyncTask {

    String url;
    IDataAdapter<UserInfo> adapter;
    private Activity context;


    public UserInfoLoader(IDataAdapter<UserInfo> adapter,Activity context) {
        super(context);
        this.adapter = adapter;
        this.context = context;
        url = ((ExTraceApplication)context.getApplication()).getMiscServiceUrl();
    }

    @Override
    public void onDataReceive(String class_name, String json_data) {
        if(class_name.equals("UserInfo")){
            UserInfo userInfo = JsonUtils.fromJson(json_data,new TypeToken<UserInfo>(){});
            if(userInfo.getStatus()== 0){
                adapter.setData(userInfo);
                adapter.notifyDataSetChanged();
            }else {
                Toast.makeText(context, "没有登陆权限", Toast.LENGTH_SHORT).show();
            }
        }else if(class_name.equals("N_UserInfo")){
            Toast.makeText(context,"没有符合要求的用户",Toast.LENGTH_SHORT).show();
        }else if(class_name.equals("R_UserInfo")){
            UserInfo userInfo = JsonUtils.fromJson(json_data,new TypeToken<UserInfo>(){});
            adapter.setData(userInfo);
            adapter.notifyDataSetChanged();
        }else if(class_name.equals("NR_UserInfo")){
            Toast.makeText(context,"该用户已注册",Toast.LENGTH_SHORT).show();
        }else if(class_name.equals("P_UserInfo")){
            UserInfo userInfo = JsonUtils.fromJson(json_data,new TypeToken<UserInfo>(){});
            adapter.setData(userInfo);
            adapter.notifyDataSetChanged();
        }else if(class_name.equals("NP_UserInfo")){
            Toast.makeText(context,"该用户未注册",Toast.LENGTH_SHORT).show();
        }
    }

    //lyy 新增
    public void Load(String uid){
        url += "getUserInfoById/"+uid+"?_type=json";
        try{
            execute(url,"GET");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //lyy 新增通过所网点id查询负责人
    public void getUserManagerById(String DptID){
        url += "getManagerByNodeID/" +DptID+"?_type=json";
        try{
            execute(url,"GET");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //lyy 新增
    public void save(UserInfo userInfo){
        String json_data = JsonUtils.toJson(userInfo,true);
        url += "saveUserInfo";
        try{
            execute(url,"POST",json_data);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //whb新增登录
    public void doLogin(String telCode,String password){
        url += "doLogin/" +telCode+"/" +password+"?_type=json";
        try{
            execute(url,"GET");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //whb新增注册
    public void doRegister(String name,String telCode,String password,String dptId,Integer uRull){
        url += "doRegister/"+name+"/"+telCode+"/"+password+"/"+dptId+"/"+uRull+"?_type=json";
        try{
            execute(url,"GET");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void resetPWD(String telCode,String newpwd){
        url += "resetPWD/" +telCode+"/" +newpwd+"?_type=json";
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
