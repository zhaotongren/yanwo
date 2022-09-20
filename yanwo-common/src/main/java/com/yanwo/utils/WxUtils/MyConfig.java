package com.yanwo.utils.WxUtils;

import java.io.InputStream;

public class MyConfig extends  WXPayConfig {
    String getAppID() {
        return "wx1c7cabae6cc5d576";
    }

    String getMchID() {
        return "1491643762";
    }

    String getKey() {
        return "jhihrhghjhghjhgfderg5213ljd854kl";
    }

    InputStream getCertStream() {
        return null;
    }

    IWXPayDomain getWXPayDomain() {
        return null;
    }
}
