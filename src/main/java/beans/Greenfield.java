package beans;

import java.util.*;
import java.util.stream.Collectors;

public class Greenfield {

    private HashMap<CleaningRobotInfo,Cell> robots = new HashMap<CleaningRobotInfo, Cell>();

    private HashMap<String, CleaningRobotInfo> robotsTest = new HashMap<String, CleaningRobotInfo>();
    private int[] districts;

    private Greenfield() {
        districts = new int[4];
    }
    private static Greenfield instance;

    public static Greenfield getInstance() {
        if (instance == null)
            instance = new Greenfield();
        return instance;
    }

    private synchronized List<String> getRobotsIds() {
        return robots.keySet().stream().map(CleaningRobotInfo::getId).collect(Collectors.toList());
    }

    private synchronized int MinNumberOfCleaningRobotPerDistrict() {
        int minIndex = 0;
        for (int i = 1; i < districts.length; i++) {
            if (districts[i] < districts[minIndex]) {
                minIndex = i;
            }
        }
        return minIndex;
    }

    private Cell generateRandomPosition(int index) {
        Random rand = new Random();
        switch (index) {
            case 0:
                return new Cell(rand.nextInt(5), rand.nextInt(5));
            case 1:
                return new Cell(rand.nextInt(5), rand.nextInt(5)+5);
            case 2:
                return new Cell(rand.nextInt(5) + 5, rand.nextInt(5));
            case 3:
                return new Cell(rand.nextInt(5)+5, rand.nextInt(5)+5);
        }
        return null;
    }

    /*public boolean addRobot(String id) {
        if (getRobotsIds().contains(id)) {
            return false;
        }
        int indexOfDisctrict = MinNumberOfCleaningRobotPerDistrict();
        Cell cell = generateRandomPosition(indexOfDisctrict);
        synchronized (this.robots) {
            robots.put(new CleaningRobot(id),cell);
        //TODO aggiungere un robot alla mappa
        }
        return true;
    }*/

    public boolean addRobotTest(CleaningRobotInfo cleaningRobot) {
        if (robotsTest.containsKey(cleaningRobot.getId())) {
            return false;
        }
        int indexOfDisctrict = MinNumberOfCleaningRobotPerDistrict();
        Cell cell = generateRandomPosition(indexOfDisctrict);
        synchronized (this.robotsTest) {
            robotsTest.put(cleaningRobot.getId(), cleaningRobot);
            //TODO aggiungere un robot alla mappa
        }
        return true;
    }

}
