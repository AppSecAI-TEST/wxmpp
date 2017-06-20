package com.baodanyun.websocket.service.impl;

import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.service.CacheService;
import com.baodanyun.websocket.service.UserCacheServer;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by liaowuhen on 2016/11/11.
 */
@Service
public class UserCacheServerImpl implements UserCacheServer {
    private static final Map<String, String> dest = new ConcurrentHashMap<>();

    // 第三方接入用户的对应关系
    private static final Map<String, AbstractUser> users = new ConcurrentHashMap<>();

    // 当前在线客服
    private static final Map<String, AbstractUser> customers = new ConcurrentHashMap<>();

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CacheService cacheService;

    /**
     * 维护发送消息目的地
     */
    @Override
    public boolean addVisitorCustomerOpenId(String openId, String to) throws BusinessException {

        if (StringUtils.isEmpty(openId)) {
            throw new BusinessException("openId is null");
        }

        if (StringUtils.isEmpty(to)) {
            throw new BusinessException("to is null");
        }

        dest.put(openId, to);

        Map map = (Map) cacheService.get(openId);
        if (null == map) {
            map = new HashMap();
        }

        map.put(openId, to);

        return cacheService.setOneMonth(openId, map);
    }

    /**
     * 获取发送地址
     */
    @Override
    public String getCustomerIdByVisitorOpenId(String openId) throws BusinessException {
        if (StringUtils.isEmpty(openId)) {
            throw new BusinessException("openId is null");
        }

        if (null != dest.get(openId)) {
            return dest.get(openId);
        } else {
            Map map = (Map) cacheService.get(openId);
            if (null != map) {
                return (String) map.get(openId);
            }
        }
        return null;
    }

    @Override
    public void addCustomer(AbstractUser abstractUser) throws BusinessException {
        customers.put(abstractUser.getId(), abstractUser);
    }

    @Override
    public AbstractUser getCustomer(String id) throws BusinessException {
        return customers.get(id);
    }

    /**
     * 获取发送地址
     */

    /*

    @Override
    public AbstractUser getVisitorByUidOrOpenID(String to) {
        return users.get(to);
    }

    @Override
    public AbstractUser saveVisitorByUidOrOpenID(String to, AbstractUser user) {
        return users.put(to, user);
    }

    *//*
    @Override
    public AbstractUser getCustomerByVisitorOpenId(String openId) throws BusinessException {
        String cJid = getCustomerIdByVisitorOpenId(openId);

        return getUserCustomer(cJid);
    }



   *//* *//*

    *//**
     * 获取发送地址
     *//*
    @Override
    public synchronized boolean add(String key, String id, AbstractUser visitorUser) {
        Map map = (Map) cacheService.get(key);
        boolean flag;
        if (null == map) {
            map = new HashMap<>();
        }
        map.put(id, visitorUser);

        flag = cacheService.setOneMonth(key, map);

        return flag;
    }

    @Override
    public boolean addCid(String key, String id, AbstractUser visitorUser) {
        Map map = (Map) cacheService.get(key);
        boolean flag;
        if (null == map) {
            map = new HashMap<>();
        }
        Set<AbstractUser> set = (Set) map.get(id);
        if (null == set) {
            set = new HashSet<>();
            map.put(id, set);
        }
        set.remove(visitorUser);
        set.add(visitorUser);
        flag = cacheService.setOneMonth(key, map);

        return flag;
    }

    @Override
    public synchronized boolean add(String key, AbstractUser visitorUser) {
        return add(key, visitorUser.getId(), visitorUser);
    }

    @Override
    public synchronized boolean addOpenId(String key, AbstractUser visitorUser) {
        return add(key, visitorUser.getOpenId(), visitorUser);
    }

    @Override
    public synchronized Set<AbstractUser> get(String key, String cid) {
        Map map = (Map) cacheService.get(key);
        if (null != map) {
            Set<AbstractUser> set = (Set) map.get(cid);
            if (null != set) {
                return set;
            }
        }
        return null;
    }

    @Override
    public Object get(String key) {
        return cacheService.get(key);
    }

    @Override
    public synchronized void delete(String key, String cid, AbstractUser visitorUser) {
        Object map = cacheService.get(key);
        if (null != map) {
            HashMap<String, Set<AbstractUser>> ma = (HashMap<String, Set<AbstractUser>>) map;
            Set<AbstractUser> set = ma.get(cid);
            if (null != set) {
                set.remove(visitorUser);
            }

        }
        cacheService.setOneMonth(key, map);
    }*/


}
