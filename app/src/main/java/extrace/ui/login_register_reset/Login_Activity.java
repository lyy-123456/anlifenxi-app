package extrace.ui.login_register_reset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import extrace.loader.UserInfoLoader;
import extrace.misc.model.UserInfo;
import extrace.net.IDataAdapter;
import extrace.ui.main.ExTraceApplication;
import extrace.ui.main.MainActivity;
import extrace.ui.main.R;
import extrace.ui.main.SettingsActivity;
import zxing.util.ValidateUtil;



public class Login_Activity extends AppCompatActivity implements View.OnClickListener , IDataAdapter<UserInfo> {


    private SharedPreferences sharedPreferences;
    private Editor editor;
    private EditText edit_account, edit_password;
    private TextView text_msg;
    private TextView tv_name,tv_pass;
    private Button btn_login, btn_register,btn_setting;
    private CheckBox check_remember,check_voluntarily;
    private ImageButton openpwd;
    private boolean flag = false;
    private UserInfo userInfo;
    private String account, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        findByID();
        readUsersInfo();
        init();

    }

    private void init() {
        edit_account.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edit_account.clearFocus();
                }
                return false;
            }
        });
        edit_account.addTextChangedListener(new TextWatcher() {
            private CharSequence word;
            private String PhoneNumber;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                word = charSequence;

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

                tv_name.setText("请输入正确的手机格式");
            }

            @Override
            public void afterTextChanged(Editable editable) {
                PhoneNumber = word.toString();
                if(word.length()==0) {
                    tv_name.setText("用户名不能为空");
                }
                if(ValidateUtil.isPhoneNumberValid(PhoneNumber)){
                    tv_name.setText("");
                }
            }
        });

        edit_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    edit_password.clearFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edit_password.getWindowToken(), 0);
                }
                return false;
            }
        });
        edit_password.addTextChangedListener(new TextWatcher() {
            private CharSequence word;
            private String PassWord;

            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                word = charSequence;

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {

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
        text_msg.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        btn_register.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
        openpwd.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_setting:
                Intent e = new Intent(Login_Activity.this, SettingsActivity.class);
                startActivity(e);
            case R.id.btn_login:
                if (edit_account.getText().toString().trim().equals("") | edit_password.getText().
                        toString().trim().equals("")) {
                    Toast.makeText(this, "请输入账号或者注册账号！", Toast.LENGTH_SHORT).show();
                } else {
                    if(check_remember.isChecked()){
                        //5.获取输入框的值
                        String name = edit_account.getText().toString();
                        String pass = edit_password.getText().toString();
                        //6.将值存入sharedPreferences
                        editor.putString("name", name);
                        editor.putString("pass", pass);
                        //存入一个勾选了的状态值
                        editor.putBoolean("r_ischeck", true);
                        //提交
                        editor.commit();
                    }else{
                        //清空
                        editor.clear();
                        editor.commit();
                    }

                    //自动登录
                    if(check_voluntarily.isChecked()){
                        editor.putBoolean("v_ischeck", true);
                        editor.commit();
                    }

                    readUserInfo();
                }
                break;
            case R.id.btn_register:
                Intent intent = new Intent(Login_Activity.this, Register_Activity.class);
                startActivity(intent);
                break;
            case R.id.btn_openpwd:
                if (flag == true) {//不可见
                    edit_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    flag = false;
                    openpwd.setBackgroundResource(R.drawable.invisible);
                } else {
                    edit_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    flag = true;
                    openpwd.setBackgroundResource(R.drawable.visible);
                }
                break;
            case R.id.text_msg:
                Intent i = new Intent(Login_Activity.this, ForgotInfo_Activity.class);
                startActivity(i);
                break;
        }
    }

    /**
     * 读取SharedPreferences存储的键值对
     * */
    public void readUsersInfo(){
        //2.得到sharedPreferences
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        //得到editor
        editor = sharedPreferences.edit();

        //7.取出记住密码的状态值进行判断
        boolean r_ischeck = sharedPreferences.getBoolean("r_ischeck", false);
        //8.判断状态值 如果为true就记住密码，如果为false就不记住
        if(r_ischeck){
            String name = sharedPreferences.getString("name", null);
            String pass= sharedPreferences.getString("pass", null);

            edit_account.setText(name);
            edit_password.setText(pass);
            check_remember.setChecked(true);
        }


        //取出自动登录的状态值
        boolean v_ischeck = sharedPreferences.getBoolean("v_ischeck", false);
        if(v_ischeck){
            login(edit_account.getText().toString(), edit_password.getText().toString());
            finish();
        }

        //勾选自动登录同时记住密码
        check_voluntarily.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    //设置记住密码为勾选
                    check_remember.setChecked(true);
                }else{
                    //清空
                    editor.clear();
                    editor.commit();
                }
            }
        });
    }
    /**
     * 读取UserData.db中的用户信息
     * */
    protected void readUserInfo() {
        login(edit_account.getText().toString(), edit_password.getText().toString());
    }

    /**
     * 验证登录信息
     * */
    public void login(String username, String password) {
        UserInfoLoader userInfoLoader = new UserInfoLoader(this,this);
        userInfoLoader.doLogin(username,password);
    }

    @Override
    public UserInfo getData() {
        return userInfo;
    }

    @Override
    public void setData(UserInfo data) {
        userInfo = data;
        Toast.makeText(this, "登陆成功！", Toast.LENGTH_SHORT).show();
        ExTraceApplication app = (ExTraceApplication)this.getApplication();
        app.setUserInfo(data);
        Intent intent = new Intent(Login_Activity.this, MainActivity.class);
        intent.putExtra("Username",edit_account.getText().toString());
        startActivity(intent);
    }

    @Override
    public void notifyDataSetChanged() {

    }
    public void findByID(){
        edit_account = (EditText) findViewById(R.id.edit_account);
        tv_name=findViewById(R.id.tv_name_wrong);
        edit_password = (EditText) findViewById(R.id.edit_password);
        tv_pass=findViewById(R.id.tv_pass_wrong);
        check_remember = (CheckBox) findViewById(R.id.check_remember);
        check_voluntarily = (CheckBox) findViewById(R.id.check_voluntarily);
        text_msg = (TextView) findViewById(R.id.text_msg);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        openpwd = (ImageButton) findViewById(R.id.btn_openpwd);
        btn_setting = (Button) findViewById(R.id.btn_setting);
    }
}

