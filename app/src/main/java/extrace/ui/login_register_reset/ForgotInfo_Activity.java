package extrace.ui.login_register_reset;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import androidx.appcompat.app.AppCompatActivity;
import extrace.ui.main.R;


import static zxing.util.ValidateUtil.isPhoneNumberValid;

public class ForgotInfo_Activity extends AppCompatActivity implements View.OnClickListener {

    private Button validateNum_btn;
    private Button landing_btn;
    private EditText userName;
    private EditText validateNum;
    public EventHandler eh; //事件接收器
    private TimeCount mTimeCount;//计时器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotinfo);
        initEvent();
        init();
    }

    private void initEvent(){
        userName = (EditText) findViewById(R.id.edit_userName);
        validateNum = (EditText) findViewById(R.id.edit_validateNum);
        validateNum_btn = (Button) findViewById(R.id.validateNum_btn);
        landing_btn = (Button) findViewById(R.id.landing_btn);
        validateNum_btn.setOnClickListener(this);
        landing_btn.setOnClickListener(this);
        mTimeCount = new TimeCount(60000, 1000);
    }

    /**
     * 初始化事件接收器
     */
    private void init(){
        eh = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {

                if (result == SMSSDK.RESULT_COMPLETE) { //回调完成

                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) { //提交验证码成功

                        Intent intent  = new Intent(ForgotInfo_Activity.this,ResetPassword_Activity.class);
                        intent.putExtra("message", userName.getText().toString().trim());//设置参数
                        startActivity(intent);

                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){ //获取验证码成功

                    } else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){ //返回支持发送验证码的国家列表

                    }
                } else{
                    ((Throwable)data).printStackTrace();
                }
            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.validateNum_btn:
//                SMSSDK.getSupportedCountries();//获取短信目前支持的国家列表
                if(!userName.getText().toString().trim().equals("")){
                    if (isPhoneNumberValid(userName.getText().toString().trim())) {
                        SMSSDK.getVerificationCode("+86",userName.getText().toString());//获取验证码
                        mTimeCount.start();
                    }else{
                        Toast.makeText(ForgotInfo_Activity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ForgotInfo_Activity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.landing_btn:
                if (!userName.getText().toString().trim().equals("")) {
                    if (isPhoneNumberValid(userName.getText().toString().trim())) {
                        if (!validateNum.getText().toString().trim().equals("")) {
                            SMSSDK.submitVerificationCode("+86",userName.getText().toString().trim(),validateNum.getText().toString().trim());//提交验证
                        }else{
                            Toast.makeText(ForgotInfo_Activity.this, "请输入验证码", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(ForgotInfo_Activity.this, "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(ForgotInfo_Activity.this, "请输入手机号码", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 检测该手机号是否已被注册
     * @param tel
     * @return
     */
    public boolean CheckIsDataAlreadyInDBorNot(String tel){

        return false;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eh);
    }

    /**
     * 计时器
     */
    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {
            validateNum_btn.setClickable(false);
            validateNum_btn.setText(l/1000 + "秒后重新获取");
        }

        @Override
        public void onFinish() {
            validateNum_btn.setClickable(true);
            validateNum_btn.setText("获取验证码");
        }
    }

}

