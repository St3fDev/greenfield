package beans;

public class CleaningRobotInfo {

    public CleaningRobotInfo() {}
    private String id;
    private String address;
    private String port;

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public String getPort() {
        return port;
    }

    public CleaningRobotInfo(String id, String address, String port) {
        this.id = id;
        this.address = address;
        this.port = port;
    }


}
