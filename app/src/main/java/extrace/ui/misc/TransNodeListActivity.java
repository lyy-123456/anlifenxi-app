package extrace.ui.misc;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.Placeholder;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.ListFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.regex.Pattern;

import extrace.loader.TransNodeListLoader;
import extrace.misc.model.TransNode;
import extrace.ui.main.R;

public class TransNodeListActivity extends AppCompatActivity {
    PlaceholderFragmentInTranNode list_fg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_trans_node_list);

        // 给左上角图标的左边加上一个返回的图标 。对应ActionBar.DISPLAY_HOME_AS_UP
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            list_fg = new PlaceholderFragmentInTranNode();
            fm.beginTransaction().add(android.R.id.content, list_fg).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        list_fg.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.transnode_list,menu);
        final Activity activity = this;
        MenuItem item = menu.findItem(R.id.action_transnode_search); //搜索
        final SearchView searchView = (SearchView) item.getActionView();
        if (searchView != null) {
            Log.d("TransNodeListActivity执行了这个","onCreateOptionsMenu");
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    //提交按钮的点击事件
                    list_fg.RefreshList(query);
                    Toast.makeText(activity, query, Toast.LENGTH_SHORT).show();
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    //当输入框内容改变的时候回调
                    //Log.i(TAG,"内容: " + newText);
                    return true;
                }

            });

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {
                    return true;
                }
            });


            MenuItemCompat.setActionView(item, searchView);
        }
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_transnode_ok:
                Toast.makeText(this,"选中了确认",Toast.LENGTH_SHORT).show();
                SelectOk();	//返回给上层
                return true;
            case R.id.action_transnode_edit:
                Toast.makeText(this,"选中了编辑",Toast.LENGTH_SHORT).show();
                //EditItem();
                return true;
            case R.id.action_transnode_new:
                Toast.makeText(this,"选中了新建",Toast.LENGTH_SHORT).show();
                //NewItem();
                return true;
            case R.id.action_transnode_search:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //选中了确认
    private void SelectOk() {
        Log.d("TransNodeListActivity执行了这个","SelectOk"+list_fg.selectItem.toString());
        if(list_fg.selectItem != null){
            Bundle bundle = new Bundle();
            bundle.putSerializable("TransNode",list_fg.selectItem);
            list_fg.mIntent.putExtras(bundle);
            this.setResult(RESULT_OK, list_fg.mIntent);
            this.finish();  //界面直接关闭
        }
        else{
            Toast.makeText(this,"没有选中任何网点",Toast.LENGTH_SHORT).show();
        }
    }

    public static  class PlaceholderFragmentInTranNode extends ListFragment{
        private TransNodeListAdapter transNodeListAdapter;
        private TransNodeListLoader transNodeListLoader;

        private TransNode selectItem;
        private int selectPosition;

        Intent mIntent;
        public PlaceholderFragmentInTranNode(){

        }
        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            Log.d("PlaceholderFragmentInTranNode执行了这个","onActivityCreated");
            super.onActivityCreated(savedInstanceState);
            setEmptyText("查找网点信息！");

            //设置适配器
            transNodeListAdapter = new TransNodeListAdapter(new ArrayList<TransNode>(),this.getActivity());
            setListAdapter(transNodeListAdapter);

            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

//            //注册上下文菜单
//            registerForContextMenu(getListView());

        }

        @Override
        public void onAttach(@NonNull Activity activity) {
            super.onAttach(activity);
            mIntent = activity.getIntent();
        }


        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            System.out.println("onListItemClick方法执行");
            selectItem = transNodeListAdapter.getItem(position);
            selectPosition = position;
            //this.getActivity().setTitle(selectItem.getName());
        }

        //查询网点：
        private void RefreshList(String query) {
            Log.d("PlaceholderFragmentInTranNode执行了这个","RefreshList");
            this.getActivity().setTitle("");
            //根据区域码、编号、网点名得到：
            if(checkRegionNode(query)){
                try{
                    Log.d("PlaceholderFragmentInTranNode执行了这个","checkRegionNode");
                    transNodeListLoader  =new TransNodeListLoader(transNodeListAdapter,this.getActivity());
                    transNodeListLoader.getTransNodeByRegion(query);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(checkDigit(query)){
                try{
                    Log.d("PlaceholderFragmentInTranNode执行了这个","checkDigit");
                    transNodeListLoader  =new TransNodeListLoader(transNodeListAdapter,this.getActivity());
                    transNodeListLoader.getTransNodeById(query);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{  //根据站点名字查询
                try{
                    Log.d("PlaceholderFragmentInTranNode执行了这个","name");
                    transNodeListLoader  =new TransNodeListLoader(transNodeListAdapter,this.getActivity());
                    transNodeListLoader.getTransNodeByNodeName(query);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }

        private boolean checkDigit(String query) {
            String regex = "^[0-9]*$";
            Pattern pattern = Pattern.compile(regex);
            return pattern.matcher(query).matches();
        }

        private boolean checkRegionNode(String query) {
            String regex_xs = "^[1-9]\\d{5}$";
            Pattern pattern_mb = Pattern.compile(regex_xs);
            return pattern_mb.matcher(query).matches();
        }
    }
}
