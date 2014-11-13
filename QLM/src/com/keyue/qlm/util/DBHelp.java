package com.keyue.qlm.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class DBHelp {
	private static String url = "http://211.149.204.5/httpservices/";

	public static List<Object[]> selsql(String sql) {

		/*
		 * try { SoapObject soapObject = new SoapObject(NAMESPACE, "getobjets");
		 * soapObject.addProperty("sql",sql);
		 * soapObject.addProperty("params",null); SoapSerializationEnvelope
		 * envelope = new SoapSerializationEnvelope( SoapEnvelope.VER11);
		 * envelope.dotNet = false; envelope.setOutputSoapObject(soapObject);
		 * HttpTransportSE transport = new HttpTransportSE(url);
		 * transport.call(NAMESPACE+"getobjets", envelope); SoapObject result =
		 * (SoapObject)envelope.bodyIn; String
		 * json=(String)result.getProperty(0).toString(); Gson gson = new
		 * Gson(); List<Object[]> list = gson.fromJson(json, new
		 * TypeToken<List<Object[]>>(){}.getType()); return list; } catch
		 * (Exception e) { return null; }
		 */
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url + "ConnDBfind");
		List<Object[]> list = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sql", sql));
		httpPost.getParams().setParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, 10 * 1000);
		Gson gson = new Gson();
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result = EntityUtils.toString(response.getEntity(),
						"utf-8");
				list = gson.fromJson(result, new TypeToken<List<Object[]>>() {}.getType());
				for(int i=0;i<list.size();i++){
					for(int j=0;j<list.get(i).length;j++){
						try {
							list.get(i)[j].toString();
						} catch (Exception e) {
							list.get(i)[j]=0;
						}
					}
				}
				
			}
		} catch (Exception e) {
			list = null;
		}
		return list;

	}

	public static int savesql(String sql) {

		/*
		 * try { SoapObject soapObject = new SoapObject(NAMESPACE,
		 * "saveorupdat"); soapObject.addProperty("sql", sql);
		 * soapObject.addProperty("params", null); SoapSerializationEnvelope
		 * envelope = new SoapSerializationEnvelope( SoapEnvelope.VER11);
		 * envelope.dotNet = false; envelope.setOutputSoapObject(soapObject);
		 * HttpTransportSE transport = new HttpTransportSE(url);
		 * transport.call(NAMESPACE + "saveorupdat", envelope); SoapObject
		 * result = (SoapObject) envelope.bodyIn; String query = (String)
		 * result.getProperty(0).toString(); return Integer.parseInt(query); }
		 * catch (Exception e) { return -1; }
		 */
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url + "ConnDBsaveorupdate");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("sql", sql));
		httpPost.getParams().setParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, 10 * 1000);
		int result = -1;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result1 = EntityUtils.toString(response.getEntity(),
						"utf-8");
				result = Integer.parseInt(result1);
			}
		} catch (Exception e) {
			result = -1;
		}
		return result;
	}

	public static int adduser(HashMap<String, Object> hashMap,int status) {

	
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url + "ConnDBaddUser");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("username", hashMap.get("username").toString()));
		params.add(new BasicNameValuePair("userpassword", hashMap.get("userpassword").toString()));
		params.add(new BasicNameValuePair("userphone", hashMap.get("userphone").toString()));
		params.add(new BasicNameValuePair("userwd", hashMap.get("userwd").toString()));
		params.add(new BasicNameValuePair("userjd", hashMap.get("userjd").toString()));
		params.add(new BasicNameValuePair("useraddress", hashMap.get("useraddress").toString()));
		params.add(new BasicNameValuePair("qqnumber", hashMap.get("qqnumber").toString()));
		params.add(new BasicNameValuePair("status", status+""));
		httpPost.getParams().setParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, 10 * 1000);
		int result = -1;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result1 = EntityUtils.toString(response.getEntity(),
						"utf-8");
					result = Integer.parseInt(result1);
			}
		} catch (Exception e) {
			result = -1;
		}
		return result;
	}
	public static int sendEmail(HashMap<String, Object> hashMap) {
		HttpClient client = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url + "ConnDBsendEmail");
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("qqnumber", hashMap.get("qqnumber").toString()));
		params.add(new BasicNameValuePair("username", hashMap.get("username").toString()));
		params.add(new BasicNameValuePair("userpassword", hashMap.get("userpassword").toString()));
		params.add(new BasicNameValuePair("userphone", hashMap.get("userphone").toString()));
		httpPost.getParams().setParameter(
				HttpConnectionParams.CONNECTION_TIMEOUT, 10 * 1000);
		int result = -1;
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			HttpResponse response = client.execute(httpPost);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String result1 = EntityUtils.toString(response.getEntity(),
						"utf-8");
					result = Integer.parseInt(result1);
			}
		} catch (Exception e) {
			result = -1;
		}
		return result;
	}
}
