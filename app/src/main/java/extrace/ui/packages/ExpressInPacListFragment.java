package extrace.ui.packages;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import extrace.loader.ExpressListLoader;
import extrace.misc.model.ExpressSheet;
import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;

public class ExpressInPacListFragment extends ListFragment {

    private ExpressInPacListAdapter eAdapter;
    private ExpressListLoader eLoader;

    private List<String> listExpress = new ArrayList<String>();
    private ExpressSheet selectItem;
    private int selectPosition;

    private TransPackage transPackage;
    private Intent eIntent;
    private  String e_type; //标记采用什么方法

    public static ExpressInPacListFragment instance(Bundle bundle)
    {
        ExpressInPacListFragment list =new ExpressInPacListFragment();//创建对象
//        Bundle bundle=new Bundle();//创建Bundle对象，类似map
//        bundle.putStringArray("data", num);//传值
        list.setArguments(bundle);
        return list;
    }

    public  ExpressInPacListFragment(){
        Log.d("ExpressInPacListFragment执行了这个：","ExpressInPacListFragment");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("ExpressInPacListFragment执行了这个：","onActivityCreated");
        super.onActivityCreated(savedInstanceState);


        setEmptyText("请往该包裹里添加快件");


        eAdapter = new ExpressInPacListAdapter(new ArrayList<ExpressSheet>(),this.getActivity());
        eLoader = new ExpressListLoader(eAdapter,this.getActivity());
        setListAdapter(eAdapter);

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        registerForContextMenu(getListView());
        RefreshList();
    }

    public void RefreshList() {
        Log.d("ExpressInPacListFragment执行了这个：","RefreshList");
        Bundle bundle = getArguments();
        if(bundle != null){
            e_type = bundle.getString("Action");
            switch (e_type)
            {
                case "Init":
                    transPackage =(TransPackage) bundle.getSerializable("transPackage");
                    Log.d("ExpressInPacListFragment执行了这个：Init为",transPackage.toString());
                    break;
                case "AddExpressToPackage":
                    ExpressSheet es = (ExpressSheet) bundle.getSerializable("ExpressSheet");
                    Log.d("ExpressInPacListFragment执行了这个：ExpressSheet为",es.toString());

                    listExpress.add(es.toString());
                    //包裹内有了该快件吗？
                    if(isRepeat()){
                        listExpress.remove(es.toString()); //先将他删除
                        Toast.makeText(this.getContext(),"该快件已存在这个包裹内,请勿重复添加",Toast.LENGTH_SHORT).show();
                    }
                    //是不是去往同一个目的地的？
                    else if(isToEnd()){
                        eAdapter.getData().add(es);
                        eAdapter.notifyDataSetChanged();
                    }
                    //不是去往同一个目的地的
                    else{
                        Toast.makeText(this.getContext(),"该快件不是去往目的地转运中心的,请勿错误添加",Toast.LENGTH_SHORT).show();
                    }
                    break;
                    //保存包裹内容
                case "ReservePackage":
                    ReservePackage();
                    break;
                default:
                    break;
            }
        }
    }

    private void ReservePackage() {
        Log.d("ExpressInPacListFragment执行了这个：","ReservePackage");
        if(eAdapter.getData() == null){
            Log.d("调试：","eadpter数据为空");
            return;
        }
        Log.d("ExpressInPacListFragment执行了这个：ReservePackage:transPackage",transPackage.toString());
        int i = 1;
        for(ExpressSheet es: eAdapter.getData()){
            Log.d("调试：",es.toString());

            eLoader.MoveExpressIntoPackage(es.getID(),transPackage.getID());
            Log.d("调试："," "+i);
            i = i+1;
        }
    }

    private boolean isToEnd() {
        return true;
    }

    //判断包裹内部是否重复,有问题需要重写
    private boolean isRepeat() {
        Log.d("ExpressInPacListFragment执行了这个：","isRepeat");
        Set<String> set=new HashSet<String>(listExpress);
        boolean result = listExpress.size() == set.size() ? false :true;
        return listExpress.size() == set.size() ? false :true;
    }

    @Override
    public void onListItemClick(@NonNull ListView l, @NonNull View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    /**
     * fragmment和activity产生关联时候执行的函数
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        eIntent = activity.getIntent();
    }

}
