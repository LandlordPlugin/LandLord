package biz.princeps.landlord.multi;

import biz.princeps.landlord.api.ILandLord;
import biz.princeps.landlord.api.IMultiTask;
import biz.princeps.landlord.api.IMultiTaskManager;
import org.bukkit.Bukkit;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class MultiTaskManager implements IMultiTaskManager {

    private final ILandLord plugin;
    private final Deque<IMultiTask> queue;

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

        for (Iterator<IMultiTask> iterator = queue.iterator(); iterator.hasNext() && operations < limit; ) {
            IMultiTask multiTask = iterator.next();
            operations += multiTask.processOperations(limit - operations);

            if (multiTask.isCompleted()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void enqueueTask(IMultiTask multiTask) {
        if (Bukkit.isPrimaryThread()) {
            queue.add(multiTask);
        } else {
            Bukkit.getScheduler().runTask(plugin.getPlugin(), () ->
                    queue.add(multiTask));
        }
    }

}