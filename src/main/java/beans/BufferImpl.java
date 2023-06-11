package beans;

import simulators.Buffer;
import simulators.Measurement;

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
        synchronized (this.measurements) {
            measurements.add(m);
        }
        if (measurements.size() >= BUFFER_SIZE) {
            List<Measurement> measurementsToProcess = new ArrayList<>(measurements);

            measurements.subList(0, (int) (BUFFER_SIZE / OVERLAP_FACTOR)).clear();
            // Calcola la media delle misurazioni nel buffer
            double media = calculateAverage(measurementsToProcess);
            // Invia la media al server amministratore
            //sendAverageToServer(media);
        }
    }

    @Override
    public List<Measurement> readAllAndClean() {
        List<Measurement> measurementsToReturn = new ArrayList<>(measurements);
        measurements.clear();
        return measurementsToReturn;
    }

    private double calculateAverage(List<Measurement> measurements) {
        double sum = 0.0;
        for (Measurement m : measurements) {
            sum += m.getValue();
        }
        return sum / measurements.size();
    }

}
