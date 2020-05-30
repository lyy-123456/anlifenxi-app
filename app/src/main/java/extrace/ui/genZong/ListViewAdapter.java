package extrace.ui.genZong;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import extrace.misc.model.ExpressSheet;
import extrace.misc.model.TransHistory;
import extrace.misc.model.TransHistoryDetail;
import extrace.misc.model.TransNode;
import extrace.misc.model.TransPackage;
import extrace.misc.model.UserInfo;
import extrace.net.IDataAdapter;
import extrace.ui.accPkg.PackageAccActivity;
import extrace.ui.main.R;

import static extrace.ui.main.R.layout.item_listview;

class ListViewAdapter extends ArrayAdapter<TransHistoryDetail> implements IDataAdapter<List<TransHistoryDetail> > {
    private Context mContext;

    private List<TransHistoryDetail> itemList;  //将要展示的列表对象
    private TransHistoryDetail item;
    private int transHistoryLength;
    private int totalLength;

    public ListViewAdapter(List<TransHistoryDetail> itemList,Context context) {
        super(context, item_listview);
        this.mContext = context;
        this.itemList = itemList;

    }

    @Override
    public int getCount() {
        if(itemList != null){
            return itemList.size();
        }
        return 0;
    }

    public TransHistoryDetail getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        if(itemList != null){
            return itemList.get(position).hashCode();
        }
        return 0;
    }

    public int getHistoryLength(){
        int ans = 0;
        for(TransHistoryDetail transHistoryDetail:itemList){
            if(transHistoryDetail.getTransHistory() != null){
                ans++;
            }
        }
        return  ans;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ListViewAdapter.hold hd= null;
        if(v == null){
            hd = new ListViewAdapter.hold();
            LayoutInflater layoutInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = layoutInflater.inflate(R.layout.item_listview,null);
            hd.express_time = (TextView)v.findViewById(R.id.express_time);
            hd.express_history_detail=(TextView)v.findViewById(R.id.express_history_detail);
            v.setTag(hd);
        }else{
            hd=(ListViewAdapter.hold)v.getTag();
        }
        TransHistoryDetail transHistoryDetail = getItem(position);
//        TransHistoryDetail transHistoryDetail = getItem(position);
//        System.out.println("总长为"+totalLength);
        StringBuffer sb = new StringBuffer();
        //TransHistory transHistory = transHistoryDetail.getTransHistory();
        UserInfo UIDFrom = transHistoryDetail.getUIDFrom();
        UserInfo UIDTo = transHistoryDetail.getUIDTo();
        TransNode fromNode= transHistoryDetail.getFromNode();
        TransNode toNode = transHistoryDetail.getToNode();
        ExpressSheet expressSheet = transHistoryDetail.getExpressSheet();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+08:00"));

        //查一下快件的状态是否为正在运输

        if(position == 0){
            hd.express_time.setText("");
            sb.append("快件待揽收，正在等待揽收");
        }else if( position == 1){
            hd.express_time.setText(sdf.format(expressSheet.getAccepteTime()));
            sb.append("快件已揽收");
        }else if( position >=2 && position <= transHistoryLength+1  ){
            System.out.println(position);
            System.out.println(transHistoryDetail.getTransHistory().toString());
            hd.express_time.setText(sdf.format(transHistoryDetail.getTransHistory().getActTime()));
            if(UIDTo.getURull() == UserInfo.STATUS.FUZEREN) //如果是负责人，说明是司机转运的记录
            {
                sb.append("快件已在【");
                sb.append(fromNode.getNodeName());
                sb.append("】装车，准备发往下一站 【");
                sb.append(toNode.getNodeName());
                sb.append("】 ,");
                sb.append("司机是：【");
                sb.append(UIDFrom.getName());
                sb.append("】");
            }else{
                sb.append("快件已到达 【"+toNode.getNodeName()+"】站点，扫描员是 【"+UIDTo.getName()+"】");
            }
        }else if(position == transHistoryLength+2){
            hd.express_time.setText("");
            sb.append("快件已到达终点站，等待派送");
        }
        else if(position == transHistoryLength+3){
            hd.express_time.setText("");
            sb.append("快件正在派送"+"派送员是： 【"+UIDFrom.getName()+"】"+"电话号码：【"+UIDFrom.getTelCode()+"】");
        }
        else if(position == transHistoryLength+4){
            hd.express_time.setText(sdf.format(expressSheet.getDeliveTime()));
            sb.append("快件已交付");
        }

        if(position == getCount()-1 && hd.express_time.getText().equals("") ){

            Date date = new Date(System.currentTimeMillis());
            hd.express_time.setText(sdf.format(date));
        }
        hd.express_history_detail.setText(sb.toString());
        return v;
    }

    @Override
    public List<TransHistoryDetail> getData() {
        return itemList;
    }

    @Override
    public void setData(List<TransHistoryDetail> data) {
        this.itemList = data;
        for(TransHistoryDetail transHistoryDetail: data){
            System.out.print(transHistoryDetail.getSN());
            if(transHistoryDetail.getTransHistory() != null){
                System.out.println(transHistoryDetail.getTransHistory().toString());
            }

        }
        transHistoryLength = getHistoryLength();
        totalLength = itemList.size();
        System.out.println("长度为"+transHistoryLength+"总长为"+itemList.size());
    }



    private class hold{
        TextView express_time;    //事件时间
        TextView express_history_detail;  //快件详细信息
    }
}
