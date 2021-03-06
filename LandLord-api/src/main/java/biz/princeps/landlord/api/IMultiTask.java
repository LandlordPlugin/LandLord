package biz.princeps.landlord.api;

public interface IMultiTask<T> {

    /**
     * Process the queued operations.
     *
     * @param limit the max amount of actions processed
     * @return the amount of processed operations
     */
    default int processOperations(int limit) {
        return 0;
    }

    /**
     * Process one queued operations
     *
     * @param value the queued operation value
     * @return if value has been successfully processed
     */
    default boolean process(T value) {
        return true;
    }

    /**
     * Check if operations should be processed.
     *
     * @return if operations can still be processed
     */
    default boolean canContinueProcessing() {
        return true;
    }

    /**
     * A method that may be overriden to execute some final code,
     * <b>once all operations have been processed successfully</b>.
     * <p>
     * Will not execute if {@link #canContinueProcessing()} returns false during execution.
     */
    void complete();

    /**
     * Check if the task is completed.
     *
     * @return if all the operations have been processed
     */
    boolean isCompleted();

    /**
     * Clear all remaining queued operations.
     *
     * @return the number of remaining operations
     */
    default int clearQueue() {
        return 0;
    }

}
