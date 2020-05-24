package extrace.ui.paiSong;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.MainActivity;

public class NotificationClickReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //todo 跳转之前要处理的逻辑
        Log.d("TAG", "userClick:我被点击啦！！！ ");
        Intent stopIntent = new Intent(context, BackLocationService.class);
        context.stopService(stopIntent);
        Toast.makeText(context,"后台服务已关闭！",Toast.LENGTH_SHORT).show();
    }
}
