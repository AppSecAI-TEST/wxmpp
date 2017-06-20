package com.baodanyun.websocket.service;

import org.springframework.stereotype.Service;

/**
 * Created by liaowuhen on 2016/11/11.
 */
@Service
public interface UserCacheServer {

   /* AbstractUser getVisitorByUidOrOpenID(String to);

    AbstractUser saveVisitorByUidOrOpenID(String to, AbstractUser user);
    *//**
     * 维护发送消息目的地
     *//*
    boolean addVisitorCustomerOpenId(String openId, String to) throws BusinessException;


    *//**
     * 获取发送地址
     *//*

    String getCustomerIdByVisitorOpenId(String openId) throws BusinessException;

    AbstractUser getCustomerByVisitorOpenId(String openId) throws BusinessException;

    Map<String, AbstractUser> getVisitors();

    Map<String, AbstractUser> getCustomers();

    AbstractUser getUser(String userId);


    Customer getUserCustomer(String userId);

    Visitor getUserVisitor(String userId);

    boolean add(String key, String id, AbstractUser visitorUser);

    boolean addCid(String key, String id, AbstractUser visitorUser);

    boolean add(String key, AbstractUser visitorUser);

    boolean addOpenId(String key, AbstractUser visitorUser);

    Set<AbstractUser> get(String key, String cid);

    Object get(String key);

    void delete(String key, String cid, AbstractUser visitorUser);*/


}
