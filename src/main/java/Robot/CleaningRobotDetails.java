package Robot;

import beans.CleaningRobotData;
import beans.Statistic;

import java.util.ArrayList;
import java.util.List;

public class CleaningRobotDetails {

    private List<CleaningRobotData> robots = new ArrayList<>();
    private boolean isInMaintenance;

    private final Object lock = new Object();
    private List<Statistic> averages = new ArrayList<>();
    private CleaningRobotData robotInfo;
    private boolean waitingForMaintenance;
    private long timestamp;

    public boolean isWaitingForMaintenance() {
        return waitingForMaintenance;
    }

    public synchronized void setWaitingForMaintenance(boolean waitingForMaintenance) {
        this.timestamp = System.currentTimeMillis();
        this.waitingForMaintenance = waitingForMaintenance;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    private static CleaningRobotDetails instance = null;

    public static synchronized CleaningRobotDetails getInstance() {
        if (instance == null)
            instance = new CleaningRobotDetails();
        return instance;
    }

    public void addRobot(CleaningRobotData robot) {
        synchronized (this.robots) {
            this.robots.add(robot);
        }
    }

    public void removeRobot(String robotId) {
        synchronized (this.robots) {
            robots.removeIf(robot -> robot.getId().equals(robotId));
        }
    }

    public List<Statistic> getAverages() {
        return averages;
    }

    public void clearLastAvg() {
        synchronized (this.averages) {
            averages.clear();
        }
    }
    public void setAverages(List<Statistic> averages) {
        this.averages = averages;
    }

    public synchronized boolean isInMaintenance() {
            return isInMaintenance;
    }

    public synchronized void setInMaintenance(boolean isInMaintenance) {
        this.isInMaintenance = isInMaintenance;
    }

    public List<CleaningRobotData> getRobots() {
        return robots;
    }

    public CleaningRobotData getRobotInfo() {
        return robotInfo;
    }

    public void setRobotInfo(CleaningRobotData robotInfo) {
        this.robotInfo = robotInfo;
    }

    public void setRobots(List<CleaningRobotData> robots) {
        this.robots = robots;
    }

    //TODO deve essere sincronizzato?
    public void addStatistic(Statistic stat) {
        synchronized (this.averages) {
            this.averages.add(stat);
        }
    }
    public boolean checkTimestamp(long timestamp) {
        return this.timestamp < timestamp;
    }

    public Object getLock() {
        return lock;
    }
}
