package extrace.ui.login_register_reset;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import extrace.loader.UserInfoLoader;
import extrace.misc.model.UserInfo;
import extrace.net.IDataAdapter;
import extrace.ui.main.R;
import zxing.util.ValidateUtil;

public class ResetPassword_Activity extends Activity implements View.OnClickListener, IDataAdapter<UserInfo> {
    private EditText edit_newpassword, edit_confirmpassword;
    private TextView tv_pass;
    private Button btn_yes, btn_cancel;
    private UserInfo userInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resetpassword);
        init();
    }


    protected void init() {


        edit_newpassword = (EditText) findViewById(R.id.edit_newpassword);
        edit_newpassword.addTextChangedListener(new TextWatcher() {
            private CharSequence word;
            private String PassWord;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                word = charSequence;

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                tv_pass=findViewById(R.id.tv_pass_wrong);
                PassWord = word.toString();
                if(word.length()<8 ){
                    if(ValidateUtil.isPassword(PassWord))
                        tv_pass.setText("");
                    else
                        tv_pass.setText("密码格式不正确，密码由8-12位的数字或字母组成");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //PassWord = edit_register.getText().toString();
                if(word.length()==0) {
                    tv_pass.setText("密码不能为空");
                }

                if(word.length()>=8 && word.length()<=12 && ValidateUtil.isPassword(PassWord)){
                    tv_pass.setText("");
                }
                if(word.length()>12){
                    tv_pass.setText("密码格式不正确，密码由8-12位的数字或字母组成");
                }
            }
        });

        edit_confirmpassword= (EditText) findViewById(R.id.edit_confirmpassword);

        btn_yes = (Button) findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(this);
        btn_cancel = (Button) findViewById(R.id.btn_cancle);
        btn_cancel.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                if (edit_newpassword.getText().toString().trim().
                        equals(edit_confirmpassword.getText().toString()) &&
                        edit_confirmpassword.getText().toString().length()>=8 && edit_confirmpassword.getText().toString().length()<=12 && ValidateUtil.isPassword(edit_confirmpassword.getText().toString())) {
                    saveUsersInfo();

                } else if(edit_newpassword.getText().toString().trim().
                        equals(edit_confirmpassword.getText().toString())){
                    Toast.makeText(this, "密码格式错误，请重新输入！",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, "两次输入密码不同，请重新输入！",
                            Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_cancle:
                Intent login_intent = new Intent(ResetPassword_Activity.this, Login_Activity.class);
                startActivity(login_intent);
                break;
            default:
                break;
        }
    }


    /**
     * 利用SharedPreferences进行默认登陆设置
     */
    private void saveUsersInfo() {
        Intent intent = getIntent();
        registerUserInfo(intent.getStringExtra("message"), edit_newpassword.getText().toString());
    }

    /**
     * 利用sql创建嵌入式数据库进行注册访问
     */
    private void registerUserInfo(String PhoneNumber, String userpassword) {
        UserInfoLoader userInfoLoader = new UserInfoLoader(this,this);
        userInfoLoader.resetPWD(PhoneNumber,userpassword);
    }


    @Override
    public UserInfo getData() {
        return userInfo;
    }

    @Override
    public void setData(UserInfo data) {
        userInfo = data;
        Toast.makeText(this, "修改成功！", Toast.LENGTH_SHORT).show();
        Intent register_intent = new Intent(ResetPassword_Activity.this,
                Login_Activity.class);
        startActivity(register_intent);
    }

    @Override
    public void notifyDataSetChanged() {

    }
}
