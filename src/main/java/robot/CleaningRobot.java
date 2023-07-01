package robot;

import robot.GRPC.RobotGRPCServer;
import robot.MQTT.RobotMqttPublisher;
import robot.beans.BufferImpl;
import common.CleaningRobotData;
import robot.beans.CleaningRobotDetails;
import server.beans.GreenfieldDetails;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import robot.Threads.*;
import robot.simulators.PM10Simulator;
import common.RESTMethods;
import robot.utils.RobotPortChecker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CleaningRobot {

    private static final String ID = "id-" + (int) (1 + Math.random() * 1000);
    private static String robotAddress;
    private static int robotPort;
    private static final List<Thread> threadsToStop = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        Client client = Client.create();
        ClientResponse clientResponse;
        setAddressAndPort();

        CleaningRobotData cleaningRobot = new CleaningRobotData(ID, robotAddress, robotPort);

        CleaningRobotDetails.getInstance().setRobotInfo(cleaningRobot);
        clientResponse = RESTMethods.postRequest(client);
        //System.out.println(clientResponse.toString());

        GreenfieldDetails details = clientResponse.getEntity(GreenfieldDetails.class);

        cleaningRobot.setPosition(details.getPosition());
        if (details.getRobots() != null)
            CleaningRobotDetails.getInstance().setRobots(details.getRobots());
        cleaningRobot.setDistrict(details.getDistrict());

        RobotGRPCServer.startGRPCServer();

        if (CleaningRobotDetails.getInstance().getRobots().size() > 0) {
            List<RobotPresentationManager> presentationThreads = new ArrayList<>();
            for (CleaningRobotData robotToPresent : CleaningRobotDetails.getInstance().getRobots()) {
                RobotPresentationManager presentation = new RobotPresentationManager(cleaningRobot, robotToPresent);
                presentationThreads.add(presentation);
                presentation.start();
            }

            for (RobotPresentationManager thread : presentationThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        RobotMqttPublisher sensorPublisher = new RobotMqttPublisher();
        sensorPublisher.start();
        threadsToStop.add(sensorPublisher);

        MalfunctionManager robotProblems = new MalfunctionManager();
        robotProblems.start();
        threadsToStop.add(robotProblems);

        BufferImpl buffer = new BufferImpl();
        PM10Simulator simulator = new PM10Simulator(buffer);
        PM10Consumer consumer = new PM10Consumer(buffer);
        simulator.start();
        consumer.start();
        threadsToStop.add(simulator);
        threadsToStop.add(consumer);
        HeartbeatManager hbRobot = new HeartbeatManager();
        hbRobot.start();
        threadsToStop.add(hbRobot);
        RobotInputManager robotInput = new RobotInputManager(threadsToStop);
        robotInput.start();
    }

    private static void setAddressAndPort() {
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
