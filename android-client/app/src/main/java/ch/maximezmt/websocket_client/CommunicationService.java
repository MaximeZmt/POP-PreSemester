package ch.maximezmt.websocket_client;



import com.tinder.scarlet.WebSocket;
import com.tinder.scarlet.ws.Receive;
import com.tinder.scarlet.ws.Send;

import io.reactivex.Flowable;


public interface CommunicationService {

    @Send
    void sendMessage(int num);

    @Receive
    Flowable<WebSocket.Event> observeEvent();

}