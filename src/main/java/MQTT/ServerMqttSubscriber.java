package MQTT;

import beans.GreenfieldModel;
import beans.PollutionData;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import org.eclipse.paho.client.mqttv3.*;

import java.sql.Timestamp;
import java.util.Scanner;

public class ServerMqttSubscriber {

    static HttpServer server;

    public ServerMqttSubscriber(HttpServer server) {
        this.server = server;
    }

    private static final String BROKER_ADDRESS = "tcp://localhost:1883";
    private static final String ID = MqttClient.generateClientId();
    private static final String[] TOPICS = {"greenfield/pollution/district1", "greenfield/pollution/district2", "greenfield/pollution/district3", "greenfield/pollution/district4"};
    private static final int[] QOSs = {2, 2, 2, 2};

    public void startMQTTSubscriber() {
        try {
            MqttClient client = new MqttClient(ServerMqttSubscriber.BROKER_ADDRESS, ServerMqttSubscriber.ID);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);

            System.out.printf("(%s) connection to the broker %s...\n", ServerMqttSubscriber.BROKER_ADDRESS, ServerMqttSubscriber.ID);
            client.connect(connOpts);
            System.out.printf("(%s) connected\n", ServerMqttSubscriber.ID);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.printf("(%s) connection lost. Caused by: %s\n", ServerMqttSubscriber.ID, cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String time = new Timestamp(System.currentTimeMillis()).toString();
                    String receivedMessage = new String(message.getPayload());
                    PollutionData averages = new Gson().fromJson(receivedMessage, PollutionData.class);
                    GreenfieldModel.getInstance().addRobotStatistic(averages);
                    System.out.println("VALUE RECEIVED");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // not used
                }
            });

            System.out.printf("(%s) subscribing to the topic...\n", ServerMqttSubscriber.ID);
            client.subscribe(ServerMqttSubscriber.TOPICS, ServerMqttSubscriber.QOSs);
            System.out.printf("(%s) topic registration completed\n", ServerMqttSubscriber.ID);

            System.out.println("Press a button to stop...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            System.out.println("Stopping Administrator server");
            server.stop(0);
            System.out.println("Administrator server stopped");
            client.disconnect();
        } catch (MqttException me) {
            System.out.println("reason " + me.getReasonCode());
            System.out.println("msg " + me.getMessage());
            System.out.println("loc " + me.getLocalizedMessage());
            System.out.println("cause " + me.getCause());
            System.out.println("excep " + me);
            me.printStackTrace();
        }
    }
}
