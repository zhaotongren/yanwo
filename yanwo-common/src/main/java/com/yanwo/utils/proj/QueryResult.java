package com.yanwo.utils.proj;

import java.util.List;

public class QueryResult {

	private List<?> result;
	private Long totalPage;
	private int currPage;
	private Long totalNum;

	public Long getTotalNum() {
		return totalNum;
	}
	public void setTotalNum(Long totalNum) {
		this.totalNum = totalNum;
	}
	public List<?> getResult() {
		return result;
	}
	public int getCurrPage() {
		return currPage;
	}
	public void setCurrPage(int currPage) {
		this.currPage = currPage;
	}
	public void setResult(List<?> result) {
		this.result = result;
	}
	public Long getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(Long totalPage) {
		this.totalPage = totalPage;
	}


}