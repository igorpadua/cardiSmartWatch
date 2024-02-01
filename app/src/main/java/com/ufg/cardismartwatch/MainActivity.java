package com.ufg.cardismartwatch;

import static com.ufg.cardismartwatch.util.Mqtt.brokerURI;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.ufg.cardismartwatch.util.Mqtt;

import org.json.JSONArray;
import org.json.JSONException;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sendSubscriptionPesoAtual("pesoAtual", this);
        sendSubscriptionTodosOsDados("ping", this);

        enviaSpinner("week", findViewById(R.id.spinner));
        enviaCheckBox("checkbox1", findViewById(R.id.checkBox));
        enviaCheckBox("checkbox2", findViewById(R.id.checkBox2));
        enviaCheckBox("checkbox3", findViewById(R.id.checkBox3));
        enviaCheckBox("checkbox4", findViewById(R.id.checkBox4));
        getWeekData();

        pegaCheckBox("checkbox1", this, findViewById(R.id.checkBox));
        pegaCheckBox("checkbox2", this, findViewById(R.id.checkBox2));
        pegaCheckBox("checkbox3", this, findViewById(R.id.checkBox3));
        pegaCheckBox("checkbox4", this, findViewById(R.id.checkBox4));

        sendSubscriptionSemanaSelecionada("week", this);
    }

    private void enviaSpinner(String topicName, Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String spinnerValue = parent.getItemAtPosition(position).toString().equals("1 week") ? "1" : "2";
                Mqtt.publishMessage(topicName, spinnerValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void pegaCheckBox(String topicName, AppCompatActivity activity, CheckBox checkBox) {
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();

        // Use a callback to show the message on the screen
        client.toAsync().subscribeWith()
                .topicFilter(topicName)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(msg -> {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            String message = new String(msg.getPayloadAsBytes(), StandardCharsets.UTF_8);
                            if (message.equals("1")) {
                                checkBox.setChecked(true);
                            } else {
                                checkBox.setChecked(false);
                            }
                        }
                    });
                })
                .send();
    }

    private void enviaCheckBox(String topicName, CheckBox checkBox) {
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Mqtt.publishMessage(topicName, "1");
                } else {
                    Mqtt.publishMessage(topicName, "0");
                }
            }
        });
    }

    private void getWeekData() {
        Spinner spinner = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.week, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void sendSubscriptionPesoAtual(String topicName, AppCompatActivity activity) {
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();

        // Use a callback to show the message on the screen
        client.toAsync().subscribeWith()
                .topicFilter(topicName)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(msg -> {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            String msgString = new String(msg.getPayloadAsBytes(), StandardCharsets.UTF_8);
                            TextView pesoAtual = findViewById(R.id.textView8);
                            pesoAtual.setText(msgString + " kg");
                        }
                    });
                })
                .send();
    }

    private void sendSubscriptionTodosOsDados(String topicName, AppCompatActivity activity) {
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();

        // Use a callback to show the message on the screen
        client.toAsync().subscribeWith()
                .topicFilter(topicName)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(msg -> {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            Spinner spinner = findViewById(R.id.spinner);
                            // pega o select do spinner
                            String spinnerValue = spinner.getSelectedItem().toString().equals("1 week") ? "1" : "2";
                            Mqtt.publishMessage("week",spinnerValue);

                            CheckBox checkBox = findViewById(R.id.checkBox);
                            String checkBoxValue = checkBox.isChecked() ? "1" : "0";
                            Mqtt.publishMessage("checkbox1", checkBoxValue);

                            CheckBox checkBox2 = findViewById(R.id.checkBox2);
                            String checkBoxValue2 = checkBox2.isChecked() ? "1" : "0";
                            Mqtt.publishMessage("checkbox2", checkBoxValue2);

                            CheckBox checkBox3 = findViewById(R.id.checkBox3);
                            String checkBoxValue3 = checkBox3.isChecked() ? "1" : "0";
                            Mqtt.publishMessage("checkbox3", checkBoxValue3);

                            CheckBox checkBox4 = findViewById(R.id.checkBox4);
                            String checkBoxValue4 = checkBox4.isChecked() ? "1" : "0";
                            Mqtt.publishMessage("checkbox4", checkBoxValue4);
                        }
                    });
                })
                .send();
    }

    private void sendSubscriptionSemanaSelecionada(String topicName, AppCompatActivity activity) {
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();

        // Use a callback to show the message on the screen
        client.toAsync().subscribeWith()
                .topicFilter(topicName)
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(msg -> {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {
                            String message = new String(msg.getPayloadAsBytes(), StandardCharsets.UTF_8);
                            Spinner spinner = findViewById(R.id.spinner);
                            if (message.equals("1")) {
                                spinner.setSelection(0);
                            } else {
                                spinner.setSelection(1);
                            }
                        }
                    });
                })
                .send();
    }
}