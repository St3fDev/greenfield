package common;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CleaningRobotData {

    private String id;
    private String address;
    private int port;
    private Position position;
    private int district;

    public CleaningRobotData() {}
    public CleaningRobotData(String id, String address, int port) {
        this.id = id;
        this.address = address;
        this.port = port;
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

    @Override
    public String toString() {
        return "CLEANING ROBOT: " + getId()
                + " POSITION: [" + getPosition().getX() + "," + getPosition().getY() + "]"
                + " DISTRICT: " + getDistrict();
    }

}

