package com.assignment.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import java.util.Calendar;
import com.google.common.collect.Maps;
import com.assignment.common.Const;
import com.assignment.common.ServerResponse;
import com.assignment.dao.*;
import com.assignment.pojo.*;
import com.assignment.service.IOrderService;
import com.assignment.util.BigDecimalUtil;
import com.assignment.util.DateTimeUtil;
import com.assignment.util.PropertiesUtil;
import com.assignment.vo.OrderItemVo;
import com.assignment.vo.OrderProductVo;
import com.assignment.vo.OrderVo;
import com.assignment.vo.ShippingVo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by tino on 10/18/18.
 */
@Service("iOrderService")
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ShippingMapper shippingMapper;

    @Autowired
    private ProductMapper productMapper;


    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    public ServerResponse createOrder(Integer userId, Integer shippingId, Integer ProductId, Integer dapperId, String start) {
        Date startTime = DateTimeUtil.strToDate(start);
        Date endTime;
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(startTime);
        if(ProductId == 1) {
            rightNow.add(Calendar.MINUTE,45);
            endTime = rightNow.getTime();
        } else if(ProductId == 2) {
            rightNow.add(Calendar.MINUTE,30);
            endTime = rightNow.getTime();
        } else {
            rightNow.add(Calendar.HOUR,1);
            endTime = rightNow.getTime();
        }

        if(orderMapper.checkTime(startTime, endTime, dapperId) != 0) {
            return ServerResponse.createByErrorMessage("The dapper you choose has been assigned at this period of time. Choose another time or another dapper.");
        }

        ServerResponse serverResponse = this.getCartOrderItem(userId, ProductId);
        if(!serverResponse.isSuccess()) {
            return serverResponse;
        }

        List<OrderItem> orderItemList = (List<OrderItem>) serverResponse.getData();
        BigDecimal payment = this.getOrderTotalPrice(orderItemList);

        Order order = this.assembleOrder(userId, shippingId, payment, dapperId, startTime, endTime);
        if(order == null) {
            return ServerResponse.createByErrorMessage("Cannot create order");
        }
        if(CollectionUtils.isEmpty(orderItemList)) {
            return ServerResponse.createByErrorMessage("No product chosen");
        }
        for(OrderItem orderItem : orderItemList) {
            orderItem.setOrderNo(order.getOrderNo());
        }
        // mybatis: batch INSERTS
        orderItemMapper.batchInsert(orderItemList);
        // Create order successfully, need to decrease stock
        //return info to client
        OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
        return ServerResponse.createBySuccess(orderVo);
    }

    private ServerResponse getCartOrderItem(Integer userId, Integer ProductId) {
        List<OrderItem> orderItemList = Lists.newArrayList();
        OrderItem orderItem = new OrderItem();
        Product product = productMapper.selectByPrimaryKey(ProductId);
        if(Const.ProductStatusEnum.ON_SALE.getCode() != product.getStatus()) {
            return ServerResponse.createByErrorMessage(product.getName() + " is not on sale");
        }
        orderItem.setUserId(userId);
        orderItem.setProductId(product.getId());
        orderItem.setProductName(product.getName());
        orderItem.setTotalPrice(product.getPrice());
        orderItemList.add(orderItem);

        return ServerResponse.createBySuccess(orderItemList);
    }

    private OrderVo assembleOrderVo(Order order, List<OrderItem> orderItemList) {
        OrderVo orderVo = new OrderVo();
        orderVo.setOrderNo(order.getOrderNo());
        orderVo.setPayment(order.getPayment());
        orderVo.setStatus(order.getStatus());
        orderVo.setStatusDesc(Const.OrderStatusEnum.codeOf(order.getStatus()).getValue());
        orderVo.setShippingId(order.getShippingId());
        orderVo.setDapperId(order.getDapperId());
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        if(shipping != null) {
            orderVo.setReceiverName(shipping.getReceiverName());
            orderVo.setShippingVo(this.assembleShippingVo(shipping));
        }
        orderVo.setPaymentTime(DateTimeUtil.dateToStr(order.getPaymentTime()));
        orderVo.setStartTime(DateTimeUtil.dateToStr(order.getStartTime()));
        orderVo.setEndTime(DateTimeUtil.dateToStr(order.getEndTime()));
        orderVo.setCreateTime(DateTimeUtil.dateToStr(order.getCreateTime()));
        orderVo.setCloseTime(DateTimeUtil.dateToStr(order.getCloseTime()));
        List<OrderItemVo> orderItemVoList = Lists.newArrayList();
        for(OrderItem orderItem : orderItemList) {
            OrderItemVo orderItemVo = this.assembleOrderItemVo(orderItem);
            orderItemVoList.add(orderItemVo);
        }
        orderVo.setOrderItemVoList(orderItemVoList);
        return orderVo;
    }

    private OrderItemVo assembleOrderItemVo(OrderItem orderItem) {
        OrderItemVo orderItemVo = new OrderItemVo();
        orderItemVo.setOrderNo(orderItem.getOrderNo());
        orderItemVo.setProductId(orderItem.getProductId());
        orderItemVo.setProductName(orderItem.getProductName());
        orderItemVo.setTotalPrice(orderItem.getTotalPrice());
        orderItemVo.setCreateTime(DateTimeUtil.dateToStr(orderItem.getCreateTime()));
        return orderItemVo;
    }

    private ShippingVo assembleShippingVo(Shipping shipping) {
        ShippingVo shippingVo = new ShippingVo();
        shippingVo.setReceiverName(shipping.getReceiverName());
        shippingVo.setReceiverAddress(shipping.getReceiverAddress());
        shippingVo.setReceiverState(shipping.getReceiverState());
        shippingVo.setReceiverCity(shipping.getReceiverCity());
        shippingVo.setReceiverDistrict(shipping.getReceiverDistrict());
        shippingVo.setReceiverMobile(shipping.getReceiverMobile());
        shippingVo.setReceiverPhone(shipping.getReceiverPhone());
        shippingVo.setReceiverZip(shipping.getReceiverZip());
        return shippingVo;
    }


    private Order assembleOrder(Integer userId, Integer shippingId, BigDecimal payment,Integer dapperId, Date startTime, Date endTime) {
        long orderNo = this.generateOrderNo();
        Order order = new Order();
        order.setUserId(userId);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setOrderNo(orderNo);
        order.setStatus(Const.OrderStatusEnum.NO_PAY.getCode());
        order.setStartTime(startTime);
        order.setEndTime(endTime);
        order.setDapperId(dapperId);
        // shipping out time, ect. Will be modified when is shipped out
        int rowCount = orderMapper.insert(order);
        if(rowCount > 0) {
            return order;
        }
        return null;
    }

    private long generateOrderNo() {
        long currTime = System.currentTimeMillis(); // not a good idea, need to change. Can let rival know important info.
        return currTime + new Random().nextInt(100);
    }

    private BigDecimal getOrderTotalPrice(List<OrderItem> orderItemList) {
        BigDecimal payment = new BigDecimal("0");
        for(OrderItem orderItem : orderItemList) {
            payment = BigDecimalUtil.add(payment.doubleValue(), orderItem.getTotalPrice().doubleValue());
        }
        return payment;
    }




    public ServerResponse<String> cancel(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if(order == null) {
            return ServerResponse.createByErrorMessage("The user's order does not exist");
        }
        if(order.getStatus() != Const.OrderStatusEnum.NO_PAY.getCode()) {
            return ServerResponse.createByErrorMessage("Paid, can not cancel order");
        }
        Order updateOrder = new Order();
        updateOrder.setId(order.getId());
        updateOrder.setStatus(Const.OrderStatusEnum.CANCELED.getCode());
        int row = orderMapper.updateByPrimaryKeySelective(updateOrder);
        if(row > 0) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByError();
    }


    public ServerResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo) {
        Order order = orderMapper.selectByUserIdAndOrderNo(userId, orderNo);
        if(order != null) {
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNoUserId(orderNo, userId);
            OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("Cannot find the order");
    }

    public ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectByUserId(userId);
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, userId);
        PageInfo pageResult = new PageInfo(orderList);
        pageResult.setList(orderVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    private List<OrderVo> assembleOrderVoList(List<Order> orderList,Integer userId){
        List<OrderVo> orderVoList = Lists.newArrayList();
        for(Order order : orderList){
            List<OrderItem>  orderItemList = Lists.newArrayList();
            if(userId == null){
                orderItemList = orderItemMapper.getByOrderNo(order.getOrderNo());
            }else{
                orderItemList = orderItemMapper.getByOrderNoUserId(order.getOrderNo(),userId);
            }
            OrderVo orderVo = assembleOrderVo(order,orderItemList);
            orderVoList.add(orderVo);
        }
        return orderVoList;
    }

    // backend

    public ServerResponse<PageInfo> manageList(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Order> orderList = orderMapper.selectAllOrder();
        // admin does not need userId
        List<OrderVo> orderVoList = this.assembleOrderVoList(orderList, null);
        PageInfo resultPage = new PageInfo(orderList);
        resultPage.setList(orderVoList);
        return ServerResponse.createBySuccess(resultPage);
    }

    public ServerResponse<OrderVo> manageDetail(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order != null) {
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo = this.assembleOrderVo(order, orderItemList);
            return ServerResponse.createBySuccess(orderVo);
        }
        return ServerResponse.createByErrorMessage("Order does not exist");
    }

    public ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order != null) {
            List<OrderItem> orderItemList = orderItemMapper.getByOrderNo(orderNo);
            OrderVo orderVo = this.assembleOrderVo(order, orderItemList);

            PageInfo resultPage = new PageInfo(Lists.newArrayList(order));
            resultPage.setList(Lists.newArrayList(orderVo));
            return ServerResponse.createBySuccess(resultPage);
        }
        return ServerResponse.createByErrorMessage("Order does not exist");
    }

    public ServerResponse<String> manageSendGoods(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if(order != null) {
            order.setStatus(Const.OrderStatusEnum.ORDER_SUCCESS.getCode());
            orderMapper.updateByPrimaryKeySelective(order);
            return ServerResponse.createBySuccess("Complete successfully");
        }
        return ServerResponse.createByErrorMessage("Order does not exist");
    }

}
