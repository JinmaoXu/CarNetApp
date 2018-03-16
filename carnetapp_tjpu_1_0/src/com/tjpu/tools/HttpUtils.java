package com.tjpu.tools;

import java.io.IOException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpUtils {
	private static 	JSONObject jsonObjects;
	private static String entityString;
	private static HttpResponse res=null;
	private static String APPKEY="3QIYhSeKPK770bpwzepo9GI1";
	private static String outputForm="json";
	//http://api.map.baidu.com/place/v2/search?ak=3QIYhSeKPK770bpwzepo9GI1&output=json&query=������ͨ��ѧ����&page_size=10&page_num=0&scope=1&region=����&mcode=F5:3E:06:3E:FC:E8:ED:19:60:2E:99:63:D8:78:85:2E:EB:12:9D:BE;com.zhongqihong.mymap
	private static String Head="http://api.map.baidu.com/place/v2/search?ak=";
	private static String IP=null;
	private static int pageSize=20;
	private static String mcode="F5:3E:06:3E:FC:E8:ED:19:60:2E:99:63:D8:78:85:2E:EB:12:9D:BE;com.zhongqihong.mymap";
	private static HttpClient clients=new DefaultHttpClient();
	@SuppressWarnings("unused")
	public static JSONObject send(String destination,List<NameValuePair> params) throws ClientProtocolException, IOException, JSONException{

		if (params==null) {
			//��ʾ����get��ʽ���󣬷���ͷ���Post��������(��Ϊget��ʽ����Ҫparams)
			IP=Head+APPKEY+"&output="+outputForm+"&query="+destination+"&page_size="+pageSize+"&page_num=0&scope=1&region=����&mcode="+mcode;
			System.out.println("IP_Address:--------->"+IP);
			HttpGet get=new HttpGet(IP);
			res=clients.execute(get);
		}else{
			HttpPost post=new HttpPost(IP);
			post.setEntity(new UrlEncodedFormEntity(params,HTTP.UTF_8));
			res=clients.execute(post);
		}
		if (res.getStatusLine().getStatusCode()==200) {
			HttpEntity entity=res.getEntity();
			entityString=EntityUtils.toString(entity,HTTP.UTF_8);
			System.out.println("httpUtils--------------->"+entityString);
			jsonObjects=new JSONObject(entityString);
		}
		return jsonObjects;
	}
	public static String getEntityString() {
		return entityString;
	}
	public static void setEntityString(String entityString) {
		HttpUtils.entityString = entityString;
	}
}
