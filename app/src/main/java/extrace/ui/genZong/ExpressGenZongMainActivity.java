package extrace.ui.genZong;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.yinglan.scrolllayout.ScrollLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import extrace.loader.TransHistoryDetailListLoader;
import extrace.misc.model.TransHistoryDetail;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;

public class ExpressGenZongMainActivity extends AppCompatActivity implements IDataAdapter<List<TransHistoryDetail>> {

    private ScrollLayout mScrollLayout;
    private TextView text_foot;
    private ListViewAdapter listViewAdapter;
    private List<TransHistoryDetail> transHistoryDetails;
    private String expressID = "9787118022070";

    private ScrollLayout.OnScrollChangedListener mOnScrollChangedListener = new ScrollLayout.OnScrollChangedListener() {
        @Override
        public void onScrollProgressChanged(float currentProgress) {
            if (currentProgress >= 0) {
                float precent = 255 * currentProgress;
                if (precent > 255) {
                    precent = 255;
                } else if (precent < 0) {
                    precent = 0;
                }
                mScrollLayout.getBackground().setAlpha(255 - (int) precent);
            }
            if (text_foot.getVisibility() == View.VISIBLE)
                text_foot.setVisibility(View.GONE);
        }

        @Override
        public void onScrollFinished(ScrollLayout.Status currentStatus) {
            if (currentStatus.equals(ScrollLayout.Status.EXIT)) {
                text_foot.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onChildScroll(int top) {
        }
    };

    private void getTransHistory(){
        TransHistoryDetailListLoader transHistoryDetailListLoader = new TransHistoryDetailListLoader(this,this);
        transHistoryDetailListLoader.getTransHistoryDetailList(expressID);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_gen_zong_main);
        initView();
    }

    private void initView() {
        FrameLayout relativeLayout = (FrameLayout) findViewById(R.id.root);
        mScrollLayout = (ScrollLayout) findViewById(R.id.scroll_down_layout);
        text_foot = (TextView) findViewById(R.id.text_foot);
        ListView listView = (ListView) findViewById(R.id.list_view);

        transHistoryDetails = new ArrayList<TransHistoryDetail>();
        listViewAdapter = new  ListViewAdapter(transHistoryDetails,this);
        listView.setAdapter(listViewAdapter);
        Button button = (Button) findViewById(R.id.btn_go_second);

        /**设置 setting*/
        mScrollLayout.setMinOffset(0);
        mScrollLayout.setMaxOffset((int) (ScreenUtil.getScreenHeight(this) * 0.5));
        mScrollLayout.setExitOffset(ScreenUtil.dip2px(this, 50));
        mScrollLayout.setIsSupportExit(true);
        mScrollLayout.setAllowHorizontalScroll(true);
        mScrollLayout.setOnScrollChangedListener(mOnScrollChangedListener);
        mScrollLayout.setToExit();

        mScrollLayout.getBackground().setAlpha(0);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScrollLayout.scrollToExit();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this, SecondActivity.class));
            }
        });
        text_foot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScrollLayout.setToOpen();
            }
        });
        getTransHistory();
    }


    @Override
    public List<TransHistoryDetail> getData() {
        return listViewAdapter.getData();
    }

    @Override
    public void setData(List<TransHistoryDetail> data) {
        Collections.sort(data);
        listViewAdapter.setData(data);
    }

    @Override
    public void notifyDataSetChanged() {
        listViewAdapter.notifyDataSetChanged();
    }
}
