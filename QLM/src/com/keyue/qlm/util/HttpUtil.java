package com.keyue.qlm.util;

import java.io.IOException;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class HttpUtil {

	private static HttpUtil httpUtil;
	
	public HttpUtil(){};
	
	public static HttpUtil getDefaultHttpUtil(){
		if(null==httpUtil){
			httpUtil=new HttpUtil();
		}
		return httpUtil;
	}
	
	public int phonePay(String phoneNumber,int price){
		int flag=-1;
		try {
			HttpClient client = new  DefaultHttpClient();
			HttpGet get = new HttpGet(PhonePay.getURL(phoneNumber, price));
			get.getParams().setParameter(
					HttpConnectionParams.CONNECTION_TIMEOUT, 10 * 1000);
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(response.getEntity(),
						"utf-8");
				if(result.substring(result.indexOf("<result>"),result.indexOf("<result>")+11).indexOf("0")>0){
					flag=0;
				}else if(result.substring(result.indexOf("<result>"),result.indexOf("<result>")+11).indexOf("7")>0){
					flag=7;
				}
				
			}
		} catch (Exception e) {
			flag= -1;
		} 
		return flag;
	}
	
	public int sendMsg(String phoneNumber,String msgcontent){
		int flag=-1;
		try {
			HttpClient client = new  DefaultHttpClient();
			HttpGet get = new HttpGet("http://106.ihuyi.com/webservice/sms.php?method=Submit&account=cf_kyrj&password=kyeqms330708&mobile="+phoneNumber+"&content="+URLEncoder.encode(msgcontent, "UTF-8"));
			
			get.getParams().setParameter(
					HttpConnectionParams.CONNECTION_TIMEOUT, 10 * 1000);
			HttpResponse response = client.execute(get);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(response.getEntity(),
						"utf-8");
				System.out.println(result);
				Document doc = DocumentHelper.parseText(result); 
				Element root = doc.getRootElement();
				String code = root.elementText("code");	
				if(code.equals("2")){
					flag=1;
				}
			}
		} catch (Exception e) {
			flag= -1;
		} 
		return flag;
	}
	
}
