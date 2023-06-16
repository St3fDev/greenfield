package beans;

import Robot.CleaningRobotData;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
import java.util.stream.Collectors;

@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class GreenfieldModel {

    private List<CleaningRobotData> robots;
    private final int[] districts;

    private GreenfieldModel() {
        robots = new ArrayList<>();
        districts = new int[4];
    }
    private static GreenfieldModel instance;

    public static synchronized GreenfieldModel getInstance() {
        if (instance == null)
            instance = new GreenfieldModel();
        return instance;
    }

    private synchronized List<String> getIds() {
        return robots.stream().map(CleaningRobotData::getId).collect(Collectors.toList());
    }

    public synchronized void setRobots(List<CleaningRobotData> robots) {
        this.robots = robots;
    }


    private int MinNumberOfCleaningRobotPerDistrict() {
        int minIndex = 0;
        synchronized (this.districts) {
            for (int i = 1; i < districts.length; i++) {
                if (districts[i] < districts[minIndex]) {
                    minIndex = i;
                }
            }
        }
        return minIndex;
    }

    private Position generateRandomPosition(int index, CleaningRobotData cleaningRobot) {
        Random rand = new Random();
        switch (index) {
            case 0:
                districts[0] += 1;
                cleaningRobot.setDistrict(1);
                return new Position(rand.nextInt(5), rand.nextInt(5));
            case 1:
                districts[1] += 1;
                cleaningRobot.setDistrict(2);
                return new Position(rand.nextInt(5), rand.nextInt(5)+5);
            case 2:
                districts[2] += 1;
                cleaningRobot.setDistrict(3);
                return new Position(rand.nextInt(5) + 5, rand.nextInt(5));
            case 3:
                districts[3] += 1;
                cleaningRobot.setDistrict(4);
                return new Position(rand.nextInt(5)+5, rand.nextInt(5)+5);
        }
        return null;
    }
    // HANDLE ROBOTS:
    public boolean addRobot(CleaningRobotData cleaningRobot) {
        if (getIds().contains(cleaningRobot.getId())) {
            return false;
        }
        int indexOfDistrict = MinNumberOfCleaningRobotPerDistrict();
        Position position = generateRandomPosition(indexOfDistrict, cleaningRobot);
        cleaningRobot.setPosition(position);
        synchronized (this.robots) {
            robots.add(cleaningRobot);
        }
        return true;
    }

    public void removeRobot(String id) {
        CleaningRobotData tempRobot = null;
        for (CleaningRobotData robot : robots) {
            if (robot.getId().equals(id)) {
                tempRobot = robot;
            }
        }
        districts[tempRobot.getDistrict()-1] -= 1;
        synchronized (this.robots) {
            robots.removeIf((elem) -> elem.getId().equals(id));
        }
    }

    // STATISTICS:
    public synchronized List<CleaningRobotData> getRobots() {
        return new ArrayList<>(robots);

    }

    /*public List<Double> avgLastNAirPollutionLevel(String id, int n) {
        List<Double> measurements = robotMeasurements.get(id);
        if (measurements != null && measurements.size() >= n) {
            List<Double> lastNMeasurements = measurements.subList(measurements.size() - n, measurements.size()); // Prendi gli ultimi n elementi dalla lista
            double average = calculateAverage(lastNMeasurements); // Calcola la media delle ultime n medie

            // Utilizza la media come desiderato
            System.out.println("Media degli ultimi " + n + " livelli di inquinamento del robot " + robotId + ": " + average);
        } else {
            // Non ci sono sufficienti medie disponibili per calcolare la media degli ultimi n livelli
            System.out.println("Non ci sono sufficienti misurazioni disponibili per il robot " + robotId);
        }
    }*/


}
