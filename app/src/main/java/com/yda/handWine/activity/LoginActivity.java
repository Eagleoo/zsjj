package com.yda.handWine.activity;

import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationManagerCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.yda.handWine.Bean.LoginInfoBean;
import com.yda.handWine.Bean.UserInfoBean;
import com.yda.handWine.DataUtil.SaveGetSharedPreferencesInfo;
import com.yda.handWine.HttpUtil.HttpManger;
import com.yda.handWine.MyTextWatcher;
import com.yda.handWine.R;
import com.yda.handWine.util.ACache;
import com.yda.handWine.util.ActivityCollector;
import com.yda.handWine.util.Constant;
import com.yda.handWine.util.Util;
import com.yda.handWine.util.Web;
import com.yda.handWine.widget.PromptDialog;
import com.yda.handWine.widget.UpDataVersionDialog;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.tencent.qzone.QZone;

public class LoginActivity extends BaseActivity implements PlatformActionListener{

    @BindView(R.id.username)
    EditText eUsername;
    @BindView(R.id.password)
    EditText ePassword;
    @BindView(R.id.login)
    TextView login;
    @BindView(R.id.phoneline)
    View phoneline;
    @BindView(R.id.passwordline)
    View passwordline;
    @BindView(R.id.escc_login)
    ImageView escc_login;

    @BindView(R.id.back)
    View back;

    @BindView(R.id.toForgetPassword)
    View toForgetPassword;
    private ACache aCache;
    String version;
    String content;
    String url;
    String createTime;
    String forgetGesture;
    String token;

    @Override
    public boolean isWidth() {
        return false;
    }

    MyTextWatcher myTextWatcher = new MyTextWatcher() {
        @Override
        public void afterTextChanged(Editable editable) {
            super.afterTextChanged(editable);
            login.setEnabled(false);
            Pattern num= Pattern.compile("[0-9]");
            Pattern english= Pattern.compile("[a-zA-Z]");
            Pattern chinese= Pattern.compile("[\u4e00-\u9fa5]");
            StringBuffer stringBuffer=new StringBuffer();
            stringBuffer.append(editable);
            for(int i=0;i<stringBuffer.length();i++){
                char answer = stringBuffer.charAt(i);
                if(!chinese.matcher(String.valueOf(answer)).matches()&&!english.matcher(String.valueOf(answer)).matches()
                        &&!num.matcher(String.valueOf(answer)).matches()&&!String.valueOf(answer).equals("_")&&!String.valueOf(answer).equals("@")
                        &&!String.valueOf(answer).equals(".")){
                   Util.show(context,"输入有误，只能为汉字、数字、字母和指定符号！");
                }
            }
            if (!TextUtils.isEmpty(eUsername.getText().toString()) && !TextUtils.isEmpty(ePassword.getText().toString())) {
                login.setEnabled(true);
            }
        }
    };

    private boolean isNotificationEnabled(Context context) {
        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void requestNotification(BaseActivity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
            // 进入设置系统应用权限界面
            Intent intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", activity.getPackageName());
            intent.putExtra("app_uid", activity.getApplicationInfo().uid);
            activity.startActivity(intent);
            return;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// 运行系统在5.x环境使用
            // 进入设置系统应用权限界面
            Intent intent = new Intent("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", activity.getPackageName());
            activity.startActivity(intent);
            return;
        }
        return;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sp= getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        ButterKnife.bind(this);
        aCache = ACache.get(this);
        forgetGesture=getIntent().getStringExtra("forgetGesture");

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Log.e("通知栏", "areNotificationsEnabled" + isNotificationEnabled(context));
        }

        if (!areNotificationsEnabled) {
            requestNotification(this, 200);
        }

        LoginInfoBean loginInfoBean = SaveGetSharedPreferencesInfo.getLoginInfo(context);
        if (loginInfoBean != null) {
            eUsername.setText(loginInfoBean.getUserid());
            ePassword.setText(loginInfoBean.getPassword());
        }

        login.setEnabled(false);
        eUsername.addTextChangedListener(myTextWatcher);
        ePassword.addTextChangedListener(myTextWatcher);

        if (!sp.getString("username","").equals("")){
            eUsername.setText(sp.getString("username",""));
        }

        Bundle bundle = this.getIntent().getExtras();
        if (bundle != null){ //防止直接启动时空指针闪退
            eUsername.setText(bundle.getString("username"));
            ePassword.setText(bundle.getString("password"));
            token=bundle.getString("token");
            login.setEnabled(true);
            login(false,false);
        }
    }


    @OnClick({R.id.login, R.id.toForgetPassword,R.id.back, R.id.toRegistered, R.id.escc_login})
    public void click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.login:
                login(true,true);
                break;
            case R.id.back:
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.toForgetPassword:
                intent = new Intent(context, MainActivity.class);
                intent.putExtra("url", "/repassword.aspx");
                intent.putExtra("forget", "forget");
                startActivity(intent);
                break;
            case R.id.toRegistered:
                intent = new Intent(context, MainActivity.class);
                intent.putExtra("url", "/register.aspx");
                startActivity(intent);
//                new PromptDialog(this)
//                        .setDialogType(PromptDialog.DIALOG_TYPE_WARNING)
//                        .setAnimationEnable(true)
//                        .setTitleText("温馨提示：")
//                        .setContentText("请先注册成为易云链会员，再使用易云链账号登录！")
//                        .setPositiveListener("去注册", new PromptDialog.OnPositiveListener() {
//                            @Override
//                            public void onClick(PromptDialog dialog) {
//                                dialog.dismiss();
//                                goEscc(2);
//                            }
//                        })
//                        .setNegativeListener("确定", new PromptDialog.OnNegativeListener() {
//                            @Override
//                            public void onClick(PromptDialog dialog) {
//                                dialog.dismiss();
//                            }
//                        }).show();
                break;
            case R.id.escc_login:
                checkEscc();
                break;
        }

    }

    public void login(boolean isYD,boolean isShow) {
        final String lName = eUsername.getText().toString();
        final String lpwd = ePassword.getText().toString();
        if (Util.isNull(lName)) {
            Util.show(context, "请输入用户名");
            return;
        } else if (Util.isNull(lpwd)) {
            Util.show(context, "请输入密码");
            return;
        } else if ("".equals(lpwd.trim())) {
            Util.show(context, "空格不能作为登录密码");
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("txtUserName", lName);
        params.put("txtPassword", lpwd);
        params.put("site_id", "1");
        if (!isShow){
            params.put("token", token);
        }

        if (isYD){
            params.put("type", "1");
        }

        String mAction = isYD ? "user_login" : "EsccLogin";
        //user_login
        HttpManger.postRequest(context, isShow,"/tools/submit_api.ashx?action=" + mAction, params, "登录中", new HttpManger.DoRequetCallBack() {

            @Override
            public void onSuccess(String o) {
                Log.e("请求结果", "str" + o);

                Gson gson = new Gson();
                UserInfoBean userInfoBean = gson.fromJson(o, UserInfoBean.class);
                UserInfoBean.setUser(userInfoBean);
                if (userInfoBean != null) {
                    if (userInfoBean.getStatus() == 1) {
                        Intent intent = new Intent(context, MainActivity.class);
                        intent.putExtra("login", "yes");
                        startActivity(intent);
                        if (forgetGesture!=null){
                            startActivity(new Intent(context,CreateGestureActivity.class));
                        }
                        String gesturePassword = aCache.getAsString(Constant.GESTURE_PASSWORD);
                        if(gesturePassword == null || "".equals(gesturePassword)) {
                            Intent intent1 = new Intent(context, CreateGestureActivity.class);
                            intent1.putExtra("ignore","ignore");
                            startActivity(intent1);
                        }

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("user_id", 1);
                        editor.putString("username",lName);
                        editor.putString("password",lpwd);
                        editor.putString("nick_name",userInfoBean.getModel().getNick_name());
                        editor.putString("token",userInfoBean.getToken());
                        editor.putString("reg_time",userInfoBean.getModel().getReg_time());
                        editor.putString("alias",userInfoBean.getModel().getAlias());
                        editor.putString("isdownlineshop",userInfoBean.getModel().getIsdownlineshop());
                        editor.putString("path","");
                        editor.putString("diamond",userInfoBean.getModel().getDiamond());
                        editor.putString("exp",userInfoBean.getModel().getExp());
                        editor.putString("group_id","Lv"+userInfoBean.getModel().getGroup_id());
                        if (userInfoBean.getModel().getAvatar().equals("")){
                            editor.putString("portrait","default");
                        }
                        else {
                            editor.putString("portrait",userInfoBean.getModel().getAvatar());
                        }
                        editor.commit();

                        JPushInterface.setAlias(LoginActivity.this, 200, userInfoBean.getModel().getAlias());
                    }
                    Util.show(context, userInfoBean.getMsg());

                }

            }

            @Override
            public void onError(String o) {

            }
        });

    }

    private void checkVersion() {
        OkGo.<String>get(Web.url + Web.version).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                version = response.body().split("<Version>")[1].split("</Version>")[0].trim();
                content = response.body().split("<Content>")[1].split("</Content>")[0].trim();
                url = response.body().split("<Url>")[1].split("</Url>")[0].trim();
                createTime = response.body().split("<CreateTime>")[1].split("</CreateTime>")[0].substring(0, 10).trim();
                String str1=Util.getString(Util.getString(version,"."),".");//服务器版本
                String str2=Util.getString(Util.getString(Util.getVerName(context),"."),".");//当前版本
                if (Integer.valueOf(str1)>Integer.valueOf(str2)) {
                    new UpDataVersionDialog(context, content, url).show();
                }

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(LoginActivity.this).setTitle("提示：").setMessage("您确定要退出吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCollector.finishAll();
                    System.exit(0);
                }
            }).setNegativeButton("取消", null).setIcon(R.drawable.logo1).show();
        }

        return super.onKeyDown(keyCode, event);
    }


    private void checkEscc() {
        final PackageManager pm = context.getPackageManager();
        Intent intent = new Intent();
        if (null != pm.getLaunchIntentForPackage("com.yda.yiyunchain")) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName("com.yda.yiyunchain", "com.yda.yiyunchain.activity.AutoLoginActivity");
            Bundle bundle = new Bundle();
            bundle.putString("autoLogin", "autoLogin");
            bundle.putString("handWine", "handWine");
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
    }

    private void goEscc(int type) {
        final PackageManager pm = context.getPackageManager();
        Intent intent = new Intent();
        if (null == pm.getLaunchIntentForPackage("com.yda.yiyunchain")) {//没有获取到intent
            Toast.makeText(context, "请先安装易云链", Toast.LENGTH_LONG).show();
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://down.yda360.com/Mall.apk")));
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle bundle = new Bundle();
            if (type==1){
                intent.setClassName("com.yda.yiyunchain", "com.yda.yiyunchain.activity.AutoLoginActivity");
                bundle.putString("autoLogin", "autoLogin");
                bundle.putString("handWine", "handWine");
                intent.putExtras(bundle);
            }
            else if (type==2){
                intent.setClassName("com.yda.yiyunchain", "com.yda.yiyunchain.activity.MainActivity");
                bundle.putString("url", "/register.aspx");
            }

            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private void authorize(Platform plat) {

        //ApplyCpd = CustomProgressDialog.showProgressDialog(this, "申请授权中...");
        plat.setPlatformActionListener(this);
        plat.removeAccount(true);
        if (plat instanceof QZone) {
            plat.SSOSetting(false);
        } else
            plat.SSOSetting(false);
        plat.showUser(null);
    }

    @Override
    public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
        String userId = platform.getDb().getUserId();//获取用户账号
        String userName = platform.getDb().getUserName();//获取用户名字
        String userIcon = platform.getDb().getUserIcon();//获取用户头像
        String token = platform.getDb().getToken();//获取用户头像
        String tokenSecret = platform.getDb().getTokenSecret();//获取用户头像
        String userGender = platform.getDb().getUserGender(); //获取用户性别，m = 男, f = 女，如果微信没有设置性别,默认返回null

        Log.e("微信信息：","账号："+userId+"名字："+userName+"头像："+userIcon+"token："+token+"secret："+tokenSecret+"性别："+userGender);

    }

    @Override
    public void onError(Platform platform, int i, Throwable throwable) {

    }

    @Override
    public void onCancel(Platform platform, int i) {

    }
}
