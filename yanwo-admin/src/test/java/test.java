import com.aliyun.opensearch.sdk.dependencies.org.apache.http.HttpResponse;
import com.aliyun.opensearch.sdk.dependencies.org.apache.http.client.HttpClient;
import com.aliyun.opensearch.sdk.dependencies.org.apache.http.client.methods.HttpGet;
import com.aliyun.opensearch.sdk.dependencies.org.apache.http.impl.client.DefaultHttpClient;
import com.aliyun.opensearch.sdk.dependencies.org.apache.http.util.EntityUtils;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.yanwo.Constant.Constants;
import com.yanwo.encrypt.BCrypt;
import com.yanwo.entity.SysindexAdEntity;
import com.yanwo.modules.utils.HttpUtils;
import com.yanwo.utils.DateUtils;
import com.yanwo.utils.JsonUtils;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class test {
    @Test
    public void test1() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.MILLISECOND, 0);
        int stime = (int) (cal.getTimeInMillis() / 1000L);
        System.out.println(stime);
        cal.add(Calendar.YEAR, 1);
        int etime = (int) (cal.getTimeInMillis() / 1000L);
        System.out.println(etime);
        cal.add(Calendar.DATE, 1);
        int gtime = (int) (cal.getTimeInMillis() / 1000L);
        System.out.println(gtime);
    }

    @Test
    public void validateCard() {
        try {
            String host = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo="
                    + "6217000010066006456" + "&cardBinCheck=true";
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet(host);
            HttpResponse response = httpClient.execute(request);
            String result = EntityUtils.toString(response.getEntity());
            System.out.println("检验银行卡号是否合法返回结果:" + result);
            JSONObject jsonObject = JSONObject.fromObject(result);

            if ((Boolean) jsonObject.get("validated")) {
                System.out.println("---------------");
            }
            System.out.println();
        } catch (Exception e) {
            System.out.println("检验银行卡号是否合法异常:" + e.getMessage());
        }
    }

    @Test
    public void getAppletAccessToken() {
        String message = HttpUtils.sendGet("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="
                + "wx7a855d5b2991ca2e" + "&secret=" + "f092b15b5df2cc45f5c545f4fd35c655");
        JSONObject demoJson = JSONObject.fromObject(message);
        System.out.println(demoJson);
        String accessToken = demoJson.getString("access_token");
        String expiresIn = demoJson.getString("expires_in");
        System.out.println("过期时间："+expiresIn);
        Map map = new HashMap();
        List list = new ArrayList();
        Map map1 = new HashMap();
        map1.put("type", "miniprogram");
        map1.put("name", "商城首页");
        map1.put("url", "https://www.bdyjs718.cn");
        map1.put("appid", "wx7a855d5b2991ca2e");
        map1.put("pagepath", "pages/home/index/index");
        list.add(map1);
        map.put("button", list);
        System.out.println(accessToken);
        String s1 = HttpUtils.sendGet("https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=" + accessToken);
        System.out.println("自定义菜单："+s1);
        System.out.println(JsonUtils.objectToJson(map));
        String s = HttpUtils.sendPost("https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=" + accessToken, JsonUtils.objectToJson(map));
        System.out.println(s);
    }
}
