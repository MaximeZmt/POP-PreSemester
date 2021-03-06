package ch.maximezmt.websocket_client;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Main welcome screen with the login for to connect to a websocket server
 */
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

        // Load the Websocket communication Activity.
        app = this.getApplication();
        wsIntent = new Intent(getApplicationContext(), Websocket.class);

        // Get button and input from view
        buttonConnect = findViewById(R.id.button_connect);
        inputAddress = findViewById(R.id.address_input);



        // Below is the listener to switch to next activity
        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wsIntent.putExtra("address", inputAddress.getText().toString());
                startActivity(wsIntent);
            }
        });


    }

}

