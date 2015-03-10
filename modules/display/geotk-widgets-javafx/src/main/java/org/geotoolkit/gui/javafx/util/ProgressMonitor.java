package org.geotoolkit.gui.javafx.util;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import org.geotoolkit.internal.GeotkFX;

/**
 * A JavaFX component which display progress and encountered errors for all tasks 
 * submitted on a specific task manager.
 * 
 * The last submitted task is displayed in an Hbox. To see other running tasks
 * and tasks in error, there's two {@link MenuButton}. Each display custom menu
 * items containing information about a task.
 * 
 * @author Alexis Manin (Geomatys)
 */
public class ProgressMonitor extends HBox {

    public static final Image ICON_CANCEL = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_BAN, 16, Color.BLACK), null);
    public static final Image ICON_ERROR = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_EXCLAMATION_CIRCLE, 16, new Color(200, 0, 0)), null);
    public static final Image ICON_RUNNING_TASKS = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_ELLIPSIS_V, 16, new Color(0, 200, 220)), null);

    private TaskProgress lastTask = new TaskProgress();

    private final MenuButton runningTasks = new MenuButton("", new ImageView(ICON_RUNNING_TASKS));
    private final MenuButton tasksInError = new MenuButton("", new ImageView(ICON_ERROR));

    private final TaskManager taskRegistry;

    public ProgressMonitor(TaskManager registry) {
        ArgumentChecks.ensureNonNull("Input task registry", registry);
        setSpacing(10);
        setAlignment(Pos.CENTER);
        minWidthProperty().bind(prefWidthProperty());
        prefWidthProperty().set(USE_COMPUTED_SIZE);

        taskRegistry = registry;

        runningTasks.setTooltip(new Tooltip("Tâches en cours"));
        tasksInError.setTooltip(new Tooltip("Tâches échouées"));
        
        SimpleListProperty runningTasksProp = new SimpleListProperty(taskRegistry.getSubmittedTasks());
        SimpleListProperty failedTasksProp = new SimpleListProperty(taskRegistry.getTasksInError());

        // Hide list of tasks if there's no information available.
        runningTasks.visibleProperty().bind(runningTasksProp.sizeProperty().greaterThan(1));
        tasksInError.visibleProperty().bind(failedTasksProp.emptyProperty().not());
        
        // Display number of tasks on menu button.
        runningTasks.textProperty().bind(runningTasksProp.sizeProperty().asString());
        tasksInError.textProperty().bind(failedTasksProp.sizeProperty().asString());

        // Set default visible task the last one submitted.
        lastTask.taskProperty().bind(runningTasksProp.valueAt(runningTasksProp.sizeProperty().subtract(1)));

        // Do not reserve size for hidden components.
        runningTasks.managedProperty().bind(runningTasks.visibleProperty());
        tasksInError.managedProperty().bind(tasksInError.visibleProperty());
        lastTask.managedProperty().bind(lastTask.visibleProperty());

        runningTasks.setMinWidth(0);
        runningTasks.setAlignment(Pos.CENTER);
        tasksInError.setAlignment(Pos.CENTER);

        runningTasks.setBorder(Border.EMPTY);
        tasksInError.setBorder(Border.EMPTY);
        
        initTasks();

        getChildren().addAll(lastTask, runningTasks, tasksInError);
    }

    /**
     * Fill panel with currently submitted tasks. Add listeners on
     * {@link TaskManager} to be aware of new events.
     */
    private void initTasks() {
        // Listen on current running tasks
        final ObservableList<Task> tmpSubmittedTasks = taskRegistry.getSubmittedTasks();
        tmpSubmittedTasks.addListener((ListChangeListener.Change<? extends Task> c) -> {

            final HashSet<Task> toAdd = new HashSet<>();
            final HashSet<Task> toRemove = new HashSet<>();
            storeChanges(c, toAdd, toRemove);

            Platform.runLater(() -> {
                for (Task task : toAdd) {
                    final CustomMenuItem item = new CustomMenuItem(new TaskProgress(task));
                    item.setHideOnClick(false);
                    runningTasks.getItems().add(item);
                }
                // remove Ended tasks
                runningTasks.getItems().removeIf(new GetItemsForTask(toRemove));
            });
        });
        final ObservableList<Task> tmpTasksInError = taskRegistry.getTasksInError();

        // Check failed tasks.
        tmpTasksInError.addListener((ListChangeListener.Change<? extends Task> c) -> {
            final HashSet<Task> toAdd = new HashSet<>();
            final HashSet<Task> toRemove = new HashSet<>();
            storeChanges(c, toAdd, toRemove);

            Platform.runLater(() -> {
                for (Task task : toAdd) {
                    tasksInError.getItems().addAll(new ErrorMenuItem(task));
                }
                // remove Ended tasks
                tasksInError.getItems().removeIf(new GetItemsForTask(toRemove));
            });
        });

        final Runnable initPanel = () -> {
            final int nbSubmitted = tmpSubmittedTasks.size();
            // do not add last task to our menu, it will be used on main display.
            for (int i = 0; i < nbSubmitted; i++) {
                final CustomMenuItem item = new CustomMenuItem(new TaskProgress(tmpSubmittedTasks.get(i)));
                item.setHideOnClick(false);
                runningTasks.getItems().add(item);
            }

            for (Task t : tmpTasksInError) {
                tasksInError.getItems().add(new ErrorMenuItem(t));
            }
        };

        if (Platform.isFxApplicationThread()) {
            initPanel.run();
        } else {
            Platform.runLater(initPanel);
        }
    }

    /**
     * Store all objects depicted by a {@link ListChangeListener} into given 
     * collections.
     * 
     * @param c The {@link ListChangeListener.Change} containing list content delta.
     * @param added The collection to store new added objects into.
     * @param removed Add removed objects in it.
     */
    private static void storeChanges(final ListChangeListener.Change c, final Collection added, final Collection removed) {
        while (c.next()) {
            final List<? extends Task> addedSubList = c.getAddedSubList();
            final List<? extends Task> removeSubList = c.getRemoved();

            if (addedSubList != null && !addedSubList.isEmpty()) {
                added.addAll(addedSubList);
            }

            Iterator<? extends Task> it = removeSubList.iterator();
            while (it.hasNext()) {
                final Task current = it.next();
                if (!added.remove(current)) {
                    removed.add(current);
                }
            }
        }
    }
    
    /**
     * The node giving information about a specific task. Allow to see title,
     * description and current progress, as to cancel the task.
     */
    private static class TaskProgress extends HBox {

        private final Label title = new Label();
        private final Tooltip description = new Tooltip();
        private final ProgressBar progress = new ProgressBar();
        private final Button cancelButton = new Button("", new ImageView(ICON_CANCEL));

        private final ObjectProperty<Task> taskProperty = new SimpleObjectProperty<>();

        public TaskProgress() {
            this(null);
        }

        public TaskProgress(final Task t) {
            setSpacing(5);
            setAlignment(Pos.CENTER);
            cancelButton.prefHeightProperty().bind(progress.prefHeightProperty());
            cancelButton.prefWidthProperty().bind(progress.prefHeightProperty());
            cancelButton.maxHeightProperty().bind(progress.prefHeightProperty());
            cancelButton.maxWidthProperty().bind(progress.prefHeightProperty());

            getChildren().addAll(title, progress, cancelButton);

            taskProperty.addListener((ObservableValue<? extends Task> observable, Task oldValue, Task newValue) -> {
                if (Platform.isFxApplicationThread()) {
                    taskUpdated();
                } else {
                    Platform.runLater(()->taskUpdated());
                }
            });
            
            taskProperty.set(t);
        }

        public ObjectProperty<Task> taskProperty() {
            return taskProperty;
        }

        public Task getTask() {
            return taskProperty.get();
        }

        public synchronized void taskUpdated() {
            title.textProperty().unbind();
            progress.progressProperty().unbind();
            description.textProperty().unbind();

            cancelButton.setOnAction(null);

            final Task t =taskProperty.get();
            if (t != null) {
                title.textProperty().bind(t.titleProperty());
                description.textProperty().bind(t.messageProperty());
                progress.progressProperty().bind(t.progressProperty());
                cancelButton.setOnAction((ActionEvent e) -> t.cancel());
                setVisible(true);
            } else {
                setVisible(false);
            }
        }
    }

    /**
     * A simple menu items for failed tasks. Display an exception Dialog when clicked.
     */
    private static class ErrorMenuItem extends MenuItem {

        final Task failedTask;

        public ErrorMenuItem(final Task failedTask) {
            ArgumentChecks.ensureNonNull("task in error", failedTask);
            this.failedTask = failedTask;
            textProperty().bind(this.failedTask.titleProperty());
            Dialog d = GeotkFX.newExceptionDialog(failedTask.getMessage(), failedTask.getException());
            d.setResizable(true);

            setOnAction((ActionEvent ae) -> d.show());
        }

        public Task getTask() {
            return failedTask;
        }
    }

    /**
     * A simple {@link Predicate} which return current monitor progress bars
     * which are focused on one of the given tasks.
     */
    private static class GetItemsForTask implements Predicate<MenuItem> {

        private final Collection<? extends Task> tasks;

        public GetItemsForTask(final Collection<? extends Task> taskFilter) {
            ArgumentChecks.ensureNonNull("Input filter tasks", taskFilter);
            tasks = taskFilter;
        }

        @Override
        public boolean test(MenuItem item) {
            if (item instanceof CustomMenuItem) {
                Node content = ((CustomMenuItem) item).getContent();
                return (content instanceof TaskProgress
                        && tasks.contains(((TaskProgress) content).getTask()));
            } else if (item instanceof ErrorMenuItem) {
                return tasks.contains(((ErrorMenuItem) item).getTask());
            }
            return false;
        }
    }
}
