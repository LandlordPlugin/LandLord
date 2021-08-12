package biz.princeps.landlord.api;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

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

        for (Iterator<T> iterator = queue.iterator(); iterator.hasNext() && iterations < limit; ) {
            T value = iterator.next();

            boolean success = process(value);
            iterator.remove();
            if (success) {
                iterations++;
            }
        }

        return iterations;
    }

    @Override
    public boolean process(T value) {
        return true;
    }

    @Override
    public boolean canProcess() {
        return true;
    }

    @Override
    public void complete() {
    }

    @Override
    public boolean isCompleted() {
        return queue.isEmpty();
    }

    @Override
    public int clear() {
        int remainingOperations = queue.size();
        queue.clear();
        return remainingOperations;
    }

}
