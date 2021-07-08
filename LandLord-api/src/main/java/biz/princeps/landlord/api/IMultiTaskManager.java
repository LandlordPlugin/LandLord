package biz.princeps.landlord.api;

public interface IMultiTaskManager {

    /**
     * Schedule the task.
     */
    void initTask();

    /**
     * Process the queued tasks.
     *
     * @param limit the max amount of operations proccessed
     */
    void processQueue(int limit);

    /**
     * Enqueue a new multi operation task.
     *
     * @param multiTask the task to enqueue
     */
    void enqueueTask(IMultiTask multiTask);

}
