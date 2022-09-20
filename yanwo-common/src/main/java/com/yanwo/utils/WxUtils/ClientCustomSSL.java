package com.yanwo.utils.WxUtils;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;

/**
 * 退款认证
 *
 */
public class ClientCustomSSL {
	 public static String doRefund(String url,String data,String cert_path,String mch_id) throws Exception {
        /** 
         s  * 注意PKCS12证书 是从微信商户平台-》账户设置-》 API安全 中下载的 
         */  
        KeyStore keyStore  = KeyStore.getInstance("PKCS12");  
        File certfile = ResourceUtils.getFile("file:"+ cert_path);
        FileInputStream instream = new FileInputStream(certfile);
        try {  
            keyStore.load(instream, mch_id.toCharArray());
        } finally {  
            instream.close();  
        }  
        SSLContext sslcontext = SSLContexts.custom()
                .loadKeyMaterial(keyStore, mch_id.toCharArray())
                .build();  
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,  
                new String[] { "TLSv1" },  
                null,  
                SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)  
                .build();
		try {
			HttpPost httpost = new HttpPost(url);
			httpost.setEntity(new StringEntity(data, "UTF-8"));
			CloseableHttpResponse response = httpclient.execute(httpost);
			try {
				HttpEntity entity = response.getEntity();
				String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
				EntityUtils.consume(entity);
				return jsonStr;
			} finally {
				response.close();
			}
		} finally {
			httpclient.close();
		}
	}


	public static String postData(String urlStr, String data)throws Exception {
		/**
		 s  * 注意PKCS12证书 是从微信商户平台-》账户设置-》 API安全 中下载的
		 */
		KeyStore keyStore  = KeyStore.getInstance("PKCS12");
		File certfile = ResourceUtils.getFile("file:"+ ConfigUtil.CERT_PATH);
		FileInputStream instream = new FileInputStream(certfile);
		try {
			keyStore.load(instream, ConfigUtil.MCH_ID.toCharArray());
		} finally {
			instream.close();
		}
		SSLContext sslcontext = SSLContexts.custom()
				.loadKeyMaterial(keyStore, ConfigUtil.MCH_ID.toCharArray())
				.build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
				sslcontext,
				new String[] { "TLSv1" },
				null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		CloseableHttpClient httpclient = HttpClients.custom()
				.setSSLSocketFactory(sslsf)
				.build();
		BufferedReader reader = null;
		try {
			URL url = new URL(urlStr);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			conn.setConnectTimeout( 5000);
			conn.setReadTimeout( 5000);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			if (data == null) {
				data = "";
			}
			writer.write(data);
			writer.flush();
			writer.close();

			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append("\r\n");
			}
			return sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
			}
		}
		return null;
	}





}
