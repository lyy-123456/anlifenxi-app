package extrace.ui.zhuanyun;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;

public class PackageListAdapter extends ArrayAdapter<TransPackage> implements IDataAdapter<List<TransPackage> > {

    private List<TransPackage> itemList;  //将要展示的列表对象
    private TransPackage item;
    private Context context;
    public PackageListAdapter(List<TransPackage> packageList, Context ctx) {
        super(ctx,R.layout.package_list_item);
        itemList = packageList;
        context = ctx;
    }

    @Override
    public int getCount() {
        if(itemList != null){
            return itemList.size();
        }
        return 0;
    }

    public TransPackage getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        if(itemList != null){
            return itemList.get(position).hashCode();
        }
        return 0;
    }

    @Override
    public List<TransPackage> getData() {
        return itemList;
    }

    @Override
    public void setData(List<TransPackage> data) {
        this.itemList = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        PackageListAdapter.hold hd = null;
        if(v == null){
            hd = new PackageListAdapter.hold();
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v  = layoutInflater.inflate(R.layout.package_list_item,null);
            hd.ID = (TextView)v.findViewById(R.id.pkg_id);
            hd.endNode =(TextView)v.findViewById(R.id.end_node);
            hd.status=(TextView)v.findViewById(R.id.pkg_status);
            v.setTag(hd);
        }else{
            hd =(PackageListAdapter.hold)v.getTag();
        }

        TransPackage transPackage = getItem(position);
        hd.ID.setText(transPackage.getID());
        hd.endNode.setText(transPackage.getTargetNode());
        int status = transPackage.getStatus();
        String s = null;
        switch (status){
            case TransPackage.PKG_NEW:
                s = "新建";
                break;
            case TransPackage.PKG_PACKED:
                s="已打包";
                break;
            case TransPackage.PKG_TRSNSIT:
                s = "运输中";
                break;
            case TransPackage.PKG_UNPACKED:
                s="已拆包";
                 break;
             default:
                s = "错误";
        }
        hd.status.setText(s);
        return  v;
    }
    private class hold{
        TextView ID;    //包裹·ID
        TextView endNode;  //终点
        TextView status;  //包裹状态
    }
}
