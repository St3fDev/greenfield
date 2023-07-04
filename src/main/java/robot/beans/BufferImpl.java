package robot.beans;

import robot.simulators.Buffer;
import robot.simulators.Measurement;

import java.util.ArrayList;
import java.util.List;

public class BufferImpl implements Buffer {

    private static final int BUFFER_SIZE = 8;
    private static final double OVERLAP_FACTOR = 0.5;
    private List<Measurement> measurements;
    public BufferImpl() {

        this.measurements = new ArrayList<>();

    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }
    @Override
    public void addMeasurement(Measurement m) {
            synchronized (this) {
                measurements.add(m);
                if (measurements.size() == BUFFER_SIZE) {
                    notify();
                }
            }
    }

    @Override
    public List<Measurement> readAllAndClean() {
        List<Measurement> measurementsToProcess = new ArrayList<>(measurements);
        measurements.subList(0, (int) (BUFFER_SIZE * OVERLAP_FACTOR)).clear();
        return measurementsToProcess;
    }

}
// TODO aggiungere sincronizzazione?