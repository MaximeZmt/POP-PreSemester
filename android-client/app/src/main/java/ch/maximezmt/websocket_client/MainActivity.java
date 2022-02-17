package ch.maximezmt.websocket_client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tinder.scarlet.Message;
import com.tinder.scarlet.Scarlet;
import com.tinder.scarlet.WebSocket;
import com.tinder.scarlet.lifecycle.android.AndroidLifecycle;
import com.tinder.scarlet.messageadapter.gson.GsonMessageAdapter;
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory;
import com.tinder.scarlet.websocket.okhttp.OkHttpClientUtils;

import io.reactivex.functions.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;


public class MainActivity extends AppCompatActivity {

    // 10.0.2.2 is the localhost for Android AVD (which is default android studio emulator)
    private static String ADDRESS = "ws://10.0.2.2:8080/ws";
    private static String LOG_TAG = "WS-AndroidClient";

    private Button buttonConnect;
    private Button buttonSend;

    private CommunicationService comServ;

    private Application app;

    private EditText inputAddress;
    private EditText inputMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = this.getApplication();

        //get button and input from view
        buttonConnect = findViewById(R.id.button_connect);
        buttonSend = findViewById(R.id.button_send);

        inputAddress = findViewById(R.id.address_input);
        inputMessage = findViewById(R.id.message_input);

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();


        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Scarlet scarletInstance = new Scarlet.Builder()
                        .webSocketFactory(OkHttpClientUtils.newWebSocketFactory(okClient, ADDRESS)) //inputAddress.getText().toString()
                        .addMessageAdapterFactory(new GsonMessageAdapter.Factory())
                        .addStreamAdapterFactory(new RxJava2StreamAdapterFactory())
                        .lifecycle(AndroidLifecycle.ofApplicationForeground(app))
                        .build();

                comServ = scarletInstance.create(CommunicationService.class);

                comServ.observeEvent().subscribe(new Consumer<WebSocket.Event>(){
                    @Override
                    public void accept(WebSocket.Event event) throws Exception {
                        if( event.getClass().equals(WebSocket.Event.OnConnectionOpened.class) ){
                            Log.d(LOG_TAG,"CONNECTED");
                        }else if( event.getClass().equals(WebSocket.Event.OnConnectionClosing.class) || event.getClass().equals(WebSocket.Event.OnConnectionFailed.class) ){
                            Log.d(LOG_TAG,"LOST CONNECTION/CLOSED");
                        }else if(event.getClass().equals(WebSocket.Event.OnMessageReceived.class)){
                            Message.Text m = (Message.Text)((WebSocket.Event.OnMessageReceived) event).getMessage();
                            Log.d(LOG_TAG, "message-> "+m.getValue());
                        }
                    }
                });



            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                comServ.sendMessage(Integer.parseInt(inputMessage.getText().toString()));
            }
        });

    }



}

