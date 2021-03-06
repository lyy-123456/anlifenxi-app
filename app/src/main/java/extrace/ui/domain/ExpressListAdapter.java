package extrace.ui.domain;

import java.text.SimpleDateFormat;
import java.util.List;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import extrace.misc.model.ExpressSheet;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;

public class ExpressListAdapter extends ArrayAdapter<ExpressSheet> implements IDataAdapter<List<ExpressSheet>>{
	
	private List<ExpressSheet> itemList;
	private Context context;
	private String ex_type;
	
	public ExpressListAdapter(List<ExpressSheet> itemList, Context ctx, String ex_type) {
		super(ctx, R.layout.express_list_item, itemList);
		this.itemList = itemList;
		this.context = ctx;	
		this.ex_type = ex_type;
	}
	
	public int getCount() {
		if (itemList != null)
			return itemList.size();
		return 0;
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

	public void delete(ExpressSheet expressSheet){
		int i = 0;
		for(ExpressSheet es:itemList){
			if(es.getID().equals(expressSheet.getID())){
				itemList.remove(i);
				break;
			}
			i++;
		}
		notifyDataSetChanged();
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		hold hd = null;
		if(v==null){
			hd = new hold();
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.express_list_item, null);
			hd.name = (TextView)v.findViewById(R.id.name);
			hd.telCode = (TextView)v.findViewById(R.id.tel);
			hd.address = (TextView)v.findViewById(R.id.addr);
			hd.time = (TextView)v.findViewById(R.id.time);
			hd.status = (TextView)v.findViewById(R.id.st);
			
			v.setTag(hd);
		}else{
			hd = (hold)v.getTag();
		}

		ExpressSheet es = getItem(position);
		switch(ex_type){
		case "ExDLV":	//待派送
			if(es.getRecever() != null){
				hd.name.setText(es.getRecever().getName());			//接收者姓名
				hd.telCode.setText(es.getRecever().getTelCode());	//接收者电话
				hd.address.setText(es.getRecever().getAddress());	//接收者
			}
			if(es.getAccepteTime() != null){
				//SimpleDateFormat myFmt=new SimpleDateFormat("MM月dd日 HH:mm");
				hd.time.setText(DateFormat.format("MM月dd日 HH:mm",es.getAccepteTime()));
			}
			break;
		case "ExRCV":	//揽收
			if(es.getSender() != null){
				hd.name.setText(es.getSender().getName());			//发送者姓名
				hd.telCode.setText(es.getSender().getTelCode());
				hd.address.setText(es.getSender().getAddress());
			}
			if(es.getAccepteTime() != null){
				//SimpleDateFormat myFmt=new SimpleDateFormat("MM月dd日 hh:mm");
				hd.time.setText(DateFormat.format("MM月dd日 hh:mm",es.getAccepteTime()));
			}
			break;
		case "MyExpress":	//派送中
			if(es.getRecever() != null){
				hd.name.setText(es.getRecever().getName());			//接收者姓名
				hd.telCode.setText(es.getRecever().getTelCode());	//接收者电话
				hd.address.setText(es.getRecever().getAddress());	//接收者
			}
			if(es.getAccepteTime() != null){
				//SimpleDateFormat myFmt=new SimpleDateFormat("MM月dd日 hh:mm");
				hd.time.setText(DateFormat.format("MM月dd日 hh:mm",es.getAccepteTime()));
			}
			break;
		}

		String stText = "";

		switch(es.getStatus()){
		case ExpressSheet.STATUS.STATUS_CREATED:
			stText = "待揽收";
			break;

		case ExpressSheet.STATUS.STATUS_TRANSPORT:
			stText = "运送途中";
			break;

		case ExpressSheet.STATUS.STATUS_DELIVERIED:
			stText = "已交付";
			break;

		case ExpressSheet.STATUS.STATUS_DAIPAISONG:
			stText = "待派送";
			break;

		case ExpressSheet.STATUS.STATUS_PAISONG:
			stText = "派送中";
		}
		hd.status.setText(stText);
		return v;		
	}

	@Override
	public List<ExpressSheet> getData() {
		return itemList;
	}

	@Override
	public void setData(List<ExpressSheet> data) {
		this.itemList = data;
	}	
	
	private class hold{
		TextView name;
		TextView telCode;
		TextView address;
		TextView time;
		TextView status;
	}
}