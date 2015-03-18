package org.geotoolkit.gui.javafx.util;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import org.apache.sis.util.ArgumentChecks;
import org.apache.sis.util.logging.Logging;

/**
 * Aim of the class is to regroup all time-consuming tasks to allow user having 
 * a quick look at current running tasks.
 * 
 * @author Alexis Manin (Geomatys)
 */
public class TaskManager implements Closeable {
    
    private static final Logger LOGGER = Logging.getLogger(TaskManager.class);
    
    private static final TimeUnit TIMEOUT_UNIT = TimeUnit.SECONDS;
    private static final int TIMEOUT = 10;
    
    public static final TaskManager INSTANCE = new TaskManager();
    
    private ExecutorService threadPool = Executors.newCachedThreadPool();
    
    private final ObservableList<Task> submittedTasks = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    private final ObservableList<Task> tasksInError = FXCollections.synchronizedObservableList(FXCollections.observableArrayList());
    
    // TODO : keep succeeded tasks in a sort of cache 
    
    protected TaskManager() {}
        
    public Task submit(final Runnable newTask) {
        return submit(new MockTask(newTask));
    }
    
    public Task submit(final String title, final Runnable newTask) {
        return submit(new MockTask(title, newTask));
    }
    
    public <T> Task<T> submit(final Callable<T> newTask) {
        return submit(new MockTask(newTask));
    }
    
    public <T> Task<T> submit(final String title, final Callable<T> newTask) {
        return submit(new MockTask<T>(title, newTask));
    }
    
    public synchronized <T> Task<T> submit(final Task<T> newTask) {
        ArgumentChecks.ensureNonNull("input task", newTask);
        if (!newTask.isDone()) {
            /* Automatically move the task from submitted to "In error" when its state change.
             * Or just dereference it if succeeded.
             */
            new TaskStateListener(newTask);
            Platform.runLater(()->submittedTasks.add(newTask));
            
            threadPool.submit(newTask);
        }
        return newTask;
    }
    
    public ObservableList<Task> getSubmittedTasks() {
        return submittedTasks;
    }

    public ObservableList<Task> getTasksInError() {
        return tasksInError;
    }

    @Override
    public synchronized void close() throws IOException {
        closeTask();
    }

    public synchronized void reset() {
        Task closeTask = closeTask();
        try {
            closeTask.get();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            LOGGER.log(Level.FINE, "Interruption requested !");
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "A thread executor cannot be closed. It's likely to create memory leaks !", ex);
        } finally {
            threadPool = Executors.newCachedThreadPool();
        }
    }

    private synchronized Task closeTask() {
        final Task shutdownTask = new MockTask("Shutdown remaining tasks.", () -> {
            try {
                threadPool.shutdown();
                threadPool.awaitTermination(TIMEOUT, TIMEOUT_UNIT);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.WARNING, "Application thread pool interrupted !", ex);
            } finally {
                final List<Runnable> waitingTasks = threadPool.shutdownNow();
                Platform.runLater(()->submittedTasks.removeAll(waitingTasks));
            }
        });
        Platform.runLater(()->submittedTasks.add(shutdownTask));
        new TaskStateListener(shutdownTask);
        new Thread(shutdownTask).start();
        return shutdownTask;
    }
    
    /**
     * Watch a task state, and move it in the right list at change. We remove it
     * from submitted tasks when it's done, and add it to tasks in error if it
     * failed.
     */
    private class TaskStateListener implements ChangeListener {

        private final Task toWatch;
        
        public TaskStateListener(final Task t) {
            ArgumentChecks.ensureNonNull("Task to watch", t);
            toWatch = t;
            t.stateProperty().addListener(this);
        }
        
        @Override
        public void changed(ObservableValue observable, Object oldValue, Object newValue) {
            if (Worker.State.FAILED.equals(newValue)) {
                Platform.runLater(()->tasksInError.add(toWatch));
            }
            if (Worker.State.FAILED.equals(newValue)
                    || Worker.State.SUCCEEDED.equals(newValue)
                    || Worker.State.CANCELLED.equals(newValue)) {
                Platform.runLater(()->submittedTasks.remove(toWatch));
            }
        }
        
    }
    
    /**
     * A wrapper allowing to execute a runnable or a callable using {@link Task} API.
     * 
     * @param <V> Return type to set on the task. It should be the same of embedded
     * callable. If a runnable is embedded, task result will always be null, whatever
     * type you choose.
     */
    public static class MockTask<V> extends Task<V> {

        private final Object runnableOrCallable;
        
        public MockTask(final Callable<V> toCall) {
            this(null, toCall);
        }
        
        public MockTask(final Runnable toRun) {
            this(null, toRun);
        }        
        
        public MockTask(final String title, final Callable<V> toCall) {
            ArgumentChecks.ensureNonNull("Callable to execute", toCall);
            runnableOrCallable = toCall;
            if (title == null || title.isEmpty()) {
                updateTitle("Tâche sans titre.");
            } else {
                updateTitle(title);
            }
        }
        
        public MockTask(final String title, final Runnable toRun) {
            ArgumentChecks.ensureNonNull("Runnable to execute", toRun);
            runnableOrCallable = toRun;
            if (title == null || title.isEmpty()) {
                updateTitle("Tâche sans titre.");
            } else {
                updateTitle(title);
            }
        }
        
        @Override
        protected V call() throws Exception {
            if (Thread.currentThread().isInterrupted()) throw new InterruptedException("Cannot start task. Thread interrupted !");
            if (runnableOrCallable instanceof Callable) {
                return ((Callable<V>)runnableOrCallable).call();
            }else {
                ((Runnable)runnableOrCallable).run();
            }
            return null;
        }
        
    }
}
