package extrace.ui.paiSong;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import extrace.loader.ExpressListLoader;
import extrace.loader.ExpressLoader;
import extrace.loader.TransPackageListLoader;
import extrace.loader.TransPackageLoader;
import extrace.misc.model.ExpressSheet;
import extrace.misc.model.ListTransPackage;
import extrace.misc.model.TransNode;
import extrace.misc.model.TransPackage;
import extrace.net.IDataAdapter;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.MainActivity;
import extrace.ui.main.R;
import extrace.ui.packages.ExpressInPacListAdapter;
import extrace.ui.qianShou.QianshouBacklocation;
import zxing.util.CaptureActivity;

public class ExpressPaiSongActivity extends AppCompatActivity implements IDataAdapter<ExpressSheet> {

    private static final int REQUEST_CAPTURE = 100;
    private ListView daiPaiSonglistView;
    private Button addExpBtn;
    private Button startPaiSong;
    private List<ExpressSheet> itemList;
    private ExpressInPacListAdapter expressInPacListAdapter;

    private TransPackage transPackage;
    private TransNode transNode;
    private boolean isPaiSong;
    private ExTraceApplication app;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_express_pai_song);
        initView();
        initData();
    }

    private void initView() {
        daiPaiSonglistView = (ListView)findViewById(R.id.daiPaiSong_list);
        addExpBtn = (Button)findViewById(R.id.paisong_addexp_btn);
        startPaiSong = (Button)findViewById(R.id.paisong_start_btn);
    }

    private void initData() {
        itemList = new ArrayList<ExpressSheet>();
        expressInPacListAdapter = new ExpressInPacListAdapter(itemList,this);
        daiPaiSonglistView.setAdapter(expressInPacListAdapter);
        addExpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartCapture();
            }
        });
        startPaiSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StartPaiSong();
            }
        });

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        transPackage = (TransPackage) bundle.getSerializable("transPackage");
        transNode = (TransNode)bundle.getSerializable("sTransNode");
        isPaiSong = false;

        app = (ExTraceApplication)getApplication();

    }

    private void StartCapture(){
        if(isPaiSong){
            Toast.makeText(this,"该包裹中的快件已经在派送中了",Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d("ExpressPaiSongActivity执行了这个：","StartCapture");
        Intent intent = new Intent();
        intent.putExtra("Action",  "Capture");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CAPTURE);
    }

    //开始派送
    private void StartPaiSong() {
        if(isPaiSong){
            Toast.makeText(this,"该包裹中的快件已经在派送中了",Toast.LENGTH_SHORT).show();
            return;
        }
        //1把所有的快件状态改变，为正在派送
        InTransPackageList inTransPackageList= new InTransPackageList();
        ListTransPackage listTransPackage = new ListTransPackage();
        listTransPackage.setTransPackageList(new ArrayList<TransPackage>());
        listTransPackage.getTransPackageList().add(transPackage);
        TransPackageListLoader transPackageListLoader= new TransPackageListLoader(inTransPackageList,this);
        transPackageListLoader.changeExpressStatusInTransPackageList(listTransPackage,ExpressSheet.STATUS.STATUS_DAIPAISONG,ExpressSheet.STATUS.STATUS_PAISONG);

        //修改包裹状态
        TransPackageLoader transPackageLoader = new TransPackageLoader(new InTransPackage(),this);
        transPackageLoader.changeTransPackageStatus(transPackage,TransPackage.PKG_PACKED);
        Toast.makeText(this,"开始派送，已开启后台定位！",Toast.LENGTH_SHORT).show();

        //2开启后台定位
        Intent startIntent = new Intent(ExpressPaiSongActivity.this, BackLocationService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("TransPackage",transPackage);
        startIntent.putExtras(bundle);
        startService(startIntent);


        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification.Builder builder1 = new Notification.Builder(this);
//        builder1.setSmallIcon(R.drawable.ic_launcher); //设置图标
//        builder1.setTicker("显示第二个通知");
//        builder1.setContentTitle("百度地图后台定位中"); //设置标题
//        builder1.setContentText("点击关闭后台定位"); //消息内容
//        builder1.setWhen(System.currentTimeMillis()); //发送时间
//        builder1.setDefaults(Notification.DEFAULT_ALL); //设置默认的提示音，振动方式，灯光
//        builder1.setAutoCancel(true);//打开程序后图标消失
        Intent intent =new Intent (this,NotificationClickReceiver.class);
        //需要携带什么参数就在的intent包裹即可，NotificationClickReceiver可以接收到发送过来的intent
        PendingIntent pendingIntent =PendingIntent.getBroadcast(this, 0, intent, 0);
//        builder1.setContentIntent(pendingIntent);
//        Notification notification1 = builder1.build();
//        notificationManager.notify(124, notification1); // 通过通知管理器发送通知

        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("0", "ChannelTest", NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(this)
                    .setChannelId("0")
                    .setContentTitle("你已经开启后台定位服务")
                    .setContentText("点击关闭后台定位服务！")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();
        } else {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("活动")
                    .setContentText("您有一项新活动")
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setOngoing(true)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setChannelId("0");//无效
            notification = notificationBuilder.build();
        }
        notificationManager.notify(1, notification);//把通知显示出来
        //startForeground(1,notification);//前台通知(会一直显示在通知栏)
//        Intent intent1  = new Intent();
//        intent1.setClass(this,MainActivity.class);
//        startActivity(intent1);
        isPaiSong = true;
    }

    class InTransPackage implements  IDataAdapter<TransPackage>{

        @Override
        public TransPackage getData() {
            return transPackage;
        }

        @Override
        public void setData(TransPackage data) {
            transPackage = data;
        }

        @Override
        public void notifyDataSetChanged() {

        }
    }
    class InTransPackageList implements  IDataAdapter<List<TransPackage>>{

        @Override
        public List<TransPackage> getData() {
            return null;
        }

        @Override
        public void setData(List<TransPackage> data) {

        }

        @Override
        public void notifyDataSetChanged() {

        }
    }
    //添加快件
    private void addExpress(ExpressSheet data) {
        //将它移进包裹里面
        InExpressList inExpressList = new InExpressList();
        ExpressListLoader expressListLoader = new ExpressListLoader(inExpressList,this);
        expressListLoader.MoveExpressIntoPackage(data.getID(),transPackage.getID());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
                switch (requestCode){
                    case REQUEST_CAPTURE:
                        if (data.hasExtra("BarCode")) {//如果扫描结果得到的单号不为空
                            String id = data.getStringExtra("BarCode");
                            try {
                                ExpressLoader expressLoader =new ExpressLoader( this,this);
                                if(!isRepeat(id)){
                                    expressLoader.Load(id); //得到这个快件id
                                }
                                else{
                                    Toast.makeText(this,"该快件已添加，请勿重复添加",Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }
                break;
        }
    }

    private boolean isRepeat(String id) {
        for(ExpressSheet expressSheet:itemList){
            if(expressSheet.getID().equals(id)){
                return true;
            }
        }
        return false;
    }

    @Override
    public ExpressSheet getData() {
        return null;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode== KeyEvent.KEYCODE_BACK){
            if(!isPaiSong){
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("提示：");
                builder.setMessage("这些快件没派送，您确定退出？");

                //设置确定按钮
                builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                //设置取消按钮
                builder.setPositiveButton("容我再想想",null);
                //显示提示框
                builder.show();
            }

        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void setData(ExpressSheet data) {
        if (data.getStatus() != ExpressSheet.STATUS.STATUS_DAIPAISONG) {
            Toast.makeText(this, "该快件状态不是待派送，无法派送", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
             data.setDeliver(String.valueOf(app.getLoginUser().getUID()));
             itemList.add(data);
             expressInPacListAdapter.notifyDataSetChanged();
             //将快件的派送人字段填写进去
            ExpressLoader expressLoader = new ExpressLoader(new InExpress(),this);
            expressLoader.saveOneExpressSheet(data);
             //快件添加进包裹
             addExpress(data);

         }
    }

    class InExpress  implements  IDataAdapter<ExpressSheet>{

        @Override
        public ExpressSheet getData() {
            return null;
        }

        @Override
        public void setData(ExpressSheet data) {

        }

        @Override
        public void notifyDataSetChanged() {

        }
    }
    class InExpressList implements IDataAdapter<List<ExpressSheet>>{

        @Override
        public List<ExpressSheet> getData() {
            return null;
        }

        @Override
        public void setData(List<ExpressSheet> data) {

        }

        @Override
        public void notifyDataSetChanged() {

        }
    }
    @Override
    public void notifyDataSetChanged() {

    }
}
