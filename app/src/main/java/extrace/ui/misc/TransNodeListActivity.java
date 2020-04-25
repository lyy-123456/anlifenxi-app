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
    PlaceholderFragment list_fg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_trans_node_list);

        // 给左上角图标的左边加上一个返回的图标 。对应ActionBar.DISPLAY_HOME_AS_UP
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();

        // Create the list fragment and add it as our sole content.
        if (fm.findFragmentById(android.R.id.content) == null) {
            list_fg = new TransNodeListActivity.PlaceholderFragment();
            fm.beginTransaction().add(android.R.id.content, list_fg).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        list_fg.onActivityResult(requestCode, resultCode, data);
    }

    public static  class PlaceholderFragment extends ListFragment{
        private TransNodeListAdapter transNodeListAdapter;
        private TransNodeListLoader transNodeListLoader;

        private TransNode selectItem;
        private int selectPosition;

        Intent mIntent;
        public PlaceholderFragment(){

        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setEmptyText("查找网点信息！");

            //设置适配器
            transNodeListAdapter = new TransNodeListAdapter(new ArrayList<TransNode>(),this.getActivity());
            setListAdapter(transNodeListAdapter);

            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);

            //注册上下文菜单
            registerForContextMenu(getListView());

        }

        @Override
        public void onAttach(@NonNull Context context) {
            super.onAttach(context);
            Activity activity = (Activity) context;
            mIntent = activity.getIntent();
        }
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            // Inflate the menu; this adds items to the action bar if it is present.
            inflater.inflate(R.menu.customer_list, menu);

            MenuItem item = menu.findItem(R.id.action_search);  //搜索
            final SearchView searchView = (SearchView) item.getActionView();
            if (searchView != null) {

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        //提交按钮的点击事件
                        RefreshList(query);
                        Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
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
        }
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            switch(id){
                case android.R.id.home:
                case R.id.action_ok:
                    Toast.makeText(this.getActivity(),"选中了确认",Toast.LENGTH_SHORT).show();
                    //SelectOk();	//返回给上层
                    return true;
                case R.id.action_edit:
                    Toast.makeText(this.getActivity(),"选中了编辑",Toast.LENGTH_SHORT).show();
                    //EditItem();
                    return true;
                case R.id.action_new:
                    Toast.makeText(this.getActivity(),"选中了新建",Toast.LENGTH_SHORT).show();
                    //NewItem();
                    return true;
                case R.id.action_search:
                    return true;
                default:
            }
            return super.onOptionsItemSelected(item);
        }
        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            selectItem = transNodeListAdapter.getItem(position);
            selectPosition = position;
            //this.getActivity().setTitle(selectItem.getName());
        }
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            super.onCreateContextMenu(menu, v, menuInfo);
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;

            //onListItemClick(null,null,info.position,0);

            selectItem = transNodeListAdapter.getItem(info.position);
            selectPosition = info.position;
//            this.getActivity().setTitle(selectItem.getName());
//            menu.setHeaderTitle("客户: "+selectItem.getName());
            menu.add(info.position, 1, 0, "选择");
            menu.add(info.position, 2, 1, "修改");
            menu.add(info.position, 3, 2, "删除");
        }
        @Override
        public boolean onContextItemSelected(MenuItem item) {
            //AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
            if (item.getTitle().equals("选择")) {
                //SelectOk();	//返回给上层
            } else if (item.getTitle().equals("修改")) {
                //EditItem();	//编辑客户
            } else if (item.getTitle().equals("删除")) {
                //DeleteItem();	//删除客户
            }
            return super.onContextItemSelected(item);
        }
        //查询网点：
        private void RefreshList(String query) {
            this.getActivity().setTitle("");
            //根据区域码、编号、网点名得到：
            if(checkRegionNode(query)){
                try{
                    transNodeListLoader  =new TransNodeListLoader(transNodeListAdapter,this.getActivity());
                    transNodeListLoader.getTransNodeByRegion(query);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(checkDigit(query)){
                try{
                    transNodeListLoader  =new TransNodeListLoader(transNodeListAdapter,this.getActivity());
                    transNodeListLoader.getTransNodeById(query);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{  //根据站点名字查询
                try{
                    transNodeListLoader  =new TransNodeListLoader(transNodeListAdapter,this.getActivity());
                    transNodeListLoader.getTransNodeByNodeName(query);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }

        private boolean checkDigit(String query) {
            String regex = "/^\\+?[0-9][0-9]*$/";
            Pattern pattern = Pattern.compile(regex);
            return pattern.matcher(query).matches();
        }

        private boolean checkRegionNode(String query) {
            String regex_xs = "/[1-8][1-7]\\d{4}/";  //县级以上行政区域
            String regex_xx = "/[1-8][1-7]\\d{7}/";  //县级以下行政区域
            Pattern pattern_mb = Pattern.compile(regex_xs);
            Pattern pattern_ph = Pattern.compile(regex_xx);
            return pattern_mb.matcher(query).matches() || pattern_ph.matcher(query).matches();
        }
    }
}
