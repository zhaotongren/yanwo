package com.yanwo.entity;

import lombok.Data;


@Data
public class MiaoshaOrderEntity {
	private String tid;
	private Integer userId;
	private Integer itemId;
	private Integer skuId;
	private Long seckillId;
	private String paymentId;
}
