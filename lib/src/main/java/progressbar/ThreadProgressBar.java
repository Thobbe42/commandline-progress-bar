package progressbar;

public class ThreadProgressBar extends ProgressBar{
    protected ThreadProgressBar(int target, int percentagePerStep) {
        super(target, percentagePerStep);
    }

    @Override
    public synchronized void step() {
        super.step();
    }

    @Override
    public synchronized void step(int steps) {
        super.step(steps);
    }
}
