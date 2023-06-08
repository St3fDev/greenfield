package beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;
@XmlRootElement
@XmlAccessorType (XmlAccessType.FIELD)
public class GreenfieldModel {

    //private final HashMap<String, CleaningRobotData> robots = new HashMap<String, CleaningRobotData>();


    private List<CleaningRobotData> robots;

    private final int[] districts;

    private GreenfieldModel() {
        robots = new ArrayList<>();
        districts = new int[4];
    }
    private static GreenfieldModel instance;

    public synchronized List<CleaningRobotData> getRobots() {
        //return new ArrayList<>(robots.values());
        return new ArrayList<>(robots);
    }

    public synchronized void setRobots(List<CleaningRobotData> robots) {
        this.robots = robots;
    }

    public static synchronized GreenfieldModel getInstance() {
        if (instance == null)
            instance = new GreenfieldModel();
        return instance;
    }

    private int MinNumberOfCleaningRobotPerDistrict() {
        int minIndex = 0;
        // TODO aggiungere sincronizzazione
        for (int i = 1; i < districts.length; i++) {
            if (districts[i] < districts[minIndex]) {
                minIndex = i;
            }
        }

        return minIndex;
    }

    private Position generateRandomPosition(int index, CleaningRobotData cleaningRobot) {
        Random rand = new Random();
        switch (index) {
            case 0:
                districts[0] += 1;
                cleaningRobot.setDistrict(0);
                return new Position(rand.nextInt(5), rand.nextInt(5));
            case 1:
                districts[1] += 1;
                cleaningRobot.setDistrict(1);
                return new Position(rand.nextInt(5), rand.nextInt(5)+5);
            case 2:
                districts[2] += 1;
                cleaningRobot.setDistrict(2);
                return new Position(rand.nextInt(5) + 5, rand.nextInt(5));
            case 3:
                districts[3] += 1;
                cleaningRobot.setDistrict(3);
                return new Position(rand.nextInt(5)+5, rand.nextInt(5)+5);
        }
        return null;
    }

    public boolean addRobot(CleaningRobotData cleaningRobot) {
        for (CleaningRobotData robot: robots) {
            if (robot.getId().equals(cleaningRobot.getId())) {
                return false;
            }
        }
        /*if (robots.containsKey(cleaningRobot.getId())) {
            return false;
        }*/
        int indexOfDistrict = MinNumberOfCleaningRobotPerDistrict();
        Position position = generateRandomPosition(indexOfDistrict, cleaningRobot);
        cleaningRobot.setPosition(position);
        synchronized (this.robots) {
            //robots.add(cleaningRobot.getId(), cleaningRobot);
            robots.add(cleaningRobot);
        }
        return true;
    }

    /*public void removeRobot(String id) {
        CleaningRobotData robotToRemove = robots.get(id);
        System.out.println(robotToRemove.getId());
        districts[robotToRemove.getDistrict()] -= 1;
        synchronized (this.robots) {
            robots.remove(id);
        }
    }*/

}

// TODO risolvere perchè non va la delete
