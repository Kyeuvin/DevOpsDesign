package com.jenkins.platform.websocket;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WebSocketTest {

    private WebSocketStompClient stompClient;
    private CompletableFuture<String> completableFuture;

    @BeforeEach
    void setUp() {
        this.completableFuture = new CompletableFuture<>();
        this.stompClient = new WebSocketStompClient(new SockJsClient(
            List.of(new WebSocketTransport(new StandardWebSocketClient()))
        ));
    }

    @Test
    void shouldReceiveLogUpdates() throws Exception {
        StompSession session = stompClient
            .connect("ws://localhost:" + port + "/ws", new StompSessionHandlerAdapter() {})
            .get(1, TimeUnit.SECONDS);

        session.subscribe("/topic/logs/test-job", new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return String.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                completableFuture.complete((String) payload);
            }
        });

        String message = completableFuture.get(5, TimeUnit.SECONDS);
        assertNotNull(message);
    }
}