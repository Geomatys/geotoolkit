package org.geotoolkit.gui.javafx.util;

import java.awt.Color;
import java.awt.Font;
import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
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
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;
import org.apache.sis.util.ArgumentChecks;
import org.geotoolkit.font.FontAwesomeIcons;
import org.geotoolkit.font.IconBuilder;
import static org.geotoolkit.gui.javafx.util.ProgressMonitor.CssClass.CANCEL_BUTTON;
import static org.geotoolkit.gui.javafx.util.ProgressMonitor.CssClass.CLEAR_MENU_ITEM;
import static org.geotoolkit.gui.javafx.util.ProgressMonitor.CssClass.MENU_ITEM;
import static org.geotoolkit.gui.javafx.util.ProgressMonitor.CssClass.TASK_PROGRESS;
import static org.geotoolkit.gui.javafx.util.ProgressMonitor.ResourceKey.ANONYM_OPERATION;
import static org.geotoolkit.gui.javafx.util.ProgressMonitor.ResourceKey.CLEAN_ERROR_LIST;
import static org.geotoolkit.gui.javafx.util.ProgressMonitor.ResourceKey.CURRENT_TASK;
import static org.geotoolkit.gui.javafx.util.ProgressMonitor.ResourceKey.ERROR_TASK;
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
 * css classes enumerated in {@link ProgressMonitor.CssClass}.
 * 
 * {@link ProgressMonitor} labels can be parametrized using {@link ResourceBundle} 
 * properties defined by {@link ProgressMonitor.ResourceKey}.
 * 
 * @author Alexis Manin (Geomatys)
 */
public class ProgressMonitor extends HBox {

    public static final Font AWESOME_FONT = IconBuilder.FONT.deriveFont(11f);
    public static final Image ICON_CANCEL = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_BAN, null, Color.BLACK, AWESOME_FONT, null, null, 2, false, true), null);
    public static final Image ICON_ERROR = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_EXCLAMATION_CIRCLE, null, new Color(200, 0, 0), AWESOME_FONT, null, null, 0, false, true), null);
    public static final Image ICON_RUNNING_TASKS = SwingFXUtils.toFXImage(IconBuilder.createImage(FontAwesomeIcons.ICON_ELLIPSIS_V, null, new Color(0, 200, 220), AWESOME_FONT, null, null, 0, false, true), null);
    
    private TaskProgress lastTask;

    private final MenuButton runningTasks;
    private final MenuButton tasksInError;
    private final Image cancelImage;
    private final ResourceBundle resourceBundle;
    private final String css;

    private final TaskManager taskRegistry;
    
    /**
     * Return the String associated to the given key for UI purposes. First, this
     * method tries to found a value using the {@link ProgressMonitor} associated
     * {@link ResourceBundle}. In case of Exception thrown during the resource
     * search and retrieval, the default value of the {@link ResourceKey} is 
     * returned.
     * 
     * @param key
     * @return The String associated to the given key for UI purpose.
     */
    private String getResourceString(final ResourceKey key){
        try{
            return resourceBundle.getString(key.getKey());
        } catch (Exception e){
            return key.getDefaultValue();
        }
    }
    
    /**
     * The keys and associated default values used in the {@link ProgressMonitor} UI.
     */
    public enum ResourceKey {
        
        CURRENT_TASK("currentTasks",        "Tâches en cours"), 
        ERROR_TASK("errorTasks",            "Tâches échouées"),
        ANONYM_OPERATION("anonymOperation", "Opération anonyme"),
        CLEAN_ERROR_LIST("cleanErrorList",  "Vider la liste");
        
        private String key;
        private String defaultValue;
        
        ResourceKey(final String key, final String defaultValue){
            this.key = key;
            this.defaultValue = defaultValue;
        }
        
        public String getKey(){return key;}
        public String getDefaultValue(){return defaultValue;}
    }
    
    /**
     * The css classes associated to the {@link ProgressMonitor} nodes.
     */
    public enum CssClass {
        CURRENT_TASK("geotk-progressMonitor-runningTasks"), 
        ERROR_TASK("geotk-progressMonitor-tasksInError"), 
        PROGRESS_MONITOR("geotk-progressMonitor"),
        CANCEL_BUTTON("geotk-progressMonitor-cancelButton"), 
        TASK_PROGRESS("geotk-progressMonitor-taskProgress"),
        MENU_ITEM("geotk-progressMonitor-menuItem"),
        CLEAR_MENU_ITEM("geotk-progressMonitor-clearMenuItem");
        
        private String cssClass;
        
        CssClass(final String cssClass){
            this.cssClass = cssClass;
        }
        
        public String getCssClass(){return cssClass;}
    }
    
    /**
     * 
     * @param registry The {@link TaskManager} followed by this progress monitor.
     * 
     * @see ProgressMonitor#ProgressMonitor(org.geotoolkit.gui.javafx.util.TaskManager, java.lang.String) 
     */
    public ProgressMonitor(final TaskManager registry){
        this(registry, null);
    }
    
    /**
     * 
     * @param registry The {@link TaskManager} followed by this progress monitor.
     * @param styleSheet The stylesheet URI used by the monitor and by its nodes.
     * 
     * @see ProgressMonitor#ProgressMonitor(org.geotoolkit.gui.javafx.util.TaskManager, java.util.ResourceBundle, java.lang.String) 
     */
    public ProgressMonitor(final TaskManager registry, final String styleSheet){
        this(registry, (ResourceBundle) null, styleSheet);
    }

    /**
     * 
     * @param registry The {@link TaskManager} followed by this progress monitor.
     * @param locale The locale used to retrieve the resource bundle for UI strings.
     * If {@code null}, the {@link Locale#getDefault() } will be used. This 
     * constructor assumes it exists some bundles for the {@link ProgressMonitor}
     * base name. On the contrary, a {@link MissingResourceException} will be thrown.
     * @param styleSheet The stylesheet URI used by the monitor and by its nodes.
     * 
     * @exception MissingResourceException if no resource bundle for the {@link ProgressMonitor} base name can be found
     * @see ProgressMonitor#ProgressMonitor(org.geotoolkit.gui.javafx.util.TaskManager, javafx.scene.image.Image, javafx.scene.image.Image, javafx.scene.image.Image, java.util.Locale, java.lang.String) 
     */
    public ProgressMonitor(final TaskManager registry, final Locale locale, final String styleSheet) {
        this(registry, ICON_RUNNING_TASKS, ICON_ERROR, ICON_CANCEL, locale, styleSheet);
    }
    
    /**
     * 
     * @param registry The {@link TaskManager} followed by this progress monitor.
     * @param resourceBundle The {@link ResourceBundle} used for UI purposes. If 
     * {@code null}, the default UI values defined by {@link ResourceKey} will be used.
     * @param styleSheet The stylesheet URI used by the monitor and by its nodes.
     * 
     * @see ProgressMonitor#ProgressMonitor(org.geotoolkit.gui.javafx.util.TaskManager, javafx.scene.image.Image, javafx.scene.image.Image, javafx.scene.image.Image, java.util.ResourceBundle, java.lang.String) 
     */
    public ProgressMonitor(final TaskManager registry, final ResourceBundle resourceBundle, final String styleSheet) {
        this(registry, ICON_RUNNING_TASKS, ICON_ERROR, ICON_CANCEL, resourceBundle, styleSheet);
    }
    
    /**
     * 
     * @param registry The {@link TaskManager} followed by this progress monitor.
     * @param runningTasksImage The image used to show running tasks.
     * @param tasksInErrorImage the image used to show tasks in error.
     * @param cancelImage The image used to cancel a running task.
     * 
     * @exception MissingResourceException if no resource bundle for the {@link ProgressMonitor} base name can be found
     * @see ProgressMonitor#ProgressMonitor(org.geotoolkit.gui.javafx.util.TaskManager, javafx.scene.image.Image, javafx.scene.image.Image, javafx.scene.image.Image, java.util.Locale) 
     */
    public ProgressMonitor(final TaskManager registry, final Image runningTasksImage, 
            final Image tasksInErrorImage, final Image cancelImage){
        this(registry, runningTasksImage, tasksInErrorImage, cancelImage, null);
    }
    
    /**
     * 
     * @param registry The {@link TaskManager} followed by this progress monitor.
     * @param runningTasksImage The image used to show running tasks.
     * @param tasksInErrorImage the image used to show tasks in error.
     * @param cancelImage The image used to cancel a running task.
     * @param locale The locale used to retrieve the resource bundle for UI strings.
     * If {@code null}, the {@link Locale#getDefault() } will be used. This 
     * constructor assumes it exists some bundles for the {@link ProgressMonitor}
     * base name. On the contrary, a {@link MissingResourceException} will be thrown.
     * 
     * @exception MissingResourceException if no resource bundle for the {@link ProgressMonitor} base name can be found
     * @see ProgressMonitor#ProgressMonitor(org.geotoolkit.gui.javafx.util.TaskManager, javafx.scene.image.Image, javafx.scene.image.Image, javafx.scene.image.Image, java.util.Locale, java.lang.String) 
     */
    public ProgressMonitor(final TaskManager registry, final Image runningTasksImage, 
            final Image tasksInErrorImage, final Image cancelImage, final Locale locale){
        this(registry, runningTasksImage, tasksInErrorImage, cancelImage, locale, null);
    }
    
    /**
     * 
     * @param registry The {@link TaskManager} followed by this progress monitor.
     * @param runningTasksImage The image used to show running tasks.
     * @param tasksInErrorImage the image used to show tasks in error.
     * @param cancelImage The image used to cancel a running task.
     * @param locale The locale used to retrieve the resource bundle for UI strings.
     * If {@code null}, the {@link Locale#getDefault() } will be used. This 
     * constructor assumes it exists some bundles for the {@link ProgressMonitor}
     * base name. On the contrary, a {@link MissingResourceException} will be thrown.
     * @param styleSheet The stylesheet URI used by the monitor and by its nodes.
     * 
     * @exception MissingResourceException if no resource bundle for the {@link ProgressMonitor} base name can be found
     */
    public ProgressMonitor(final TaskManager registry, final Image runningTasksImage, 
            final Image tasksInErrorImage, final Image cancelImage, 
            final Locale locale, final String styleSheet) {
        this(registry, runningTasksImage, tasksInErrorImage, cancelImage, ResourceBundle.getBundle(ProgressMonitor.class.getName(), locale==null ? Locale.getDefault() : locale), styleSheet);
    }
    
    /**
     * The base constructor of progress monitors.
     * 
     * @param registry The {@link TaskManager} followed by this progress monitor.
     * @param runningTasksImage The image used to show running tasks.
     * @param tasksInErrorImage the image used to show tasks in error.
     * @param cancelImage The image used to cancel a running task.
     * @param resourceBundle The {@link ResourceBundle} used for UI purposes. If 
     * {@code null}, the default UI values defined by {@link ResourceKey} will be used.
     * @param styleSheet The stylesheet URI used by the monitor and by its nodes.
     * If the parameter is {@code null}, or the URI does not exists, the 
     * constructor tries to found a resource file in the very 
     * {@link ProgressMonitor} package, named "ProgressMonitor.css". If such a 
     * resource is not found, a default style is applied.
     */
    public ProgressMonitor(final TaskManager registry, final Image runningTasksImage, 
            final Image tasksInErrorImage, final Image cancelImage, 
            final ResourceBundle resourceBundle, final String styleSheet) {
        ArgumentChecks.ensureNonNull("Input task registry", registry);
        
        if(styleSheet==null){
            final URL cssResource = ProgressMonitor.class.getResource(ProgressMonitor.class.getSimpleName()+".css");
            css = cssResource==null ? null : cssResource.toString();
        } 
        else{
            css = styleSheet;
        }
        
        taskRegistry = registry;
        this.cancelImage = cancelImage;
        this.resourceBundle = resourceBundle;

        runningTasks = new MenuButton("", new ImageView(runningTasksImage));
        tasksInError = new MenuButton("", new ImageView(tasksInErrorImage));
        
        runningTasks.setTooltip(new Tooltip(getResourceString(CURRENT_TASK)));
        tasksInError.setTooltip(new Tooltip(getResourceString(ERROR_TASK)));
        
        final SimpleListProperty runningTasksProp = new SimpleListProperty(taskRegistry.getSubmittedTasks());
        final SimpleListProperty failedTasksProp = new SimpleListProperty(taskRegistry.getTasksInError());

        // Hide list of tasks if there's no information available.
        runningTasks.visibleProperty().bind(runningTasksProp.sizeProperty().greaterThan(1));
        tasksInError.visibleProperty().bind(failedTasksProp.emptyProperty().not());
        
        // Display number of tasks on menu button.
        runningTasks.textProperty().bind(runningTasksProp.sizeProperty().asString());
        tasksInError.textProperty().bind(failedTasksProp.sizeProperty().asString());

        // Set default visible task the last one submitted.
        lastTask = new TaskProgress(cancelImage);
        lastTask.taskProperty().bind(runningTasksProp.valueAt(runningTasksProp.sizeProperty().subtract(1)));

        // Do not reserve size for hidden components.
        runningTasks.managedProperty().bind(runningTasks.visibleProperty());
        tasksInError.managedProperty().bind(tasksInError.visibleProperty());
        lastTask.managedProperty().bind(lastTask.visibleProperty());
        
        initTasks();
                    
        getChildren().addAll(lastTask, runningTasks, tasksInError);
        
        minWidthProperty().bind(prefWidthProperty());
        prefWidthProperty().set(USE_COMPUTED_SIZE);
        
        if(css==null){
            setSpacing(10);
            setAlignment(Pos.CENTER);
            runningTasks.setFont(javafx.scene.text.Font.font(9f));
            tasksInError.setFont(javafx.scene.text.Font.font(9f));
            runningTasks.setAlignment(Pos.CENTER);
            tasksInError.setAlignment(Pos.CENTER);

            // TODO : put style in CSS          
            runningTasks.setBorder(Border.EMPTY);
            tasksInError.setBorder(Border.EMPTY);

            runningTasks.setBackground(Background.EMPTY);
            tasksInError.setBackground(Background.EMPTY);
            runningTasks.setMinWidth(0);   
        }
        else { 
            getStylesheets().add(css);
            getStyleClass().add(CssClass.PROGRESS_MONITOR.getCssClass());
            runningTasks.getStyleClass().add(CssClass.CURRENT_TASK.getCssClass());
            tasksInError.getStyleClass().add(CssClass.ERROR_TASK.getCssClass());
        }
    }

    /**
     * Fill panel with currently submitted tasks. Add listeners on
     * {@link TaskManager} to be aware of new events.
     */
    private void initTasks() {

        final MenuItem clearErrorItem = new MenuItem(getResourceString(CLEAN_ERROR_LIST));
        clearErrorItem.setOnAction(evt -> taskRegistry.getTasksInError().clear());
        clearErrorItem.setGraphic(new ImageView(GeotkFX.ICON_DELETE));
        if(css!=null){
            clearErrorItem.getStyleClass().add(CLEAR_MENU_ITEM.getCssClass());
        }
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
                    final CustomMenuItem item = new CustomMenuItem(new TaskProgress(task, cancelImage));
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
                final CustomMenuItem item = new CustomMenuItem(new TaskProgress(tmpSubmittedTasks.get(i), cancelImage));
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
    private class TaskProgress extends HBox {

        private final Label title = new Label();
        private final Tooltip description = new Tooltip();
        private final ProgressBar progress = new ProgressBar();
        private final Button cancelButton;
        
        private final ObjectProperty<Task> taskProperty = new SimpleObjectProperty<>();

        TaskProgress(final Image cancelImage) {
            this(null, cancelImage);
        }

        TaskProgress(final Task t, final Image cancelImage) {
            
            cancelButton = new Button("", new ImageView(cancelImage));

            taskProperty.addListener((ObservableValue<? extends Task> observable, Task oldValue, Task newValue) -> {
                if (Platform.isFxApplicationThread()) {
                    taskUpdated();
                } else {
                    Platform.runLater(()->taskUpdated());
                }
            });
            
            taskProperty.set(t);
            
            getChildren().addAll(title, progress, cancelButton);
            
            if(css==null){ // Default Layout
                setSpacing(5);
                setAlignment(Pos.CENTER);
                // TODO : put style rule in CSS.
                cancelButton.setPadding(Insets.EMPTY);
                cancelButton.setBackground(Background.EMPTY);
                cancelButton.setBorder(Border.EMPTY);
            }
            else {
                getStyleClass().add(TASK_PROGRESS.getCssClass());
                cancelButton.getStyleClass().add(CANCEL_BUTTON.getCssClass());
            }
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
                title = getResourceString(ANONYM_OPERATION);
            setText(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))+" - "+title);
            Dialog d = GeotkFX.newExceptionDialog(failedTask.getMessage(), failedTask.getException());
            d.setResizable(true);

            setOnAction((ActionEvent ae) -> d.show());
            
            if(css!=null){
                getStyleClass().add(MENU_ITEM.getCssClass());
            }
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