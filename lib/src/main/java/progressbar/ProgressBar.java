package progressbar;

import java.text.DecimalFormat;

public abstract class ProgressBar {

    private final int target;
    private final int percentagePerStep;
    private int progress;
    private double percentage;
    private String state;
    private boolean started = false;

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
     *
     * @param steps The amount of evens to add to the progress.
     */
    public void step(int steps) {
        if (started) {
            progress += steps;
            calculatePercentage();
            createState();
            print();
            if (percentage == 100.0) {
                started = false;
            }
        }
    }

    /**
     * Start the progressbar, by printing the initial state
     * and enabling the control functions.
     */
    public void start() {
        if(!started) {
            started = true;
            print();
        }
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
        private boolean threadSafe = false;

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

        public Builder threadSafe(){
            this.threadSafe = !threadSafe;
            return this;
        }

        public Builder threadSafe(final boolean threadSafe){
            this.threadSafe = threadSafe;
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
            if(threadSafe){
                return new ThreadProgressBar(target, percentageBlockSize);
            }
            return new SimpleProgressBar(target, percentageBlockSize);
        }
    }
}
