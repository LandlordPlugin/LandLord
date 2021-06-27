package biz.princeps.landlord.api;

public interface IMultiTask {

    /**
     * Process the queued operations.
     *
     * @param limit the max amount of actions processed
     * @return the amount of processed operations
     */
    int processOperations(int limit);

    /**
     * Clear all remaining queued operations.
     */
    void clear();

    /**
     * Check if the task is completed.
     *
     * @return if all the operations have been processed
     */
    boolean isCompleted();

}
