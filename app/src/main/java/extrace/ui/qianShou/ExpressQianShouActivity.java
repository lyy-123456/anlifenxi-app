package extrace.ui.qianShou;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import extrace.loader.ExpressLoader;
import extrace.misc.model.ExpressSheet;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;
import zxing.util.CaptureActivity;

public class ExpressQianShouActivity extends AppCompatActivity implements IDataAdapter<ExpressSheet> {
    private static final int REQUEST_CAPTURE = 100;
    private ExpressSheet expressSheet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qianshou_activity);

        StartCapture();
    }


    private void StartCapture(){
        Intent intent = new Intent();
        intent.putExtra("Action","Capture");
        intent.setClass(this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CAPTURE);
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
                            ExpressLoader mLoader = new ExpressLoader(this, this);  //加载一个快件
                            mLoader.Load(id);
                        }
                        break;
                }
        }
    }

    @Override
    public ExpressSheet getData() {
        return expressSheet;
    }

    @Override
    public void setData(ExpressSheet data) {

        if(data == null) {
            Toast.makeText(this,"快件不存在！",Toast.LENGTH_SHORT).show();
            finish();
        }else{
            expressSheet = data;
            QianShouExpress();
        }
    }

    private void QianShouExpress() {
        ExpressLoader expressLoader = new ExpressLoader(this,this);
        //expressLoader.Receive();
    }

    @Override
    public void notifyDataSetChanged() {

    }
}
