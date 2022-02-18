package ch.maximezmt.websocket_client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

public class Websocket extends AppCompatActivity {

    // 10.0.2.2 is the localhost for Android AVD (which is default android studio emulator)

    private Button buttonSend;
    private Button buttonDisconnect;

    private CommunicationService comServ;

    private Application app;

    private Intent wsIntent;

    private EditText inputMessage;

    private TextView convTV;

    public static String content = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_websocket);
        content = "";
        //io.reactivex.exceptions.OnErrorNotImplementedException: Only the original thread that created a view hierarchy can touch its views.
        Intent intent = getIntent();
        String address = intent.getStringExtra("address");
        address = address.replace("ws://", "http://");
        address = address.replace("wss://", "https://");
        if(address == null || address == "" || !URLUtil.isValidUrl(address)){
            finish();
            return;
        }


        app = this.getApplication();
        wsIntent = new Intent(getApplicationContext(), Websocket.class);

        //get button and input from view
        buttonSend = findViewById(R.id.button_send);
        buttonDisconnect = findViewById(R.id.button_disconnect);

        convTV = findViewById(R.id.textView);
        inputMessage = findViewById(R.id.message_input);

        comServ = scarletBuilder(app, address);
        if(comServ == null){
            finish();
            return;
        }


        comServ.observeEvent().subscribe(new Consumer<WebSocket.Event>(){
            @Override
            public void accept(WebSocket.Event event) throws Exception {
                if( event.getClass().equals(WebSocket.Event.OnConnectionOpened.class) ){
                    Log.d(getString(R.string.log_tag),"CONNECTED");
                }else if( event.getClass().equals(WebSocket.Event.OnConnectionClosing.class) || event.getClass().equals(WebSocket.Event.OnConnectionFailed.class) ){
                    Log.d(getString(R.string.log_tag),"LOST CONNECTION/CLOSED");
                    finish();
                }else if(event.getClass().equals(WebSocket.Event.OnMessageReceived.class)){
                    Message.Text m = (Message.Text)((WebSocket.Event.OnMessageReceived) event).getMessage();
                    Log.d(getString(R.string.log_tag), "message-> "+m.getValue());
                    //String current = convTV.getText().toString();
                    Websocket.content +=  "\n" + m.getValue() + " <=(Serv)";
                    runOnUiThread(()->updateContent());
                }
            }
        });


        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Websocket.content += "\n (Client)=>"+inputMessage.getText().toString();
                convTV.setText(Websocket.content);
                comServ.sendMessage(Integer.parseInt(inputMessage.getText().toString()));
            }
        });

    }

    private static CommunicationService scarletBuilder(Application app, String address){
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
                .build();

        return new Scarlet.Builder()
            .webSocketFactory(OkHttpClientUtils.newWebSocketFactory(okClient, address)) //inputAddress.getText().toString()
            .addMessageAdapterFactory(new GsonMessageAdapter.Factory())
            .addStreamAdapterFactory(new RxJava2StreamAdapterFactory())
            .lifecycle(AndroidLifecycle.ofApplicationForeground(app))
            .build().create(CommunicationService.class);
    }

    private void updateContent(){
        convTV.setText(content);
    }


}
