package com.baodanyun.websocket.core.handle;

import com.baodanyun.websocket.bean.user.AbstractUser;
import com.baodanyun.websocket.core.common.Common;
import com.baodanyun.websocket.node.AbstractTerminal;
import com.baodanyun.websocket.node.terminal.WebSocketTerminal;
import com.baodanyun.websocket.node.ChatNode;
import com.baodanyun.websocket.node.ChatNodeAdaptation;
import com.baodanyun.websocket.node.ChatNodeManager;
import com.baodanyun.websocket.service.WebSocketService;
import com.baodanyun.websocket.service.impl.terminal.WebSocketTerminalCustomerFactory;
import com.baodanyun.websocket.util.JSONUtil;
import com.baodanyun.websocket.util.SpringContextUtil;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * 进入当前处理器后
 * 节点已经验证完成了 afterConnectionEstablished  afterConnectionClosed 非一个线程调用
 */
public class CustomerWebSocketHandler extends AbstractWebSocketHandler {
    public WebSocketService webSocketService = SpringContextUtil.getBean("webSocketServiceImpl", WebSocketService.class);
    WebSocketTerminalCustomerFactory webSocketTerminalCustomerFactory = SpringContextUtil.getBean("webSocketTerminalCustomerFactory", WebSocketTerminalCustomerFactory.class);

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        AbstractUser au = (AbstractUser) session.getHandshakeAttributes().get(Common.USER_KEY);
        webSocketService.saveSession(au.getId(), session);
        //获取一个customerNode节点
        ChatNode chatNode = ChatNodeManager.getVisitorXmppNode(au);
        WebSocketTerminal webSocketTerminal = new WebSocketTerminal(au,session);

        ChatNodeAdaptation chatNodeAdaptation = new ChatNodeAdaptation(chatNode);

        AbstractTerminal wn = webSocketTerminalCustomerFactory.getNode(chatNodeAdaptation,webSocketTerminal);

        chatNode.online(wn);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try{
            AbstractUser au = (AbstractUser) session.getHandshakeAttributes().get(Common.USER_KEY);
            logger.info("webSocket receive message:" + JSONUtil.toJson(message));
            String content = message.getPayload();
            WebSocketTerminal webSocketTerminal = new WebSocketTerminal(au,session);

            ChatNode chatNode = ChatNodeManager.getVisitorXmppNode(au);


            AbstractTerminal wn = chatNode.getNode(webSocketTerminalCustomerFactory.getId(webSocketTerminal));
            chatNode.receiveFromGod(wn,content);



        }catch (Exception e){
            logger.error("error", e);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        AbstractUser au = (AbstractUser) session.getHandshakeAttributes().get(Common.USER_KEY);
        logger.info("customer session is closed: id[" + au.getId() + "]" + session);
        WebSocketTerminal webSocketTerminal = new WebSocketTerminal(au,session);

        ChatNode chatNode = ChatNodeManager.getVisitorXmppNode(au);

        AbstractTerminal wn = chatNode.getNode(webSocketTerminalCustomerFactory.getId(webSocketTerminal));

        chatNode.removeNode(wn.getId());


    }
}
