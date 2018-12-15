package com.assignment.service;

import com.github.pagehelper.PageInfo;
import com.assignment.common.ServerResponse;
import com.assignment.pojo.Shipping;

/**
 * Created by tino on 10/17/18.
 */

public interface IShippingService {

    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse<String> del(Integer userId, Integer shippingId);

    ServerResponse update(Integer userId, Shipping shipping);

    ServerResponse<Shipping> select(Integer userId, Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);

}
