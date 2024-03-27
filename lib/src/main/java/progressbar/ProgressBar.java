package progressbar;

public interface ProgressBar {

    //TODO: configuration methods




    /**
     * Increase the progress by one default step.
     */
    void step();

    /**
     * Increase the progress by a specified amount
     * of steps.
     *
     * @param steps The number of executed actions to
     *              add to the progress.
     */
    void step(int steps);

    /**
     * Print the state of this ProgressBar to
     * System.out.
     */
    void print();
}
