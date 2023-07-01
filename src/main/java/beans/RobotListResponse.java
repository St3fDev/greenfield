package beans;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
@XmlRootElement
public class RobotListResponse {

    public RobotListResponse(){}
    private List<CleaningRobotData> robots;

    public RobotListResponse(List<CleaningRobotData> robots) {
        this.robots = robots;
    }

    public List<CleaningRobotData> getRobots() {
        return robots;
    }

    public void setRobots(List<CleaningRobotData> robots) {
        this.robots = robots;
    }
}
