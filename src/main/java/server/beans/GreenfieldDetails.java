package server.beans;

import common.CleaningRobotData;
import common.Position;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class GreenfieldDetails {

    private Position position;
    private List<CleaningRobotData> robots;
    private int district;

    public GreenfieldDetails() {}

    public GreenfieldDetails(Position position, List<CleaningRobotData> robots, int district) {
        this.position = position;
        this.robots = robots;
        this.district = district;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public List<CleaningRobotData> getRobots() {
        return robots;
    }

    public void setRobots(List<CleaningRobotData> robots) {
        this.robots = robots;
    }

    public int getDistrict() {
        return district;
    }

    public void setDistrict(int district) {
        this.district = district;
    }
}
