package com.baodanyun.websocket.node;

import com.baodanyun.websocket.bean.msg.Msg;
import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.enums.MsgStatus;
import com.baodanyun.websocket.exception.BusinessException;
import com.baodanyun.websocket.service.UserCacheServer;
import com.baodanyun.websocket.util.SpringContextUtil;
import com.baodanyun.websocket.util.XMPPUtil;
import org.apache.commons.lang.StringUtils;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by liaowuhen on 2017/5/23.
 */
public class AbstractTerminal {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTerminal.class);
    protected String id;
    UserCacheServer userCacheServer = SpringContextUtil.getBean("userCacheServerImpl", UserCacheServer.class);
    private ChatNodeAdaptation chatNodeAdaptation;

    AbstractTerminal(ChatNodeAdaptation chatNodeAdaptation) {
        this.chatNodeAdaptation = chatNodeAdaptation;
    }

    public AbstractUser getAbstractUser() {
        return getChatNodeAdaptation().getAbstractUser();
    }

    public ChatNodeAdaptation getChatNodeAdaptation() {
        return chatNodeAdaptation;
    }

    /**
     * 移除访客通知
     *
     * @return
     * @throws InterruptedException
     */
    boolean uninstall() throws InterruptedException {
        return false;
    }

    /**
     * 接入通知
     *
     * @return
     * @throws InterruptedException
     */

    boolean joinQueue() throws InterruptedException, BusinessException {
        return false;
    }

    public String getId() {
        return id;
    }

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

    void receiveFromGod(String content) throws InterruptedException, BusinessException, SmackException.NotConnectedException {
        if (!StringUtils.isEmpty(content)) {
            Msg msg = Msg.handelMsg(content);
            if (msg != null) {
                receiveFromGod(msg);
            }
        } else {
            logger.error("error", "msg is blank");
        }
    }


    boolean sendMsgToGod(Msg msg) {
        return false;
    }


    void receiveFromGod(Msg msg) throws InterruptedException, BusinessException, SmackException.NotConnectedException {
        Message xmppMsg = new Message();

        xmppMsg.setFrom(msg.getFrom());
        /**
         * 可能会有转接的情况
         */
        String realTo = this.getChatNodeAdaptation().getRealTo();
        if(!StringUtils.isEmpty(realTo)){
            xmppMsg.setTo(realTo);
        } else {
            xmppMsg.setTo(msg.getTo());
        }
        xmppMsg.setType(Message.Type.chat);
        xmppMsg.setBody(msg.getContent().toString());
        xmppMsg.setSubject(msg.getContentType());

        sendMessageTOXmpp(xmppMsg);
    }

    /**
     * 发送信息到xmpp
     *
     * @param
     * @throws SmackException.NotConnectedException
     */

    void sendMessageTOXmpp(Message xmppMsg) throws SmackException.NotConnectedException{
        this.getChatNodeAdaptation().sendMessageTOXmpp(xmppMsg);
    }

    /**
     * 发送消息到xmpp
     *
     * @throws SmackException.NotConnectedException
     */

    boolean messageCallBack(AbstractUser abstractUser, MsgStatus msgStatus) throws InterruptedException {
        return true;
    }

    void receiveFromXmpp(Message message) {
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

    void online() throws InterruptedException, BusinessException {

    }

    void offline() throws InterruptedException, BusinessException {

    }
}