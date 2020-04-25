package extrace.ui.packages;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.List;

import extrace.misc.model.ExpressSheet;
import extrace.net.IDataAdapter;
import extrace.ui.domain.ExpressListAdapter;
import extrace.ui.main.R;

public class ExpressInPacListAdapter extends ArrayAdapter<ExpressSheet> implements IDataAdapter<List<ExpressSheet>> {

    private List<ExpressSheet> itemList;
    private Context context;

    public ExpressInPacListAdapter(List<ExpressSheet> itemList, Context ctx) {
        super(ctx, R.layout.express_list_item, itemList);
        this.itemList = itemList;
        this.context = ctx;
    }

    public int getCount() {
        if (itemList != null)
            return itemList.size();
        return 0;
    }

    public void removeExpressfromList(String id){

    }
    public ExpressSheet getItem(int position) {
        if (itemList != null)
            return itemList.get(position);
        return null;
    }

    public long getItemId(int position) {
        if (itemList != null)
            return itemList.get(position).hashCode();
        return 0;
    }

    @Override
    public List<ExpressSheet> getData() {
        return itemList;
    }

    @Override
    public void setData(List<ExpressSheet> data) {
        this.itemList = data;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        ExpressInPacListAdapter.hold hd = null;
        if(v==null){
            hd = new ExpressInPacListAdapter.hold();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.express_list_item, null);
            hd.name = (TextView)v.findViewById(R.id.name);
            hd.telCode = (TextView)v.findViewById(R.id.tel);
            hd.address = (TextView)v.findViewById(R.id.addr);
            hd.time = (TextView)v.findViewById(R.id.time);
            hd.status = (TextView)v.findViewById(R.id.st);

            v.setTag(hd);
        }else{
            hd = (ExpressInPacListAdapter.hold)v.getTag();
        }

        ExpressSheet es = getItem(position);

        if(es.getSender() != null){
            hd.name.setText(es.getSender().getName());			//发送者姓名
            hd.telCode.setText(es.getSender().getTelCode());
            hd.address.setText(es.getSender().getAddress());
        }
        if(es.getAccepteTime() != null){
            //SimpleDateFormat myFmt=new SimpleDateFormat("MM月dd日 hh:mm");
            hd.time.setText(DateFormat.format("MM月dd日 hh:mm",es.getAccepteTime()));
        }
        String stText = "还没写";


        hd.status.setText(stText);
        return v;
    }

    private class hold{
        TextView name;
        TextView telCode;
        TextView address;
        TextView time;
        TextView status;
    }
}
