package com.cn.selenium.spider.socket;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.springframework.stereotype.Component;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author: MuYaHai
 * Date: 2020/9/21, Time: 14:48
 */
@Component
@ServerEndpoint("/websocket/{userName}")
public class WebSocket {

	private Session session;

	private static CopyOnWriteArraySet<WebSocket> webSockets = new CopyOnWriteArraySet<>();
	private static Map<String, Session> sessionPool = new HashMap<String, Session>();

	@OnOpen
	public void onOpen(Session session, @PathParam(value = "userName") String userName) {
		this.session = session;
		webSockets.add(this);
		sessionPool.put(userName, session);

	}

	@OnClose
	public void onClose() {
		webSockets.remove(this);
	}

	@OnMessage
	public void onMessage(String message) {

	}

	public void sendAllMessage(String message) {
		for (WebSocket webSocket : webSockets) {
			try {
				webSocket.session.getAsyncRemote().sendText(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
