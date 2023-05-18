package beans;

import java.util.*;

public class Greenfield {

    private HashMap<String, CleaningRobotData> robots = new HashMap<String, CleaningRobotData>();
    private int[] districts;

    private Greenfield() {
        districts = new int[4];
    }
    private static Greenfield instance;

    public List<CleaningRobotData> getRobots() {
        return new ArrayList<>(robots.values());
    }

    public static Greenfield getInstance() {
        if (instance == null)
            instance = new Greenfield();
        return instance;
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

    private Position generateRandomPosition(int index) {
        Random rand = new Random();
        switch (index) {
            case 0:
                districts[0] += 1;
                return new Position(rand.nextInt(5), rand.nextInt(5));
            case 1:
                districts[1] += 1;
                return new Position(rand.nextInt(5), rand.nextInt(5)+5);
            case 2:
                districts[2] += 1;
                return new Position(rand.nextInt(5) + 5, rand.nextInt(5));
            case 3:
                districts[3] += 1;
                return new Position(rand.nextInt(5)+5, rand.nextInt(5)+5);
        }
        return null;
    }

    public boolean addRobotTest(CleaningRobotData cleaningRobot) {
        if (robots.containsKey(cleaningRobot.getId())) {
            return false;
        }
        int indexOfDisctrict = MinNumberOfCleaningRobotPerDistrict();
        Position position = generateRandomPosition(indexOfDisctrict);
        cleaningRobot.setPosition(position);
        synchronized (this.robots) {
            robots.put(cleaningRobot.getId(), cleaningRobot);

            //TODO aggiungere un robot alla mappa
        }
        return true;
    }

}
