package Robot;

import beans.Position;
import beans.Statistic;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
public class CleaningRobotData {

    public CleaningRobotData() {}
    private String id;
    private String address;
    private int port;
    private Position position;
    private  int district;
    private transient List<CleaningRobotData> robots;
    private transient boolean isInMaintenance;
    private transient List<Statistic> averages;


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
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDistrict(int district) {
        this.district = district;
    }
    public int getDistrict() {
        return district;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean isInMaintenance() {
        return isInMaintenance;
    }

    public void setInMaintenance(boolean inMaintenance) {
        isInMaintenance = inMaintenance;
    }

    public CleaningRobotData(String id, String address, int port) {
        this.id = id;
        this.address = address;
        this.port = port;
        this.robots = new ArrayList<>();
        this.isInMaintenance = false;
        this.averages = new ArrayList<>();
    }

    public List<CleaningRobotData> getRobots() {
        return robots;
    }

    public void setRobots(List<CleaningRobotData> robots) {
        this.robots = robots;
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

    //TODO deve essere sincronizzato?
    public void addStatistic(Statistic stat) {
        synchronized (this.averages) {
            this.averages.add(stat);
        }
    }
}
