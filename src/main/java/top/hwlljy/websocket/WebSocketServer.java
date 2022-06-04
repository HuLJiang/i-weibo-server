package top.hwlljy.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.hwlljy.model.pojo.User;
import top.hwlljy.service.UserService;
import top.hwlljy.utils.SessionUtil;
import top.hwlljy.utils.SocketManager;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@Component
@Slf4j
@ServerEndpoint("/socket/msg/{userId}")
public class WebSocketServer {
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    private String userId;

    @Autowired
    private UserService userService;

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId){
        this.session = session;
        this.userId = userId;
        SocketManager.add(userId,this);
    }

    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error(e.getMessage(),e);
        }
    }

    @OnClose
    public void onClose(){
        log.info("用户退出");
        SocketManager.remove(userId);
    }


    @OnMessage
    public void onMessage(String message, Session session) {
        log.info(message);
    }

    @OnError
    public void onError(Session session, Throwable error){
        log.error(error.getMessage(),error);
    }
}
