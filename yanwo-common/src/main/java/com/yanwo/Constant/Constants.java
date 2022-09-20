package com.yanwo.Constant;

public final class Constants {

    //正式
    //public static final String PAY_TITLE="商品支付";//支付名称
    //public static final String ENDPOINT="http://oss-cn-qingdao-internal.aliyuncs.com";//内网图片服务器
    //public static final String APPNAME="yanwo";
    //public static final String TABLENAME="item";
    //public static final String HOST="http://intranet.opensearch-cn-qingdao.aliyuncs.com";//开放搜索内网


    //本地
    public static final String PAY_TITLE="商品支付";//支付名称
    public static final String ENDPOINT="http://oss-cn-beijing.aliyuncs.com";//外网图片服务器
    public static final String APPNAME="yanwo";
    public static final String TABLENAME="item";
    public static final String HOST="http://opensearch-cn-qingdao.aliyuncs.com";//开放搜索外网


    public static final String REDIS_TOKEN_APP_SESSION_PREFIX = "REDIS_TOKEN_APP:";

    //在redis中存储用户购物车信息的key
    public final static String REDIS_CART_PORTAL = "REDIS_CART_PORTAL";
    //在redis中存储用户订单确认信息
    public final static String REDIS_ORDER_PORTAL="REDIS_ORDER_PORTAL";
    //由redis来维护订单id
    public final static String REDIS_TRADE_ID = "REDIS_TRADE_ID";
    public final static String REDIS_ORDER_ID = "REDIS_ORDER_ID";
    public static final String REDIS_PAYMENT_ID = "PAYMENT_ID";
    //redis来维护退款单编号
    public final static String REDIS_REFUND_BN = "REDIS_REFUND_BN";


    public static final String TRADE_PAY_ID_WE_CHAT="wechat";//微信支付
    public static final String TRADE_PAY_NAME_WE_CHAT="微信支付";//微信支付

    public static final String TRADE_PAY_ID_POINT="point";//积分兑换
    public static final String TRADE_PAY_NAME_POINT="积分兑换";//积分兑换


    //支付状态
    public static final String PAY_MENT_STATUS_SUSS="succ";//成功
    public static final String PAY_MENT_STATUS_FAILED="failed";//失败
    public static final String PAY_MENT_STATUS_PAYING="paying";//支付中
    public static final String PAY_MENT_STATUS_READY="ready";//准备中

    //发送短信验证次数
    public static final String VERIFICATION_NUM = "verificationnum";
    //手机的验证码
    public static final String VERIFY_M_CODE = "VERIFY_M_CODE:";
    //设置支付密码的验证码
    public static final String PAY_M_CODE = "PAY_M_CODE:";

    public static final String USER_PAY_BY_WELFARE= "USER_PAY_BY_WELFARE";//用户使用福利钱包支付密码支付判断


    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";

    //公众号ACCESSTOKEN
    public static final String ACCESSTOKEN = "ACCESS_TOKEN";
    //小程序ACCESSTOKEN
    public static final String  APPLETACCESSTOKEN = "APPLET_ACCESS_TOKEN";

}
