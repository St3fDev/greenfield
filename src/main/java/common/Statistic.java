package common;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Statistic {

    private Long timestamp;
    private double average;

    public Statistic() {
        timestamp = 0L;
        average = 0.0;
    }

    public Statistic(double average, Long timestamp) {
        this.timestamp = timestamp;
        this.average = average;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }


    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }
}
