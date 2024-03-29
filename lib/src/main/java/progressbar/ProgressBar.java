package progressbar;

import java.text.DecimalFormat;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class ProgressBar {

    private final int target;
    private final int percentagePerStep;
    private int progress;
    private double percentage;
    private String state;
    private boolean started = false;
    private final ScheduledExecutorService scheduler =
            Executors.newSingleThreadScheduledExecutor();

    public ProgressBar(final int target, final int percentagePerStep) {
        this.target = target;
        this.percentagePerStep = percentagePerStep;
        progress = 0;
        percentage = 0.0;
        createState();
    }

    //INTERFACE METHODS

    /**
     * Progress the state of this ProgressBar by
     * one event.
     */
    public void step() {
        if (started) {
            step(1);
        }
    }

    /**
     * Progress the state of this ProgressBar by a defined
     * amount of events.
     * Terminates the ExecutorService of this ProgressBar if
     * 100% of the target is reached.
     *
     * @param steps The amount of evens to add to the progress.
     */
    public void step(int steps) {
        if (started) {
            progress += steps;
            calculatePercentage();
            createState();
            if (percentage == 100.0) {
                started = false;
                scheduler.shutdown();
                //call final print to ensure that 100% progress is always presented
                print();
            }
        }
    }

    /**
     * Start the progressbar by enabling the control functions
     * and starting the ScheduledExecutorService.
     */
    public void start() {
        started = true;
        scheduler.scheduleAtFixedRate(this::print, 0, 250, TimeUnit.MILLISECONDS);
    }


    //UTILITY METHODS


    /**
     * Print the current state of the bar to
     * System.out.
     */
    private void print() {
        System.out.print(state);
    }

    /**
     * Calculate the percentage of the progress
     * based on the specified target.
     */
    private void calculatePercentage() {
        DecimalFormat df = new DecimalFormat("####0.00");
        double val = ((double) progress / target) * 100;
        percentage = Double.parseDouble(df.format(val));
    }

    /**
     * Creates the String representation of the state
     * of this ProgressBar.
     */
    private void createState() {
        StringBuilder builder = new StringBuilder("\r[");
        double remainder = percentage;
        int countOfSteps = 0;
        //set already completed progress visually
        while (remainder >= percentagePerStep) {
            builder.append("#");
            remainder -= percentagePerStep;
            countOfSteps++;
        }

        //set remaining progress visually
        remainder = 100 - (countOfSteps * percentagePerStep);
        countOfSteps = Math.ceilDiv((int) remainder, percentagePerStep);

        builder.append(".".repeat(Math.max(0, countOfSteps)))
                .append("] ")
                .append(percentage)
                .append("%");

        state = builder.toString();
    }

    /**
     * Creates a builder object to construct and configure
     * a ProgressBar.
     *
     * @return a new Builder object.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private int target = 100;
        private int percentageBlockSize = 5;

        /**
         * Set the target for the ProgressBar.
         *
         * @param target A numerical value describing the amount
         *               of events considered as 100 percent.
         * @return This builder with the target property set.
         * @throws IllegalArgumentException if the actual parameter is less
         *                                  than or equal to zero.
         */
        public Builder target(final int target) throws IllegalArgumentException {
            if (target <= 0)
                throw new IllegalArgumentException("Target must be greater than zero but was " + target);
            this.target = target;
            return this;
        }

        /**
         * Set the step size for the ProgressBar.
         *
         * @param percentageBlockSize A numerical value describing the size
         *                            of one visual step, i.e. a percentageBlockSize
         *                            of 5 partitions the bar in 20 5% steps.
         * @return This builder with the percentageBlockSize property set.
         * @throws IllegalArgumentException if the actual parameter is less
         *                                  than or equal to zero.
         */
        public Builder percentageBlockSize(final int percentageBlockSize) throws IllegalArgumentException {
            if (percentageBlockSize <= 0)
                throw new IllegalArgumentException(
                        "PercentageBlockSize must be greater than zero but was " + percentageBlockSize
                );
            this.percentageBlockSize = percentageBlockSize;
            return this;
        }

        /**
         * Creates the ProgressBar object as configured by
         * this builder.
         *
         * @return A ProgressBar object as configured by the
         * properties of this builder.
         */
        public ProgressBar create() {
            return new SimpleProgressBar(target, percentageBlockSize);
        }
    }
}
