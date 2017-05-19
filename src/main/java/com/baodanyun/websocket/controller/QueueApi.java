package com.baodanyun.websocket.controller;

import com.baodanyun.websocket.bean.Response;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.ComparatorFriend;
import com.baodanyun.websocket.bean.user.Friend;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.enums.MsgStatus;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.service.UserCacheServer;
import com.baodanyun.websocket.service.XmppServer;
import com.baodanyun.websocket.util.CommonConfig;
import com.baodanyun.websocket.util.Render;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * Created by yutao on 2016/10/4.
 */
@RestController
public class QueueApi extends BaseController {
    protected static Logger logger = Logger.getLogger(CustomerApi.class);

    @Autowired
    private UserCacheServer userCacheServer;

    @Autowired
    private XmppServer xmppServer;

    @RequestMapping(value = "queue/{q}")
    public void backupQueue(@PathVariable("q") String q, HttpServletRequest request, HttpServletResponse response) {
        Response msgResponse = new Response();
        try {
            AbstractUser au = (AbstractUser) request.getSession().getAttribute(Common.USER_KEY);
            if (null != au) {

                Set<Friend> friendList = new HashSet<>();

                if (QueueName.online.getValue().equals(QueueName.getQueueNameByKey(q))) {

                    Set<AbstractUser> onlineQueue = userCacheServer.get(CommonConfig.USER_ONLINE, au.getId());

                    if (!CollectionUtils.isEmpty(onlineQueue)) {
                        for (AbstractUser visitor : onlineQueue) {

                            Friend friend = getFriend(visitor, au);
                            if (null != friend) {
                                friendList.add(friend);
                            }
                        }
                    }
                }

                List<Friend> li = new ArrayList<>(friendList);
                ComparatorFriend comparator=new ComparatorFriend();
                Collections.sort(li, comparator);
                msgResponse.setData(li);
                msgResponse.setSuccess(true);
            } else {
                msgResponse.setMsg("非法访问");
                msgResponse.setSuccess(false);
            }
        } catch (Exception e) {
            logger.error(e);
            msgResponse.setSuccess(false);
        }
        Render.r(response, XMPPUtil.buildJson(msgResponse));
    }

    private Friend getFriend(AbstractUser visitor, AbstractUser customer) {

        Friend friend = new Friend();
        friend.setId(visitor.getId());

        friend.setName(visitor.getUserName());

        try {
            String id = userCacheServer.getCustomerIdByVisitorOpenId(visitor.getOpenId());
            if (customer.getId().equals(id)) {
                if (!xmppServer.isAuthenticated(visitor.getId())) {
                    friend.setOnlineStatus(MsgStatus.history);
                } else {
                    friend.setOnlineStatus(MsgStatus.online);
                }
            } else {
                friend.setOnlineStatus(MsgStatus.changeOffline);
            }
        } catch (BusinessException e) {
            logger.equals(e);
        }


        friend.setNickname(visitor.getNickName() == null ? visitor.getUserName() == null ? "未知" : visitor.getUserName() : visitor.getNickName());
        friend.setIcon(visitor.getIcon());
        friend.setNickname(visitor.getNickName());
        friend.setLoginUsername(visitor.getLoginUsername());
        friend.setOpenId(visitor.getOpenId());
        friend.setLoginTime(visitor.getLoginTime());

        return friend;

    }

    public enum QueueName {
        online("1", "online"), wait("2", "wait");

        private String key;
        private String value;

        QueueName(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public static String getQueueNameByKey(String key) {
            if (StringUtils.isBlank(key)) {
                return null;
            }
            for (QueueName queueName : QueueName.values()) {
                if (key.equals(queueName.getKey())) {
                    return queueName.name();
                }
            }
            return null;
        }

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

}
