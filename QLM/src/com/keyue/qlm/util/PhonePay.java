package com.keyue.qlm.util;

import java.security.MessageDigest;
import java.util.Date;

public class PhonePay {
	
	private final static String Key = "SQ2MgC3SOozF9gprkz5XBvZJLzMYDjmYRBK4hVsPbJUV8J6HjPURAjXUovuXUQSM";
    private final static String Partner = "11868";
    private final static String MyWebSite = "http://www.kyeqms.com";
    //private final static String Host = "http://api.99huafei.com/";
	
	public final static String getMD5(String s,String charset) {  
        //16进制下数字到字符的映射数�?   
       char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  
               'a', 'b', 'c', 'd', 'e', 'f' };  
       try {  
           byte[] strTemp = s.getBytes(charset);  
           MessageDigest mdTemp = MessageDigest.getInstance("MD5");  
           mdTemp.update(strTemp);  
           byte[] md = mdTemp.digest();  
           int j = md.length;  
           char str[] = new char[j * 2];  
           int k = 0;  
           for (int i = 0; i < j; i++) {  
               byte byte0 = md[i];  
               str[k++] = hexDigits[byte0 >>> 4 & 0xf];  
               str[k++] = hexDigits[byte0 & 0xf];  
           }  
           return new String(str);  
       } catch (Exception e) {
           e.printStackTrace();  
           return null;  
       }  
	}
	
	public static String getURL(String str,int value){
		Date date = new Date();
		String sign = getMD5("partner=" + Partner + "&out_trade_id="+date.getTime()+"&mobile=" + str + "&value=" + value + "&type=0&notify_url="+MyWebSite+"&"+ Key, "utf-8");
		String url = "http://api.99huafei.com/mobile/direct.aspx?partner="+Partner+"&out_trade_id="+date.getTime()+"&mobile="+str+"&value="+value+"&type=0&notify_url="+MyWebSite+"&sign="+sign+"&charset=utf-8";
		return url;
	}
	
	
	
}
