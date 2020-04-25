package extrace.ui.misc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import extrace.misc.model.TransNode;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;

public class TransNodeListAdapter extends ArrayAdapter<TransNode> implements IDataAdapter<List<TransNode> > {
    private List<TransNode> itemList;
    private Context context;
    public TransNodeListAdapter(List<TransNode> transNodeList,Context ctx) {
        super(ctx, R.layout.transnode_list_item,transNodeList);
        itemList = transNodeList;
        context = ctx;
    }

    @Override
    public int getCount() {
        if(itemList != null){
            return itemList.size();
        }
        return 0;
    }

    @Nullable
    @Override
    public TransNode getItem(int position) {
        if(itemList != null){
            return itemList.get(position);
        }
        return null;
    }

    public void setItemList(TransNode item,int position) {
        if(itemList != null){
            itemList.set(position,item);
        }
    }

    @Override
    public long getItemId(int position) {
        if(itemList != null){
            return itemList.get(position).hashCode();
        }
        return 0;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        Hold hd = null;
        if(v == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.transnode_list_item,null);
            hd = new Hold();
            hd.node_id = (TextView)v.findViewById(R.id.transNode_id);
            hd.node_name=(TextView)v.findViewById(R.id.transNode_name);
            hd.node_type = (TextView)v.findViewById(R.id.transNode_type);
            v.setTag(hd);
        }else{
            hd = (Hold)v.getTag();
        }
        TransNode node = getItem(position);
        hd.node_id.setText(node.getID());
        hd.node_type.setText(node.getNodeType());
        hd.node_name.setText(node.getNodeName());
        return v;


    }

    @Override
    public List<TransNode> getData() {
        return itemList;
    }

    @Override
    public void setData(List<TransNode> data) {
        itemList = data;
    }
    private class Hold {
        TextView node_id;
        TextView node_type;
        TextView node_name;
    }
}
