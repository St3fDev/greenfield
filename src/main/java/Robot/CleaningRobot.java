package Robot;

import GRPC.RobotGRPCServer;
import MQTT.RobotMqttSensorPublisher;
import beans.BufferImpl;
import beans.CleaningRobotData;
import beans.GreenfieldDetails;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import simulators.PM10Simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CleaningRobot {

    private static final String SERVER_ADDRESS = "http://localhost:1337";
    private static final String ID = "id-" + (int) (1 + Math.random() * 1000);
    private static String robotAddress;
    private static int robotPort;
    private static final List<Thread> threadsToStop = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        Client client = Client.create();
        ClientResponse clientResponse;
        setRobotsDetails();

        CleaningRobotData cleaningRobot = new CleaningRobotData(ID, robotAddress, robotPort);

        CleaningRobotDetails.getInstance().setRobotInfo(cleaningRobot);
        clientResponse = RESTMethod.postRequest(client);
        //System.out.println(clientResponse.toString());

        GreenfieldDetails details = clientResponse.getEntity(GreenfieldDetails.class);

        cleaningRobot.setPosition(details.getPosition());
        if (details.getRobots() != null)
            CleaningRobotDetails.getInstance().setRobots(details.getRobots());
        cleaningRobot.setDistrict(details.getDistrict());

        RobotGRPCServer.startGRPCServer();

        if (CleaningRobotDetails.getInstance().getRobots().size() > 0) {
            List<RobotPresentationThread> presentationThreads = new ArrayList<>();
            for (CleaningRobotData robotToPresent : CleaningRobotDetails.getInstance().getRobots()) {
                RobotPresentationThread presentation = new RobotPresentationThread(cleaningRobot, robotToPresent);
                presentationThreads.add(presentation);
                presentation.start();
            }

            for (RobotPresentationThread thread : presentationThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        RobotMqttSensorPublisher sensorPublisher = new RobotMqttSensorPublisher();
        sensorPublisher.start();
        threadsToStop.add(sensorPublisher);

        MalfunctionThread robotProblems = new MalfunctionThread();
        robotProblems.start();
        threadsToStop.add(robotProblems);

        BufferImpl buffer = new BufferImpl();
        PM10Simulator simulator = new PM10Simulator(buffer);
        PM10Consumer consumer = new PM10Consumer(buffer);
        simulator.start();
        consumer.start();
        threadsToStop.add(simulator);
        threadsToStop.add(consumer);
        HeartbeatThread hbRobot = new HeartbeatThread();
        hbRobot.start();
        threadsToStop.add(hbRobot);
        RobotInputThread robotInput = new RobotInputThread(threadsToStop);
        robotInput.start();
    }

    // TODO gestire input errati
    private static void setRobotsDetails() {
        Scanner in = new Scanner(System.in);
        boolean validPort = false;

        System.out.println("Type the address of the robot: ");
        System.out.print("> ");
        robotAddress = in.next();
        while (!validPort) {
            System.out.println("Type the port of the robot: ");
            System.out.print("> ");
            robotPort = Integer.parseInt(in.next());

            validPort = true; // Assume che la porta sia valida

            // Verifica se l'indirizzo o la porta sono già occupati
            if (RobotPortChecker.isPortOccupied(robotAddress, robotPort)) {
                System.out.println("The specified port is already occupied. Please choose another port.");
                validPort = false; // La porta non è valida
            }

            if (!validPort) {
                System.out.println("Invalid address or port. Please try again.");
            }
        }

    }


}
