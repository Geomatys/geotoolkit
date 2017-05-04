package org.geotoolkit.gui.javafx.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import javafx.scene.text.Font;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.internal.GeotkFX;

/**
 * A JavaFX component which display progress and encountered errors for all tasks
 * submitted on a specific task manager.
 *
 * The last submitted task is displayed in an Hbox. To see other running tasks
 * and tasks in error, there's two {@link MenuButton}. Each display custom menu
 * items containing information about a task.
 *
 * The {@link ProgressMonitor} is skinnable using a css stylesheet and the specific
 * following css classes:
 * {@link ProgressMonitor#CURRENT_TASK_CSS_CLASS}
 * {@link ProgressMonitor#CURRENT_TASK_GRAPHIC_CSS_CLASS}
 * {@link ProgressMonitor#ERROR_TASK_CSS_CLASS}
 * {@link ProgressMonitor#ERROR_TASK_GRAPHIC_CSS_CLASS}
 * {@link ProgressMonitor#PROGRESS_MONITOR_CSS_CLASS}
 * {@link ProgressMonitor#CANCEL_BUTTON_CSS_CLASS}
 * {@link ProgressMonitor#TASK_PROGRESS_CSS_CLASS}
 * {@link ProgressMonitor#TASK_PROGRESS_GRAPHIC_CSS_CLASS}
 * {@link ProgressMonitor#MENU_ITEM_CSS_CLASS}
 * {@link ProgressMonitor#CLEAR_MENU_ITEM_CSS_CLASS}
 *
 * @author Alexis Manin (Geomatys)
 */
public class ProgressMonitor extends HBox {

    private static String ICON_LABEL_FONT_FAMILY = "-fx-font-family: FontAwesome Regular;";

    /**
     * The css classes associated to the {@link ProgressMonitor} nodes.
     */
    public static final String CURRENT_TASK_CSS_CLASS="geotk-progressMonitor-runningTasks";
    public static final String CURRENT_TASK_GRAPHIC_CSS_CLASS="geotk-progressMonitor-runningTasks-graphic";
    public static final String ERROR_TASK_CSS_CLASS="geotk-progressMonitor-tasksInError";
    public static final String ERROR_TASK_GRAPHIC_CSS_CLASS="geotk-progressMonitor-tasksInError-graphic";
    public static final String PROGRESS_MONITOR_CSS_CLASS="geotk-progressMonitor";
    public static final String CANCEL_BUTTON_CSS_CLASS="geotk-progressMonitor-cancelButton";
    public static final String TASK_PROGRESS_CSS_CLASS="geotk-progressMonitor-taskProgress";
    public static final String TASK_PROGRESS_GRAPHIC_CSS_CLASS="geotk-progressMonitor-taskProgress-graphic";
    public static final String MENU_ITEM_CSS_CLASS="geotk-progressMonitor-menuItem";
    public static final String CLEAR_MENU_ITEM_CSS_CLASS="geotk-progressMonitor-clearMenuItem";

    private TaskProgress lastTask;

    private final MenuButton runningTasks;
    private final MenuButton tasksInError;

    private final TaskManager taskRegistry;

    static {
        // Load Font Awesome.
        final Font font = FXUtilities.FONTAWESOME;
    }

    /**
     * The base constructor of progress monitors.
     *
     * @param registry The {@link TaskManager} followed by this progress monitor.
     */
    public ProgressMonitor(final TaskManager registry) {
        ArgumentChecks.ensureNonNull("Input task registry", registry);

        taskRegistry = registry;

        final Label runningIcon = new Label(FontAwesomeIcons.ICON_GEARS_ALIAS);
        runningIcon.setStyle(ICON_LABEL_FONT_FAMILY);
        runningIcon.getStyleClass().add(CURRENT_TASK_GRAPHIC_CSS_CLASS);
        runningTasks = new MenuButton(GeotkFX.getString(ProgressMonitor.class, "currentTasksLabel"), runningIcon);
        final Label errorIcon = new Label(FontAwesomeIcons.ICON_EXCLAMATION_CIRCLE);
        errorIcon.setStyle(ICON_LABEL_FONT_FAMILY);
        errorIcon.getStyleClass().add(ERROR_TASK_GRAPHIC_CSS_CLASS);
        tasksInError = new MenuButton(GeotkFX.getString(ProgressMonitor.class, "errorTasksLabel"), errorIcon);

        final Tooltip runninTasksTooltip = new Tooltip(GeotkFX.getString(ProgressMonitor.class, "currentTasksTooltip"));
        runningTasks.setTooltip(runninTasksTooltip);
        final Tooltip tasksInErrorTooltip = new Tooltip(GeotkFX.getString(ProgressMonitor.class, "errorTasksTooltip"));
        tasksInError.setTooltip(tasksInErrorTooltip);

        final SimpleListProperty runningTasksProp = new SimpleListProperty(taskRegistry.getSubmittedTasks());
        final SimpleListProperty failedTasksProp = new SimpleListProperty(taskRegistry.getTasksInError());

        // Hide list of tasks if there's no information available.
        runningTasks.visibleProperty().bind(runningTasksProp.sizeProperty().greaterThan(1));
        tasksInError.visibleProperty().bind(failedTasksProp.emptyProperty().not());

        // Display number of tasks on menu button.
        runningTasks.textProperty().bind(runningTasksProp.sizeProperty().asString());
        tasksInError.textProperty().bind(failedTasksProp.sizeProperty().asString());

        // Set default visible task the last one submitted.
        lastTask = new TaskProgress();
        lastTask.taskProperty().bind(runningTasksProp.valueAt(runningTasksProp.sizeProperty().subtract(1)));

        // Do not reserve size for hidden components.
        runningTasks.managedProperty().bind(runningTasks.visibleProperty());
        tasksInError.managedProperty().bind(tasksInError.visibleProperty());
        lastTask.managedProperty().bind(lastTask.visibleProperty());

        initTasks();

        getChildren().addAll(lastTask, runningTasks, tasksInError);
        minWidthProperty().bind(prefWidthProperty());
        prefWidthProperty().set(USE_COMPUTED_SIZE);

        getStyleClass().add(PROGRESS_MONITOR_CSS_CLASS);
        runningTasks.getStyleClass().add(CURRENT_TASK_CSS_CLASS);
        tasksInError.getStyleClass().add(ERROR_TASK_CSS_CLASS);
    }

    /**
     * Fill panel with currently submitted tasks. Add listeners on
     * {@link TaskManager} to be aware of new events.
     */
    private void initTasks() {

        final MenuItem clearErrorItem = new MenuItem(GeotkFX.getString(ProgressMonitor.class, "cleanErrorList"));
        clearErrorItem.setOnAction(evt -> taskRegistry.getTasksInError().clear());

        final Label icon = new Label(FontAwesomeIcons.ICON_TRASH_O);
        icon.setStyle(ICON_LABEL_FONT_FAMILY);
        clearErrorItem.setGraphic(icon);

        clearErrorItem.getStyleClass().add(CLEAR_MENU_ITEM_CSS_CLASS);

        tasksInError.getItems().add(clearErrorItem);
        tasksInError.getItems().add(new SeparatorMenuItem());

        // Listen on current running tasks
        final ObservableList<Task> tmpSubmittedTasks = taskRegistry.getSubmittedTasks();
        tmpSubmittedTasks.addListener((ListChangeListener.Change<? extends Task> c) -> {

            final Set<Task> toAdd = new HashSet<>();
            final Set<Task> toRemove = new HashSet<>();
            storeChanges(c, toAdd, toRemove);

            Platform.runLater(() -> {
                for (final Task task : toAdd) {
                    final CustomMenuItem item = new CustomMenuItem(new TaskProgress(task));
                    item.setHideOnClick(false);
                    runningTasks.getItems().add(item);
                }
                // remove Ended tasks
                runningTasks.getItems().removeIf(new GetItemsForTask(toRemove));
            });
        });

        // Check failed tasks.
        final ObservableList<Task> tmpTasksInError = taskRegistry.getTasksInError();
        tmpTasksInError.addListener((ListChangeListener.Change<? extends Task> c) -> {
            final Set<Task> toAdd = new HashSet<>();
            final Set<Task> toRemove = new HashSet<>();
            storeChanges(c, toAdd, toRemove);

            Platform.runLater(() -> {
                for (final Task task : toAdd) {
                    tasksInError.getItems().add(new ErrorMenuItem(task));
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

            for (final Task t : tmpTasksInError) {
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

            final Iterator<? extends Task> it = removeSubList.iterator();
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
        private final Button cancelButton;

        private final ObjectProperty<Task> taskProperty = new SimpleObjectProperty<>();

        TaskProgress() {
            this(null);
        }

        TaskProgress(final Task t) {
            final Label icon = new Label(FontAwesomeIcons.ICON_BAN);
            icon.setStyle(ICON_LABEL_FONT_FAMILY);
            icon.getStyleClass().add(TASK_PROGRESS_GRAPHIC_CSS_CLASS);
            cancelButton = new Button("", icon);

            taskProperty.addListener((ObservableValue<? extends Task> observable, Task oldValue, Task newValue) -> {
                if (Platform.isFxApplicationThread()) {
                    taskUpdated();
                } else {
                    Platform.runLater(()->taskUpdated());
                }
            });

            taskProperty.set(t);

            getChildren().addAll(title, progress, cancelButton);

            getStyleClass().add(TASK_PROGRESS_CSS_CLASS);
            cancelButton.getStyleClass().add(CANCEL_BUTTON_CSS_CLASS);
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
    private class ErrorMenuItem extends MenuItem {

        final Task failedTask;

        ErrorMenuItem(final Task failedTask) {
            ArgumentChecks.ensureNonNull("task in error", failedTask);

            this.failedTask = failedTask;
            // No need for binding here. Task failed, its state should not change anymore.
            String title = failedTask.getTitle();
            if (title == null || title.isEmpty())
                title = GeotkFX.getString(ProgressMonitor.class, "anonymOperation");
            setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))+" - "+title);

            final Label icon = new Label(FontAwesomeIcons.ICON_TRASH_O);
            icon.setStyle(ICON_LABEL_FONT_FAMILY);

            final Button deleteButton = new Button("", icon);
            deleteButton.setBorder(Border.EMPTY);
            deleteButton.setPadding(Insets.EMPTY);
            deleteButton.setBackground(Background.EMPTY);
            deleteButton.setOnAction(e -> {
                taskRegistry.getTasksInError().remove(failedTask);
                e.consume();
            });
            setGraphic(deleteButton);

            final Dialog d = GeotkFX.newExceptionDialog(failedTask.getMessage(), failedTask.getException());
            d.setResizable(true);

            setOnAction((ActionEvent ae) -> d.show());

            getStyleClass().add(MENU_ITEM_CSS_CLASS);
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

        GetItemsForTask(final Collection<? extends Task> taskFilter) {
            ArgumentChecks.ensureNonNull("Input filter tasks", taskFilter);
            tasks = taskFilter;
        }

        @Override
        public boolean test(MenuItem item) {
            if (item instanceof CustomMenuItem) {
                final Node content = ((CustomMenuItem) item).getContent();
                return (content instanceof TaskProgress
                        && tasks.contains(((TaskProgress) content).getTask()));
            } else if (item instanceof ErrorMenuItem) {
                return tasks.contains(((ErrorMenuItem) item).getTask());
            }
            return false;
        }
    }
}
