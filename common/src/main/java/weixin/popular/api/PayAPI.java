package weixin.popular.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import weixin.popular.bean.BaseResult;
import weixin.popular.bean.Delivernotify;
import weixin.popular.bean.pay.OrderInfo;
import weixin.popular.bean.pay.Orderquery;
import weixin.popular.client.JsonResponseHandler;
import weixin.popular.util.MapUtil;
import weixin.popular.util.SignatureUtil;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class PayAPI extends BaseAPI {

	/**
	 * 发货通知
	 * @param access_token
	 * @param delivernotifyJson
	 * @return
	 */
	private BaseResult payDelivernotify(String access_token,String delivernotifyJson){
		Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
		HttpUriRequest httpUriRequest = RequestBuilder.post()
										.setHeader(header)
										.setUri(BASE_URI + "/pay/delivernotify")
										.addParameter("access_token", access_token)
										.setEntity(new StringEntity(delivernotifyJson,Charset.forName("utf-8")))
										.build();
		return localHttpClient.execute(httpUriRequest, JsonResponseHandler.createResponseHandler(BaseResult.class));
	}



	/**
	 * 标记客户的投诉处理状态
	 * @param access_token
	 * @param openid
	 * @param feedbackid
	 * @return
	 */
	public BaseResult payfeedbackUpdate(String access_token,String openid,String feedbackid){
		HttpUriRequest httpUriRequest = RequestBuilder.post()
									.setUri(BASE_URI + "/payfeedback/update")
									.addParameter("access_token", access_token)
									.addParameter("openid", openid)
									.addParameter("feedbackid", feedbackid)
									.build();
		return localHttpClient.execute(httpUriRequest, JsonResponseHandler.createResponseHandler(BaseResult.class));
	}


	/**
	 * 订单查询
	 * @param access_token
	 * @param orderqueryJson
	 * @return
	 */
	private OrderInfo payOrderquery(String access_token,String orderqueryJson){
		Header header = new BasicHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.toString());
		HttpUriRequest httpUriRequest = RequestBuilder.post()
										.setHeader(header)
										.setUri(BASE_URI + "/pay/orderquery")
										.addParameter("access_token", access_token)
										.setEntity(new StringEntity(orderqueryJson,Charset.forName("utf-8")))
										.build();
		return localHttpClient.execute(httpUriRequest, JsonResponseHandler.createResponseHandler(OrderInfo.class));
	}


	/**
	 * 发货通知
	 * @param access_token
	 * @param delivernotify
	 * @param paySignKey
	 * @return
	 */
	public BaseResult payDelivernotify(String access_token,Delivernotify delivernotify,String paySignKey){
		Map<String, String> map = MapUtil.objectToMap(delivernotify);
		String app_signature = SignatureUtil.generatePaySign(map, paySignKey);
		map.put("app_signature",app_signature);
		map.put("sign_method", "sha1");
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return payDelivernotify(access_token,objectMapper.writeValueAsString(map));
		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
		return null;
	}


	/**
	 * 订单查询
	 * @param access_token
	 * @param orderquery
	 * @param paySignKey
	 * @return
	 */
	public OrderInfo payOrderquery(String access_token,Orderquery orderquery,String paySignKey){
		//builder package  start
		Map<String, String> tmap = new LinkedHashMap<String, String>();
		tmap.put("out_trade_no", orderquery.getOut_trade_no());
		tmap.put("partner", orderquery.getPartner());
		String packAge = SignatureUtil.generatePackage(tmap, orderquery.getPartner_key());
		//builder package  end

		//builder app_signature start
		Map<String, String> map = new HashMap<String, String>();
		map.put("appid", orderquery.getAppid());
		map.put("package",packAge);
		map.put("timestamp",orderquery.getTimestamp());
		String app_signature = SignatureUtil.generatePaySign(map, paySignKey);
		//builder app_signature end

		map.put("app_signature",app_signature);
		map.put("sign_method", "sha1");
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return payOrderquery(access_token,objectMapper.writeValueAsString(map));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
