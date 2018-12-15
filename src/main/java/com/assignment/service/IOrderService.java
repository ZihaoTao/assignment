package com.assignment.service;

import com.github.pagehelper.PageInfo;
import com.assignment.common.ServerResponse;
import com.assignment.vo.OrderVo;

import java.util.Date;
import java.util.Map;

/**
 * Created by tino on 10/18/18.
 */
public interface IOrderService {

    ServerResponse createOrder(Integer userId, Integer shippingId,Integer ProductId, String startTime);

    ServerResponse<String> cancel(Integer userId, Long orderNo);

    ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);

    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);
}
