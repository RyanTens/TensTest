package com.tens.communicator;

public class TaskCommunicator {
    private final String taskId;

    private boolean readerIsFinished = false;

    private boolean transformerIsFinished = false;

    private boolean writerIsFineshed = false;

    public TaskCommunicator(final String taskId) {
        this.taskId = taskId;
    }

    public String getTaskId() {
        return taskId;
    }

    public boolean isReaderIsFinished() {
        return readerIsFinished;
    }

    public synchronized void setReaderIsFinished(boolean readerIsFinished) {
        this.readerIsFinished = readerIsFinished;
    }

    public boolean isTransformerIsFinished() {
        return transformerIsFinished;
    }

    public synchronized void setTransformerIsFinished(boolean transformerIsFinished) {
        this.transformerIsFinished = transformerIsFinished;
    }

    public boolean isWriterIsFineshed() {
        return writerIsFineshed;
    }

    public synchronized void setWriterIsFineshed(boolean writerIsFineshed) {
        this.writerIsFineshed = writerIsFineshed;
    }
}
