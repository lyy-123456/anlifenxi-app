package extrace.ui.packages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import extrace.loader.ExpressLoader;
import extrace.misc.model.ExpressSheet;
import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;
import extrace.ui.domain.ExpressEditActivity;
import extrace.ui.misc.CustomerEditActivity;

public class ExpressInPacListFragment extends ListFragment {

    private final int EXPRESS_EDIT  = 110;

    private ExpressInPacListAdapter eAdapter;
    private ExpressListLoader elistLoader;
    private ExpressLoader eLoader;

    private List<String> listExpress = new ArrayList<String>();
    private ExpressSheet selectItem;
    private int selectPosition;

    private TransPackage transPackage;
    private Intent eIntent;
    private  String e_type; //标记采用什么方法
    private Context eContext;

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

    public void setData(List<ExpressSheet> data){
        Log.d("ExpressInPacListFragment执行了这个：","setData");
        eAdapter.setData(data);
        for(ExpressSheet es : eAdapter.getData()){
            listExpress.add(es.toString());
        }
    }
    public void notifyDataSetChanged(){
        eAdapter.notifyDataSetChanged();
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Log.d("ExpressInPacListFragment执行了这个：","onActivityCreated");
        super.onActivityCreated(savedInstanceState);


        setEmptyText("请往该包裹里添加快件");


        eAdapter = new ExpressInPacListAdapter(new ArrayList<ExpressSheet>(),this.getActivity());
        elistLoader = new ExpressListLoader(eAdapter,this.getActivity());
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
                        elistLoader = new ExpressListLoader(eAdapter,this.getActivity());
                        elistLoader.MoveExpressIntoPackage(es.getID(),transPackage.getID()); //加入
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
                case "RefreshList":
                    ReloadList();
                    eAdapter.getData();
                    eAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }
    }
    public void ReloadList() {
        eAdapter.getData().clear();
        listExpress.clear();
        elistLoader = new ExpressListLoader(eAdapter,this.getActivity());
        elistLoader.getExpressListInPackage(transPackage.getID());
    }

    private void ReservePackage() {
        Log.d("ExpressInPacListFragment执行了这个：","ReservePackage");
        if(eAdapter.getData() == null){
            Log.d("调试：","eadpter数据为空");
            return;
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
        Log.d("ExpressInPacListFragment执行了这个：","onListItemClick");
        selectItem = eAdapter.getItem(position);
        selectPosition = position;
        Log.d("ExpressInPacListFragment执行了这个：",selectItem.toString());
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

        //onListItemClick(null,null,info.position,0);

        selectItem = eAdapter.getItem(info.position);
        selectPosition = info.position;
        menu.setHeaderTitle("发件人: "+selectItem.getSender().toString());
        menu.add(info.position, 1, 0, "修改");
        menu.add(info.position, 2, 1, "删除");

    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        //AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getTitle().equals("修改")) {
            Log.d("ExpressInPacListFragment执行了这个：","点击了修改");
            EditItem();	//编辑快件
        } else if (item.getTitle().equals("删除")) {
            DeleteItem();	//删除快件
        }
        return super.onContextItemSelected(item);
    }

    private void EditItem() {
        Log.d("ExpressInPacListFragment执行了这个：","EditItem");
        Intent intent = new Intent();
        intent.putExtra("Action","Edit");
        intent.putExtra("ExpressSheet",selectItem);
        intent.setClass(eContext, ExpressEditActivity.class);
        Activity activity = (Activity)eContext;
        activity.startActivityForResult(intent,EXPRESS_EDIT );	//编辑之后
    }

    private void DeleteItem() {
        listExpress.remove(selectItem.toString()); //先将他删除
        elistLoader = new ExpressListLoader(eAdapter,this.getActivity());
        elistLoader.MoveExpressFromPackage(selectItem.getID(),transPackage.getID());
        eAdapter.getData().remove(selectItem);
        eAdapter.notifyDataSetChanged();
    }

    /**
     * fragmment和activity产生关联时候执行的函数
     * @param //activity
     */
    //@Overrides
    public void onAttach(Context context) {
        Log.d("ExpressInPacListFragment执行了这个：","onAttach");
        super.onAttach(context);
        Activity activity = (Activity)context;
        eContext = context;
        eIntent = activity.getIntent();
    }

}
