package com.ruyiruyi.ruyiruyi.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ruyiruyi.ruyiruyi.MainActivity;
import com.ruyiruyi.ruyiruyi.R;
import com.ruyiruyi.ruyiruyi.db.DbConfig;
import com.ruyiruyi.ruyiruyi.db.model.User;
import com.ruyiruyi.ruyiruyi.utils.RequestUtils;
import com.ruyiruyi.ruyiruyi.utils.UIOpenHelper;
import com.ruyiruyi.rylibrary.android.rx.rxbinding.RxViewAction;
import com.ruyiruyi.rylibrary.base.BaseActivity;
import com.ruyiruyi.rylibrary.cell.ActionBar;
import com.ruyiruyi.rylibrary.utils.TripleDESUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;
import org.xutils.DbManager;
import org.xutils.common.Callback;
import org.xutils.ex.DbException;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import rx.functions.Action1;

public class RegisterActivity extends BaseActivity implements DatePicker.OnDateChangedListener{

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private ActionBar actionBar;
    private int year;
    private int month;
    private int day;
    private TextView chooseData;
    private StringBuffer date;
    private int hour;
    private TextView chooseAge;
    private String[] sexArry = new String[]{ "女", "男"};

    private static int isBoy = 0;
    private TextView chooseSex;
    private static boolean isShowPassOne = false;
    private static boolean isShowPassTwo = false;
    private EditText passwordEdit1;
    private EditText passwordEdit2;
    private ImageView showPassOne;
    private ImageView showPassTwo;
    private String phone;
    private TextView phoneText;
    private TextView registerButton;
    private EditText userNameEdit;
    private EditText emailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register,R.id.my_action);
        actionBar = (ActionBar) findViewById(R.id.my_action);
        actionBar.setTitle("完善个人资料");
        actionBar.setShowBackView(false);
        date = new StringBuffer();

        Intent intent = getIntent();
        phone = intent.getStringExtra("PHONE");


        initView();
        initDateTime();

    }

    private void initView() {
        chooseData = (TextView) findViewById(R.id.choose_date);
        chooseAge = (TextView) findViewById(R.id.choose_age);
        chooseSex = (TextView) findViewById(R.id.choose_sex);
        passwordEdit1 = (EditText) findViewById(R.id.password_edit1);
        passwordEdit2 = (EditText) findViewById(R.id.password_edit2);
        showPassOne = (ImageView) findViewById(R.id.show_password_one);
        showPassTwo = (ImageView) findViewById(R.id.show_password_two);
        phoneText = (TextView) findViewById(R.id.phone_text);
        registerButton = (TextView) findViewById(R.id.register_button);
        userNameEdit = (EditText) findViewById(R.id.username_edit);
        emailEdit = (EditText) findViewById(R.id.email_edit);

        phoneText.setText(phone);


        showPasswordOne();
        showPasswordTwo();
        RxViewAction.clickNoDouble(showPassOne)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (isShowPassOne){
                            isShowPassOne = false;
                        }else {
                            isShowPassOne = true;
                        }
                        showPasswordOne();
                    }
                });
        RxViewAction.clickNoDouble(showPassTwo)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        if (isShowPassTwo){
                            isShowPassTwo = false;
                        }else {
                            isShowPassTwo = true;
                        }
                        showPasswordTwo();
                    }
                });


        String dataString = chooseData.getText().toString();
        date = new StringBuffer(dataString);

        RxViewAction.clickNoDouble(chooseData)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showDataDialog();
                    }
                });

        RxViewAction.clickNoDouble(chooseSex)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        showSexChooseDialog();
                    }
                });
        
        RxViewAction.clickNoDouble(registerButton)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        saveUserInfo();
                    }
                });
    }

    //提交用户信息
    private void saveUserInfo() {
        final String passwordStr1 = passwordEdit1.getText().toString();
        String passwordStr2 = passwordEdit2.getText().toString();
        byte[] oneMd5 = new byte[0];
        byte[] twoMd5 = new byte[0];
        try {
            oneMd5 = TripleDESUtil.MD5(passwordStr1);
            twoMd5 = TripleDESUtil.MD5(passwordStr2);
        } catch (NoSuchAlgorithmException e) {

        } catch (UnsupportedEncodingException e) {
        }
        final String password1 = TripleDESUtil.bytes2HexString(oneMd5);
        final String password2 = TripleDESUtil.bytes2HexString(twoMd5);

        final String userName = userNameEdit.getText().toString();
        final String age = chooseAge.getText().toString();
        final String email = emailEdit.getText().toString();
        final String birthday = chooseData.getText().toString();
        String sexStr = chooseSex.getText().toString();
        final int sex = (sexStr.equals("男")? 1:0);
        if (passwordStr1.length() < 6){
            Toast.makeText(RegisterActivity.this, "密码不能少于6位", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!password1.equals(password2)){
            Toast.makeText(RegisterActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userName.isEmpty()){
            Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (age.isEmpty()){
            Toast.makeText(RegisterActivity.this, "年龄不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("phone",phone);
            jsonObject.put("age",age);
            jsonObject.put("birthday",birthday);
            jsonObject.put("email",email);
            jsonObject.put("gender",sex);
            jsonObject.put("nick",userName);
            jsonObject.put("password",password1);
        } catch (JSONException e) {
        }
        RequestParams params = new RequestParams(RequestUtils.REQUEST_URL_TEST + "registerUser");
        params.addBodyParameter("reqJson",jsonObject.toString());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e(TAG, "onSuccess: " + result);
                try {
                    JSONObject jsonObject1 = new JSONObject(result);
                    String status = jsonObject1.getString("status");
                    String msg = jsonObject1.getString("msg");
                    Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
                    if (status.equals("1")){//注册成功
                        JSONObject data = jsonObject1.getJSONObject("data");
                        Log.e(TAG, "onSuccess: ---" + data );
                        int id = data.getInt("id");
                        String token = data.getString("token");
                        String headimgurl = data.getString("headimgurl");
                        String status1 = data.getString("status");
                        int firstAddCar = data.getInt("firstAddCar");
                        User user = new User();
                        user.setId(id);
                        user.setNick(userName);
                        user.setPassword(password1);
                        user.setPhone(phone);
                        user.setAge(age);
                        user.setBirthday(birthday);
                        user.setEmail(email);
                        user.setGender(sex);
                        user.setHeadimgurl(headimgurl);
                        user.setToken(token);
                        user.setStatus(status1);
                        user.setIsLogin("1");
                        user.setFirstAddCar(firstAddCar);
                        savaUserToDb(user);//保存到数据库
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        //UIOpenHelper.openHomeActivity(getApplicationContext());
                    }
                } catch (JSONException e) {

                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });


    }

    private void savaUserToDb(User user) {
        Log.e(TAG, "savaUserToDb: 1111111");
        DbConfig dbConfig = new DbConfig();
        DbManager.DaoConfig daoConfig = dbConfig.getDaoConfig();
        DbManager db = x.getDb(daoConfig);
        List<User> data = new ArrayList<>();
        data.add(user);
        try {
            db.saveOrUpdate(data);
        } catch (DbException e) {

        }
    }

    private void showPasswordTwo() {
        passwordEdit2.setInputType(isShowPassTwo ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD: InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        showPassTwo.setImageResource(isShowPassTwo ? R.drawable.ic_noshow : R.drawable.ic_show);
    }

    private void showPasswordOne() {
        passwordEdit1.setInputType(isShowPassOne ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD: InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        showPassOne.setImageResource(isShowPassOne ? R.drawable.ic_noshow : R.drawable.ic_show);
    }

    /**
     * 日期选择控件
     */
    private void showDataDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setPositiveButton("设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (date.length() > 0) { //清除上次记录的日期
                    date.delete(0, date.length());
                }
                chooseData.setText(date.append(String.valueOf(year)).append("-").append(String.valueOf(month +1)).append("-").append(day));
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });


        final AlertDialog dialog = builder.create();
        View dialogView = View.inflate(this, R.layout.dialog_date, null);
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datePicker);

        dialog.setTitle("设置日期");
        dialog.setView(dialogView);
        dialog.show();
        //初始化日期监听事件
        datePicker.init(year, month, day, this);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }

    /**
     * 获取当前的日期和时间
     */
    private void initDateTime() {
        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

    }


    private void showSexChooseDialog() {
        String sexStr = chooseSex.getText().toString();
        int sex;
        if (sexStr.equals("男")){
            sex = 1;
        }else {
            sex = 0;
        }
        AlertDialog.Builder builder3 = new AlertDialog.Builder(this);// 自定义对话框

        builder3.setSingleChoiceItems(sexArry, sex, new DialogInterface.OnClickListener() {// 2默认的选中

            @Override
            public void onClick(DialogInterface dialog, int which) {// which是被选中的位置
                // showToast(which+"");
                chooseSex.setText(sexArry[which]);
                dialog.dismiss();// 随便点击一个item消失对话框，不用点击确认取消
            }
        });
        builder3.show();// 让弹出框显示
    }


}
