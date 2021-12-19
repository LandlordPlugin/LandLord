package biz.princeps.landlord.api;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

public abstract class AMultiTask<T> implements IMultiTask<T> {

    protected final ILandLord plugin;
    protected final Deque<T> queue;

    public AMultiTask(ILandLord plugin, Collection<T> operations) {
        this.plugin = plugin;
        this.queue = new ArrayDeque<>(operations);
    }

    @Override
    public int processOperations(int limit) {
        int iterations = 0;

        while (iterations < limit && !queue.isEmpty()) {
            T value = queue.remove();

            boolean success = process(value);
            if (success) {
                iterations++;
            }
        }

        return iterations;
    }

    @Override
    public void complete() {
    }

    @Override
    public boolean isCompleted() {
        return queue.isEmpty();
    }

    @Override
    public int clearQueue() {
        int remainingOperations = queue.size();
        queue.clear();
        return remainingOperations;
    }

}
