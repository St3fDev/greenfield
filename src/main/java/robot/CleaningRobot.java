package robot;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import common.CleaningRobotData;
import common.RESTMethods;
import robot.GRPC.RobotGRPCServer;
import robot.MQTT.RobotMqttPublisher;
import robot.Threads.*;
import robot.beans.CleaningRobotModel;
import robot.simulators.BufferImpl;
import robot.simulators.PM10Simulator;
import common.IOManager;
import server.beans.GreenfieldData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class CleaningRobot {

    private static final String ID = "id-" + (int) (1 + Math.random() * 1000);
    private static final List<Thread> threadsToStop = new ArrayList<>();
    private static final Logger LOG = Logger.getLogger(CleaningRobot.class.getName());
    public static void main(String[] args) throws IOException {
        Client client = Client.create();
        ClientResponse clientResponse;
        String robotAddress = IOManager.setAddress();
        int robotPort = IOManager.setPort(robotAddress);

        CleaningRobotData cleaningRobot = new CleaningRobotData(ID, robotAddress, robotPort);

        CleaningRobotModel.getInstance().setRobotInfo(cleaningRobot);
        clientResponse = RESTMethods.postRequest(client);

        if (Objects.requireNonNull(clientResponse).getStatus() == 200) {
            GreenfieldData details = clientResponse.getEntity(GreenfieldData.class);
            cleaningRobot.setPosition(details.getPosition());
            if (details.getRobots() != null)
                CleaningRobotModel.getInstance().addRobotList(details.getRobots());
            cleaningRobot.setDistrict(details.getDistrict());
            IOManager.printRobot(cleaningRobot);

            RobotGRPCServer.startGRPCServer();

            if (CleaningRobotModel.getInstance().getRobots().size() > 0) {
                LOG.info("PRESENTATION STARTED");
                List<RobotPresentationManager> presentationThreads = new ArrayList<>();
                for (CleaningRobotData robotToPresent : CleaningRobotModel.getInstance().getRobots()) {
                    RobotPresentationManager presentation = new RobotPresentationManager(cleaningRobot, robotToPresent);
                    presentationThreads.add(presentation);
                    presentation.start();
                }

                for (RobotPresentationManager thread : presentationThreads) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        LOG.warning("Interrupted while waiting for thread to finish: " + e.getMessage());
                    }
                }
                LOG.info("PRESENTATION ENDED");
            }
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

            RobotMqttPublisher sensorPublisher = new RobotMqttPublisher();
            sensorPublisher.start();
            threadsToStop.add(sensorPublisher);

            HeartbeatManager hbRobot = new HeartbeatManager();
            hbRobot.start();
            threadsToStop.add(hbRobot);

            RobotCommandsManager robotInput = new RobotCommandsManager(threadsToStop);
            robotInput.start();
        } else {
            LOG.warning(clientResponse.getEntity(String.class));
        }
    }
}
