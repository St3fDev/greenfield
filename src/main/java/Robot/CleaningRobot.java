package Robot;

import GRPC.RobotGRPCServer;
import MQTT.RobotMqttSensorPublisher;
import beans.BufferImpl;
import beans.GreenfieldDetails;
import com.google.gson.Gson;
import com.sun.jersey.api.client.*;
import org.eclipse.paho.client.mqttv3.*;
import simulators.PM10Simulator;

import java.awt.*;
import java.util.*;
import java.util.List;

public class CleaningRobot {
    private static final String SERVER_ADDRESS = "http://localhost:1337";
    private static final String ID = "id-" + (int) (1 + Math.random() * 100);
    private static String robotAddress;
    private static final double MALFUNCTION_PROBABILITY = 0.1;
    private static int robotPort;

    public static void main(String[] args) throws InterruptedException {
        Client client = Client.create();
        ClientResponse clientResponse = null;
        setRobotsDetails();

        CleaningRobotData cleaningRobot = new CleaningRobotData(ID, robotAddress, robotPort);
        clientResponse = postRequest(client, cleaningRobot);
        System.out.println(clientResponse.toString());

        GreenfieldDetails details = clientResponse.getEntity(GreenfieldDetails.class);

        cleaningRobot.setPosition(details.getPosition());
        if (details.getRobots() != null)
            cleaningRobot.setRobots(details.getRobots());
        cleaningRobot.setDistrict(details.getDistrict());

        RobotInputHandler robotInput = new RobotInputHandler(cleaningRobot);
        robotInput.start();

        RobotGRPCServer serverGRPC = new RobotGRPCServer(cleaningRobot);
        serverGRPC.start();

        if (cleaningRobot.getRobots().size() > 0) {
            List<RobotPresentationThread> presentationThreads = new ArrayList<>();
            for (CleaningRobotData robotToPresent: cleaningRobot.getRobots()) {
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

        RobotMqttSensorPublisher sensorPublisher = new RobotMqttSensorPublisher(cleaningRobot);
        sensorPublisher.start();

        MalfunctionThread robotProblems = new MalfunctionThread(cleaningRobot);
        robotProblems.start();

        BufferImpl buffer = new BufferImpl(cleaningRobot);
        PM10Simulator simulator = new PM10Simulator(buffer);
        simulator.start();
    }

    private static void setRobotsDetails() {
        Scanner in = new Scanner(System.in);
        System.out.println("Type the address of the robot: ");
        System.out.print("> ");
        robotAddress = in.next();
        System.out.println("type the port of the robot: ");
        System.out.print(">  ");
        robotPort = Integer.parseInt(in.next());
    }

    private static ClientResponse postRequest(Client client, CleaningRobotData cleaningRobot){
        String postPath = SERVER_ADDRESS + "/robots/addRobot";
        WebResource webResource = client.resource(postPath);
        String input = new Gson().toJson(cleaningRobot);
        System.out.println(input);
        try {
            return webResource.type("application/json").post(ClientResponse.class, input);
        } catch (ClientHandlerException e) {
            System.out.println("Server unavailable");
            return null;
        }
    }

}
