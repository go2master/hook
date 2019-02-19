package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;

public class HttpUtils {
	private static final int SUCCESS_CODE = 200;
	public static final String HTTP_SERVER = "http://192.168.101.69:8090/server2";

	public static String executePOST(String data) {
		String result = null;
		BufferedReader reader = null;
		HttpClient client = new DefaultHttpClient();
		HttpPost req = new HttpPost();
		try {
			req.setURI(new URI(HTTP_SERVER));
			req.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
					HttpVersion.HTTP_1_1);
			StringEntity entity = new StringEntity(data, "utf-8");
			req.setEntity(entity);

			HttpResponse response = client.execute(req);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
			StringBuffer stringBuf = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				stringBuf.append(line);
			}
			result = stringBuf.toString();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	public static Object sendGet(String url,
			List<NameValuePair> nameValuePairList) throws Exception {
		JSONObject jsonObject = null;
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		try {
			client = HttpClients.createDefault();
			URIBuilder uriBuilder = new URIBuilder(url);
			uriBuilder.addParameters(nameValuePairList);
			HttpGet httpGet = new HttpGet(uriBuilder.build());
			httpGet.setHeader(new BasicHeader("Content-Type",
					"application/x-www-form-urlencoded; charset=utf-8"));
			httpGet.setHeader(new BasicHeader("Accept",
					"text/plain;charset=utf-8"));
			/**
			 * 请求服务
			 */
			response = client.execute(httpGet);
			/**
			 * 获取响应吗
			 */
			int statusCode = response.getStatusLine().getStatusCode();

			if (SUCCESS_CODE == statusCode) {
				/**
				 * 获取返回对象
				 */
				HttpEntity entity = response.getEntity();
				/**
				 * 通过EntityUitls获取返回内容
				 */
				String result = EntityUtils.toString(entity, "UTF-8");
				/**
				 * 转换成json,根据合法性返回json或者字符串
				 */
				try {
					jsonObject = JSONObject.parseObject(result);
					return jsonObject;
				} catch (Exception e) {
					return result;
				}
			} else {
			}
		} catch (Exception e) {
		} finally {
			response.close();
			client.close();
		}
		return null;
	}

	/**
	 * 发送POST请求
	 * 
	 * @param url
	 * @param nameValuePairList
	 * @return JSON或者字符串
	 * @throws Exception
	 */
	public static Object sendPost(String url,
			List<NameValuePair> nameValuePairList) throws Exception {
		JSONObject jsonObject = null;
		CloseableHttpClient client = null;
		CloseableHttpResponse response = null;
		try {
			/**
			 * 创建一个httpclient对象
			 */
			client = HttpClients.createDefault();
			/**
			 * 创建一个post对象
			 */
			HttpPost post = new HttpPost(url);
			/**
			 * 包装成一个Entity对象
			 */
			StringEntity entity = new UrlEncodedFormEntity(nameValuePairList,
					"UTF-8");
			/**
			 * 设置请求的内容
			 */
			post.setEntity(entity);
			/**
			 * 设置请求的报文头部的编码
			 */
			post.setHeader(new BasicHeader("Content-Type",
					"application/json; charset=utf-8"));
			/**
			 * 设置请求的报文头部的编码
			 */
			post.setHeader(new BasicHeader("Accept", "text/plain;charset=utf-8"));
			/**
			 * 执行post请求
			 */
			response = client.execute(post);
			/**
			 * 获取响应码
			 */
			int statusCode = response.getStatusLine().getStatusCode();
			if (SUCCESS_CODE == statusCode) {
				/**
				 * 通过EntityUitls获取返回内容
				 */
				String result = EntityUtils.toString(response.getEntity(),
						"UTF-8");
				/**
				 * 转换成json,根据合法性返回json或者字符串
				 */
				try {
					jsonObject = JSONObject.parseObject(result);
					return jsonObject;
				} catch (Exception e) {
					return result;
				}
			} else {
			}
		} catch (Exception e) {
		} finally {
			response.close();
			client.close();
		}
		return null;
	}

	/**
	 * 组织请求参数{参数名和参数值下标保持一致}
	 * 
	 * @param params
	 *            参数名数组
	 * @param values
	 *            参数值数组
	 * @return 参数对象
	 */
	public static List<NameValuePair> getParams(Object[] params, Object[] values) {
		/**
		 * 校验参数合法性
		 */
		boolean flag = params.length > 0 && values.length > 0
				&& params.length == values.length;
		if (flag) {
			List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();
			for (int i = 0; i < params.length; i++) {
				nameValuePairList.add(new BasicNameValuePair(params[i]
						.toString(), values[i].toString()));
			}
			return nameValuePairList;
		} else {
		}
		return null;
	}

	public static JSONObject toJson(String json) {
		if (json == null || json.length() <= 0) {
			return null;
		}
		return JSONObject.parseObject(json);
	}

	public static void main(String[] args) {
		String data = "{'pay':20,'p':30}";
		JSONObject jsonObject = toJson(data);
		List<NameValuePair> list = getParams(jsonObject.keySet().toArray(),
				jsonObject.values().toArray());
		System.out.println(jsonObject);
		System.out.println(list.toString());
	}
}