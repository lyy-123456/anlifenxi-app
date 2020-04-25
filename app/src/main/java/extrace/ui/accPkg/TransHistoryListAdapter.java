package extrace.ui.accPkg;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Set;

import extrace.misc.model.CustomerInfo;
import extrace.misc.model.TransHistory;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;

public class TransHistoryListAdapter implements IDataAdapter<List<TransHistory>> {
    private  List<TransHistory> transHistorySet;
    private  Context context;

    public TransHistoryListAdapter(List<TransHistory> transHistorySet, Context ctx) {
        //super(ctx, android.R.layout.simple_list_item_single_choice, itemList);
        this.transHistorySet = transHistorySet;
        this.context = ctx;
    }
    @Override
    public List<TransHistory> getData() {

        Log.d("TransHistoryListAdapter：", "getData");
        return transHistorySet;
    }

    @Override
    public void setData(List<TransHistory> data) {
        Log.d("TransHistoryListAdapter：", "setData");
        transHistorySet = data;
    }

    @Override
    public void notifyDataSetChanged() {

    }
}
