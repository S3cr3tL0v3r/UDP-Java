package de.henrikkaltenbach.udpjava;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button updateButton;
    private EditText portEditText;
    private Switch permanentUpdateSwitch;
    private TextView ipAddressTextView, boostTextView, exceptionTextView;
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
    private byte[] byteArray = new byte[512];
    private int port = 1024;

    float boost = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateButton = findViewById(R.id.updateButton);
        portEditText = findViewById(R.id.portEditText);
        permanentUpdateSwitch = findViewById(R.id.permanentUpdateSwitch);
        ipAddressTextView = findViewById(R.id.ipAddressTextView);
        boostTextView = findViewById(R.id.boostTextView);
        exceptionTextView = findViewById(R.id.exceptionTextView);

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        ipAddressTextView.setText(Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress()));

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // port = Integer.parseInt(portEditText.toString());
                receive();
                // boostTextView.setText(String.format(Locale.ENGLISH, "%1.2f", boost));
                boostTextView.setText(String.valueOf(boost));
            }
        });

    }

    private void receive() {
        new Thread() {
            @Override
            public void run() {
                try {
                    datagramSocket = new DatagramSocket(port);
                    datagramPacket = new DatagramPacket(byteArray, byteArray.length);
                    datagramSocket.receive(datagramPacket);
                    byteArray = datagramPacket.getData();
                    ByteBuffer byteBuffer = ByteBuffer.wrap(Arrays.copyOfRange(byteArray, 272, 276)).order(ByteOrder.LITTLE_ENDIAN);
                    boost = byteBuffer.getFloat();
                } catch (IOException e) {
                    exceptionTextView.setText(Arrays.toString(e.getStackTrace()));
                } finally {
                    datagramPacket.setLength(byteArray.length);
                }
            }
        }.start();
    }
}