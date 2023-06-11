package Robot;

import beans.Position;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CleaningRobotData {

    public CleaningRobotData() {}
    private String id;
    private String address;
    private String port;
    private Position position;
    private int district;

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
    public String getPort() {
        return port;
    }

    public void setPort(String port) {
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

    public CleaningRobotData(String id, String address, String port) {
        this.id = id;
        this.address = address;
        this.port = port;
    }
}
