package com.baodanyun.websocket.node;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.msg.status.StatusMsg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.bean.user.Customer;
import com.baodanyun.websocket.enums.MsgStatus;
import com.baodanyun.websocket.event.SynchronizationMsgEvent;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.node.dispatcher.CustomerDispather;
import com.baodanyun.websocket.util.CommonConfig;
import com.baodanyun.websocket.util.Config;
import com.baodanyun.websocket.util.EventBusUtils;
import com.baodanyun.websocket.util.JSONUtil;
import org.apache.commons.lang.SerializationUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by liaowuhen on 2017/5/23.
 */
public abstract class CustomerNode extends AbstractNode implements CustomerDispather {
    private static final Logger logger = LoggerFactory.getLogger(CustomerNode.class);
    private Customer customer;


    public CustomerNode(Customer customer) {
        this.customer = customer;
    }

    @Override
    public AbstractUser getAbstractUser() {
        return customer;
    }

    @Override
    public Message receiveFromGod(Msg msg) throws InterruptedException, BusinessException, SmackException.NotConnectedException {

        SynchronizationMsgEvent sme = new SynchronizationMsgEvent();
        Msg clone = (Msg) SerializationUtils.clone(msg);
        clone.setIcon(null);
        sme.setMsg(clone);
        sme.setNode(this);
        sme.setFromJid(this.getAbstractUser().getId());
        EventBusUtils.post(sme);
        return super.receiveFromGod(msg);

    }

    @Override
    public boolean joinQueue(AbstractUser abstractUser) throws InterruptedException {
        logger.info("保存到缓存--->" + userCacheServer.addCid(CommonConfig.USER_ONLINE, this.getAbstractUser().getId(), abstractUser));

        return true;
    }

    @Override
    public boolean uninstall(AbstractUser abstractUser) throws InterruptedException {
        userCacheServer.delete(CommonConfig.USER_ONLINE, this.getAbstractUser().getId(), abstractUser);
        return true;
    }

    public StatusMsg getSMMsgSendTOCustomer(MsgStatus status) {
        StatusMsg statusSysMsg = StatusMsg.buildStatus(Msg.Type.status);
        statusSysMsg.setStatus(status);
        statusSysMsg.setTo(customer.getId());
        return statusSysMsg;
    }

    public StatusMsg getSMMsgOnlineSendTOCustomer(AbstractUser visitor, MsgStatus status) {
        StatusMsg statusSysMsg = StatusMsg.buildStatus(Msg.Type.status);
        if (null != visitor) {
            statusSysMsg.setFromName(visitor.getNickName());
            statusSysMsg.setFrom(visitor.getId());
            statusSysMsg.setIcon(visitor.getIcon());
            statusSysMsg.setOpenId(visitor.getOpenId());
            statusSysMsg.setLoginUsername(visitor.getLoginUsername());
            statusSysMsg.setLoginTime(visitor.getLoginTime());
        }
        statusSysMsg.setStatus(status);
        statusSysMsg.setTo(customer.getId());
        return statusSysMsg;
    }


    public Msg getMsgHelloToCustomer(AbstractUser visitor) {
        logger.info("user--->" + JSONUtil.toJson(customer));
        String body = Config.greetingWord;
        Msg sendMsg = new Msg(body);
        String to = customer.getId();
        String from = visitor.getId();
        String type = Msg.Type.msg.toString();
        Long ct = new Date().getTime();

        sendMsg.setType(type);
        sendMsg.setContentType(Msg.MsgContentType.text.toString());
        sendMsg.setTo(to);
        sendMsg.setFrom(from);
        sendMsg.setCt(ct);
        // 获取发送端用户
        sendMsg.setIcon(customer.getIcon());
        sendMsg.setFromName(customer.getNickName());

        return sendMsg;
    }


}
