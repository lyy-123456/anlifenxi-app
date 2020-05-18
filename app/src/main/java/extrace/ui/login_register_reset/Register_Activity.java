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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import extrace.loader.UserInfoLoader;
import extrace.misc.model.UserInfo;
import extrace.net.IDataAdapter;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.MainActivity;
import extrace.ui.main.R;
import zxing.util.ValidateUtil;
/**
 * Created by renkai on 17/7/7.
 */

public class Register_Activity extends Activity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener , IDataAdapter<UserInfo> {

    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton0,mRadioButton1,mRadioButton2,mRadioButton3;
    private Integer uRull;
    private UserInfo userInfo;
    private EditText edit_dptid,edit_username,edit_register, edit_setpassword, edit_resetpassword;
    private TextView tv_name,tv_pass;
    private Button btn_yes, btn_cancel;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mRadioGroup = (RadioGroup)findViewById(R.id.radioGroup);
        mRadioGroup.setOnCheckedChangeListener(this);
        init();

    }


    protected void init() {
        edit_dptid = (EditText) findViewById(R.id.edit_dptid);
        edit_dptid.addTextChangedListener(new TextWatcher() {
            private CharSequence word;
            private String DptId;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                word = charSequence;

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if(word.length()> 8){
                    Toast.makeText(Register_Activity.this, "所在网点编号格式错误", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                DptId = word.toString();
                if(word.length()==0) {
                    Toast.makeText(Register_Activity.this, "所在网点编号不能为空", Toast.LENGTH_SHORT).show();
                }

            }
        });

        edit_username = (EditText) findViewById(R.id.edit_username);
        edit_username.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        for (int i = start; i < end; i++) {
                            if (!Character.isLetterOrDigit(source.charAt(i)) &&
                                    !Character.toString(source.charAt(i)).equals("_")) {
                                Toast.makeText(Register_Activity.this, "只能使用'_'、字母、数字、汉字注册！", Toast.LENGTH_SHORT).show();
                                return "";
                            }
                        }
                        return null;
                    }
                }
        });

        edit_username.addTextChangedListener(new TextWatcher() {
            private CharSequence word;
            private String userName;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                word = charSequence;

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                userName = word.toString();
                if(word.length()==0) {
                    Toast.makeText(Register_Activity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
        edit_register = (EditText) findViewById(R.id.edit_register);
        edit_register.addTextChangedListener(new TextWatcher() {
            private CharSequence word;
            private String PhoneNumber;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                word = charSequence;

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                tv_name=findViewById(R.id.tv_name_wrong);
                tv_name.setText("请输入正确的手机格式");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                PhoneNumber = word.toString();
                if(word.length()==0) {
                    tv_name.setText("手机号不能为空");
                }
                if(ValidateUtil.isPhoneNumberValid(PhoneNumber)){
                    tv_name.setText("");
                }
            }
        });

        edit_setpassword = (EditText) findViewById(R.id.edit_setpassword);
        edit_setpassword.addTextChangedListener(new TextWatcher() {
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

        edit_resetpassword = (EditText) findViewById(R.id.edit_resetpassword);

        btn_yes = (Button) findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(this);
        btn_cancel = (Button) findViewById(R.id.btn_cancle);
        btn_cancel.setOnClickListener(this);


    }
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

        switch (checkedId){
            case  R.id.radio0:

                Toast.makeText(Register_Activity.this, "你选择了司机类型", Toast.LENGTH_SHORT).show();
                uRull=0;
                break;
            case  R.id.radio1:
                Toast.makeText(Register_Activity.this, "你选择了业务员类型", Toast.LENGTH_SHORT).show();
                uRull=1;
                break;
            case  R.id.radio2:
                Toast.makeText(Register_Activity.this, "你选择了快递员类型", Toast.LENGTH_SHORT).show();
                uRull=2;
                break;
            case  R.id.radio3:
                Toast.makeText(Register_Activity.this, "你选择了管理员类型", Toast.LENGTH_SHORT).show();
                uRull=3;
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_yes:
                    if (edit_setpassword.getText().toString().trim().
                            equals(edit_resetpassword.getText().toString()) &&
                            ValidateUtil.isPhoneNumberValid(edit_register.getText().toString().trim()) && edit_resetpassword.getText().toString().length()>=8 && edit_resetpassword.getText().toString().length()<=12 && ValidateUtil.isPassword(edit_resetpassword.getText().toString())) {
                        saveUsersInfo();

                    } else if(edit_setpassword.getText().toString().trim().
                            equals(edit_resetpassword.getText().toString())){
                        Toast.makeText(this, "用户名或密码格式错误，请重新输入！",
                                Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(this, "两次输入密码不同，请重新输入！",
                                Toast.LENGTH_SHORT).show();
                    }

                break;
            case R.id.btn_cancle:
                Intent login_intent = new Intent(Register_Activity.this, Login_Activity.class);
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
        registerUserInfo(edit_username.getText().toString(),edit_register.getText().toString(),
                edit_setpassword.getText().toString(),edit_dptid.getText().toString(),uRull);
    }

    /**
     * 利用数据库进行注册访问
     */
    private void registerUserInfo(String userName,String phoneNumber, String userpassword,String dptID,Integer uRull) {
        UserInfoLoader userInfoLoader = new UserInfoLoader(this,this);
        userInfoLoader.doRegister(userName,phoneNumber,userpassword,dptID,uRull);
    }



    @Override
    public UserInfo getData() {
        return userInfo;
    }

    @Override
    public void setData(UserInfo data) {
        userInfo = data;
        Toast.makeText(this, "注册成功！", Toast.LENGTH_SHORT).show();
        Intent register_intent = new Intent(Register_Activity.this,
                Login_Activity.class);
        startActivity(register_intent);
    }

    @Override
    public void notifyDataSetChanged() {

    }
}

