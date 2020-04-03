package extrace.ui.packages;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import java.util.ArrayList;

import extrace.loader.ExpressListLoader;
import extrace.misc.model.ExpressSheet;

public class ExpressInPacListFragment extends ListFragment {
    String data[]={};
    private ExpressInPacListAdapter eAdapter;
    private ExpressListLoader mLoader;
    public static ExpressInPacListFragment instance(String num[])
    {
        ExpressInPacListFragment list =new ExpressInPacListFragment();//创建对象
        Bundle bundle=new Bundle();//创建Bundle对象，类似map
        bundle.putStringArray("data", num);//传值
        list.setArguments(bundle);
        return list;
    }

    public  ExpressInPacListFragment(){

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setEmptyText("请往该包裹里添加快件");

        eAdapter = new ExpressInPacListAdapter(new ArrayList<ExpressSheet>(),this.getActivity());
        setListAdapter(eAdapter);

        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        registerForContextMenu(getListView());


    }

    /**
     * fragmment和activity产生关联时候执行的函数
     * @param activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Bundle bundle=getArguments();//获取fragment对象中bundle参数
        data= bundle.getStringArray("data");//初始化自己的数据源
        ArrayAdapter<String> adapter= new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,android.R.id.text1,data);
        //创建适配器
        setListAdapter(adapter);//为我们的宿主activity设置适配器
    }

}
