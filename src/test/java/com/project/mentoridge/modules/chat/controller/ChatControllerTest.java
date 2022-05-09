package com.project.mentoridge.modules.chat.controller;

import com.project.mentoridge.modules.chat.vo._Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class ChatControllerTest {
    // STOMP + SOCKJS

    private static final String WEBSOCKET_URI = "ws://localhost:8080/ws";
    private static final String SEND_URI = "/pub/chat";
    private static final String TOPIC_URI = "/sub/chat/room"; // /{chatroom_id}

    private static final int NUMBER_OF_CONNECTIONS = 10;

    CountDownLatch lock = new CountDownLatch(NUMBER_OF_CONNECTIONS);
    WebSocketStompClient client;
    BlockingQueue<_Message> blockingQueue;
    List<StompSession> sessions;

    private List<Transport> createTransportClients() {
        List<Transport> transports = new ArrayList<>(1);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        return transports;
    }

    @BeforeEach
    public void setup() {
        blockingQueue = new LinkedBlockingDeque<>();
        client = new WebSocketStompClient(new SockJsClient(createTransportClients()));
        client.setMessageConverter(new MappingJackson2MessageConverter());
        sessions = new ArrayList<>(NUMBER_OF_CONNECTIONS);
    }

    // @Test
    public void contextLoads() throws InterruptedException {

        for (int i = 0; i < NUMBER_OF_CONNECTIONS; i++) {
            TimeUnit.SECONDS.sleep(1);

            WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
            headers.add("id", "50");

            StompHeaders stompHeaders = new StompHeaders();
            stompHeaders.add("id", "55");

            try {
                StompSession session =
                        client.connect(WEBSOCKET_URI, headers, stompHeaders, new StompSessionHandlerAdapter() {})
                                .get(1, TimeUnit.SECONDS);
                sessions.add(session);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < sessions.size(); i++) {
            Thread th = new Thread(new MessageSender(i));
            th.start();
        }

        lock.await();
    }

    class DefaultStompFrameHandler implements StompFrameHandler {

        @Override
        public Type getPayloadType(StompHeaders headers) {
            return _Message.class;
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            blockingQueue.offer((_Message) payload);
        }
    }

    class MessageSender implements Runnable {

        private int index;

        public MessageSender(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            System.out.println("run : " + index);

            while(true) {
                System.out.println("while : " + index);
                sessions.get(index).send(SEND_URI, "data" + index);

                try {
                    TimeUnit.SECONDS.sleep(7);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}