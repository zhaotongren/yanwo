package com.yanwo.utils.WxUtils;
import java.io.Serializable;

/************************************************
 * 支付ViewBean
 ************************************************/

public class PayOrder implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String productId;// 商品ID
	private String subject;//订单名称 
	private String body;// 商品描述
	private String totalAmount;// 总金额(单位是分)
	private String outTradeNo;// 订单号(唯一)
	private String spbillCreateIp;// 发起人IP地址
	private String attach = "1";// 附件数据主要用于商户携带订单的自定义数据
	private Short payType;// 支付类型(1:支付宝 2:微信 3:银联)
	private Short payWay;// 支付方式 (1：PC,平板 2：手机)
	private String frontUrl;// 前台回调地址  非扫码支付使用
	private String txnTime;//订单发送时间
	private String tradeNo;
	private String outRequestNo; //外部请求号（阿里）
	
	/**微信使用**/
	private String tradeType;
	private String queryId; //银联退款时使用
	private String refundFee; //退款金额
	private String outRefundNo; //退款号（微信）



	private String openid;
	
	private String requestSN; //请求编号
	private String appid;

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public PayOrder() {
		super();
	}
	
	
	
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	
	public String getTotalAmount() {
		return totalAmount;
	}


	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}


	public String getOutTradeNo() {
		return outTradeNo;
	}
	public void setOutTradeNo(String outTradeNo) {
		this.outTradeNo = outTradeNo;
	}
	public String getSpbillCreateIp() {
		return spbillCreateIp;
	}
	public void setSpbillCreateIp(String spbillCreateIp) {
		this.spbillCreateIp = spbillCreateIp;
	}
	public String getAttach() {
		return attach;
	}
	public void setAttach(String attach) {
		this.attach = attach;
	}
	public Short getPayType() {
		return payType;
	}
	public void setPayType(Short payType) {
		this.payType = payType;
	}
	public Short getPayWay() {
		return payWay;
	}
	public void setPayWay(Short payWay) {
		this.payWay = payWay;
	}
	public String getFrontUrl() {
		return frontUrl;
	}
	public void setFrontUrl(String frontUrl) {
		this.frontUrl = frontUrl;
	}
	public String getTxnTime() {
		return txnTime;
	}
	public void setTxnTime(String txnTime) {
		this.txnTime = txnTime;
	}
	public String getTradeNo() {
		return tradeNo;
	}
	public void setTradeNo(String tradeNo) {
		this.tradeNo = tradeNo;
	}


	public String getTradeType() {
		return tradeType;
	}


	public void setTradeType(String tradeType) {
		this.tradeType = tradeType;
	}
	
	
	public String getQueryId() {
		return queryId;
	}


	public void setQueryId(String queryId) {
		this.queryId = queryId;
	}
	
	


	public String getOutRequestNo() {
		return outRequestNo;
	}



	public void setOutRequestNo(String outRequestNo) {
		this.outRequestNo = outRequestNo;
	}

	

	public String getRefundFee() {
		return refundFee;
	}



	public void setRefundFee(String refundFee) {
		this.refundFee = refundFee;
	}



	public String getOutRefundNo() {
		return outRefundNo;
	}



	public void setOutRefundNo(String outRefundNo) {
		this.outRefundNo = outRefundNo;
	}

	
	

	public String getRequestSN() {
		return requestSN;
	}



	public void setRequestSN(String requestSN) {
		this.requestSN = requestSN;
	}


	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}
	@Override
	public String toString() {
		return "PayOrder [subject=" + subject + ", body=" + body
				+ ", totalAmount=" + totalAmount + ", outTradeNo=" + outTradeNo
				+ ", txnTime=" + txnTime + "]";
	}


	
	
	
	
}
