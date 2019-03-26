package com.yda.handWine.wxapi;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yda.handWine.Bean.OrderInfo;
import com.yda.handWine.HttpUtil.HttpManger;
import com.yda.handWine.MyView.MoreTextView;
import com.yda.handWine.PayUtils.PayInfo;
import com.yda.handWine.PayUtils.WeiXinUtlis.WeiXinPay;
import com.yda.handWine.PopWindowHelp.BasePopWindow;
import com.yda.handWine.R;
import com.yda.handWine.activity.BaseActivity;
import com.yda.handWine.activity.LoginActivity;
import com.yda.handWine.activity.MainActivity;
import com.yda.handWine.util.SelectorFactory;
import com.yda.handWine.util.Util;
import com.yda.handWine.widget.ShapeLoadingDialog;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018/5/11.
 */

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    @BindView(R.id.refresh)
    public View refresh;
    @BindView(R.id.center)
    public TextView center;

    @BindView(R.id.share_iv)
    ImageView share_iv;

    @BindView(R.id.back)
    ImageView back;

    @BindView(R.id.ordermoney_tv)
    TextView orderMoney;
    @BindView(R.id.orderid_tv)
    TextView orderid;

    @BindView(R.id.shwo_pay)
    View shwoPay;
    @BindView(R.id.isSuccess)
    public MoreTextView isSuccess;

    @BindView(R.id.submit)
    Button submit;

    private IWXAPI api;

    //订单传递
    final public static String ORDERNO = "order_no";

    //支付方式
    public static int WEIXINPAY = 9;
    public static int ZHIFUBAOPAY = 8;

    //支付结果
    public static int DOCANCEL = -2;
    public static int DOFAILURE = -1;
    public static int DOSUCCESS = 0;

    private ShapeLoadingDialog shapeLoadingDialog;

    //支付金额
    double money = 0;

    int payType = 0;

    //当前页面是显示支付页面还是支付结果页
    boolean ispay;

    //当支付结果
    int isresult;

    //订单
    String order;
    String subject = "商品购买";

    public goBackCall goBackCall;

    public interface goBackCall{
        void goBack();
    }

    void UiStatus(boolean ispay, int isresult) {
        this.ispay = ispay;
        this.isresult = isresult;
        refresh.setVisibility(View.GONE);
        String submitStr = "";

        if (ispay) {
            submitStr = "确认支付";
            shwoPay.setVisibility(View.VISIBLE);
            isSuccess.setVisibility(View.GONE);

        } else {
            shwoPay.setVisibility(View.GONE);
            isSuccess.setVisibility(View.VISIBLE);
            if (isresult == DOCANCEL) {
                submitStr = "返回酒窖列表";
                isSuccess.setText("支付失败");
                isSuccess.setImag(R.drawable.wxfail);
            } else if (isresult == DOFAILURE) {
                submitStr = "返回酒窖列表";
                isSuccess.setText("支付失败");
                isSuccess.setImag(R.drawable.wxfail);

            } else if (isresult == DOSUCCESS) {
                isSuccess.setText("支付成功");
                isSuccess.setImag(R.drawable.wxsuccess);
                if (order.contains("B")) {
                    submitStr = "查看提酒记录";
                } else if (order.contains("TR")) {
                    submitStr = "查看转账记录";
                }
            }
        }
        submit.setText(submitStr);

    }


    @OnClick({R.id.back, R.id.submit})
    public void click(View view) {
        Intent intent = new Intent(context, MainActivity.class);
        switch (view.getId()) {
            case R.id.back:
                finish();
                goBackCall.goBack();
                break;
            case R.id.submit:
                if (ispay) {
//                    if (payType == ZHIFUBAOPAY) {
//                        new PayUtil().payV2(WXPayEntryActivity.this, subject, order, Util.getTwoDecimal(money), new PayUtil.PayCallBack() {
//                            @Override
//                            public void doCallBackSuccess(PayResult payResult) {
////                                Log.e("Ali Success", "Success" + payResult.toString());
//                                UiStatus(false, DOSUCCESS);
//                            }
//
//                            @Override
//                            public void doCallBackFailure(PayResult payResult) {
//                                UiStatus(false, DOFAILURE);
//                            }
//                        });
//                    } else if (payType == WEIXINPAY) {
//                        final ShapeLoadingDialog shapeLoadingDialog = new ShapeLoadingDialog.Builder(context)
//                                .loadText("微信支付中...")
//                                .build();
//                        shapeLoadingDialog.show();
////                        CustomProgressDialog pd = CustomProgressDialog.showProgressDialog(WXPayEntryActivity.this, "微信支付中...");
//                        new WeiXinPay(shapeLoadingDialog, context, Util.getTwoDecimal(money), order, subject).Pay();
//                    }
                    shapeLoadingDialog = new ShapeLoadingDialog.Builder(context)
                            .loadText("微信支付中...")
                            .build();
                    shapeLoadingDialog.show();
//                        CustomProgressDialog pd = CustomProgressDialog.showProgressDialog(WXPayEntryActivity.this, "微信支付中...");
                    new WeiXinPay(context, Util.getTwoDecimal(money), order, subject).Pay();
                } else {
                    if (isresult == DOSUCCESS) {

                        if (order.contains("B")) {
                            intent.putExtra("dealWith", 1);
                        } else if (order.contains("TR")) {
                            intent.putExtra("dealWith", 2);
                        }

                        startActivity(intent);
                    } else {

                        if (isresult == DOCANCEL || isresult == DOFAILURE) {

                            if (order.contains("B")) {
                                intent.putExtra("dealWith", 3);
                            } else if (order.contains("TR")) {
                                intent.putExtra("dealWith", 4);
                            }

                            startActivity(intent);

                        } else {
                            finish();
                        }

                    }

                }
                break;
        }

    }

    public void setSubmit(boolean isclick) {
        submit.setEnabled(isclick);
    }


    public void queryOrder(final Context context, final String orderid) {

        HashMap<String, String> params = new HashMap<>();
        params.put("orderno", orderid);
        HttpManger.postRequest(context, "/tools/submit_api.ashx?action=SeachOrder", params, "加载中...", new HttpManger.DoRequetCallBack() {


            @Override
            public void onSuccess(String t) {
                Log.e("回掉 Success", "Success" + t);
                Gson gson = new Gson();

                OrderInfo orderInfo = gson.fromJson(t, OrderInfo.class);
                if (orderInfo.getStatus() == 0) {
                    View view = LayoutInflater.from(context).inflate(R.layout.popwind_prompt, null);
                    TextView content = view.findViewById(R.id.content);
                    TextView sure = view.findViewById(R.id.sure);
                    int with = (int) (Util.getScreenSize(context).getWidth() * 0.75);
                    int hight = (int) (Util.getScreenSize(context).getHeight() * 0.75);
                    final BasePopWindow basePopWindow = new BasePopWindow.Builder(context, view).setWidth(with, ViewGroup.LayoutParams.WRAP_CONTENT).setIschagebackcolor(true).build();
                    sure.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            basePopWindow.dismiss();
//                            Intent intent=new Intent(context,MainActivity.class);
//                            if (order.contains("B")) {
//                                intent.putExtra("dealWith", 1);
//                            } else if (order.contains("TR")) {
//                                intent.putExtra("dealWith", 2);
//                            }
//                            startActivity(intent);

                            Util.showIntent(context, LoginActivity.class);
                            finish();
                        }
                    });

                    content.setText(orderInfo.getMsg());
                    basePopWindow.showCenter();
                    setSubmit(false);
                }
                else if (orderInfo.getStatus() == 1) {
                    money = orderInfo.getModel().getOrder_amount();
                    orderMoney.setText(money+"");
                    payType = orderInfo.getPay_type();
                    setSubmit(true);
                } else if (orderInfo.getStatus() == 2) {
                    Util.show(context, "订单获取失败");
                    setSubmit(false);
                }


            }

            @Override
            public void onError(String t) {
//                Log.e("回掉 Error", "Success" + t);
                Util.show(context, t);
                setSubmit(false);
            }


        });


    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.pay_result);
        ButterKnife.bind(this);

        refresh.setVisibility(View.GONE);
        share_iv.setVisibility(View.GONE);
        back.setVisibility(View.VISIBLE);
        Intent intent = getIntent();
        center.setText("订单支付");
        submit.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#fa902c"))
                .setPressedBgColor(Color.parseColor("#ffffff"))
                .setCornerRadius(Util.dpToPx(context, 5))
                .create(context));
        order = intent.getStringExtra(ORDERNO);
        orderid.setText(order);
        goBackCall=MainActivity.goBackCall;
        setSubmit(false);
        queryOrder(context, order);
        UiStatus(true, -20);

        api = WXAPIFactory.createWXAPI(this, PayInfo.WI_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        Log.e(TAG, "onPayFinish, errCode = " + resp.errCode + "\n"
                + "errStr=" + resp.errStr + "\n"
                + "transaction=" + resp.transaction + "\n"
                + "openId=" + resp.openId + "\n"
        );

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle(R.string.app_name);
//            builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
//            builder.show();
            UiStatus(false, resp.errCode);
            if (shapeLoadingDialog!=null){
                shapeLoadingDialog.cancel();
                shapeLoadingDialog.dismiss();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            finish();
            goBackCall.goBack();
        }

        return super.onKeyDown(keyCode, event);
    }
}