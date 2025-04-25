package com.example.hou.common;

import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @ClassName: OnlineUserCountWebSocket
 * @Description: TODO
 * @Author: 代刘斌
 * @Date: 2023/12/10 - 12 - 10 - 14:27
 * @version: 1.0
 **/
@Component
@ServerEndpoint("/websocket")  //该注解表示该类被声明为一个webSocket终端
public class OnlineUserCountWebSocket {

    private Session session;

    //初始在线人数
    private static int onlineNum = 0;

    private static CopyOnWriteArraySet<OnlineUserCountWebSocket> userCountWebSockets =
            new CopyOnWriteArraySet<OnlineUserCountWebSocket>();

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        userCountWebSockets.add(this);
        addOnlineCount();
    }

    @OnClose
    public void onClose() {
        userCountWebSockets.remove(this);
        subOnlineCount();
    }

    public synchronized int getonlineNum() {
        return OnlineUserCountWebSocket.onlineNum;
    }

    public synchronized int subOnlineCount() {
        return OnlineUserCountWebSocket.onlineNum--;
    }

    public synchronized int addOnlineCount() {
        return OnlineUserCountWebSocket.onlineNum++;
    }

}
