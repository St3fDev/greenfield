package Robot;

import java.util.Random;

public class MalfunctionThread extends Thread {
    private static final int DELAY = 10000; // 10 seconds delay
    private static final double MALFUNCTION_PROBABILITY = 0.1; // 10% probability

    private CleaningRobotData robot;

    private Random random;

    public MalfunctionThread(CleaningRobotData robot) {
        this.random = new Random();
        this.robot = robot;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(DELAY);

                if (random.nextDouble() < MALFUNCTION_PROBABILITY) {
                    // Perform malfunction operation
                    System.out.println("Robot malfunction occurred!");
                    robot.setInMaintenance(true);
                    // Handle the malfunction
                    handleMalfunction();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private void handleMalfunction() {
        // Implement the logic to handle the robot malfunction
        // This method will be called when a robot malfunctions
    }
}
