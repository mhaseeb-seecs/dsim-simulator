package com.haseeb.simulator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.EmptyStackException;

public class MainActivity extends AppCompatActivity {
    EditText serverIP;
    EditText clients;
    Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        serverIP = (EditText) findViewById(R.id.serverIP);
        clients = (EditText) findViewById(R.id.clients);
        btn = (Button) findViewById(R.id.start);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int totalClients = Integer.parseInt(clients.getText().toString());
                String serverAddr =  serverIP.getText().toString();
                Thread c[] = new Thread[totalClients];
                int cport = 4446;
                for(int i=0; i<totalClients; i++) {
                    cport++;
                    String cname = "Client-"+cport;
                    c[i]= new Client(cname, cport, serverAddr);
                    c[i].start();
                }
            }
        });
    }

}
