package server.MQTT;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import common.PollutionData;
import org.eclipse.paho.client.mqttv3.*;
import server.beans.GreenfieldModel;

import java.util.Scanner;
import java.util.logging.Logger;

public class ServerMqttSubscriber {

    private static final Logger LOG = Logger.getLogger(ServerMqttSubscriber.class.getName());
    private final HttpServer server;

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

            LOG.info(String.format("(%s) connection to the broker %s...", ServerMqttSubscriber.BROKER_ADDRESS, ServerMqttSubscriber.ID));
            client.connect(connOpts);
            LOG.info(String.format("(%s) connected", ServerMqttSubscriber.ID));

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    LOG.warning(String.format("(%s) connection lost. Caused by: %s\n", ServerMqttSubscriber.ID, cause.getMessage()));
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    String receivedMessage = new String(message.getPayload());
                    PollutionData averages = new Gson().fromJson(receivedMessage, PollutionData.class);
                    GreenfieldModel.getInstance().addRobotStatistic(averages);
                    System.out.println("VALUE RECEIVED FROM " + averages.getRobotId());
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // not used
                }
            });

            LOG.info(String.format("(%s) subscribing to the topic...", ServerMqttSubscriber.ID));
            client.subscribe(ServerMqttSubscriber.TOPICS, ServerMqttSubscriber.QOSs);
            LOG.info(String.format("(%s) topic registration completed", ServerMqttSubscriber.ID));

            System.out.println("Press a button to stop...");
            Scanner scanner = new Scanner(System.in);
            scanner.nextLine();
            LOG.info("Stopping Administrator server");
            server.stop(0);
            LOG.info("Administrator server stopped");
            client.disconnect();
        } catch (MqttException me) {
            LOG.warning("reason " + me.getReasonCode());
            LOG.warning("msg " + me.getMessage());
            LOG.warning("loc " + me.getLocalizedMessage());
            LOG.warning("cause " + me.getCause());
            LOG.warning("excep " + me);
            me.printStackTrace();
        }
    }
}
