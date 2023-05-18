package beans;

public class CleaningRobotData {

    public CleaningRobotData() {}
    private String id;
    private String address;
    private String port;
    private Position position;

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getPort() {
        return port;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
    public Position getPosition() {
        return position;
    }

    public CleaningRobotData(String id, String address, String port) {
        this.id = id;
        this.address = address;
        this.port = port;
    }
}
