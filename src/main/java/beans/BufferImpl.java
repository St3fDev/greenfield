package beans;

import Robot.CleaningRobotData;
import simulators.Buffer;
import simulators.Measurement;

import java.util.ArrayList;
import java.util.List;

public class BufferImpl implements Buffer {

    private static final int BUFFER_SIZE = 8;
    private static final double OVERLAP_FACTOR = 0.5;

    private List<Measurement> measurements;
    private final CleaningRobotData robot;
    public BufferImpl(CleaningRobotData robot) {

        this.measurements = new ArrayList<>();
        this.robot = robot;

    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<Measurement> measurements) {
        this.measurements = measurements;
    }
    @Override
    public void addMeasurement(Measurement m) {
        measurements.add(m);
        if (measurements.size() == BUFFER_SIZE) {
            readAllAndClean();
        }
    }

    @Override
    public List<Measurement> readAllAndClean() {
        List<Measurement> measurementsToProcess = new ArrayList<>(measurements);
        measurements.subList(0, (int) (BUFFER_SIZE * OVERLAP_FACTOR)).clear();
        robot.addStatistic(calculateAverage(measurementsToProcess));
        return null;
    }

    private Statistic calculateAverage(List<Measurement> measurements) {
        double sum = 0.0;
        for (Measurement m : measurements) {
            sum += m.getValue();
        }
        return new Statistic(sum/measurements.size(), System.currentTimeMillis());
    }

}
// TODO aggiungere sincronizzazione?