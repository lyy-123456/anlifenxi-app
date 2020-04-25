package extrace.ui.zhuanyun;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import extrace.loader.ExpressLoader;
import extrace.loader.TransPackageLoader;
import extrace.misc.model.TransNode;
import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;
import extrace.ui.misc.TransNodeListActivity;
import zxing.util.CaptureActivity;

public class ZhuanyunCreateActivity extends AppCompatActivity implements IDataAdapter<TransPackage>{

    private static final int REQUEST_CAPTURE = 100;
    private static final int REQUEST_GET_NODE = 101;
    private Button zhuanyun_start_btn;
    private ImageButton zhuanyun_nextNode_btn;
    private EditText zhuanyun_nextNode_edt;
    private Button zhuanyun_addpkg_btn;
    private ListView zhuanyun_pkg_list;
    private PackageListAdapter packageListAdapter;
    private List<TransPackage> itemList;
    private TransNode nextTransNode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zhuanyun_create);
        itemList = new ArrayList<TransPackage>();
        zhuanyun_addpkg_btn = (Button)findViewById(R.id.zhuanyun_addpkg_btn);
        zhuanyun_start_btn=(Button)findViewById(R.id.zhuanyun_start_btn);
        zhuanyun_pkg_list =(ListView)findViewById(R.id.zhuanyun_pkg_list);
        zhuanyun_nextNode_btn = (ImageButton) findViewById(R.id.zhuanyun_nextNode_btn);
        zhuanyun_nextNode_edt = (EditText)findViewById(R.id.zhuanyun_nextNode_edt);
        //添加包裹
        zhuanyun_addpkg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartCapture();
            }
        });

        //网点信息获取：
        zhuanyun_nextNode_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetNextNode();
            }
        });
        //适配器初始化
        packageListAdapter = new PackageListAdapter(itemList,this);
        zhuanyun_pkg_list.setAdapter(packageListAdapter);
    }
    //得到网点信息
    private void GetNextNode() {
        Intent intent  = new Intent();
        intent.setClass(this, TransNodeListActivity.class);
        startActivityForResult(intent,REQUEST_GET_NODE);
    }

    private void StartCapture(){
        Log.d("PackageEditActivity执行了这个：","StartCapture");
        Intent intent = new Intent();
        intent.putExtra("Action","Capture");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d("PackageEditActivity执行了这个：","onActivityResult"+requestCode+"|"+resultCode);
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
                switch (requestCode){
                    case REQUEST_CAPTURE:
                        if (data.hasExtra("BarCode")) {//如果扫描结果得到的单号不为空
                            String id = data.getStringExtra("BarCode");
                            try {
                                TransPackageLoader transPackageLoader  =new TransPackageLoader( this,this);
                                if(!isRepeat(id)){
                                    transPackageLoader.Load(id); //得到这个包裹id
                                }
                                else{
                                    Toast.makeText(this,"该包裹已添加，请勿重复添加",Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case REQUEST_GET_NODE:
                        Log.d("PackageEditActivity执行了这个：onActivityResult返回了：","sd");
                        Bundle bundle = data.getExtras();
                        TransNode transNode = (TransNode) bundle.getSerializable("TransNode");
                        if(transNode != null){
                            nextTransNode = transNode;
                            //Toast.makeText(this,transNode.toString(),Toast.LENGTH_SHORT).show();
                            Log.d("PackageEditActivity执行了这个：onActivityResult返回了：",transNode.toString());
                            RefreshUI();
                        }
                        break;
                }
        }
    }

    private boolean isRepeat(String id) {
        for(TransPackage transPackage : itemList){
            if(transPackage.getID().equals(id)){
                return true;
            }
        }
        return false;
    }

    private void RefreshUI() {
        if (nextTransNode != null){
            zhuanyun_nextNode_edt .setText(nextTransNode.getNodeName());
        }

    }


    @Override
    public TransPackage getData() {

        return null;
    }

    @Override
    public void setData(TransPackage data) {
        itemList.add(data);
        packageListAdapter.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {

    }
}
