package MQTT;

import Robot.CleaningRobotDetails;
import beans.PollutionData;
import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.*;

public class RobotMqttSensorPublisher extends Thread {

    private static final String BROKER = "tcp://localhost:1883";
    private static String topic;
    private static final int QOS = 2;
    private static final String ID = MqttClient.generateClientId();
    private static final int SLEEP_TIME = 15 * 1_000;
    private volatile boolean stopCondition = false;

    public RobotMqttSensorPublisher() {
        topic = "greenfield/pollution/district" + CleaningRobotDetails.getInstance().getRobotInfo().getDistrict();
    }

    @Override
    public void run() {
        while (!stopCondition) {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized (CleaningRobotDetails.getInstance().getLock()) {
                while (CleaningRobotDetails.getInstance().isWaitingForMaintenance()) {
                    try {
                        CleaningRobotDetails.getInstance().getLock().wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            PollutionData data = new PollutionData(CleaningRobotDetails.getInstance().getRobotInfo().getId(), CleaningRobotDetails.getInstance().getAverages(), System.currentTimeMillis());

            String payload = new Gson().toJson(data);

            try {
                MqttClient clientMqtt = new MqttClient(BROKER, ID);
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);

                // Connessione del client.
                //System.out.printf("(%s)  connection to the broker %s...\n", ID, BROKER);
                clientMqtt.connect(connOpts);
                //System.out.printf("(%s) connected\n", ID);

                // Si genera il messaggio.
                MqttMessage message = new MqttMessage(payload.getBytes());
                CleaningRobotDetails.getInstance().clearLastAvg();
                // Si definisce il QoS.
                message.setQos(QOS);
                System.out.printf("(%s) publication of the new message: %s...\n", ID, payload);
                // Si pubblica il messaggio.
                clientMqtt.publish(topic, message);
                System.out.printf("(%s) published message\n", ID);

                // Si effettua la disconnessione.
                if (clientMqtt.isConnected()) clientMqtt.disconnect();
                System.out.printf("Sensor %s disconnected\n", ID);
            } catch (MqttException mqttException) {
                System.err.printf("An error occurred: %s\n", mqttException);
            }

        }
        System.out.println("------------- ROBOT SENSOR PUBLISHER CLOSED -------------");
    }

    public void stopMeGently() {
        stopCondition = true;
    }
}
