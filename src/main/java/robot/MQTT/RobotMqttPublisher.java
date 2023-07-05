package robot.MQTT;

import com.google.gson.Gson;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import robot.beans.CleaningRobotModel;
import server.beans.PollutionData;

public class RobotMqttPublisher extends Thread {

    private static final String BROKER = "tcp://localhost:1883";
    private static String topic;
    private static final int QOS = 2;
    private static final String ID = MqttClient.generateClientId();
    private static final int SLEEP_TIME = 15 * 1_000;
    private volatile boolean stopCondition = false;

    public RobotMqttPublisher() {
        topic = "greenfield/pollution/district" + CleaningRobotModel.getInstance().getRobotInfo().getDistrict();
    }

    @Override
    public void run() {
        while (!stopCondition) {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized (CleaningRobotModel.getInstance().getLock()) {
                while (CleaningRobotModel.getInstance().isWaitingForMaintenance()) {
                    try {
                        CleaningRobotModel.getInstance().getLock().wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            //TODO: per dimostrazione all'esame (la coerenza delle medie prodotte)
            //System.out.println(CleaningRobotDetails.getInstance().getAverages().stream().map(Statistic::getAverage).reduce(0.0,Double::sum) / CleaningRobotDetails.getInstance().getAverages().size());
            PollutionData data = new PollutionData(CleaningRobotModel.getInstance().getRobotInfo().getId(), CleaningRobotModel.getInstance().getAverages(), System.currentTimeMillis());
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
                CleaningRobotModel.getInstance().clearLastAvg();
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
