package top.hwlljy.utils;

import lombok.extern.slf4j.Slf4j;
import top.hwlljy.websocket.WebSocketServer;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SocketManager {
    private SocketManager() {

    }

    private static ConcurrentHashMap<String, WebSocketServer> servers = new ConcurrentHashMap<>();

    public static void add(String userId,WebSocketServer webSocketServer) {
        servers.put(userId,webSocketServer);
        log.info(servers.toString());
    }

    public static void remove(String userId) {
        servers.remove(userId);
    }

    public static void setMessage(String userId, String type) {
        if(servers.containsKey(userId)) {
            WebSocketServer socketServer = servers.get(userId);
            socketServer.sendMessage(type);
        }
    }
}
