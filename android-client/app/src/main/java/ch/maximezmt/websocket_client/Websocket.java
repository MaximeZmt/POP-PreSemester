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

/**
 * Activity that handle the WebSocket communication
 */
public class Websocket extends AppCompatActivity {


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

        // Variable that contains the WebSocket Discussion
        content = "";

        // Recover the address given in previous activity form
        Intent intent = getIntent();
        String address = intent.getStringExtra("address");

        // Write address in form http/https (even if for websocket it is common to use ws/wss)
        // But this is more convenient to use the URLUtil.isValidUrl that will prevent crashing with bad input
        address = address.replace("ws://", "http://");
        address = address.replace("wss://", "https://");
        if(address == null || address == "" || !URLUtil.isValidUrl(address)){
            finish();
            return;
        }


        app = this.getApplication();
        wsIntent = new Intent(getApplicationContext(), Websocket.class);

        // Get button and input from view
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
                    Websocket.content +=  "\n" + m.getValue() + " <=(Serv)";

                    // Very usefull method to make the main thread update its view
                    runOnUiThread(()->updateContent());
                }
            }
        });


        // Disconnect button listener
        buttonDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                return;
            }
        });


        // Send button Listener
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Websocket.content += "\n (Client)=>"+inputMessage.getText().toString();
                convTV.setText(Websocket.content);
                try{
                    int conv = Integer.parseInt(inputMessage.getText().toString());
                    comServ.sendMessage(conv);
                }catch (Exception e){
                    comServ.sendMessage(inputMessage.getText().toString());
                }
            }
        });

    }

    /**
     * WebSocket Communication Object Builder
     */
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

    /**
     * Update the view
     */
    private void updateContent(){
        convTV.setText(content);
    }


}
