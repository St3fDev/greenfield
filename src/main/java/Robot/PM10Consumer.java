package Robot;

import beans.BufferImpl;
import beans.Statistic;
import simulators.Measurement;

import java.util.List;

public class PM10Consumer extends Thread {
    private BufferImpl buffer;

    public PM10Consumer(BufferImpl buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        while (true) {
            synchronized (buffer) {
                while (buffer.getMeasurements().size() < 8) {
                    try {
                        buffer.wait(); // Consumer attende finché il buffer non raggiunge la dimensione desiderata
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                List<Measurement> measurementToProcess = buffer.readAllAndClean();
                System.out.println("NEL CONSUMER CAZZO:");
                /*for(Measurement me : measurements) {
                System.out.println(me.getValue());
                 }*/
                CleaningRobotDetails.getInstance().addStatistic(calculateAverage(measurementToProcess));
            }
        }
    }

    private Statistic calculateAverage(List<Measurement> measurements) {
        double sum = 0.0;
        for (Measurement m : measurements) {
            sum += m.getValue();
        }
        return new Statistic(sum / measurements.size(), System.currentTimeMillis());
    }
}