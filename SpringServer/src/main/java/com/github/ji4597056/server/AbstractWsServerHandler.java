package com.github.ji4597056.server;

import com.github.ji4597056.client.WsForwardClient;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

/**
 * websocket server handler
 *
 * @author Jeffrey
 * @since 2017/01/22 17:33
 */
public abstract class AbstractWsServerHandler extends AbstractWebSocketHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractWsServerHandler.class);

    /**
     * channel map(key:session id, value:channel)
     */
    private static final Map<String, Channel> CHANNELS = new ConcurrentHashMap<>(100);


    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage message) throws Exception {
        CHANNELS.get(session.getId()).writeAndFlush(
                new BinaryWebSocketFrame(
                        Unpooled.wrappedBuffer(((BinaryMessage)message).getPayload()
                        )
                )
        );
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        WsForwardClient client = WsForwardClient
                .create(getForwardUrl(session), session);
        client.connect();
        Channel channel = client.getChannel();
        CHANNELS.put(session.getId(), channel);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception)
            throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        closeGracefully(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus)
            throws Exception {
        closeGracefully(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * get forward url
     *
     * @param session websocket session
     * @return forward url
     */
    public abstract String getForwardUrl(WebSocketSession session);

    /**
     * close client
     *
     * @param session WebSocketSession
     */
    private void closeGracefully(WebSocketSession session) {
        Optional.ofNullable(CHANNELS.get(session.getId()))
                .ifPresent(channel -> {
                    try {
                        if (channel.isOpen()) {
                            channel.close();
                        }
                    } catch (Exception e) {
                        LOGGER.warn("Close websocket forward client error!error: {}", e);
                    }
                });
        CHANNELS.remove(session.getId());
    }
}