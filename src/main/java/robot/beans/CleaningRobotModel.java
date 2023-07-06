package robot.beans;

import common.CleaningRobotData;
import common.Statistic;

import java.util.ArrayList;
import java.util.List;

public class CleaningRobotModel {

    private final List<CleaningRobotData> robots = new ArrayList<>();
    private final List<Statistic> averages = new ArrayList<>();
    private CleaningRobotData robotInfo;
    private volatile boolean isInMaintenance;
    private volatile boolean waitingForMaintenance;
    private long timestamp;
    private final Object sizeListLock = new Object();
    private final Object lock = new Object();
    private final Object waitingForMaintenanceLock = new Object();
    private final Object isInMaintenanceLock = new Object();

    public boolean isWaitingForMaintenance() {
        synchronized (waitingForMaintenanceLock) {
            return waitingForMaintenance;
        }
    }

    public void setWaitingForMaintenance(boolean waitingForMaintenance) {
        synchronized (waitingForMaintenanceLock) {
            this.waitingForMaintenance = waitingForMaintenance;
        }
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    private static CleaningRobotModel instance = null;
    public static synchronized CleaningRobotModel getInstance() {
        if (instance == null)
            instance = new CleaningRobotModel();
        return instance;
    }

    public void addRobot(CleaningRobotData robot) {
        synchronized (this.robots) {
            this.robots.add(robot);
        }
    }

    public void addRobotList(List<CleaningRobotData> robotList) {
        synchronized (this.robots) {
            robots.addAll(robotList);
        }
    }

    public void removeRobot(String robotId) {
        synchronized (this.robots) {
            robots.removeIf(robot -> robot.getId().equals(robotId));
        }
    }

    public List<Statistic> getAverages() {
        synchronized (this.averages) {
            return averages;
        }
    }

    public void clearLastAvg() {
        synchronized (this.averages) {
            averages.clear();
        }
    }

    public boolean isInMaintenance() {
        synchronized (isInMaintenanceLock) {
            return isInMaintenance;
        }
    }

    public void setInMaintenance(boolean isInMaintenance) {
        synchronized (isInMaintenanceLock) {
            this.isInMaintenance = isInMaintenance;
        }
    }

    public List<CleaningRobotData> getRobots() {
        synchronized (this.robots) {
            return robots;
        }
    }

    public CleaningRobotData getRobotInfo() {
        return robotInfo;
    }

    public void setRobotInfo(CleaningRobotData robotInfo) {
        this.robotInfo = robotInfo;
    }

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

    public Object getSizeListLock() {
         return sizeListLock;
    }

}
