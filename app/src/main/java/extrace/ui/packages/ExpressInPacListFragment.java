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

public class ExpressInPacListFragment extends ListFragment {

    private ExpressInPacListAdapter eAdapter;
    private ExpressListLoader eLoader;

    private List<String> listExpress = new ArrayList<String>();
    private ExpressSheet selectItem;
    private int selectPosition;

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
                case "AddExpressToPackage":
                    ExpressSheet es = (ExpressSheet) bundle.getSerializable("ExpressSheet");
                    Log.d("ExpressInPacListFragment执行了这个：ExpressSheet为",es.toString());

                    listExpress.add(es.toString());
                    if(isRepeat()){
                        Toast.makeText(this.getContext(),"该快件已存在这个包裹内,请勿重复添加",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        eAdapter.getData().add(es);
                        eAdapter.notifyDataSetChanged();
                    }
                    break;
                case "ReservePackage":

                    break;
                default:
                    break;
            }
        }
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
