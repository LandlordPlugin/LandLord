package biz.princeps.landlord.api;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

public abstract class AMultiTask<T> implements IMultiTask {

    protected final ILandLord plugin;
    protected final Deque<T> queue;

    public AMultiTask(ILandLord plugin, Collection<T> operations) {
        this.plugin = plugin;
        this.queue = new ArrayDeque<>(operations);
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean isCompleted() {
        return queue.isEmpty();
    }

}
