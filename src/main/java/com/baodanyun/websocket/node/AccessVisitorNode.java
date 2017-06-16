package com.baodanyun.websocket.node;

import com.baodanyun.websocket.bean.user.Visitor;
import com.baodanyun.websocket.exception.BusinessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

/**
 * Created by liaowuhen on 2017/5/23.
 */
public class AccessVisitorNode extends WeChatNode {
    private static final Logger logger = LoggerFactory.getLogger(WeChatNode.class);

    private WebSocketSession session;

    public AccessVisitorNode(Visitor visitor) {
        super(visitor);
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }

    @Override
    public void online() throws InterruptedException, BusinessException {
        super.online();
        joinQueue();
    }

}
