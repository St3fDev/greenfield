package server.beans;

import common.Statistic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class PollutionData {
    private String robotId;
    private List<Statistic> pollutionAverages;
    private long timestamp;

    public PollutionData(){}

    public PollutionData(String robotId, List<Statistic> pollutionAverages, long timestamp) {
        this.robotId = robotId;
        this.pollutionAverages = pollutionAverages;
        this.timestamp = timestamp;
    }


    public String getRobotId() {
        return robotId;
    }

    public void setRobotId(String robotId) {
        this.robotId = robotId;
    }

    public List<Statistic> getPollutionAverages() {
        return pollutionAverages;
    }

    public void setPollutionAverages(List<Statistic> pollutionAverages) {
        this.pollutionAverages = pollutionAverages;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

