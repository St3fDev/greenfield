package beans;

import simulators.Buffer;
import simulators.Measurement;

import java.util.List;

public class PollutionSensorBuffer implements Buffer {

    @Override
    public void addMeasurement(Measurement m) {

    }

    @Override
    public List<Measurement> readAllAndClean() {
        return null;
    }
}
