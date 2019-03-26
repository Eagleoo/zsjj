package com.yda.handWine.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gitonway.lee.niftynotification.lib.Configuration;
import com.gitonway.lee.niftynotification.lib.Effects;
import com.gitonway.lee.niftynotification.lib.NiftyNotificationView;
import com.yanzhenjie.sofia.Sofia;
import com.yda.handWine.Bean.UserInfoBean;
import com.yda.handWine.ConfigurationInfor;
import com.yda.handWine.HttpUtil.HttpManger;
import com.yda.handWine.MyChromeClient;
import com.yda.handWine.R;
import com.yda.handWine.WebViewJsInterface;
import com.yda.handWine.receiver.MyReceiver;
import com.yda.handWine.util.ACache;
import com.yda.handWine.util.ActivityCollector;
import com.yda.handWine.util.CommonUtil;
import com.yda.handWine.util.Constant;
import com.yda.handWine.util.DownLoadPicUtil;
import com.yda.handWine.util.Util;
import com.yda.handWine.util.Web;
import com.yda.handWine.widget.PromptDialog;
import com.yda.handWine.widget.ShapeLoadingDialog;
import com.yda.handWine.widget.toast.BamToast;
import com.yda.handWine.wxapi.WXPayEntryActivity;
import com.yda.handWine.zxing.activity.CaptureActivity;

import org.apache.http.util.EncodingUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.yda.handWine.MyChromeClient.mCameraFilePath;

public class MainActivity extends BaseActivity implements WebViewJsInterface.StrCallBack,MyReceiver.ReceiverCallBack,WXPayEntryActivity.goBackCall {

    @BindView(R.id.center)
    TextView center;
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.sao_ewm)
    ImageView sao_ewm;
    @BindView(R.id.delete)
    ImageView delete;
    @BindView(R.id.refresh)
    ImageView refresh;
    @BindView(R.id.dian)
    ImageView dian;
    @BindView(R.id.share_iv)
    ImageView share_iv;
    @BindView(R.id.mMessage)
    ImageView mMessage;
    @BindView(R.id.mall_collect)
    RadioButton mall_collect;
    @BindView(R.id.tv_read)
    TextView tv_read;
    @BindView(R.id.webView)
    WebView webView;
    @BindView(R.id.status)
    LinearLayout status;
    @BindView(R.id.relative_title)
    RelativeLayout relative_title;
    @BindView(R.id.nowifi)
    View nowifi;
    private ShapeLoadingDialog shapeLoadingDialog;
    private int state = 0; // 0登录 1 会员中心 2 商品
    private String shareurl = "";
    String url = "";
    SharedPreferences sp;
    private String msg1="";
    private boolean flag=false;
    String title="";
    //打开扫描界面请求码
    private int REQUEST_CODE = 0x01;
    private HomeWatcherReceiver mHomeWatcherReceiver = null;
    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final int RESULT_CODE_PICK_FROM_ALBUM_BELLOW_LOLLILOP = 1;
    private final int RESULT_CODE_PICK_FROM_ALBUM_ABOVE_LOLLILOP = 2;
    private static final String APP_CACAHE_DIRNAME = "/webcache";
    boolean unbind = false;
    boolean isDian = false,isCollect=false;
    private String id,values,scanResult;
    private Effects effect;
    public static MyReceiver.ReceiverCallBack cbs;
    public static WXPayEntryActivity.goBackCall goBackCall;

    String goods_id;
    String content;
    String type;
    String createTime;

    private void setNowifi(boolean isnowifi) {
        if (isnowifi) {
            nowifi.setVisibility(View.VISIBLE);
        } else {
            nowifi.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (getIntent().hasExtra("dealWith")) {
            int dealwithid = getIntent().getIntExtra("dealWith", -1);
//            Log.e("checkEquipment", getIntent().getIntExtra("dealWith", -1) + "");
            if (dealwithid == 1) {
                webView.loadUrl(Web.url + "/userorder.aspx?action=list");
            } else if (dealwithid == 2) {
                webView.loadUrl(Web.url + "/userwinrice.aspx?action=list");
            } else if (dealwithid == 3) {
                webView.loadUrl(Web.url + "/userwine.aspx?action=list");
            } else if (dealwithid == 4) {
                webView.loadUrl(Web.url + "/userwine.aspx?action=list");
            } else if (dealwithid == 5) {
                webView.loadUrl(Web.url + "/useramount.aspx?action=silver");
            }

        }else if(getIntent().hasExtra("login")){
            String str= getIntent().getStringExtra("login");
            if(str.equals("yes")){
                StringBuilder builder1 = new StringBuilder();
                builder1.append("token=").append(UserInfoBean.getUser().getToken());
                String postData = builder1.toString();
                webView.postUrl(Web.url + Web.autologin, EncodingUtils.getBytes(postData, "UTF-8"));
                webView.addJavascriptInterface(new WebViewJsInterface(this), "webviewcall");
            }else if(str.equals("back")){
                webView.loadUrl(Web.url);
            }
            else if (str.equals("false")){
                webView.loadUrl(Web.url);
            }
        }
        else if (getIntent().hasExtra("url")){
            String str= getIntent().getStringExtra("url");
            webView.loadUrl(Web.url+str);
        }
        else if (getIntent().hasExtra("result")){
            String str= getIntent().getStringExtra("result");
            webView.loadUrl(Web.url+"/user_downlineshop_applay.aspx?action=shoptransfor&code="+str);
        }else if (getIntent().hasExtra("loginout")){
            login_out();

            //webView.loadUrl(Web.url+"/usercenter.aspx?action=exit");
        }else if (getIntent().hasExtra("forgetPassword")){
            webView.loadUrl(Web.url+"/usercenter.aspx?action=forget_pay_password");
        }
        else if (getIntent().hasExtra("about")){
            webView.loadUrl(Web.url+"/content.aspx?id=54");
        }
        else if (getIntent().hasExtra("notify_url")){
            tv_read.setVisibility(View.GONE);
            String str= getIntent().getStringExtra("notify_url");
            webView.loadUrl(str);
            if (str.contains("userjiguang_show.aspx")){
                delete.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.GONE);
                share_iv.setVisibility(View.GONE);
                dian.setVisibility(View.GONE);

            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                        | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        setContentView(R.layout.activity_webview);
        if (getIntent().hasExtra("url")) {
            url = getIntent().getStringExtra("url");
        }

        ButterKnife.bind(this);

        aCache = ACache.get(this);


        //判断全面屏手势是否启动
        if (Settings.Global.getInt(context.getContentResolver(),"force_fsg_nav_bar",0)!=0){
            Sofia.with(context)
                    .statusBarBackground(Color.parseColor("#fa902c"))
                    .invasionNavigationBar()
                    .navigationBarBackground(Color.TRANSPARENT)
                    .navigationBarBackgroundAlpha(0);
        }else {
            Sofia.with(context)
                    .statusBarBackground(Color.parseColor("#fa902c"))
                    .navigationBarBackground(Color.parseColor("#fa902c"));
        }


        status.setVisibility(View.GONE);

        if (getIntent().hasExtra("show_ver")){
            //checkVersion();
        }



        effect= Effects.flip;

        registerReceiver();
        sp= getSharedPreferences("loginUser", Context.MODE_PRIVATE);
        cbs=this;
        goBackCall=this;

        setView();
        ReqMessage();

    }

    boolean errorurl = false;

    public void setView() {
        shapeLoadingDialog = new ShapeLoadingDialog.Builder(context)
                .loadText("加载中...")
                .build();
        //声明WebSettings子类
        WebSettings webSettings = webView.getSettings();
        if (Util.isNetworkAvalible(context)){
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);//根据cache-control决定是否从网络上取数据。
        }
        else {
            //只要本地有，无论是否过期，或者no-cache，都使用缓存中的数据。
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
        webSettings.setUserAgentString(ConfigurationInfor.UserAgent);//设置代理
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        webSettings.setSupportZoom(false); //支持缩放，默认为true。是下面那个的前提。
        webSettings.setBuiltInZoomControls(false); //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        webSettings.setAllowFileAccess(true); //设置可以访问文件
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        webSettings.setLoadsImagesAutomatically(true); //支持自动加载图片
        webSettings.setDefaultTextEncodingName("utf-8");//设置编码格式
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        // 开启 DOM storage API 功能
        webSettings.setDomStorageEnabled(true);
        //开启 database storage API 功能
        webSettings.setDatabaseEnabled(true);
        //开启 Application Caches 功能
        webSettings.setAppCacheEnabled(true);
        webView.setBackgroundColor(ContextCompat.getColor(this, android.R.color.transparent));
        webView.setBackgroundResource(R.color.black80);
        webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            Log.e("开启硬件加速","开启硬件加速");
        }
        webView.setWebChromeClient(new MyChromeClient(this));
        //如果不设置WebViewClient，请求会跳转系统浏览器
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                Log.e("错误", "onReceivedError" + view.getUrl());
                //单独处理拍照时特殊情况
                if (view.getUrl().equals(Web.url + "/usercenter.aspx?action=index")&&Util.isNetworkAvalible(MainActivity.this)){
                    webView.loadUrl(Web.url+"/usercenter.aspx?action=index");
                }
                else {
                    errorurl = true;
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                shareurl = url;
                Log.e("现在加载的页面", "url" + url);
                if (url.equals(Web.url + "/usercenter.aspx?action=index")&&sp.getString("token","").equals("")){
                    webView.loadUrl(Web.url+"/usercenter.aspx?action=exit");
                }
                //支付宝微信上传二维码时不能更新本地用户头像
                if (shareurl.equals(Web.url+"/usercenter.aspx?action=index")){
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("other","yes");
                    editor.commit();
                }
                else {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("other","no");
                    editor.commit();
                }

                if (url.contains("login.aspx")&&!url.contains("action=yuandapay")) {
                    SharedPreferences.Editor editor = sp.edit();
                    editor.remove("token");
                    editor.commit();
                    Util.showIntent(context, LoginActivity.class);
                    Util.setIsLogin(false);
                    return true;
                }else if (url.contains("userjiguang_show.aspx")){
                    delete.setVisibility(View.VISIBLE);
                    refresh.setVisibility(View.GONE);
                    share_iv.setVisibility(View.GONE);
                    dian.setVisibility(View.GONE);
                    tv_read.setVisibility(View.GONE);
                }else if (url.contains("usercenter.aspx?action=exit")){
                    SharedPreferences.Editor editor = sp.edit();
                    editor.remove("token");
                    editor.commit();
                }
                else if (url.contains("userjiguang.aspx")){
                    tv_read.setVisibility(View.VISIBLE);
                    share_iv.setVisibility(View.GONE);
                }

                if (url.startsWith("weixin://wap/pay?") || url.startsWith("alipays://platformapi/startApp?")) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(context, "暂未安装相关APP", Toast.LENGTH_LONG).show();
                    }
                    return true;
                }

                if (url.contains("usercenter.aspx?action=account") && unbind) {
                    unbind = false;
                    webView.reload();
//                    webView.loadUrl("http://app.szcsf.cn/usercenter.aspx?action=index");
                }

                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

                shapeLoadingDialog.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                shareurl = url;
                Log.e("errorurl", "errorurl:" + errorurl);
                if (errorurl) {
                    setNowifi(true);
                    errorurl = false;
                } else {
                    setNowifi(false);
                }


                if (view.getTitle().equals("掌上酒窖")) {
                    center.setText(R.string.app_name);
                } else {
                    center.setText(Util.formatTitleStr(view.getTitle()));
                }
                title=Util.formatTitleStr(view.getTitle());
                Log.e("标题****",title);
                if (title.equals("")||shareurl.equals(Web.url+"/index.aspx")||shareurl.contains("/userwine.aspx?action=list")||shareurl.contains("/usercenter.aspx?action=index")||shareurl.contains("/userwinrice.aspx?action=list")){

                    relative_title.setVisibility(View.GONE);
                    back.setVisibility(View.GONE);
                    sao_ewm.setVisibility(View.GONE);
                }else if (url.contains("usercellar.aspx?action=gold")||url.contains("userwine.aspx?action=show_wine")||url.contains("usercellar.aspx?action=silver")){
                    relative_title.setVisibility(View.VISIBLE);
                    relative_title.setBackgroundColor(Color.parseColor("#fa902c"));
                    back.setVisibility(View.VISIBLE);
                    sao_ewm.setVisibility(View.GONE);
                }
                else {
                    relative_title.setVisibility(View.VISIBLE);
                    relative_title.setBackgroundColor(Color.parseColor("#f5f5f5"));
                    back.setVisibility(View.VISIBLE);
                    sao_ewm.setVisibility(View.GONE);
                }

                if (shapeLoadingDialog!=null){
                    shapeLoadingDialog.dismiss();
                }

            }
        });
        Intent intent = getIntent();

        if (intent.hasExtra("login")) {
            if (UserInfoBean.getUser()!=null){
                StringBuilder builder1 = new StringBuilder();
                builder1.append("token=").append(UserInfoBean.getUser().getToken());
                String postData = builder1.toString();
                webView.postUrl(Web.url + Web.autologin, EncodingUtils.getBytes(postData, "UTF-8"));
            }
        }
        else if (getIntent().hasExtra("notify_url")){
            state=1;
            tv_read.setVisibility(View.GONE);
            String str= getIntent().getStringExtra("notify_url");
            webView.loadUrl(str);
            if (str.contains("userjiguang_show.aspx")){
                delete.setVisibility(View.VISIBLE);
                refresh.setVisibility(View.GONE);
                share_iv.setVisibility(View.GONE);
                dian.setVisibility(View.GONE);
            }

        }else {
            if (!url.equals("")) {
                webView.loadUrl(Web.url + url);
            } else {
                webView.loadUrl(Web.url);
            }

        }
        webView.addJavascriptInterface(new WebViewJsInterface(this), "webviewcall");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            if (webView.canGoBack()) {

                //锁屏页面忘记密码、注册页面跳转
                if (getIntent().getStringExtra("forget")!=null||webView.getUrl().equals(Web.url+"/repassword.aspx")
                        ||webView.getUrl().equals(Web.url+"/register.aspx")){
                    Util.showIntent(context, LoginActivity.class);
                    finish();
                }
                else {
                    delete.setVisibility(View.GONE);
                    refresh.setVisibility(View.VISIBLE);
                    share_iv.setVisibility(View.GONE);
                    tv_read.setVisibility(View.GONE);
                    mall_collect.setVisibility(View.GONE);
                    if (isDian){
                        dian.setVisibility(View.GONE);
                    }

                    if(webView.getUrl().equals(Web.url+"/userjiguang.aspx")){
                        ReqMessage();
                    }

                    if (title.equals("掌上酒窖")||title.equals("我的酒窖")||title.equals("会员中心")){
                        exit();
                    }
                    else {
                        if (webView.canGoBack()){
                            webView.goBack();
                        }else {
                            exit();
                        }

                    }

                    Log.e("address", "address:" + webView.getUrl());
                    if (webView.getUrl().contains("usercenter.aspx?action=password") || webView.getUrl().contains("usercenter.aspx?action=pay_password")) {
                        webView.loadUrl(Web.url + "/usercenter.aspx?action=index");
                    }else if (webView.getUrl().contains("userorder.aspx?action=list")||webView.getUrl().contains("userordercellar_tran.aspx?action=list")) {
                        webView.loadUrl(Web.url+"/usercenter.aspx?action=index");
                    }
                }
            }else {

                if (state==1){
                    //MainActivity不存在时返回应加载主页
                    webView.loadUrl(Web.url);
                    state=0;
                }else if (webView.getUrl().contains("userwalkline.aspx")||webView.getUrl().contains("usercircle.aspx?action=index")){
                    finish();
                }else if (webView.getUrl().contains("userdiamond.aspx?action=yw_list2")){
                    webView.loadUrl(Web.url+"/diamonds_mall.aspx");
                }
                else {
                    exit();
                }

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @OnClick({R.id.back,R.id.center,R.id.sao_ewm, R.id.refresh, R.id.mMessage,R.id.delete, R.id.share_iv, R.id.tv_read, R.id.nowifi, R.id.mall_collect})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.back:
                delete.setVisibility(View.GONE);
                refresh.setVisibility(View.VISIBLE);
                share_iv.setVisibility(View.GONE);
                tv_read.setVisibility(View.GONE);
                mall_collect.setVisibility(View.GONE);
                if (isDian) {
                    dian.setVisibility(View.GONE);
                }

                Log.e("加载地址", "地址:" + webView.getUrl());
                if (webView.getUrl().equals(Web.url + "/userjiguang.aspx")) {
                    ReqMessage();
                }

                if (getIntent().getStringExtra("forget") != null || webView.getUrl().equals(Web.url + "/repassword.aspx") ||
                        webView.getUrl().equals(Web.url + "/register.aspx")) {
                    Util.showIntent(context, LoginActivity.class);
                    finish();
                }

                else {
                    if (webView.getUrl().contains("usercenter.aspx?action=password") || webView.getUrl().contains("usercenter.aspx?action=pay_password")) {
                        webView.loadUrl(Web.url + "/usercenter.aspx?action=index");
                    } else if (webView.getUrl().contains("userbussions.aspx?action=yw_list&page=")) {
                        int page = Integer.parseInt(Util.getValueByName(webView.getUrl(), "page"));
                        if (page >= 2) {
                            webView.loadUrl(Web.url + "/userbussions.aspx?action=list");
                        }
                    } else if (webView.getUrl().contains("/userbussions_aplay.aspx?action=list&back=zc")) {
                        webView.loadUrl(Web.url + "/asset.aspx");
                    } else if (webView.getUrl().contains(Web.url + "userjiguang_show.aspx?id")) {
                        webView.loadUrl(Web.url + "/userjiguang.aspx");
                    } else if (webView.getUrl().contains("userorder.aspx?action=list")||webView.getUrl().contains("userordercellar_tran.aspx?action=list")) {
                        webView.loadUrl(Web.url+"/usercenter.aspx?action=index");
                    }else if (webView.canGoBack()) {
                        webView.goBack();
                    }   else {
                        if (state == 1) {
                            //MainActivity不存在时返回因加载主页
                            webView.loadUrl(Web.url);
                            state = 0;
                        }  else if (webView.getUrl().contains("userwalkline.aspx")||webView.getUrl().contains("usercircle.aspx?action=index")){
                            finish();
                        }else {
                            alert();
                        }
                    }
                }
                break;
            case R.id.sao_ewm:
                goCapture();
                webView.reload();

                break;
            case R.id.refresh:
                Util.clearAllCache(context);
                webView.reload();
                ReqMessage();
                break;
            case R.id.center:
                //handler1.post(task);

                break;
            case R.id.delete:
                DeleteMessage();
                break;
            case R.id.share_iv:
                share_iv.setVisibility(View.GONE);
                dian.setVisibility(View.GONE);
                tv_read.setVisibility(View.VISIBLE);
                webView.loadUrl(Web.url + "/userjiguang.aspx");
                break;
            case R.id.mMessage:
                share_iv.setVisibility(View.GONE);
                dian.setVisibility(View.GONE);
                tv_read.setVisibility(View.GONE);
                webView.loadUrl(Web.url + "/userjiguang.aspx");
                break;
            case R.id.tv_read:
                clearMesaage();
                break;
            case R.id.nowifi:
                webView.reload();
                break;
            case R.id.mall_collect:
//                webView.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        // 注意调用的JS方法名要对应上
//                        // 调用javascript的callJS()方法
//                        webView.loadUrl("javascript:AddUserFavorite()");
//                    }
//                });
                Log.e("goodsID",goods_id);
                if (isCollect){
                    DelUserFavoriteList(goods_id);
                }
                else {
                    AddUserFavorite(goods_id,type);
                }

                break;
        }
    }

    public void alert() {
        new AlertDialog.Builder(context).setTitle("提示：").setMessage("您确定要退出吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ActivityCollector.finishAll();
                System.exit(0);
            }
        }).setNegativeButton("取消", null).setIcon(R.drawable.logo1).show();
    }
    private long exitTime=0;
    private void exit(){

        if((System.currentTimeMillis()-exitTime)>2000) {
            BamToast.showText(context,"再按一次退出掌上酒窖");
            exitTime = System.currentTimeMillis();
        }
        else{
            ActivityCollector.finishAll();
            System.exit(0);
        }
    }


    private void showShare(String sharetext, String commentText) {
        Random r=new Random();
        int i = r.nextInt(10);//0-9随机数
        OnekeyShare oks = new OnekeyShare();
        oks.setTitle(sharetext);
        oks.setTitleUrl(shareurl);
        oks.setUrl(shareurl);
        //oks.setImagePath(Environment.getExternalStorageDirectory()+"/LOGO.png");
        //oks.setImageUrl("http://m.web20190201.demo.dnsrw.com//templates/mobile/images/logo1.png?i="+i);
//        oks.setAddress("10086");
        oks.setImageData(BitmapFactory.decodeResource(context.getResources(), R.drawable.logo1));
        oks.setComment(commentText);
        oks.setText(commentText);
        oks.setSite(shareurl);
        oks.setSilent(false);
        oks.setSiteUrl(shareurl);
        oks.show(this);
    }

    boolean isRefresh = false;
    private ACache aCache;
    private String gesturePassword;
    @Override
    protected void onResume() {
        super.onResume();
        if (isRefresh) {
            Log.e("重新刷新", "isRefresh:" + isRefresh);
            webView.reload();
            isRefresh = false;
        }
        if (!sp.getString("token","").equals("")&&!Util.islock){
            gesturePassword = aCache.getAsString(Constant.GESTURE_PASSWORD);
            if(gesturePassword!=null){
                if (getIntent().getStringExtra("forget")==null){
                    Intent intent = new Intent(context, GestureLoginActivity.class);
                    startActivity(intent);
                }
            }
        }
    }

    //js交互接口
    @Override
    public void doCall(String str) {
        try {
            JSONObject jsonObject = new JSONObject(str);
            final String type = jsonObject.optString("type") + "";
            String ptype = jsonObject.optString("ptype") + "";
            String page = jsonObject.optString("page") + "";
            String wxpay = jsonObject.optString("wxpay") + "";
            id = jsonObject.optString("id") + "";

            if (type.equals("share")) {
                String user = jsonObject.optString("url") + "";
                shareurl = user;
                showShare("邀请你注册掌上酒窖会员", "酒厂开在手机上，财富美酒自然来！酱香酒，健康酒，品牌酒，文化酒，财富酒……" );

            } else if (!Util.isNull(wxpay)) {
//                Util.show(context,"暂时只能支持线下支付");

                String orderNo = jsonObject.optString(WXPayEntryActivity.ORDERNO) + "";

                Intent intent = new Intent(this, WXPayEntryActivity.class);
                intent.putExtra(WXPayEntryActivity.ORDERNO, wxpay);
                startActivity(intent);

            } else if (type.equals("bind") || (type.equals("onbind"))) {

                if (type.equals("onbind")) {
                    unbind = true;
                }
                if (page.equals("withdraw_deposit")) {
                    isRefresh = true;
                }

                Intent intent = new Intent(this, AddWeiXinAliActivity.class);
                if (!Util.isNull(ptype)) {
                    if (ptype.equals("ay")) {
                        intent.putExtra(AddWeiXinAliActivity.BINGTAG, AddWeiXinAliActivity.ALIACCOUNT);
                    } else if (ptype.equals("wx")) {
                        intent.putExtra(AddWeiXinAliActivity.BINGTAG, AddWeiXinAliActivity.WINXINACCOUNT);
                    }
                }
                startActivity(intent);

            }else if (type.equals("shoushi")) {
                gesturePassword = aCache.getAsString(Constant.GESTURE_PASSWORD);
                if (gesturePassword != null) {
                    Intent intent = new Intent(context, GestureLoginActivity.class);
                    intent.putExtra("update", "update");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, CreateGestureActivity.class);
                    startActivity(intent);
                }
            }

            else if (type.equals("appshare")){
                shareurl = Web.url + "/index.aspx?action=app_download";
                //showShare("【掌上酒窖】诚挚邀你下载掌上酒窖", "诚挚邀你注册掌上酒窖，共享财富。下载地址：" + shareurl);
                showShare("邀请你注册掌上酒窖会员", "酒厂开在手机上，财富美酒自然来！酱香酒，健康酒，品牌酒，文化酒，财富酒……");
            }else if (type.equals("setting")){
                startActivity(new Intent(this, SettingActivity.class));
            }
            else if (type.equals("appewm")||type.equals("ewm")){
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                final String[] requestPermissionstr = {

                                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,

                                };
                                Util.checkpermissions(requestPermissionstr, context, new Util.PermissionsCallBack() {
                                    @Override
                                    public void success() {
                                        final WebView.HitTestResult htr = webView.getHitTestResult();//获取所点击的内容
                                        if (htr.getType() == WebView.HitTestResult.IMAGE_TYPE
                                                || htr.getType() == WebView.HitTestResult.IMAGE_ANCHOR_TYPE
                                                || htr.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                                            Log.e("是图片", "判断被点击的类型为图片  " + htr.getExtra());
                                            final Dialog dialog = new Dialog(context);
                                            View view1 = LayoutInflater.from(context).inflate(R.layout.dialog_layout, null);
                                            dialog.setContentView(view1);
                                            TextView textView1 = view1.findViewById(R.id.downloadpic);
                                            final TextView textView2 = view1.findViewById(R.id.reQRcode);
                                            textView2.setVisibility(View.GONE);
                                            textView1.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    dialog.dismiss();
                                                    DownLoadPicUtil.download(htr.getExtra(),context,new DownLoadPicUtil.DownLoadCallBack() {


                                                        @Override
                                                        public void onSuccess(Bitmap bitmap, String downloadpath) {
                                                            Util.show(context, "下载路径为:" + downloadpath);
                                                        }

                                                        @Override
                                                        public void onFailure(String message) {

                                                        }
                                                    });

                                                }
                                            });

                                            dialog.setTitle("Dialog");
                                            Window window = dialog.getWindow();
                                            WindowManager.LayoutParams lp = window.getAttributes();
                                            window.setGravity(Gravity.CENTER);
                                            WindowManager windowManager = getWindowManager();
                                            Display display = windowManager.getDefaultDisplay();
                                            lp.width = (int) (display.getWidth() * 0.8); //设置宽度
                                            window.setAttributes(lp);
                                            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//内部背景透明
                                            dialog.show();
                                        }
                                    }

                                    @Override
                                    public void failure() {
                                        Toast.makeText(context, "请求允许应用权限请求", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }
                        }
                );

            }

        } catch (Exception e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //扫描结果回调
        if (resultCode == 161) {

            if (data!=null){
                Bundle bundle = data.getExtras();
                scanResult = bundle.getString("qr_scan_result");
                get_User(scanResult);

                //将扫描出的信息显示出来
                Log.e("主页面将扫描出的信息显示出来", "scanResult:" + scanResult);
            }

        }
        Uri uri;
        if (data == null && mCameraFilePath.equals("")
                &&mUploadMessage == null && mUploadCallbackAboveL == null) {
            return;
        }

        if (data == null) {
            File file = new File(mCameraFilePath);
            uri = Uri.fromFile(file);
        } else {
            uri = data.getData();
            //支付宝微信上传二维码时不能更新本地用户头像
//            if (!shareurl.equals(Web.url+"/usercenter.aspx?action=webcat")&&!shareurl.equals(Web.url+"/usercenter.aspx?action=alipay")
//                    &&!webView.getUrl().contains("/user_downlineshop_applay.aspx?action=index")){
//
//                SharedPreferences.Editor editor = sp.edit();
//                editor.putString("path", String.valueOf(uri));
//                editor.putString("portrait","change");
//                editor.commit();
//            }

            if (webView.getUrl().equals(Web.url+"/usercenter.aspx?action=index")){
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("path", String.valueOf(uri));
                editor.putString("portrait","change");
                editor.commit();
            }

        }

        switch (requestCode) {
            case RESULT_CODE_PICK_FROM_ALBUM_BELLOW_LOLLILOP:
//                uri = afterChosePic(data);
                if (mUploadMessage != null) {
                    mUploadMessage.onReceiveValue(uri);
                    mUploadMessage = null;
                }
                break;
            case RESULT_CODE_PICK_FROM_ALBUM_ABOVE_LOLLILOP:
                try {
//                    uri = afterChosePic(data);
                    if (uri == null) {
                        mUploadCallbackAboveL.onReceiveValue(new Uri[]{});
                        mUploadCallbackAboveL = null;
                        break;
                    }
                    if (mUploadCallbackAboveL != null && uri != null) {
                        mUploadCallbackAboveL.onReceiveValue(new Uri[]{uri});
                        mUploadCallbackAboveL = null;
                    }
                } catch (Exception e) {
                    mUploadCallbackAboveL = null;
                    e.printStackTrace();
                }
                break;

        }
    }

    @Override
    protected void onDestroy() {
        if (webView != null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }

            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearHistory();
            webView.clearView();
            webView.removeAllViews();
            webView.destroy();
            if (mHomeWatcherReceiver != null) {
                try {
                    unregisterReceiver(mHomeWatcherReceiver);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        super.onDestroy();
    }

    private void goMall() {
        final PackageManager pm = context.getPackageManager();
        Intent intent = new Intent();
        if (null == pm.getLaunchIntentForPackage("com.mall.view")) {//没有获取到intent
            Toast.makeText(context, "请先安装远大云商", Toast.LENGTH_LONG).show();
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://down.yda360.com/Mall.apk")));
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClassName("com.mall.view", "com.mall.view.Leading");
            intent.putExtra("openClassName", "com.yda.yiyunchain");
            startActivity(intent);
        }
    }

    //极光消息回调接口
    @Override
    public void doCallBack(String value) {
        this.values=value;
        dian.setVisibility(View.GONE);

        if (value.equals("token")){

            if (!sp.getString("token","").equals("")){
                new PromptDialog(this)
                        .setDialogType(PromptDialog.DIALOG_TYPE_WARNING)
                        .setAnimationEnable(true)
                        .setTitleText("下线通知")
                        .setContentText("你的账号于"+CommonUtil.getCurTime()+"在另外一台设备登录。如非本人操作，" +
                                "则密码可能已经泄露，建议前往修改密码。")
                        .setPositiveListener("修改密码", new PromptDialog.OnPositiveListener() {
                            @Override
                            public void onClick(PromptDialog dialog) {
                                dialog.dismiss();
                                if (!sp.getString("token","").equals("")){
                                    login_out();
                                }
                                else {
                                    Util.showIntent(context,LoginActivity.class);
                                    finish();
                                }
                                //webView.loadUrl(Web.url+"/usercenter.aspx?action=exit");
                            }
                        })
                        .setNegativeListener("确定", new PromptDialog.OnNegativeListener() {
                            @Override
                            public void onClick(PromptDialog dialog) {
                                dialog.dismiss();
                                webView.loadUrl(Web.url+"/repassword.aspx");
                            }
                        }).show();
            }

            SharedPreferences.Editor editor = sp.edit();
            editor.remove("token");
            editor.remove("alias");
            editor.commit();

        }
        else {
            showNotification();
        }

       Log.e("%%%%%%%%%%%%",values+"%%%%%%%%%");
    }


    public void showNotification(){
        Configuration cfg=new Configuration.Builder()
                .setAnimDuration(700)
                .setDispalyDuration(5000).build();
        NiftyNotificationView.build(this,"你有一条新通知，点击前往查看", effect,R.id.mLyout,cfg)
                .setIcon(R.drawable.logo1)//You must call this method if you use ThumbSlider effect
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        webView.loadUrl(values);
                        if (values.contains("userjiguang_show.aspx")){
                            delete.setVisibility(View.VISIBLE);
                            refresh.setVisibility(View.GONE);
                            share_iv.setVisibility(View.GONE);
                            dian.setVisibility(View.GONE);
                        }
                    }
                })
                .show();
    }

    @Override
    public void goBack() {
        webView.goBack();
    }

    public class HomeWatcherReceiver extends BroadcastReceiver {

        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";

        @Override
        public void onReceive(Context context, Intent intent) {

            String intentAction = intent.getAction();
            if (TextUtils.equals(intentAction, Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (TextUtils.equals(SYSTEM_DIALOG_REASON_HOME_KEY, reason)) {
                    if (Util.isRecent){
                        Util.isRecent=false;
                    }
                    else {
                        Util.islock=false;
                    }
                }
                else if (TextUtils.equals(SYSTEM_DIALOG_REASON_RECENT_APPS, reason)) {
                    Util.isRecent=true;
                    Util.islock=true;
                }
            }
            else if (TextUtils.equals(intentAction, Intent.ACTION_SCREEN_OFF)){
                Util.islock=false;
            }
        }
    }

    //注册home、锁屏广播
    private void registerReceiver() {
        mHomeWatcherReceiver = new HomeWatcherReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mHomeWatcherReceiver, filter);
    }

    //删除已读消息
    public void DeleteMessage(){

        HashMap<String, String> params = new HashMap<>();
        params.put("id", id);
        params.put("token", sp.getString("token",""));
        HttpManger.postRequest(context, "/tools/submit_api.ashx?action=DelJiguangMsg", params, "请稍后...", new HttpManger.DoRequetCallBack() {
            @Override
            public void onSuccess(String t) {

                JSONObject json = null;
                try {
                    json = new JSONObject(t);
                    String status=json.optString("status");
                    Util.show(context, json.optString("msg"));
                    if (status.equals("1")){
                        webView.loadUrl(Web.url+"/userjiguang.aspx");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("请求结果", "str" + t);
            }

            @Override
            public void onError(String t) {

            }
        });
    }

    //获取未读消息数量
    public void ReqMessage(){

        HashMap<String, String> params = new HashMap<>();
        params.put("token", sp.getString("token",""));
        HttpManger.postRequest(context, "/tools/submit_api.ashx?action=GetCountJiguangMsg", params, "请稍后...", new HttpManger.DoRequetCallBack() {
            @Override
            public void onSuccess(String t) {

                JSONObject json = null;
                try {
                    json = new JSONObject(t);
                    if (json.optString("status").equals("1")){
                        if (Integer.valueOf(json.optString("count"))>0){
                            isDian=true;
                            if (share_iv.getVisibility()==View.VISIBLE){
                                dian.setVisibility(View.GONE);
                            }

                        }
                        else {
                            isDian=false;
                            dian.setVisibility(View.GONE);
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("请求结果", "str" + t);
            }

            @Override
            public void onError(String t) {

            }
        });
    }

    //退出登录
    public void login_out(){
        webView.loadUrl(Web.url+"/usercenter.aspx?action=exit");
        HashMap<String, String> params = new HashMap<>();
        params.put("site_id", "1");
        params.put("token", sp.getString("token",""));
        HttpManger.postRequest(context, "/tools/submit_api.ashx?action=checkout", params, "请稍后...", new HttpManger.DoRequetCallBack() {
            @Override
            public void onSuccess(String t) {

                JSONObject json = null;
                try {
                    json = new JSONObject(t);
                    String msg=json.optString("msg");
                    if (json.optString("status").equals("1")){
                        SharedPreferences.Editor editor = sp.edit();
                        editor.remove("token");
                        editor.remove("alias");
                        JPushInterface.setAlias(context, 200, "");
                        editor.commit();
                        Util.showIntent(context, LoginActivity.class);
                        finish();
                    }
                    else {
                        Util.show(context,msg);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("请求结果", "str" + t);
            }

            @Override
            public void onError(String t) {

            }
        });
    }

    public static final int UPDATE = 0x1;
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case UPDATE:
                    if (flag){
                        webView.loadUrl(Web.url+"/user_downlineshop_applay.aspx?action=shoptransfor&code="+scanResult);
                    }
                    else {
                        Util.show(context,msg1);
                    }

                    break;
            }
        }
    };

    //验证扫描的二维码商家是否存在
    public void get_User(String guidno){
        HashMap<String, String> params = new HashMap<>();
        params.put("guidno",guidno);
        params.put("token", sp.getString("token",""));
        HttpManger.postRequest(context, "/tools/submit_api.ashx?action=GetshopUserByGuid", params, "请稍后...", new HttpManger.DoRequetCallBack() {

            @Override
            public void onSuccess(String o) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(o);
                    String status = jsonObject.optString("status") + "";
                    msg1 = jsonObject.optString("msg") + "";
                    flag = status.equals("1");
                    Message msg = new Message();
                    msg.what = UPDATE;
                    handler.sendMessage(msg);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("请求结果", "str" + o);
            }

            @Override
            public void onError(String o) {
                Log.e("请求结果", o);
            }
        });

    }

    public void clearMesaage(){
        HashMap<String, String> params = new HashMap<>();
        params.put("token", sp.getString("token",""));
        HttpManger.postRequest(context, "/tools/submit_api.ashx?action=UpdateAllRead", params, "请稍后...", new HttpManger.DoRequetCallBack() {

            @Override
            public void onSuccess(String o) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(o);
                    String status = jsonObject.optString("status") + "";
                    msg1 = jsonObject.optString("msg") + "";

                    if (status.equals("1")){
                        webView.reload();
                    }
                    Util.show(context,msg1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("请求结果", "str" + o);
            }

            @Override
            public void onError(String o) {
                Log.e("请求结果", o);
            }
        });

    }

    //线下商家付款
    public void goCapture(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    final String[] requestPermissionstr = {

                            android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,

                    };
                    Util.checkpermissions(requestPermissionstr, context, new Util.PermissionsCallBack() {
                        @Override
                        public void success() {
                            if (CommonUtil.isCameraCanUse()) {
                                Intent intent = new Intent(context, CaptureActivity.class);
                                intent.putExtra("paybussion","paybussion");
                                startActivityForResult(intent, REQUEST_CODE);
                            } else {
                                Toast.makeText(context, "请打开此应用的摄像头权限！", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void failure() {

                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                    Util.show(context,e.getMessage());
                }
            }
        });
    }

    private void AddUserFavorite(String id,String type){
        HashMap<String, String> params = new HashMap<>();
        params.put("type", type);
        params.put("article_id", id);
        HttpManger.postRequest(context, false,"/tools/submit_api.ashx?action=AddUserFavorite", params, "请稍后...", new HttpManger.DoRequetCallBack() {

            @Override
            public void onSuccess(String o) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(o);
                    String status = jsonObject.optString("status") + "";
                    msg1 = jsonObject.optString("msg") + "";

                    if (status.equals("1")){
                        BamToast.showText(context, msg1, true);
                        mall_collect.setChecked(true);
                        isCollect=true;
                    }
                    else {
                        BamToast.showText(context, msg1);
                        mall_collect.setChecked(false);
                        isCollect=false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("请求结果", "str" + o);
            }

            @Override
            public void onError(String o) {
                Log.e("请求结果", o);
            }
        });

    }

    private void DelUserFavoriteList(String id){
        HashMap<String, String> params = new HashMap<>();
        params.put("article_id", id);
        HttpManger.postRequest(context, false,"/tools/submit_api.ashx?action=DelUserFavoriteList", params, "请稍后...", new HttpManger.DoRequetCallBack() {

            @Override
            public void onSuccess(String o) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(o);
                    String status = jsonObject.optString("status") + "";
                    msg1 = jsonObject.optString("msg") + "";
                    mall_collect.setChecked(false);
                    isCollect=false;
                    BamToast.showText(context, msg1);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("请求结果", "str" + o);
            }

            @Override
            public void onError(String o) {
                Log.e("请求结果", o);
            }
        });

    }

    private void IsUserFavorite(String id){
        HashMap<String, String> params = new HashMap<>();
        params.put("article_id", id);
        HttpManger.postRequest(context, false,"/tools/submit_api.ashx?action=IsUserFavorite", params, "请稍后...", new HttpManger.DoRequetCallBack() {

            @Override
            public void onSuccess(String o) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(o);
                    String status = jsonObject.optString("status") + "";
                    msg1 = jsonObject.optString("msg") + "";

                    if (status.equals("1")){
                        mall_collect.setChecked(true);
                        isCollect=true;
                    }
                    else {
                        mall_collect.setChecked(false);
                        isCollect=false;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.e("请求结果", "str" + o);
            }

            @Override
            public void onError(String o) {
                Log.e("请求结果", o);
            }
        });

    }
}
