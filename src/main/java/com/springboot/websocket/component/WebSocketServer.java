package com.springboot.websocket.component;

import com.sun.xml.internal.ws.resources.SenderMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Create by hyhweb on 2020/9/7 9:56
 */
@ServerEndpoint(value = "/ws/asset")
@Component
public class WebSocketServer {
    @PostConstruct
    public void init(){
        System.out.println("websocket 加载");
    }

    private static Logger logger = LoggerFactory.getLogger(WebSocketServer.class);
    private static final AtomicInteger OnlineCount = new AtomicInteger(0);
    // concurrent包的线程安全Set，用来存放每个客户端对应的Session对象。
    private static CopyOnWriteArraySet<Session> sessionSet = new CopyOnWriteArraySet<Session>();
    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session){
        sessionSet.add(session);
        int cnt = OnlineCount.incrementAndGet();
        logger.info("有连接加入，当前连接数为：{}", cnt);
    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session){
        sessionSet.remove(session);
        int cnt = OnlineCount.decrementAndGet();
    }
    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     *            客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message,Session session){
        logger.info("来自客户端的消息：{}", message);
        sendMessages(session, "收到消息，消息内容：" + message);
    }
    /**
     * 出现错误
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        logger.error("发生错误：{}，Session ID:{}", error.getMessage(), session.getId());
        error.printStackTrace();
    }

    /**
     * 发送消息，实践表明，每次浏览器刷新，session会发生变化。
     * @param session
     * @param message
     */
    public static void sendMessages(Session session,String message){
            try {
                session.getBasicRemote().sendText(String.format("%s (From Server，Session ID=%s)", message, session.getId()));
            }catch (IOException e){
                logger.error("发送消息出错：{}", e.getMessage());
                e.printStackTrace();
            }
    }
    /**
     * 群发消息
     * @param message
     * @throws IOException
     */
    public static void broadCastInfo(String message) throws IOException{
        for (Session session:sessionSet){
            if(session.isOpen()){
                sendMessages(session, message);
            }
        }
    }
    public static void sendMessage(String message,String sessionId) throws IOException{
        Session session =null;
        for(Session session1:sessionSet){
            if(session1.getId().equals(sessionId)){
                session =session1;
                break;
            }
        }
        if(session !=null){
            sendMessages(session, message);
        }else{
            logger.warn("没有找到你指定ID的会话：{}",sessionId);
        }
    }
}
