package ch.maximezmt.websocket_client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
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

    private Button buttonConnect;

    private CommunicationService comServ;

    private Application app;

    private Intent wsIntent;

    private EditText inputAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = this.getApplication();
        wsIntent = new Intent(getApplicationContext(), websocket.class);

        //get button and input from view
        buttonConnect = findViewById(R.id.button_connect);
        inputAddress = findViewById(R.id.address_input);



        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wsIntent.putExtra("address", inputAddress.getText().toString());
                startActivity(wsIntent);
            }
        });


    }

}

