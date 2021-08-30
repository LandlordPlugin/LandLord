package biz.princeps.landlord.multi;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMultiTask;
import biz.princeps.landlord.api.IMultiTaskManager;
import org.bukkit.Bukkit;

import java.util.ArrayDeque;
import java.util.Deque;

public class MultiTaskManager implements IMultiTaskManager {

    private final ILandLord plugin;
    private final Deque<IMultiTask<?>> queue;

    public MultiTaskManager(ILandLord plugin) {
        this.plugin = plugin;
        this.queue = new ArrayDeque<>(64);
    }

    @Override
    public void initTask() {
        Bukkit.getScheduler().runTaskTimer(plugin.getPlugin(), () -> {
            if (queue.isEmpty())
                return;

            // 10 operations max per tick seems adequate for the majority of cases.
            processQueue(10);
        }, 0L, 1L);
    }

    @Override
    public void processQueue(int limit) {
        int operations = 0;

        while (operations < limit && !queue.isEmpty()) {
            IMultiTask<?> multiTask = queue.poll();

            if (multiTask.canContinueProcessing()) {
                operations += multiTask.processOperations(limit - operations);

                if (multiTask.isCompleted()) {
                    multiTask.complete();
                    queue.remove();
                }
            } else {
                multiTask.clearQueue();
                queue.remove();
            }
        }
    }

    @Override
    public void enqueueTask(IMultiTask<?> multiTask) {
        if (Bukkit.isPrimaryThread()) {
            queue.add(multiTask);
        } else {
            Bukkit.getScheduler().runTask(plugin.getPlugin(), () ->
                    enqueueTask(multiTask));
        }
    }

    @Override
    public int clear() {
        int remainingTasks = queue.size();
        queue.clear();
        return remainingTasks;
    }

}