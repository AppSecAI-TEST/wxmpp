package com.baodanyun.websocket.node;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.service.UserCacheServer;
import com.baodanyun.websocket.util.SpringContextUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;

/**
 * Created by liaowuhen on 2017/5/23.
 */
public abstract class AbstractNode implements Node {
    private static final Logger logger = LoggerFactory.getLogger(AbstractNode.class);

    UserCacheServer userCacheServer = SpringContextUtil.getBean("userCacheServerImpl", UserCacheServer.class);


    /*public boolean pushOfflineMsg() throws BusinessException {
        //加载离线记录
        XMPPConnection xmppConnection = this.getXmppNode().getXMPPConnection();
        OfflineMessageManager offlineManager = new OfflineMessageManager(xmppConnection);
        try {
            List<Message> msgList = offlineManager.getMessages();
            if (!CollectionUtils.isEmpty(msgList)) {
                for (Message message : msgList) {
                    receiveFromXmpp(message);
                    offlineManager.deleteMessages();
                }
            }
        } catch (Exception e) {
            logger.error("error", "offline msg error");
        }

        return true;
    }*/

    @Override
    public Message receiveFromGod(String content) throws InterruptedException, BusinessException, SmackException.NotConnectedException {
        if (!StringUtils.isEmpty(content)) {
            Msg msg = Msg.handelMsg(content);
            if (msg != null) {
                return receiveFromGod(msg);
            }
        } else {
            logger.error("error", "msg is blank");
        }

        return null;
    }

    @Override
    public Message receiveFromGod(Msg msg) throws InterruptedException, BusinessException, SmackException.NotConnectedException {
        Message xmppMsg = new Message();

        xmppMsg.setFrom(msg.getFrom());
        xmppMsg.setTo(msg.getTo());
        xmppMsg.setType(Message.Type.chat);
        xmppMsg.setBody(msg.getContent().toString());
        xmppMsg.setSubject(msg.getContentType());

        return xmppMsg;
    }

    @Override
    public void receiveFromXmpp(Message message) {
        Msg sendMsg = null;
        String body = message.getBody();
        if (StringUtils.isNotBlank(body)) {
            sendMsg = new Msg(body);
            String from = XMPPUtil.removeSource(message.getFrom());
            String to = XMPPUtil.removeSource(message.getTo());
            String type = Msg.Type.msg.toString();
            Long ct = new Date().getTime();

            sendMsg.setType(type);
            sendMsg.setFrom(from);
            sendMsg.setTo(to);
            sendMsg.setCt(ct);
            sendMsg.setOpenId(this.getAbstractUser().getOpenId());

            if (StringUtils.isEmpty(message.getSubject())) {
                sendMsg.setContentType("text");
            } else {
                sendMsg.setContentType(message.getSubject());
            }
        }

        sendMsgToGod(sendMsg);
    }


    @Override
    public boolean logout() throws BusinessException, IOException, XMPPException, SmackException {
        return true;
    }

    @Override
    public boolean login() throws BusinessException, IOException, XMPPException, SmackException {
        return true;
    }

    @Override
    public void online() throws InterruptedException, BusinessException {

    }
}
