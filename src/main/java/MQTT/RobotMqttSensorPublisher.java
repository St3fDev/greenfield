package MQTT;

import Robot.CleaningRobot;
import Robot.CleaningRobotData;
import org.eclipse.paho.client.mqttv3.*;

import java.util.Random;

public class RobotMqttSensorPublisher extends Thread{

    private static final String BROKER = "tcp://localhost:1883";
    private static String topic;
    private static final int QOS = 2;
    private static final String ID = MqttClient.generateClientId();
    private static final int SLEEP_TIME = 15 * 1_000;
    CleaningRobotData robot;

    public RobotMqttSensorPublisher(CleaningRobotData robot) {
        this.robot = robot;
        topic = "greenfield/pollution/district" + robot.getDistrict();
    }

    @Override
    public void run() {
        while (true) {
            // Si genera la temperatura (un numero da 18 a 22).
            Random rand = new Random();
            // Obtain a number between [0 - 49].
            String payload = String.valueOf(rand.nextInt(50));
            // Si iniva il messaggio.
            try (MqttClient clientMqtt = new MqttClient(BROKER, ID)) {
                MqttConnectOptions connOpts = new MqttConnectOptions();
                connOpts.setCleanSession(true);

                // Connessione del client.
                System.out.printf("(%s) connessione al broker %s...\n", ID, BROKER);
                clientMqtt.connect(connOpts);
                System.out.printf("(%s) connesso\n", ID);

                // Si definisce il callback.
                clientMqtt.setCallback(new MqttCallback() {
                    public void messageArrived(String topic, MqttMessage message) {
                        // Non utilizzato dai publisher.
                    }

                    public void connectionLost(Throwable cause) {
                        System.out.printf("(%s) connessione persa. Causa: %s\n", ID, cause.getMessage());
                    }

                    public void deliveryComplete(IMqttDeliveryToken token) {
                        if (token.isComplete()) {
                            System.out.printf("(%s) messaggio consegnato con successo\n", ID);
                        }
                    }
                });

                // Si genera il messaggio.
                MqttMessage message = new MqttMessage(payload.getBytes());

                // Si definisce il QoS.
                message.setQos(QOS);
                System.out.printf("(%s) pubblicazione del nuovo messaggio: %s...\n", ID, payload);
                // Si pubblica il messaggio.
                clientMqtt.publish(topic, message);
                System.out.printf("(%s) messaggio pubblicato\n", ID);

                // Si effettua la disconnessione.
                if (clientMqtt.isConnected()) clientMqtt.disconnect();
                System.out.printf("Sensore %s disconnesso\n", ID);
            } catch (MqttException mqttException) {
                System.err.printf("Si è verificato un errore: %s\n", mqttException);
            }

            // Si aspettano cinque secondi e si invia nuovamente un messaggio.
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
