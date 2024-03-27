package progressbar;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class SimpleProgressBar implements ProgressBar{

    private final int target;
    private final int percentagePerStep;
    private int progress;
    private double percentage;
    private String state;
    protected SimpleProgressBar(final int target, final int percentagePerStep){
        this.target = target;
        this.percentagePerStep = percentagePerStep;
        progress = 0;
        percentage = 0.0;
        createState();
    }
    @Override
    public void step() {
        step(1);
    }

    @Override
    public void step(int steps) {
        progress += steps;
        calculatePercentage();
        createState();
    }

    @Override
    public void print() {
        System.out.print(state);
    }

    //utility methods

    /**
     * Calculate the percentage of the progress
     * based on the specified target.
     */
    private void calculatePercentage(){
        DecimalFormat df = new DecimalFormat("####0.00");
        df.setRoundingMode(RoundingMode.HALF_UP);
        double val = ((double) progress / target) * 100;
        percentage = Double.parseDouble(df.format(val));
    }

    /**
     * Creates the String representation of the state
     * of this ProgressBar.
     */
    private void createState(){
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
        countOfSteps = Math.ceilDiv((int)remainder, percentagePerStep);

        builder.append(".".repeat(Math.max(0, countOfSteps)))
                .append("] ")
                .append(percentage)
                .append("%");

        state = builder.toString();
    }
}
