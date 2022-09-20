package com.yanwo.utils.WxUtils;

/******************************************
 *返回结果:
 *   支付宝调用返回结构 ResultBean
 ******************************************/

public class ResEntity {

	private String code;    //返回结果 Constants.SUCCESS(success)、 Constants.FAIL(fail)、
    private String message; //提示信息
    private Object content; //返回数据内容

    
    public ResEntity() {
		// TODO Auto-generated constructor stub
	}
    
    public ResEntity(String code, String message, Object content) {
        this.code = code;
        this.message = message;
        this.content = content;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }
}
