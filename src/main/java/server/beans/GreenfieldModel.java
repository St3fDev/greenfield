package server.beans;

import common.CleaningRobotData;
import common.Position;

import java.util.*;
import java.util.stream.Collectors;


public class GreenfieldModel {

    private List<CleaningRobotData> robots;
    private Map<String,List<Statistic>> robotStatistics;
    private final int[] districts;

    private GreenfieldModel() {
        robots = new ArrayList<>();
        districts = new int[4];
        robotStatistics = new HashMap<>();
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
                return new Position(rand.nextInt(5)+5, rand.nextInt(5)+5);
            case 3:
                districts[3] += 1;
                cleaningRobot.setDistrict(4);
                return new Position(rand.nextInt(5) + 5, rand.nextInt(5));
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

    public boolean removeRobot(String id) {
        CleaningRobotData tempRobot = null;
        synchronized (this.robots) {
            for (CleaningRobotData robot : robots) {
                if (robot.getId().equals(id)) {
                    tempRobot = robot;
                }
            }
        }
        if (tempRobot != null) {
            districts[tempRobot.getDistrict()-1] -= 1;
            synchronized (this.robots) {
                robots.removeIf((elem) -> elem.getId().equals(id));
            }
            return true;
        }
        return false;
    }

    // STATISTICS:
    public synchronized List<CleaningRobotData> getRobots() {
        return new ArrayList<>(robots);
    }

    public synchronized Map<String, List<Statistic>> getRobotStatistics() {
        return robotStatistics;
    }

    public synchronized void setRobotStatistics(Map<String, List<Statistic>> robotStatistics) {
        this.robotStatistics = robotStatistics;
    }

    public Double avgLastNAirPollutionLevel(String id, int n) {
        List<Statistic> statistics;
        synchronized (this.robotStatistics) {
            if(!robotStatistics.containsKey(id)) {
                return 0.0;
            }
            statistics = new ArrayList<>(robotStatistics.get(id));
        }
        List<Double> averages = statistics.stream()
                .map(Statistic::getAverage)
                .collect(Collectors.toList());
        List<Double> lastN = averages.subList(Math.max(averages.size() - n, 0), averages.size());
        return lastN.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    public double averageAirPollutionLevelInRange(long t1, long t2) {

        Map<String,List<Statistic>> averages;
        synchronized (this.robotStatistics) {
            averages = new HashMap<>(robotStatistics);
        }
        return averages.values().stream()
                .flatMap(List::stream)
                .filter(statistic -> statistic.getTimestamp() >= t1 && statistic.getTimestamp() <= t2)
                .map(Statistic::getAverage)
                .collect(Collectors.toList()).stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    public void addRobotStatistic(PollutionData average) {
        String robotId = average.getRobotId();
        List<Statistic> existingAverages = robotStatistics.getOrDefault(robotId, new ArrayList<>());
        List<Statistic> newAverages = average.getPollutionAverages();

        existingAverages.addAll(newAverages);
        robotStatistics.put(robotId, existingAverages);
    }

}
